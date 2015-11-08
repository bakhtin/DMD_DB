package core.sys.descriptive;

import core.sys.exceptions.RecordStatus;
import core.sys.exceptions.SQLError;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/22/2015
 */
public class Page implements Comparable<Integer> {
    public final static int pageSize = 8096;
    public final static int headerSize = 17; // 4*int + byte
    public final static int dataSize = pageSize - headerSize;

    public static final byte T_FREE = 0;
    public static final byte T_TUPLE = 1;
    public static final byte T_INODE = 2;
    public static final byte T_LNODE = 3;
    public Map<Integer, Record> records = new HashMap<>(500);
    /**
     * HEADER
     **/
    int number;
    /**
     * 1 - page with tuples
     * 2 - page with b+tree internal nodes
     * 3 - page with b+tree leaf nodes
     */
    byte type;
    // linked pointers to previous and next page
    int previous = 0;
    int next = 0;
    /**
     * END OF HEADER
     **/

    int recordsSize = 0; // not written in the file

    Page() {
    }

    public Page(int n) {
        number = n;
        type = 0;
    }

    public static Page deserialize(ByteBuffer buf) throws SQLError {
        Page p = new Page();
        p.number = buf.getInt();
        p.type = buf.get();
        byte numberOfRecords = buf.get();
        p.previous = buf.getInt();
        p.next = buf.getInt();

        for (int i = 0; i < numberOfRecords; i++) {
            Record r = Record.deserialize(buf);
            p.records.put(r.rowid, r);
        }
        return p;
    }

    public short free() {
        return (short) (dataSize - recordsSize);
    }

    public short recordsSize() {
        return (short) recordsSize;
    }

    public boolean canInsert(Record record) {
        return (record.record_length + recordsSize <= dataSize);
    }

    public int addRecord(Record record) throws RecordStatus {
        if (canInsert(record)) {
            records.put(record.rowid, record);
            recordsSize += record.size();
            return recordsSize;
        } else
            throw new RecordStatus("Not enough space to add this record");
    }

    public int removeRecord(int rowid) throws RecordStatus {
        Record record = records.get(rowid);
        if (record != null) {
            recordsSize -= record.size();
            return record.size();
        } else {
            throw new RecordStatus("Record " + rowid + " not found in page ");
        }
    }

    public int updateRecord(int rowid, Record record) throws RecordStatus {
        if (records.containsKey(rowid)) {
            Record old = records.get(rowid);
            recordsSize -= old.size();
            records.put(rowid, record);
            recordsSize += record.size();

            return recordsSize;
        } else {
            throw new RecordStatus("Record " + rowid + " not found in page ");
        }
    }

    public ByteBuffer serialize() {
        ByteBuffer buf = ByteBuffer.allocate(pageSize);
        buf.clear();
        buf.putInt(number);
        buf.put(type);
        buf.put((byte) records.size()); // number of records
        buf.putInt(previous);
        buf.putInt(next);

        records.forEach((k, v) -> {
            ByteBuffer payload = v.serialize();
            if (buf.position() + payload.capacity() <= buf.capacity())
                buf.put(payload);
            else
                throw new IllegalStateException("You are trying to write more data than page can hold!");
        });

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
