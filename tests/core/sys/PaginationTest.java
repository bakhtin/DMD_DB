package core.sys;

import org.junit.Test;

import java.io.File;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/23/2015
 */
public class PaginationTest {
    @Test
    public void testPageRead(){
        File f = new File("huidb");
        f.delete();
        Pager pager = new Pager("huidb");

        long start1;
        long start2;

        Page p = null;

        int n = 100000;
        try {
            System.out.println("####################PAGE READ######################");

            start1 = System.nanoTime();
            for (int i = 0; i < n; i++) {
                pager.allocatePage();
            }
            start2 = System.nanoTime();

            System.out.println("Allocating of " + n + " pages: " + Math.abs(start2 - start1)/1e9 + " s");

            ////////////////////////////////////////
            p = pager.readPage(n-1);
            p.data[0] = (byte)0xac;
            start1 = System.nanoTime();
            for (int i = 0; i < n; i++) {
                pager.writePage(p);
            }
            start2 = System.nanoTime();
            System.out.println("Writing of " + n + " pages: " + Math.abs(start2 - start1)/1e9 + " s");


            start1 = System.nanoTime();
            for (int i = 0; i < n; i++) {
                Page q = pager.readPage(i);
                if ( q.number != i) System.out.println(i + "\t" + q.number);
                if(q.data[0] == 0xac) System.out.println("OOps: " + i + "\t" + q.data[0]);
            }
            start2 = System.nanoTime();
            System.out.println("Reading of " + n + " pages: " + Math.abs(start2 - start1)/1e9 + " s");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testChannelReading(){
        File f = new File("chdb");
        f.delete();
        Pager pager = new Pager("chdb");

        long start1;
        long start2;

        Page p = null;

        int n = 100000;
        try {
            System.out.println("############CHANNEL PAGE READ######################");

            start1 = System.nanoTime();
            for (int i = 0; i < n; i++) {
                pager.allocatePage();
            }
            start2 = System.nanoTime();
            System.out.println("Allocating of " + n + " pages: " + Math.abs(start2 - start1)/1e9 + " s");



            p = pager.readPage(n-1);
            p.data[0] = (byte)0xab;
            start1 = System.nanoTime();
            for (int i = 0; i < n; i++) {
                pager.writePage(p);
            }
            start2 = System.nanoTime();
            System.out.println("Writing of " + n + " pages: " + Math.abs(start2 - start1)/1e9 + " s");


            start1 = System.nanoTime();
            for (int i = 0; i < n; i++) {
                Page q = pager.readPageChannel(i);
                if ( q.number != i) throw new Exception(i + "\t" + q.number);
                if(q.data[0] == 0xab) throw new Exception("OOps: " + i + "\t" + q.data[0]);
            }
            start2 = System.nanoTime();
            System.out.println("Channeled Reading of " + n + " pages: " + Math.abs(start2 - start1)/1e9 + " s");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
