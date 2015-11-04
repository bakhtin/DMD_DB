package core.sys.managers;

import core.sys.descriptive.Page;
import core.sys.exceptions.DBStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/4/2015
 */
public class DBManager {
    static int totalPages = 0;
    String dbpath;
    RandomAccessFile file;
    File db;
    CacheManager pageCache;
    PageManager pager;

    DBManager(String path) {
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
                    System.out.println("DB File not exists. Create new DB file...");
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
            file = new RandomAccessFile(dbpath, "rw");
        } catch (FileNotFoundException e) {
            throw new DBStatus(DBStatus.DB_ACCESS_ERROR); // can't get access
        }

        pager = new PageManager(file);
        pageCache = new CacheManager(pager);

        throw stat;
    }

}
