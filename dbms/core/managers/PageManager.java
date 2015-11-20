package core.managers;

import core.datastructures.bptree2.Node;
import core.descriptive.Page;
import core.exceptions.DBStatus;
import core.exceptions.SQLError;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/22/2015
 */
public class PageManager {
    private RandomAccessFile file;
    private Queue<Integer> freelist = new LinkedList<>();

    public PageManager(RandomAccessFile f) throws DBStatus {
        this.file = f;
    }

    public Page readPage(int n) throws IOException, SQLError {
        if (n > DBManager.totalPages)
            throw new IOException("READ PAGE ERROR: page number " + n + " > total: " + DBManager.totalPages);

        byte[] page = new byte[Page.pageSize];

        file.seek((long) n * Page.pageSize);
        file.read(page);

        // page[4] is page type
        if (page[4] == Page.T_INODE || page[4] == Page.T_LNODE)
            return Node.deserialize(ByteBuffer.wrap(page));
        else
            return Page.deserialize(ByteBuffer.wrap(page));
    }

    public void writePage(Page p) throws Exception {
        if (p.getNumber() > DBManager.totalPages)
            throw new IOException("WRITE PAGE ERROR: page number " + p.getNumber() + " > total: " + DBManager.totalPages);

        file.seek((long) p.getNumber() * Page.pageSize);
        file.write(p.serialize().array());
    }

    private Page allocatePage() throws Exception {
        Page p = new Page(DBManager.totalPages++);
        writePage(p);
        return p;
    }

    public Page getFreePage() throws Exception, SQLError {
        if (freelist.isEmpty())
            return allocatePage();
        else
            return readPage(freelist.poll());
    }

    public void addFreePage(Page p) {
        this.freelist.add(p.getNumber());
    }

    public void addFreePage(Integer p) {
        this.freelist.add(p);
    }
}