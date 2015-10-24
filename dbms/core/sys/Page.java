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

    int number;
    byte type;
    int numberOfRecords;

    byte[] data;

    int[] offsets;
    int overflow;

    Page(int n) {
        number = n;
        type = 0;
        numberOfRecords = 0;
        data = new byte[pageSize - 4 - headerSize];
        offsets = new int[0];
        overflow = 0;
    }

    public ByteBuffer serialize() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(pageSize);
        buf.putInt(number);
        buf.put(type);
        buf.putInt(numberOfRecords);
        buf.put(data);
        if (offsets.length != numberOfRecords) throw new IOException("offsets.length!=numberOfRecords");
        for (int i = 0; i < offsets.length; i++) {
            buf.putInt(offsets[i]);
        }
        buf.putInt(overflow);

        return buf;

    }

    public static Page deserialize(ByteBuffer buf) {
        Page p = new Page(0);
        p.number = buf.getInt();
        p.type = buf.get();
        p.numberOfRecords = buf.getInt();

        int offsetFromEnd = p.numberOfRecords * 4 + 4;
        p.data = new byte[pageSize - buf.position() - offsetFromEnd];
        buf.get(p.data);
        p.offsets = new int[p.numberOfRecords];
        for (int i = 0; i < p.numberOfRecords; i++) {
            p.offsets[i] = buf.getInt();
        }
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
