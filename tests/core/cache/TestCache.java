package core.cache;

import core.sys.Page;
import core.sys.cache.Cache;
import org.junit.Test;

import java.util.Random;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/24/2015
 */
public class TestCache {
    @Test
    public void testCache() throws Exception {
        Cache cache = new Cache("huidb");
        int n = 700;
        int runs = 1000000;
        Random R = new Random();
        for (int i = 0; i < runs; i++) {
            int rand = R.nextInt(n);
            Page p = cache.get(rand);
            //if (p.getNumber() != rand) throw new Exception("Not equal: " + p.getNumber() + " " + rand);
            if (p.getNumber() != rand) System.out.println("Not equal: " + p.getNumber() + " " + rand);

        }

        System.out.println("Cache hit rate: " + cache.hitRate() * 100);
    }
}
