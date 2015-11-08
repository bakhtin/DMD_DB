package core.sys.managers;

import core.sys.descriptive.Page;
import core.sys.descriptive.Record;
import core.sys.descriptive.Relation;
import core.sys.exceptions.RecordStatus;
import core.sys.exceptions.SQLError;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/8/2015
 */
public class TreeManager {
    public static final byte min_free_space = Record.getOverflowHeaderSize(); // bytes

    PageManager pageManager;

    TreeManager(PageManager p) {
        this.pageManager = p;
    }

    public List<Page> bulkInsert(Relation table) throws Exception, RecordStatus, SQLError {
        List<Page> pages = new LinkedList<>();
        for (int i = 0; i < table.size(); i++) {
            final Page[] p = {pageManager.allocatePage()};
            table.forEach((rowid, row) -> {
                try {
                    // if free space on the page lesser than min_free_space, then allocate new page
                    if (p[0].free() < min_free_space) {
                        pages.add(p[0]);
                        p[0] = pageManager.allocatePage();
                    }
                    List<Record> records = RecordManager.make(row, rowid, p[0].free());
                    for (Record record : records) {
                        if (p[0].canInsert(record)) {
                            p[0].addRecord(record);
                        } else {
                            pages.add(p[0]);
                            p[0] = pageManager.allocatePage();
                        }
                    }
                } catch (RecordStatus recordStatus) {
                    // not enough space to add record
                    recordStatus.printStackTrace();
                } catch (SQLError sqlError) {
                    sqlError.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace(); // general exception
                }
            });
            //pages.add(p[0]);
        }

        return pages;
    }
}
