package core.sys;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/22/2015
 */
public class Pager {
    private String path;
    private RandomAccessFile file;
    private FileChannel channel;

    private int totalPages = 0;


    Pager(String path) {
        this.path = path;
        try {
            file = new RandomAccessFile(path, "rw");
            totalPages = (int) (file.length() / Page.pageSize);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public Page readPage(int n) throws Exception {
        if (n > totalPages) throw new Exception("WRITE PAGE ERROR: page number " + n + " > total: " + totalPages);

        byte[] page = new byte[Page.pageSize];

        file.seek(n * Page.pageSize);
        file.read(page);

        Page p = Page.deserialize(ByteBuffer.wrap(page));
        return p;
    }

    public void writePage(Page p) throws Exception {
        if (p.number > totalPages)
            throw new Exception("WRITE PAGE ERROR: page number " + p.number + " > total: " + totalPages);
        file.seek((long) p.number * Page.pageSize);
            file.write(p.serialize().array());
        }

    public Page allocatePage() throws Exception {
        Page p = new Page(totalPages++);
        writePage(p);
        return p;
    }

}
