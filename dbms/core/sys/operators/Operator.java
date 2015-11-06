package core.sys.operators;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/6/2015
 */
public interface Operator<Chunk> {
    void open();    // initializes the operator

    Chunk next();   // returns the next chunk of data

    void close();   // performs cleanup work (if necessary)
}
