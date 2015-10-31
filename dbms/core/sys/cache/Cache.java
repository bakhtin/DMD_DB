package core.sys.cache;

import core.sys.Page;
import core.sys.PageManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/24/2015
 */
public class Cache {
    private static int cacheSize = 2048;
    private HashMap<Integer, Page> map = new HashMap<>(cacheSize, 1);
    private TreeMap<Integer, LRU> used = new TreeMap<>();

    private String path;
    private PageManager pager;

    private class LRU implements Comparable<Long> {
        Long timestamp = 0L;
        long counter = 0L;
        int pnumber;

        void use() {
            counter++;
            timestamp = System.nanoTime();
        }

        LRU(int n) {
            pnumber = n;
            this.use();
        }

        @Override
        public int compareTo(Long o) {
            return timestamp.compareTo(o);
        }
    }

    // statistics
    private Long hit = 0L;
    private Long miss = 0L;


    public Cache(String p) {
        this.path = p;
        pager = new PageManager(path);
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
        if (map.containsKey(n)) {
            hit++;
            used.get(n).use();
            return map.get(n);
        }else{
            miss++;
            // FIXIT/TODO
            try {
                return pager.readPage(n);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Write page P directly to file.
     *
     * @param p - Page
     */
    public void write(Page p) {
        map.put(p.getNumber(), p);
        used.put(p.getNumber(), new LRU(p.getNumber()));
        try {
            pager.writePage(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
