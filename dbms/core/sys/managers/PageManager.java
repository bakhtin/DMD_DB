package core.sys.managers;

import core.sys.descriptive.Page;
import core.sys.exceptions.DBStatus;
import core.sys.exceptions.SQLError;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/22/2015
 */
class PageManager {
    private RandomAccessFile file;

    public PageManager(RandomAccessFile f) throws DBStatus {
        this.file = f;
    }

    public Page readPage(int n) throws IOException, SQLError {
        if (n > DBManager.totalPages)
            throw new IOException("WRITE PAGE ERROR: page number " + n + " > total: " + DBManager.totalPages);

        byte[] page = new byte[Page.pageSize];

        file.seek((long) n * Page.pageSize);
        file.read(page);

        return Page.deserialize(ByteBuffer.wrap(page));
    }

    public void writePage(Page p) throws IOException {
        if (p.getNumber() > DBManager.totalPages)
            throw new IOException("WRITE PAGE ERROR: page number " + p.getNumber() + " > total: " + DBManager.totalPages);

        file.seek((long) p.getNumber() * Page.pageSize);
        file.write(p.serialize().array());
    }

    public Page allocatePage() throws IOException {
        Page p = new Page(DBManager.totalPages++);
        writePage(p);
        return p;
    }
}