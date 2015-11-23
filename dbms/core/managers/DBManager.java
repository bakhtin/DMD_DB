package core.managers;

import Server.DBServer;
import core.descriptive.Attribute;
import core.descriptive.Page;
import core.descriptive.TableSchema;
import core.exceptions.DBStatus;
import core.exceptions.RecordStatus;
import core.exceptions.SQLError;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.Index;
import net.sf.jsqlparser.statement.insert.Insert;
import org.mapdb.BTreeKeySerializer;
import org.mapdb.Serializer;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentNavigableMap;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/4/2015
 */
public class DBManager {
    public static HashMap<String, TableSchema> tables; // String key: table_name; TableSchema value: table_description
    public static PageManager pageManager;
    public static CacheManager cacheManager;
    public static TreeManager treeManager;
    public static RecordManager recordManager;
    static int totalPages = 0;
    String dbpath;
    RandomAccessFile file;
    File db;

    public DBManager(String path) throws Exception, RecordStatus, SQLError {
        this.dbpath = path;

        try {
            open();
        } catch (DBStatus dbStatus) {
            switch (dbStatus.getStatus()) {
                /** OK **/
                case DBStatus.DB_EXISTS:
                    System.out.println("DB File exists. Initializing...");

                    break;

                /** OK **/
                case DBStatus.DB_NOT_EXISTS:
                    System.out.println("DB File does not exist. Creating new DB file...");

                    break;

                /** ERROR **/
                case DBStatus.DB_ACCESS_ERROR:
                    System.err.println("Can't get access to file");
                    System.exit(DBStatus.DB_ACCESS_ERROR);
                    break;

                /** ERROR **/
                case DBStatus.DB_WRONG_FORMAT:
                    System.err.println("DB File has wrong format (Extra bytes in the file)");
                    System.exit(DBStatus.DB_WRONG_FORMAT);
                    break;
            }
        }
    }

    private static Object TypeCaster(Object obj) {
        if (obj instanceof LongValue) obj = (int) ((LongValue) obj).getValue();
        else if (obj instanceof StringValue) obj = ((StringValue) obj).getValue();
        return obj;
    }

    private static String normalizeString(String string) {
        return string.replace("`", "");
    }

    public static String processQuery(String query) throws SQLError, Exception, RecordStatus {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        query = normalizeString(query);

        //String statement = "INSERT INTO mytable VALUES (1, 'sadfsd', 234)";
        Statements statements = CCJSqlParserUtil.parseStatements(query);
        if (statements.getStatements().get(0) instanceof Insert) {
            analyzeInsert(statements);
        } else if (statements.getStatements().get(0) instanceof CreateTable) {
            analyzeCreateTable(statements);
        } else if (statements.getStatements().get(0) instanceof net.sf.jsqlparser.statement.select.Select) {
            analyzeSelect(statements);
        }
        return "QUERY_OK";
    }


    /**
     * SELECT
     **/
    private static void analyzeSelect(Statements statements) {
        net.sf.jsqlparser.statement.select.Select statement = (net.sf.jsqlparser.statement.select.Select) statements.getStatements().get(0);
        Select visitor = new Select();
        statement.getSelectBody().accept(visitor);
    }

    public static ConcurrentNavigableMap<Object[], Object[]> getTable(String tbl_name) throws SQLError {
        if (!DBServer.tables.containsKey(tbl_name)) {
            throw new SQLError("No such table: " + tbl_name);
        }

        TableSchema tSchema = DBServer.tables.get(tbl_name);
        int pk_len = tSchema.getPKlength();

        Serializer[] keySerializer = new Serializer[pk_len];
        Serializer[] valueSirializer = new Serializer[tSchema.attributes.length - pk_len];

        int q = 0, w = 0;
        for (int i = 0; i < tSchema.attributes.length; i++) {
            if (tSchema.attributes[i].hasFlag(Attribute.F_PK))
                keySerializer[q++] = tSchema.attributes[i].getSerializer();
            else
                valueSirializer[w++] = tSchema.attributes[i].getSerializer();
        }

        ConcurrentNavigableMap<Object[], Object[]> table = DBServer.db.treeMapCreate(tbl_name)
                .keySerializer(new BTreeKeySerializer.ArrayKeySerializer(keySerializer))
                .valueSerializer(new Serializer<Object[]>() {
                    @Override
                    public void serialize(DataOutput dataOutput, Object[] objects) throws IOException {
                        for (int i = 0; i < objects.length; i++) {
                            valueSirializer[i].serialize(dataOutput, objects[i]);
                        }
                    }

                    @Override
                    public Object[] deserialize(DataInput dataInput, int q) throws IOException {
                        Object[] result = new Object[valueSirializer.length];
                        for (int i = 0; i < result.length; i++) {
                            result[i] = valueSirializer[i].deserialize(dataInput, q);
                        }

                        return result;
                    }
                })
                .counterEnable()
                .makeOrGet();

        return table;

    }


