package core.sys.operators;

import core.sys.descriptive.Row;

import java.util.function.Predicate;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/6/2015
 */
public class Selection implements Operator<Row> {
    private Operator<Row> input;
    private Predicate<Row> sel;

    public Selection(Operator<Row> input, Predicate<Row> sel) {
        this.input = input;
        this.sel = sel;
    }

    @Override
    public void open() {
        input.open();
    }

    @Override
    public Row next() {
        for (Row tmp = input.next(); tmp != null; tmp = input.next()) {
            if (sel.test(tmp)) return tmp;
        }
        return null;
    }

    @Override
    public void close() {
        input.close();
    }
}
