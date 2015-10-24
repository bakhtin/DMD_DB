package core.sys.cache;

import core.sys.Page;
import core.sys.Pager;

import java.io.IOException;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/24/2015
 */
public class Cache {
    private static int cacheSize = 512;
    private CachedPage[] table = new CachedPage[cacheSize];
    private int pointer = 0;
    private String path;
    private Pager pager;

    // statistics
    private Long hit = 0L;
    private Long miss = 0L;


    public Cache(String p) {
        this.path = p;
        pager = new Pager(path);
    }


    public Double hitRate() {
        return ((double) hit / (hit + miss));
    }

    /**
     * Get page number n from cache. If page is not in cache (miss) -- read page from file.
     *
     * @param n - page number
     * @return - Page
     */
    public Page get(Integer n) {
        // look for a page in the table
        Integer min = Integer.MAX_VALUE;
        Integer index = 0;
        for (int i = 0; i < cacheSize; i++) {
            // find the place for future page if miss by discipline LRU
            if (table[i] == null || table[i].used <= min) {
                if (table[i] != null) min = table[i].used;
                index = i;
            }

            if (table[i] == null) break;

            if (table[i] != null && table[i].getNumber().equals(n)) {                        // hit
                hit++;
                table[i].use();
                return table[i].getPage();
            }
        }

        miss++;                                                                              // miss
        try {
            Page p = pager.readPage(n);
            table[index] = new CachedPage(p);
            table[index].use();
            return p;
        } catch (IOException e) {
            // check for correctness of input argument of readPage
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Write page P directly to file.
     *
     * @param p - Page
     */
    public void write(Page p) {
        try {
            for (int i = 0; i < cacheSize; i++) {
                if (table[i] != null & table[i].getNumber() == p.getNumber()) table[i].page = p;
            }
            pager.writePage(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