    private static void analyzeInsert(Statements statements) throws SQLError {
        Insert statement = (Insert) statements.getStatements().get(0);
        List<Expression> expr;

        String tbl_name = statement.getTable().toString();

        TableSchema tSchema = DBServer.tables.get(tbl_name);
        ConcurrentNavigableMap<Object[], Object[]> table = getTable(tbl_name);

        int q, w;

        // if INSERT with more than 1 tuple
        try {
            List<ExpressionList> recordsList = ((MultiExpressionList) statement.getItemsList()).getExprList();
            for (ExpressionList row : recordsList) {
                expr = row.getExpressions();
                Object[] line = expr.toArray();
                for (int i = 0; i < line.length; i++) {
                    line[i] = TypeCaster(line[i]);
                }

                Attribute[] attrsArray = tSchema.attributes;
                TreeSet<Integer> indexList = new TreeSet<>();
                for (int i = 0; i < attrsArray.length; i++) {
                    if (attrsArray[i].hasFlag(Attribute.F_PK)) {
                        indexList.add(i);
                    }
                }

                Object[] pk = new Object[indexList.size()];
                Object[] data = new Object[line.length - indexList.size()];

                q = 0;
                w = 0;
                for (int i = 0; i < line.length; i++) {
                    if (indexList.contains(i))
                        pk[q++] = line[i];
                    else
                        data[w++] = line[i];
                }


                table.put(pk, data);

            }

            DBServer.db.commit();
        } catch (ClassCastException e) {
            // if INSERT with just one tuple
            try {
                expr = ((ExpressionList) statement.getItemsList()).getExpressions();
                Object[] line = expr.toArray();
                for (int i = 0; i < line.length; i++) {
                    line[i] = TypeCaster(line[i]);
                }

                if ((int) line[0] == 145873)
                    System.out.println("hui");

                Attribute[] attrsArray = tSchema.attributes;
                TreeSet<Integer> indexList = new TreeSet<>();
                for (int i = 0; i < attrsArray.length; i++) {
                    // indexed fields if PK or UQ
                    if (attrsArray[i].hasFlag(Attribute.F_PK)) {
                        indexList.add(i);
                    }
                }

                Object[] pk = new Object[indexList.size()];
                Object[] data = new Object[line.length - indexList.size()];

                q = 0;
                w = 0;
                for (int i = 0; i < line.length; i++) {
                    if (indexList.contains(i))
                        pk[q++] = line[i];
                    else
                        data[w++] = line[i];
                }

                table.put(pk, data);
                DBServer.db.commit();

            } catch (ClassCastException d) {
                throw new SQLError("Insert cannot be empty");
            }
                    /*
                    if row has the only PK
                     */
        }
    }

