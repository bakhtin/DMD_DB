package core.sys.managers;

import core.sys.descriptive.TableSchema;
import core.sys.exceptions.DBStatus;
import core.sys.exceptions.SQLError;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/27/2015
 */
public class RecordManager {
    private static final byte min_free_space = 25; // bytes

    CacheManager pageCache;
    PageManager pageManager;

    // map<Table_name, TableSchema>
    Map<String, TableSchema> tables = new TreeMap<>();

    RecordManager(RandomAccessFile file) throws DBStatus {
        this.pageManager = new PageManager(file);
        this.pageCache = new CacheManager(pageManager);
    }

    void initialize() throws IOException, SQLError {

    }


}
