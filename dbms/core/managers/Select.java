package core.managers;

import core.descriptive.Row;
import core.descriptive.TableSchema;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import org.mapdb.DBMaker;

import java.util.LinkedList;
import java.util.NavigableSet;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/22/2015
 */
public class Select implements SelectVisitor {
    TableSchema tSchema;
    NavigableSet<Row> relation = DBMaker.tempTreeSet();

    @Override
    public void visit(PlainSelect plainSelect) {
        FIVisitor fiv = new FIVisitor();

        plainSelect.getFromItem().accept(fiv);

        int offset = 0;
        int limit = 0;
        if (plainSelect.getLimit() != null) {
            offset = (int) plainSelect.getLimit().getOffset();
            limit = (int) plainSelect.getLimit().getRowCount();
        }

        if (fiv.tableNames.size() > 1) {
        } // cartesian product
        else {

        }


        //tSchema = DBServer.tables.get(from.toString());
        //ConcurrentNavigableMap <Object[], Object[]> map = DBServer.db.treeMap(from.toString());

    }

    @Override
    public void visit(SetOperationList setOperationList) {

    }

    @Override
    public void visit(WithItem withItem) {

    }

    class FIVisitor implements FromItemVisitor {

        public LinkedList<String> tableNames = new LinkedList<>();

        @Override
        public void visit(Table table) {
            this.tableNames.add(table.getName());
        }

        @Override
        public void visit(SubSelect subSelect) {

        }

        @Override
        public void visit(SubJoin subJoin) {

        }

        @Override
        public void visit(LateralSubSelect lateralSubSelect) {

        }

        @Override
        public void visit(ValuesList valuesList) {

        }
    }
}
