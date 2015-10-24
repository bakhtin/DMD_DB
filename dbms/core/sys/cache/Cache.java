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
    private String path;
    private Pager pager;

    // statistics
    private Long hit = 0L;
    private Long miss = 0L;


    Cache(String p) {
        this.path = p;
        pager = new Pager(path);
    }


    public Double hitRate() {
        return (double) (hit / (hit + miss));
    }

    /**
     * Get page number n from cache. If page is not in cache (miss) -- read page from file.
     *
     * @param n - page number
     * @return - Page
     */
    public Page get(Integer n) {
        // look for a page in the table
        Integer min = null;
        for (int i = 0; i < cacheSize; i++) {
            // find the place for future page if miss by discipline LRU
            if (min == null || table[i] == null || table[i].compareTo(i) > 0)
                min = i;

            if (table[i] != null && table[i].getNumber().equals(n)) {                        // hit
                hit++;
                table[i].use();
                return table[i].getPage();
            }
        }

        miss++;                                                                              // miss
        try {
            CachedPage p = new CachedPage(pager.readPage(n));
            table[min] = p;
            return p.getPage();
        } catch (IOException e) {
            // check for correctness of input argument of readPage
            e.printStackTrace();
            return null;
        }
    }


    public void put(Page p) {

    }
}
