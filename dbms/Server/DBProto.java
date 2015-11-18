package Server;

import SQL.SQLParser;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.insert.Insert;

import java.io.StringReader;
import java.util.Map;

public class DBProto {
    public String processInput(String theInput)  {
        if (theInput != null) {
            return SQLParser.processQuery(theInput);
        }
        else {
            return "SMDB v.0.1-pre-alpha Connection ready";
        }
    }
}
