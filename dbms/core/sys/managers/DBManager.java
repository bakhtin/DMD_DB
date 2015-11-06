package core.sys.managers;

import core.sys.descriptive.Attribute;
import core.sys.descriptive.Page;
import core.sys.descriptive.Table;
import core.sys.exceptions.DBStatus;
import core.sys.exceptions.SQLError;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.TreeMap;

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

    RecordManager recordManager;
    SQL cursor;

    Map<String, Table> tables = new TreeMap<>();

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
            file = new RandomAccessFile(dbpath, "rw");
        } catch (FileNotFoundException e) {
            throw new DBStatus(DBStatus.DB_ACCESS_ERROR); // can't get access
        }

        recordManager = new RecordManager(file);
        cursor = new SQL();

        throw stat;
    }

    private void createDb() throws IOException {
        Page main = recordManager.pageManager.allocatePage();
        Page freelist = recordManager.pageManager.allocatePage();
        Page freespace = recordManager.pageManager.allocatePage();
    }

    /**
     * @throws IOException
     */
    private void createMainPage() throws IOException {
        Page main = recordManager.pageManager.allocatePage();

    }

    /**
     * Table with:
     *
     * @throws IOException
     */
    private Page createFreelistPage() throws IOException, SQLError {
        Page freelist = recordManager.pageManager.allocatePage();
        Table t_freelist = new Table("freelist", 1);
        t_freelist.attributes[0].setName("number");
        t_freelist.attributes[0].setType(Attribute.T_INT);

        return freelist;
    }

    /**
     * All pages in this table are pages with tuples
     * Table with:
     * page_id INT
     * free SHORT
     *
     * @throws IOException
     */
    private Page createPageListPage() throws IOException, SQLError {
        Page freespace = recordManager.pageManager.allocatePage();
        Table t_freespace = new Table("freespace", 2);

        t_freespace.attributes[0].setName("page_id");
        t_freespace.attributes[0].setType(Attribute.T_TEXT);

        t_freespace.attributes[1].setName("free");
        t_freespace.attributes[1].setType(Attribute.T_SHORT);


        return freespace;
    }

}
