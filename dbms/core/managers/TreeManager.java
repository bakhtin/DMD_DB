package core.managers;

import core.descriptive.*;
import core.exceptions.RecordStatus;
import core.exceptions.SQLError;

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
    public static final int MAX_PAGES_IN_MEMORY = 5; // just a random small number greater than 3
    public static final byte min_free_space = Record.getOverflowHeaderSize(); // bytes

    PageManager pageManager;
    CacheManager cacheManager;
    private LinkedList<Page> pages = new LinkedList<>();

    TreeManager(PageManager p, CacheManager c) {
        this.pageManager = p;
        this.cacheManager = c;
    }

    public LinkedList<Pointer> bulkInsert(Relation table) throws Exception, RecordStatus, SQLError {
        LinkedList<Page> pages = new LinkedList<>();
        LinkedList<Pointer> pointers = new LinkedList<>();

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

                Page last = pages.getLast();
                if (last != null) {
                    last.setNext(p[0].getNumber());
                    p[0].setPrev(last.getNumber());
                }

                pages.add(p[0]);
                p[0] = pageManager.getFreePage();
            }

            List<Record> records = RecordManager.make(row, rowid, p[0].free());
            for (Record record : records) {
                if (p[0].canInsert(record)) {
                    p[0].addRecord(record);
                    pointers.add(new Pointer(p[0].getNumber(), record.getRowid()));
                } else {

                    Page last = pages.getLast();
                    if (last != null) {
                        last.setNext(p[0].getNumber());
                        p[0].setPrev(last.getNumber());
                    }

                    pages.add(p[0]);
                    p[0] = pageManager.getFreePage();
                }
            }

            // if in structure 'pages' more than MAX_PAGES_IN_MEMORY pages
            // then write it on the file
            if (pages.size() > MAX_PAGES_IN_MEMORY) {
                Page popped = pages.pollFirst();
                pageManager.writePage(popped);
            }
        }

        // add last (probably not full) page
        if (!it.hasNext()) {

            Page last = pages.getLast();
            if (last != null) {
                last.setNext(p[0].getNumber());
                p[0].setPrev(last.getNumber());
            }

            pages.add(p[0]);
        }


        return pointers;
    }

    /**
     * Used for bulk-loading
     *
     * @param row - Row
     */
    public void insertLast(int rowid, Row row, Page root) throws SQLError, Exception, RecordStatus {
        // if it is leaf
        if (root.getType() == Page.T_LNODE) {
            LeafNode leaf = (LeafNode) root;

            List<Record> records = RecordManager.make(row, rowid, leaf.free());
            for (Record record : records) {
                if (leaf.canInsert(record)) {
                    leaf.addRecord(record);
                } else {
                    if (leaf.getNext() != 0) {
                        insertLast(rowid, row, cacheManager.get(leaf.getNext()));
                    } else {
                        Page free = pageManager.getFreePage();

                        Page last = pages.getLast();
                        if (last != null) {
                            last.setNext(leaf.getNumber());
                            leaf.setPrev(last.getNumber());
                        }

                        pages.add(leaf);
                    }

                }
            }
        } else if (root.getType() == Page.T_INODE) {

        } else {
            throw new Exception("Bad page type");
        }

        // write remaining MAX_PAGES_IN_MEMORY pages
        while (!pages.isEmpty()) {
            Page popped = pages.pollFirst();
            pageManager.writePage(popped);
        }

    }

    /**
     * Used for usual insertions. We don't care about order of the rows during the calls.
     *
     * @param row - Row
     */
    public void insertRandom(Row row) {

    }
}

