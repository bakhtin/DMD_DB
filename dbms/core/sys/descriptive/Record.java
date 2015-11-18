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
    public static final byte T_OVERFLOW_TUPLE = 2;
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
     * <p>
     * THEY APPEARS ONLY WHEN PAGE TYPE = overflow tuple
     */
    int backward_overflow = 0;
    int forward_overfow = 0;

    /**
     * Real size of the record
     */
    int record_length;
    byte[] payload;

    Record() {
    }

    /**
     * BODY
     **/
    public Record(byte type, int rowid) throws SQLError {
        this.setType(type);
        this.setRowid(rowid);
    }

    public static byte getHeaderSize() {
        return (byte) 9;
    }

    public static byte getOverflowHeaderSize() {
        return (byte) (9 + 8);
    }

    public static Record deserialize(ByteBuffer buf) throws SQLError {
        Record r = new Record();
        r.type = buf.get();
        r.rowid = buf.getInt();

        if (r.type == T_OVERFLOW_TUPLE) {
            r.backward_overflow = buf.getInt();
            r.forward_overfow = buf.getInt();
        }

        r.record_length = buf.getInt();
        r.payload = new byte[r.record_length];
        buf.get(r.payload);

        return r;
    }

    public ByteBuffer serialize() {
        int size = this.size();
        ByteBuffer buf = ByteBuffer.allocate(size);
        // put header
        buf.put(type);
        buf.putInt(rowid);

        if (type == T_OVERFLOW_TUPLE) {
            buf.putInt(backward_overflow);
            buf.putInt(forward_overfow);
        }

        // put body
        buf.putInt(record_length);
        buf.put(payload);

        buf.flip();

        return buf;
    }

    public int getRowid() {
        return rowid;
    }

    public void setRowid(int rowid) throws SQLError {
        if (rowid > 0) this.rowid = rowid;
        else throw new SQLError("Wrong record rowid");
    }

    public int size() {
        return (type == T_OVERFLOW_TUPLE ? getOverflowHeaderSize() : getHeaderSize()) + payload.length;
    }

    @Override
    public int compareTo(Integer o) {
        return Integer.compareUnsigned(record_length, o);
    }

    public void setType(byte type) throws SQLError {
        if (type >= 0 && type <= 3) this.type = type;
        else throw new SQLError("Wrong record type");
    }

    public void setBackOverflow(int bo) throws SQLError {
        if (bo > 0) this.backward_overflow = bo;
        else throw new SQLError("Wrong record backward overflow");
    }

    public void setForwOverflow(int fo) throws SQLError {
        if (fo > 0) this.forward_overfow = fo;
        else throw new SQLError("Wrong record forward overflow");
    }

    public void setPayload(byte[] pl) throws SQLError {
        if (pl.length > 0) {
            this.payload = pl;
            this.record_length = pl.length;
        } else {
            throw new SQLError("Wrong record payload");
        }
    }
}
