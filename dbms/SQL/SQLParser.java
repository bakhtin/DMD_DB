package SQL;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
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

                ItemsList hui = statement.getItemsList();
                List<ExpressionList> hui2 = ((MultiExpressionList) hui).getExprList();

                // for each expression
                for (ExpressionList row : hui2) {
                    List<Expression> expr = row.getExpressions();
                    Object[] line = new Object[expr.size()];
                    // for each attribute
                    int i = 0;
                    for (Object attr : expr) {
                        // if attr instanceof StringValue then line[i++] = (String) attr;
                    }
                }
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