    /**
     * CREATE TABLE STATEMENT
     **/
    private static void analyzeCreateTable(Statements statements) throws SQLError {
        CreateTable statement = (CreateTable) statements.getStatements().get(0);
        int attrn = statement.getColumnDefinitions().size();

        Attribute[] attrs = new Attribute[attrn];
        String tbl_name = statement.getTable().toString();
        HashMap<String, Attribute> hmAttr = new HashMap<>();

        if (DBServer.tables.containsKey(tbl_name)) throw new SQLError("Table " + tbl_name + " algready exists");

        // for each attribute
        for (int i = 0; i < statement.getColumnDefinitions().size(); i++) {
            byte colDataType = (byte) Attribute.getDataType(statement.getColumnDefinitions().get(i).getColDataType().toString());
            attrs[i] = new Attribute(statement.getColumnDefinitions().get(i).getColumnName(), colDataType);
            int columnSpecStringsSize = statement.getColumnDefinitions().get(i).getColumnSpecStrings().size();

            // for each spec
            for (int j = 0; j < columnSpecStringsSize; j++) {
                if (columnSpecStringsSize > j + 1) {
                    if (statement.getColumnDefinitions().get(i).getColumnSpecStrings().get(j).equals("NOT") &&
                            statement.getColumnDefinitions().get(i).getColumnSpecStrings().get(j + 1).equals("NULL")) {
                        attrs[i].setFlag(Attribute.getConstraintType("NOT NULL"));
                    }
                }
                if (statement.getColumnDefinitions().get(i).getColumnSpecStrings().get(j).equals("AUTO_INCREMENT")) {
                    attrs[i].setFlag(Attribute.getConstraintType("AUTO_INCREMENT"));
                }
            }

            hmAttr.put(attrs[i].getName(), attrs[i]);

        }

        // if indexed
        if (statement.getIndexes().size() > 0) {
            for (Index constraint : statement.getIndexes()) {
                byte type = Attribute.getConstraintType(constraint.getType());
                for (String attrName : constraint.getColumnsNames()) {
                    hmAttr.get(attrName).setFlag(type);
                }
            }
        }

        TableSchema tableSchema = new TableSchema(tbl_name);
        tableSchema.attributes = attrs;
        int pk_len = tableSchema.getPKlength();

        DBServer.tables.put(tbl_name, tableSchema);

        Serializer[] keySerializer = new Serializer[pk_len];
        Serializer[] valueSerializer = new Serializer[tableSchema.attributes.length - pk_len];

        int q = 0, w = 0;
        for (int i = 0; i < tableSchema.attributes.length; i++) {
            if (tableSchema.attributes[i].hasFlag(Attribute.F_PK))
                keySerializer[q++] = tableSchema.attributes[i].getSerializer();
            else
                valueSerializer[w++] = tableSchema.attributes[i].getSerializer();
        }


        ConcurrentNavigableMap<Object[], Object[]> table = DBServer.db.treeMapCreate(tbl_name)
                .keySerializer(new BTreeKeySerializer.ArrayKeySerializer(keySerializer))
                .valueSerializer(new Serializer<Object[]>() {
                    @Override
                    public void serialize(DataOutput dataOutput, Object[] objects) throws IOException {
                        for (int i = 0; i < objects.length; i++) {
                            valueSerializer[i].serialize(dataOutput, objects[i]);
                        }
                    }

                    @Override
                    public Object[] deserialize(DataInput dataInput, int q) throws IOException {
                        Object[] result = new Object[valueSerializer.length];
                        for (int i = 0; i < result.length; i++) {
                            result[i] = valueSerializer[i].deserialize(dataInput, q);
                        }

                        return result;
                    }
                })
                .counterEnable()
                .make();

        DBServer.db.commit();
    }

    /**
     * Open database
     *
     * @throws DBStatus
     */
    private void open() throws DBStatus {
        db = new File(dbpath);

        DBStatus stat;

        // if db file exists -- try to calculate number of pages
        if (db.exists()) {
            long len = db.length();
            if (len % Page.pageSize != 0) throw new DBStatus(DBStatus.DB_WRONG_FORMAT); // wrong db format
            else totalPages = (int) (len / Page.pageSize);

            stat = new DBStatus(DBStatus.DB_EXISTS);
        } else {
            stat = new DBStatus(DBStatus.DB_NOT_EXISTS);
        }

        // open db file
        try {
            file = new RandomAccessFile(dbpath, "rws");
        } catch (FileNotFoundException e) {
            throw new DBStatus(DBStatus.DB_ACCESS_ERROR); // can't get access
        }

        pageManager = new PageManager(file);
        cacheManager = new CacheManager(pageManager);
        recordManager = new RecordManager(pageManager);
        treeManager = new TreeManager(recordManager.pageManager, cacheManager);

        tables = new HashMap<>();

        throw stat;
    }

}
