package core.managers;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.*;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.util.HashMap;
import java.util.List;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/22/2015
 */
public class ParseSelectVisitor implements SelectVisitor {
    @Override
    public void visit(PlainSelect plainSelect) {
        List<Join> tables = plainSelect.getJoins();
        List<SelectItem> selectItems = plainSelect.getSelectItems();
        Expression where = plainSelect.getWhere();
        FromItem from = plainSelect.getFromItem();

        HashMap<String, String> tableAlias = getTableAlias(plainSelect);

        DB db = DBMaker.memoryDB().make();

    }


    private HashMap<String, String> getTableAlias(PlainSelect plainSelect) {
        HashMap<String, String> tableAlias = new HashMap<>();
        for (Join j : plainSelect.getJoins()) {
            String s[] = j.toString().split(" ");
            tableAlias.put(j.getRightItem().getAlias().getName(), s[0]);
        }

        FromItem it = plainSelect.getFromItem();
        String s[] = it.toString().split(" ");
        tableAlias.put(it.getAlias().getName(), s[0]);
        return tableAlias;
    }

    @Override
    public void visit(SetOperationList setOperationList) {

    }

    @Override
    public void visit(WithItem withItem) {

    }
}
