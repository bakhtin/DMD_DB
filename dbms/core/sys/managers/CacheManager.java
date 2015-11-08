package core.sys.managers;

import core.sys.descriptive.Page;
import core.sys.exceptions.SQLError;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/24/2015
 */
public class CacheManager {
    private static int cacheSize = 2048;
    Map<Integer, Page> cache = new LinkedHashMap<Integer, Page>(cacheSize, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, Page> eldest) {
            return size() > cacheSize;
        }
    };

    private PageManager pager;


    // statistics
    private Long hit = 0L;
    private Long miss = 0L;


    public CacheManager(PageManager f) {
        pager = f;
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
    public Page get(Integer n) throws IOException, SQLError {
        if (cache.containsKey(n)) {
            hit++;
            return cache.get(n);
        } else {
            miss++;
            Page p = pager.readPage(n);
            cache.put(n, p);
            return p;
        }
    }

    /**
     * Write page P directly to file.
     *
     * @param p - Page
     */
    public void put(Page p) throws IOException {
        if (cache.containsKey(p.getNumber())) {
            cache.put(p.getNumber(), p);
        }
        pager.writePage(p);
    }
}
