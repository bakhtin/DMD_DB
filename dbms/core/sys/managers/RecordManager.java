package core.sys.managers;

import core.sys.descriptive.Page;
import core.sys.descriptive.Record;
import core.sys.descriptive.Row;
import core.sys.descriptive.Table;
import core.sys.exceptions.DBStatus;
import core.sys.exceptions.SQLError;

import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/27/2015
 */
public class RecordManager {
    CacheManager pageCache;
    PageManager pageManager;

    RecordManager(RandomAccessFile file) throws DBStatus {
        this.pageManager = new PageManager(file);
        this.pageCache = new CacheManager(pageManager);
    }

    public static Record makeRecord(Table t, int rowid) throws SQLError {
        Record r = new Record(Record.T_TABLE, rowid);
        r.setPayload(t.serialize().array());
        return r;
    }

    public static Record makeRecord(Row t, int rowid) throws SQLError, Exception {
        Record r = new Record(Record.T_TUPLE, rowid);
        r.setPayload(t.serialize().array());
        return r;
    }

    public List<Page> insertRecord(Record one) {
        List<Page> result = new LinkedList<>();
        return result;
    }

    public List<Page> insertRecords(List<Record> many) {
        List<Page> result = new LinkedList<>();
        return result;
    }

}
