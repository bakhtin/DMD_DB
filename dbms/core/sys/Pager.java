package core.sys;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/22/2015
 */
public class Pager {
    private String path;
    private RandomAccessFile file;

    private int totalPages = 0;


    public Pager(String path) {
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

        Page p = Page.deserialize(ByteBuffer.wrap(page));

        return p;
    }

    public void writePage(Page p) throws IOException {
        if (p.number > totalPages)
            throw new IOException("WRITE PAGE ERROR: page number " + p.number + " > total: " + totalPages);

        file.seek((long) p.number * Page.pageSize);
        file.write(p.serialize().array());
    }

    public Page allocatePage() throws IOException {
        Page p = new Page(totalPages++);
        writePage(p);
        return p;
    }

}