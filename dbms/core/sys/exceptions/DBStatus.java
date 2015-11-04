package core.sys.exceptions;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/4/2015
 */
public class DBStatus extends Throwable {
    public static final byte DB_EXISTS = 1;
    public static final byte DB_NOT_EXISTS = 2;
    public static final byte DB_ACCESS_ERROR = 3;
    public static final byte DB_WRONG_FORMAT = 4;


    /**
     * 0 - default status. Means nothing.
     */
    private int status = 0;

    public DBStatus(int status) {
        if (status == DB_EXISTS ||
                status == DB_NOT_EXISTS ||
                status == DB_ACCESS_ERROR ||
                status == DB_WRONG_FORMAT)
            this.status = status;
        else throw new IllegalStateException("wrong status");
    }

    public int getStatus() {
        return status;
    }
}
