package core.sys.descriptive;

import java.nio.ByteBuffer;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/22/2015
 */
public class Page implements Comparable<Integer> {
    public final static int pageSize = 8096;
    public final static int headerSize = 17; // 4*int + byte
    public final static int dataSize = pageSize - headerSize;

    /**
     * HEADER
     **/
    int number;
    /**
     * 0 - page with tuples
     * 1 - page with b+tree internal nodes
     * 2 - page with b+tree leaf nodes
     */
    byte type;

    int numberOfRecords;

    // linked pointers to previous and next page
    int previous = 0;
    int next = 0;

    ByteBuffer data = ByteBuffer.allocate(dataSize);

    /**
     * END OF HEADER
     **/

    public Page(int n) {
        number = n;
        type = 0;
        numberOfRecords = 0;
    }

    public static Page deserialize(ByteBuffer buf) {
        Page p = new Page(0);
        p.number = buf.getInt();
        p.type = buf.get();
        p.numberOfRecords = buf.getInt();
        p.previous = buf.getInt();
        p.next = buf.getInt();
        p.data = buf;
        p.data.flip(); // change mode from read to write
        return p;
    }

    public ByteBuffer serialize(){
        ByteBuffer buf = ByteBuffer.allocate(pageSize);
        buf.clear();
        buf.putInt(number);
        buf.put(type);
        buf.putInt(numberOfRecords);
        buf.putInt(previous);
        buf.putInt(next);
        buf.put(data);
        buf.flip();
        return buf;

    }

    @Override
    public int compareTo(Integer o) {
        return Integer.compareUnsigned(number, o);
    }

    public Integer getNumber() {
        return number;
    }
}
