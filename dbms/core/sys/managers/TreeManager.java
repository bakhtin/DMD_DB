package core.sys.managers;

import core.sys.descriptive.*;
import core.sys.exceptions.RecordStatus;
import core.sys.exceptions.SQLError;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

        final Page[] p = {pageManager.getFreePage()};
        Iterator<Map.Entry<Integer, Row>> it = table.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<Integer, Row> entry = it.next();
            Row row = entry.getValue();
            Integer rowid = null;
            Object pk = row.getPk();

            if (row.getPkLength() == 1 && pk instanceof Integer) rowid = (Integer) row.getPk();
            else rowid = entry.getKey();

            // if free space on the page lesser than min_free_space, then allocate new page
            if (p[0].free() < min_free_space) {
                pages.add(p[0]);
                p[0] = pageManager.getFreePage();
            }

            if (p[0].free() < 0)
                throw new Exception("Negative free space on the page");

            List<Record> records = RecordManager.make(row, rowid, p[0].free());
            for (Record record : records) {
                if (p[0].canInsert(record)) {
                    p[0].addRecord(record);
                } else {
                    pages.add(p[0]);
                    p[0] = pageManager.getFreePage();
                }
            }
        }

        // add last (probably not full) page
        if (!it.hasNext())
            pages.add(p[0]);

        return pages;
    }

    void createIndex(List<Pointer> data) {

    }
}
