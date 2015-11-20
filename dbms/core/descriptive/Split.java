package core.descriptive;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/19/2015
 */
public class Split<K extends Comparable<K>> {
    public final K key;
    public final Node left;
    public final Node right;

    public Split(K k, Node l, Node r) {
        key = k;
        left = l;
        right = r;
    }

}
