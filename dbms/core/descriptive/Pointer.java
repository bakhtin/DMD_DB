package core.descriptive;

import java.nio.ByteBuffer;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/4/2015
 */
public class Pointer implements Comparable<Pointer> {
    public int page;
    public int rowid;

    public Pointer(int pn, int rowid) {
        this.page = pn;
        this.rowid = rowid;
    }

    Pointer() {
    }

    public static Pointer deserialize(ByteBuffer b) {
        Pointer p = new Pointer(0, 0);
        p.page = b.getInt();
        p.rowid = b.getInt();

        return p;
    }

    public ByteBuffer serialize() {
        ByteBuffer b = ByteBuffer.allocate(8);
        b.putInt(page);
        b.putInt(rowid);

        return b;
    }

    @Override
    public int compareTo(Pointer o) {
        return ((Comparable) rowid).compareTo(o.rowid);
    }
}
