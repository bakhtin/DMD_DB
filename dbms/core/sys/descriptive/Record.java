package core.sys.descriptive;

import core.sys.exceptions.SQLError;

import java.nio.ByteBuffer;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/27/2015
 */
public class Record implements Comparable<Integer> {
    public static final byte T_TABLE = 0;
    public static final byte T_TUPLE = 1;
    public static final byte T_INODE = 2;
    public static final byte T_LNODE = 3;
    public static final byte T_OTUPLE = 4;

    /** HEADER **/
    /**
     * 0 - table
     * 1 - tuple
     * 2 - internal b+tree node
     * 3 - leaf b+tree node
     * 4 - overflow tuple
     */
    byte type;

    /**
     * Unique record id
     */
    int rowid;

    /**
     * Suppose we are trying to add a record with size = 100 to page with free space 50.
     * Page1: first 50 bytes: forward overflow: pointer to page 2
     * Page2: second 50 bytes: backward overflow: pointer to page 1
     * 0 means there is no overflow
     *
     * THEY APPEARS ONLY WHEN PAGE TYPE = overflow tuple
     */
    int backward_overflow = 0;
    int forward_overfow = 0;

    /** BODY **/
    /**
     * Real length of the record
     */
    int record_length;
    byte[] payload;

    Record() {
    }

    public Record(byte type, int rowid) throws SQLError {
        this.setType(type);
        this.setRowid(rowid);
    }

    public static Record deserialize(ByteBuffer buf) throws SQLError {
        Record r = new Record();
        r.type = buf.get();
        r.rowid = buf.getInt();

        if (r.type == T_OTUPLE) {
            r.backward_overflow = buf.getInt();
            r.forward_overfow = buf.getInt();
        }

        r.record_length = buf.getInt();
        r.payload = new byte[r.record_length];
        buf.get(r.payload);

        return r;
    }

    public static boolean check(byte[] pl) {
        if (pl.length > 0) {
            return true;
        } else {
            return false;
        }
    }

    public int size() {
        return 9 + payload.length + (type == T_OTUPLE ? 8 : 0);
    }

    public ByteBuffer serialize() {
        int size = 17 + payload.length; // 1 + 4 + 4 + 4 + 4 + payload
        ByteBuffer buf = ByteBuffer.allocate(size);
        // put header
        buf.put(type);
        buf.putInt(rowid);

        if (type == T_OTUPLE) {
            buf.putInt(backward_overflow);
            buf.putInt(forward_overfow);
        }

        // put body
        buf.putInt(record_length);
        buf.put(payload);

        buf.flip();

        return buf;
    }

    @Override
    public int compareTo(Integer o) {
        return Integer.compareUnsigned(record_length, o);
    }

    public void setType(byte type) throws SQLError {
        if (type >= 0 && type <= 3) this.type = type;
        else throw new SQLError("Wrong record type");
    }

    public void setRowid(int rowid) throws SQLError {
        if (rowid > 0) this.rowid = rowid;
        else throw new SQLError("Wrong record rowid");
    }

    public void setBackward_overflow(int bo) throws SQLError {
        if (bo > 0) this.backward_overflow = bo;
        else throw new SQLError("Wrong record backward overflow");
    }

    public void setForward_overfow(int fo) throws SQLError {
        if (fo > 0) this.forward_overfow = fo;
        else throw new SQLError("Wrong record forward overflow");
    }

    public void setPayload(byte[] pl) throws SQLError {
        if (check(pl)) {
            this.payload = pl;
            this.record_length = pl.length;
        } else {
            throw new SQLError("Wrong record payload");
        }
    }
}
