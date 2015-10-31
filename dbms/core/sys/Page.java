package core.sys;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/22/2015
 */
public class Page implements Comparable<Integer> {
    public final static int pageSize = 1024;
    public final static int headerSize = 9;
    public final static int dataSize = pageSize - headerSize - 4;

    int number;
    byte type;
    int numberOfRecords;

    byte[] data;

    int overflow;

    public Page(int n) {
        number = n;
        type = 0;
        numberOfRecords = 0;
        data = new byte[dataSize];
        overflow = 0;
    }

    public ByteBuffer serialize() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(pageSize);
        buf.putInt(number);
        buf.put(type);
        buf.putInt(numberOfRecords);
        buf.put(data);
        buf.putInt(overflow);
        return buf;

    }

    public static Page deserialize(ByteBuffer buf) {
        Page p = new Page(0);
        p.number = buf.getInt();
        p.type = buf.get();
        p.numberOfRecords = buf.getInt();
        p.data = new byte[dataSize];
        buf.get(p.data);
        p.overflow = buf.get();
        return p;
    }

    @Override
    public int compareTo(Integer o) {
        return Integer.compareUnsigned(number, o);
    }

    public Integer getNumber() {
        return number;
    }
}
