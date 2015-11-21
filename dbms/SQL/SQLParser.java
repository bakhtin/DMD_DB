package SQL;

import core.descriptive.Attribute;
import core.descriptive.Row;
import core.descriptive.TableSchema;
import core.exceptions.SQLError;
import core.managers.DBManager;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.insert.Insert;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;


public class SQLParser {
    public static Object TypeCaster(Object obj) {
        if (obj instanceof LongValue) obj = (int) ((LongValue) obj).getValue();
        else if (obj instanceof StringValue) obj = ((StringValue) obj).getValue();
        return obj;
    }

    public static String normalizeString(String string) {
        return string.replace("`", "");
    }

    public static String processQuery(String query) throws SQLError {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        query = normalizeString(query);
        try {
            //String statement = "INSERT INTO mytable VALUES (1, 'sadfsd', 234)";
            Statements statements = CCJSqlParserUtil.parseStatements(query);
            if (statements.getStatements().get(0) instanceof Insert) {
                Insert statement = (Insert) statements.getStatements().get(0);
                List<Expression> expr;
                LinkedList<Row> rows = new LinkedList<>();
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
                        for (int i=0; i<attrsArray.length; i++) {
                            if (attrsArray[i].hasFlag(2) || attrsArray[i].hasFlag(16)) {
                                indexList.add(i);
                            }
                        }
                        int[] array = indexList.stream().mapToInt(i->i).toArray();
                        rows.add(new Row(line, array));
                    }
                }
                catch (ClassCastException e) {
                    // if INSERT with just one tuple
                    try {
                        expr = ((ExpressionList) statement.getItemsList()).getExpressions();
                        Object[] line = expr.toArray();
                        for (int i = 0; i < line.length; i++) {
                            line[i] = TypeCaster(line[i]);
                        }
                        TableSchema tSchema = DBManager.tables.get(statement.getTable());
                        Attribute[] attrsArray = tSchema.attributes;
                        LinkedList<Integer> indexList = new LinkedList<>();
                        for (int i=0; i<attrsArray.length; i++) {
                            // indexed fields if PK or UQ
                            if (attrsArray[i].hasFlag(2) || attrsArray[i].hasFlag(16)) {
                                indexList.add(i);
                            }
                        }
                        int[] array = indexList.stream().mapToInt(i->i).toArray();
                        rows.add(new Row(line, array));
                    }
                    catch (ClassCastException d) {
                        throw new SQLError("Insert cannot be empty");
                    }
                    /*
                    if row has the only PK
                     */
                }

                // to obtain descriptive TableSchema object you can write
                // DBManager.tables.get(String tbl_name);
            }
            else if (statements.getStatements().get(0) instanceof CreateTable) {
                CreateTable statement = (CreateTable) statements.getStatements().get(0);
                Attribute[] attrs = new Attribute[statement.getColumnDefinitions().size()];
                for (int i=0; i < statement.getColumnDefinitions().size(); i++) {
                    byte colDataType = (byte) Attribute.getDataType(statement.getColumnDefinitions().get(i).getColDataType().toString());
                    attrs[i] = new Attribute(statement.getColumnDefinitions().get(i).getColumnName(), colDataType);
                    int columnSpecStringsSize = statement.getColumnDefinitions().get(i).getColumnSpecStrings().size();
                    for (int j=0; j<columnSpecStringsSize; j++) {
                        if (columnSpecStringsSize > j+1) {
                            if (statement.getColumnDefinitions().get(i).getColumnSpecStrings().get(j).equals("NOT") &&
                                    statement.getColumnDefinitions().get(i).getColumnSpecStrings().get(j + 1).equals("NULL")) {
                                attrs[i].setFlag(Attribute.getConstraintType("NOT NULL"));
                            }
                        }
                        if (statement.getColumnDefinitions().get(i).getColumnSpecStrings().get(j).equals("AUTO_INCREMENT")) {
                            attrs[i].setFlag(Attribute.getConstraintType("AUTO_INCREMENT"));
                        }
                    }
                    if (statement.getIndexes().size() > 0) {
                        attrs[i].setFlag(Attribute.getConstraintType(statement.getIndexes().get(0).getType()));
                    }
                }
                TableSchema tableSchema = new TableSchema(statement.getTable().toString());
                tableSchema.attributes = attrs;

                //DBServer.SMDB
            }
            Insert insert = (Insert) parserManager.parse(new StringReader(query));
            return insert.getTable().toString();
        } catch (JSQLParserException e) {
            e.printStackTrace();
            return e.toString();
        }
    }
}
