package core.sys;

import java.io.*;
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
            channel = file.getChannel();
            totalPages = (int) (file.length() / Page.pageSize);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public Page readPage(int n) throws Exception{
        if(n > totalPages) throw new Exception("WRITE PAGE ERROR: page number " + n + " > total: " + totalPages );

        byte[] page = new byte[Page.pageSize];
        Page p = null;
        try {
            file.seek(0);
            file.seek(n * Page.pageSize);
            file.read(page);

            p = Page.deserialize(ByteBuffer.wrap(page));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return p;
    }

    public void writePage(Page p) throws Exception{
        if(p.number > totalPages) throw new Exception("WRITE PAGE ERROR: page number " + p.number + " > total: " + totalPages );
        try {
            file.seek(0);
            file.seek(p.number * Page.pageSize);
            file.write(p.serialize().array());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Page allocatePage() throws IOException {
        Page p = new Page(totalPages++);
        file.seek(0);
        file.seek(p.number * Page.pageSize);
        file.write(p.serialize().array());
        return p;
    }

    public static void main(String[] arg) {
        Pager pager = new Pager("huidb");
        try {

            Page q = pager.readPage(10);
            q.data[0] = (byte)0xFF;
            pager.writePage(q);

            for (int i = 0; i < pager.totalPages; i++) {
                Page p = pager.readPage(i);
                System.out.println(p.number + " " + p.data[0]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
