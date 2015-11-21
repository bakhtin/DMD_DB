package core.managers;

import core.descriptive.Page;
import core.descriptive.Record;
import core.descriptive.Row;
import core.descriptive.TableSchema;
import core.exceptions.DBStatus;
import core.exceptions.SQLError;
import core.util.Misc;

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

    public static Record make(TableSchema table) throws SQLError {
        Record record = new Record(Record.T_TABLE, DBManager.tables.size() + 1);
        record.setPayload(table.serialize().array());
        return record;
    }


}
