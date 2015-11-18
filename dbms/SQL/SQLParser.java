package SQL;

import core.sys.descriptive.Row;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;

import java.io.StringReader;


public class SQLParser {
    public static String processQuery(String query) {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        try {
            //String statement = "INSERT INTO mytable VALUES (1, 'sadfsd', 234)";
            Statements statements = CCJSqlParserUtil.parseStatements(query);
            if (statements.getStatements().get(0) instanceof Insert) {
                Insert statement = (Insert) statements.getStatements().get(0);
                //Row row = new Row(statement.getItemsList(), );
                //statement.
            }
            Insert insert = (Insert) parserManager.parse(new StringReader(query));
            return insert.getTable().toString();
        }
        catch (JSQLParserException e) {
            e.printStackTrace();
            return e.toString();
        }
    }
}
