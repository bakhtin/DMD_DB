package core.sys.managers;

import core.sys.descriptive.Page;
import core.sys.descriptive.Record;
import core.sys.descriptive.Row;
import core.sys.descriptive.TableSchema;
import core.sys.exceptions.DBStatus;
import core.sys.exceptions.SQLError;
import core.sys.util.Misc;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/27/2015
 */
public class RecordManager {
    private static final byte min_free_space = 25; // bytes

    PageManager pageManager;

    // map<Table_name, TableSchema>
    Map<String, TableSchema> tables = new TreeMap<>();

    RecordManager(PageManager pm) throws DBStatus {
        this.pageManager = pm;
    }

    private static Record make(Row row, int rowid) throws SQLError, Exception {
        Record record = new Record(Record.T_TUPLE, rowid);
        record.setPayload(row.serialize().array());
        return record;
    }

    /**
     * Encapsulate Row into Record. If record size > 'size' (argument), then split record into more records
     *
     * @param row   - Row to insert
     * @param rowid - int rowid
     * @param size  - available free space in the Page
     * @return - LinkedList of records as result
     * @throws SQLError
     * @throws Exception
     */
    public static List<Record> make(Row row, int rowid, int size) throws SQLError, Exception {
        byte[] payload = row.serialize().array();

        List<Record> records = new LinkedList<>();
        if (payload.length <= size) {
            records.add(make(row, rowid));
            return records;
        } else {
            int begin = 0;

            final int maxDataOnPage = Page.dataSize - Record.getOverflowHeaderSize();

            do {
                Record ov_rec = new Record(Record.T_OVERFLOW_TUPLE, rowid);
                int end = size > maxDataOnPage ? maxDataOnPage : size;
                ov_rec.setPayload(Misc.subbyte(payload, begin, end));
                records.add(ov_rec);
                size -= end;
            } while (size > 0);

        }

        return records;
    }


}
