package core.sys.managers;

import core.sys.descriptive.Page;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/22/2015
 */
public class PageManager {
    private String path;
    private RandomAccessFile file;

    private int totalPages = 0;


    public PageManager(String path) {
        this.path = path;
        try {
            file = new RandomAccessFile(path, "rw");

            totalPages = (int) (file.length() / Page.pageSize);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public Page readPage(int n) throws IOException {
        if (n > totalPages) throw new IOException("WRITE PAGE ERROR: page number " + n + " > total: " + totalPages);

        byte[] page = new byte[Page.pageSize];

        file.seek((long) n * Page.pageSize);
        file.read(page);

        return Page.deserialize(ByteBuffer.wrap(page));
    }

    public void writePage(Page p) throws IOException {
        if (p.getNumber() > totalPages)
            throw new IOException("WRITE PAGE ERROR: page number " + p.getNumber() + " > total: " + totalPages);

        file.seek((long) p.getNumber() * Page.pageSize);
        file.write(p.serialize().array());
    }

    public Page allocatePage() throws IOException {
        Page p = new Page(totalPages++);
        writePage(p);
        return p;
    }

}