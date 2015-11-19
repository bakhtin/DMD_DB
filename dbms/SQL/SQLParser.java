package SQL;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.insert.Insert;

import java.io.StringReader;
import java.util.List;


public class SQLParser {
    public static String processQuery(String query) {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        try {
            //String statement = "INSERT INTO mytable VALUES (1, 'sadfsd', 234)";
            Statements statements = CCJSqlParserUtil.parseStatements(query);
            if (statements.getStatements().get(0) instanceof Insert) {
                Insert statement = (Insert) statements.getStatements().get(0);

                List<ExpressionList> recordsList = ((MultiExpressionList) statement.getItemsList()).getExprList();

                // for each expression
                for (ExpressionList row : recordsList) {
                    List<Expression> expr = row.getExpressions();
                    Object[] line = expr.toArray();
                    for (int i = 0; i < line.length; i++) {
                        if (line[i] instanceof LongValue) line[i] = (int) ((LongValue) line[i]).getValue();
                        else if (line[i] instanceof StringValue) line[i] = ((StringValue) line[i]).getValue();
                    }
                }

                // to obtain descriptive TableSchema object you can write
                // DBManager.tables.get(String tbl_name);
                //Row row = new Row(statement.getItemsList(), );
                //statement.
            }
            Insert insert = (Insert) parserManager.parse(new StringReader(query));
            return insert.getTable().toString();
        } catch (JSQLParserException e) {
            e.printStackTrace();
            return e.toString();
        }
    }
}
