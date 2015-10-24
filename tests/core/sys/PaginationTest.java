package core.sys;

import org.junit.Test;

import java.io.File;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/23/2015
 */
public class PaginationTest {
    static int n = 1000000;

    @Test
    public void testPageRead(){
        File f = new File("huidb");
        f.delete();
        Pager pager = new Pager("huidb");

        long start1;
        long start2;

        Page p = null;

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
}
