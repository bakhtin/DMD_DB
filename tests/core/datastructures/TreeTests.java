package core.datastructures;

import core.datastructures.btree.BTree;
import org.junit.Test;

import java.util.Random;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/21/2015
 */
public class TreeTests {
    @Test
    public void bptreeTest() throws Exception {
        final int NUMBER = 1000000;

        System.out.println("Adding " + NUMBER + " elements.");

        BPTree<Integer, Integer> tree = new BPTree<>(500, 500);
        for (int i = 1; i <= NUMBER; i++) {
            tree.put(i, -i);
        }
        System.out.println("Succeed\n");

        System.out.println("Finding test:");
        Random rand = new Random();
        for (int i = 0; i < NUMBER; i++) {
            int r = Math.abs(rand.nextInt()) % NUMBER + 1;
            Integer num = tree.get(r);
            if(num == null || num != -r) {
                System.out.println(num + " " + -r);
                throw new Exception("FINDING TEST FAILED");
            }
        }
        System.out.println("Find test succeeded!\n");

        System.out.println("Removing test");
        for (int i = 0; i < 1000; i++) {
            int r = Math.abs(rand.nextInt()) % NUMBER + 1;
            Integer num = tree.remove(r);
            if(num != null && num != -r) {
                System.out.println(num + " " + -r);
                throw new Exception("REMOVING TEST FAILED");
            }
        }
        System.out.println("Removing test succeeded!\n");

    }

    @Test
    public void anotherBpTreeTest() throws Exception {
        BTree<Integer> tree = new BTree<>(250);

        for (int i = 0; i < 1000000; i++) {
            tree.add(i);
        }

        System.out.println(tree.toString());
    }
}
