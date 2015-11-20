package core.managers;

import core.descriptive.Page;
import core.descriptive.TableSchema;
import core.exceptions.DBStatus;
import core.exceptions.RecordStatus;
import core.exceptions.SQLError;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.HashMap;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/4/2015
 */
public class DBManager {
    public static HashMap<String, TableSchema> tables; // String key: table_name; TableSchema value: table_description
    public static PageManager pageManager;
    public static CacheManager cacheManager;
    static int totalPages = 0;
    static RecordManager recordManager;
    static TreeManager treeManager;
    String dbpath;
    RandomAccessFile file;
    File db;

    public DBManager(String path) {
        this.dbpath = path;

        try {
            open();
        } catch (DBStatus dbStatus) {
            switch (dbStatus.getStatus()) {
                /** OK **/
                case DBStatus.DB_EXISTS:
                    System.out.println("DB File exists. Initializing...");

                    break;

                /** OK **/
                case DBStatus.DB_NOT_EXISTS:
                    System.out.println("DB File does not exist. Creating new DB file...");

                    break;

                /** ERROR **/
                case DBStatus.DB_ACCESS_ERROR:
                    System.err.println("Can't get access to file");
                    System.exit(DBStatus.DB_ACCESS_ERROR);
                    break;

                /** ERROR **/
                case DBStatus.DB_WRONG_FORMAT:
                    System.err.println("DB File has wrong format (Extra bytes in the file)");
                    System.exit(DBStatus.DB_WRONG_FORMAT);
                    break;
            }
        }
    }

    /**
     * Open database
     *
     * @throws DBStatus
     */
    private void open() throws DBStatus {
        db = new File(dbpath);

        DBStatus stat;

        // if db file exists -- try to calculate number of pages
        if (db.exists()) {
            long len = db.length();
            if (len % Page.pageSize != 0) throw new DBStatus(DBStatus.DB_WRONG_FORMAT); // wrong db format
            else totalPages = (int) (len / Page.pageSize);

            stat = new DBStatus(DBStatus.DB_EXISTS);
        } else {
            stat = new DBStatus(DBStatus.DB_NOT_EXISTS);
        }

        // open db file
        try {
            file = new RandomAccessFile(dbpath, "rws");
        } catch (FileNotFoundException e) {
            throw new DBStatus(DBStatus.DB_ACCESS_ERROR); // can't get access
        }

        pageManager = new PageManager(file);
        cacheManager = new CacheManager(pageManager);
        recordManager = new RecordManager(pageManager);
        treeManager = new TreeManager(recordManager.pageManager, cacheManager);

        throw stat;
    }

    private void createDB() throws Exception, SQLError, RecordStatus {
        // very first (main) page
        recordManager.pageManager.getFreePage();
    }

}
