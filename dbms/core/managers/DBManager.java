package core.managers;

import Server.DBServer;
import core.descriptive.*;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
            Insert statement = (Insert) statements.getStatements().get(0);
            List<Expression> expr;
            LinkedList<Row> rows = new LinkedList<>();

            Relation relation = new Relation();
            int counter = 1;
            // if INSERT with more than 1 tuple
            try {
                List<ExpressionList> recordsList = ((MultiExpressionList) statement.getItemsList()).getExprList();
                for (ExpressionList row : recordsList) {
                    expr = row.getExpressions();
                    Object[] line = expr.toArray();
                    for (int i = 0; i < line.length; i++) {
                        line[i] = TypeCaster(line[i]);
                    }
                    TableSchema tSchema = DBManager.tables.get(statement.getTable());
                    Attribute[] attrsArray = tSchema.attributes;
                    LinkedList<Integer> indexList = new LinkedList<>();
                    for (int i = 0; i < attrsArray.length; i++) {
                        if (attrsArray[i].hasFlag(Attribute.F_PK)) {
                            indexList.add(i);
                        }
                    }

                    int[] indices = indexList.stream().mapToInt(i -> i).toArray();

                    Row _row = new Row(line, indices);
                    int rowid = 0;
                    // get rowid
                    if (_row.getPkLength() == 1 && _row.getPk() instanceof Integer) rowid = (Integer) _row.getPk();
                    else rowid = counter++;

                    relation.addRow(rowid, _row);

                }
            } catch (ClassCastException e) {
                // if INSERT with just one tuple
                try {
                    expr = ((ExpressionList) statement.getItemsList()).getExpressions();
                    Object[] line = expr.toArray();
                    for (int i = 0; i < line.length; i++) {
                        line[i] = TypeCaster(line[i]);
                    }

                    if (!DBManager.tables.containsKey(statement.getTable().toString()))
                        throw new SQLError("Table " + statement.getTable() + " is not present in the DB");

                    TableSchema tSchema = DBManager.tables.get(statement.getTable().toString());
                    Attribute[] attrsArray = tSchema.attributes;
                    LinkedList<Integer> indexList = new LinkedList<>();
                    for (int i = 0; i < attrsArray.length; i++) {
                        // indexed fields if PK or UQ
                        if (attrsArray[i].hasFlag(Attribute.F_PK)) {
                            indexList.add(i);
                        }
                    }

                    int[] indices = indexList.stream().mapToInt(i -> i).toArray();

                    Row _row = new Row(line, indices);
                    int rowid = 0;
                    // get rowid
                    if (_row.getPkLength() == 1 && _row.getPk() instanceof Integer) rowid = (Integer) _row.getPk();
                    else rowid = counter++;

                    relation.addRow(rowid, _row);

                } catch (ClassCastException d) {
                    throw new SQLError("Insert cannot be empty");
                }
                    /*
                    if row has the only PK
                     */
            }

            // INSERT them


            // to obtain descriptive TableSchema object you can write
            // DBManager.tables.get(String tbl_name);
        } else if (statements.getStatements().get(0) instanceof CreateTable) {
            CreateTable statement = (CreateTable) statements.getStatements().get(0);
            Attribute[] attrs = new Attribute[statement.getColumnDefinitions().size()];
            String tbl_name = statement.getTable().toString();
            HashMap<String, Attribute> hmAttr = new HashMap<>();


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
                    // TODO: add parsing of others FLAGS
                    if (type == Attribute.F_PK) {
                        for (String attrName : constraint.getColumnsNames()) {
                            hmAttr.get(attrName).setFlag(type);
                        }
                    }
                }
            }


            TableSchema tableSchema = new TableSchema(statement.getTable().toString());
            tableSchema.attributes = attrs;

            DBServer.tables.put(tbl_name, tableSchema);

            DBServer.tables = DBServer.db.

            return "";
        }
        Insert insert = (Insert) parserManager.parse(new StringReader(query));
        return insert.getTable().toString();
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
