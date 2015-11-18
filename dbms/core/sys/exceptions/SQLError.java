package core.sys.exceptions;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/4/2015
 */
public class SQLError extends Throwable {
    public SQLError(String n) {
        super(n);
    }
}
