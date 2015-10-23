package core.sys;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;

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
            channel = file.getChannel();
            totalPages = (int) (file.length() / Page.pageSize);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        file.close();
        super.finalize();
    }

    public Page readPage(int n) throws Exception {
        if (n > totalPages)
            throw new Exception("READ PAGE ERROR: page number " + n + " > total: " + totalPages);

        byte[] page = new byte[Page.pageSize];

        file.seek((long) n * Page.pageSize);
        file.read(page);
        Page p = Page.deserialize(ByteBuffer.wrap(page));

        return p;
    }

    public Page readPageChannel(int n) throws Exception {
        if (n > totalPages)
            throw new Exception("READ PAGE ERROR: page number " + n + " > total: " + totalPages);

        ByteBuffer page = ByteBuffer.allocate(Page.pageSize);

        Page p = null;
        channel.read(page, (long)n * Page.pageSize);
        page.flip();
        p = Page.deserialize(page);

        return p;
    }

    public void writePage(Page p) throws Exception {
        if (p.number > totalPages)
            throw new Exception("WRITE PAGE ERROR: page number " + p.number + " > total: " + totalPages);

        try {
            file.seek((long) p.number * Page.pageSize);
            file.write(p.serialize().array());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Page allocatePage() throws IOException {
        Page p = new Page(totalPages++);
        file.seek((long) p.number * Page.pageSize);
        file.write(p.serialize().array());
        return p;
    }

}
