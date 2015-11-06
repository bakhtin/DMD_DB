package core.sys.operators;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/6/2015
 */
public interface ResultSet<Row> {
    void open();

    boolean next();

    void close();

    Row getRow(int slot);

}
