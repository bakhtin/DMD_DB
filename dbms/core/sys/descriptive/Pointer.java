package core.sys.descriptive;

import java.nio.ByteBuffer;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/4/2015
 */
public class Pointer {
    public final int page;
    public final int rowid;

    public Pointer(int pn, int rowid) {
        this.page = pn;
        this.rowid = rowid;
    }

    public ByteBuffer serialize() {
        return null;
    }
}
