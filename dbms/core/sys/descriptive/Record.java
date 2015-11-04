package core.sys.descriptive;

import java.nio.ByteBuffer;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/27/2015
 */
public class Record implements Comparable<Integer> {
    /** HEADER **/
    /**
     * 1 - tuple
     * 2 - internal b+tree node
     * 3 - leaf b+tree node
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
     */
    int backward_overflow = 0;
    int forward_overfow = 0;

    /** BODY **/
    /**
     * Real length of the record
     */
    int record_length;
    byte[] payload;


    public static Record deserialize(ByteBuffer buf) {
        Record r = new Record();
        r.type = buf.get();
        r.rowid = buf.getInt();
        r.backward_overflow = buf.getInt();
        r.forward_overfow = buf.getInt();
        r.record_length = buf.getInt();
        r.payload = new byte[buf.capacity() - buf.position()];
        buf.get(r.payload);

        return r;
    }

    public ByteBuffer serialize() {
        int size = 17 + payload.length; // 1 + 4 + 4 + 4 + 4 + payload
        ByteBuffer buf = ByteBuffer.allocate(size);
        // put header
        buf.put(type);
        buf.putInt(rowid);
        buf.putInt(backward_overflow);
        buf.putInt(forward_overfow);

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
}
