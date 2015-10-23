package core.sys;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 * @date 10/22/2015
 */
public class Pager {
    private String path;
    private RandomAccessFile file;

    private int totalPages = 0;
    FileChannel inChannel = file.getChannel();


    Pager(String path) {
        this.path = path;
        try {
            file = new RandomAccessFile(path, "rw");
            totalPages = (int)(file.length() / Page.pageSize);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public Page readPage(int n) {
        byte [] page = new byte[Page.pageSize];
        try {
            file.seek(file.length() / n);
            file.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Page p = Page.deserialize()

    }

    public void writePage(Page p) {

    }

    public Page allocatePage() throws IOException {
        Page p = new Page(totalPages++);
        byte[] data = p.serialize();
        file.write(data,0, Page.pageSize);
        return p;
    }

    public static void main(String []arg){
        Pager pager = new Pager("huidb");
        try {
            pager.allocatePage();
            pager.allocatePage();
            pager.allocatePage();
            pager.allocatePage();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
