package core.sys.operators;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/6/2015
 */
public class Enumerate implements Operator<Integer> {
    private int current, from, to;

    public Enumerate(int from, int to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void open() {
        current = from;
    }

    @Override
    public Integer next() {
        if (current <= to) {
            int temp = current;
            current++;
            return temp;
        }
        return null;
    }

    @Override
    public void close() {
        // must be empty
    }
}
