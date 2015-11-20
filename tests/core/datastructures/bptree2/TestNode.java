package core.datastructures.bptree2;


import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/20/2015
 */
public class TestNode {

    @Test
    public void testBPNode() throws Exception {
        Node n = new Node(128, true);
        ArrayList<Integer> keys = new ArrayList();
        keys.add(5);
        keys.add(6);
        keys.add(7);
        keys.add(8);
        n.setKeys(keys);
        n.number = 20;
        n.previous = 19;
        n.nextious = 21;
        //n.setNextNode(null);
        //n.setPrevNode(new Node());
        ArrayList<Integer> pointers = new ArrayList<>();
        pointers.add(45454);
        pointers.add(666);
        n.setPointers(pointers);

        ByteBuffer buf = n.serialize();

        Node nd = Node.deserialize(buf);

        if (nd.number != n.number) throw new Exception("NODE: node numbers are not equal");
        if (nd.previous != n.previous) throw new Exception("NODE: node prev  are not equal");
        if (nd.nextious != n.nextious) throw new Exception("NODE: node next  are not equal");
        if (nd.getNodeSize() != n.getNodeSize()) throw new Exception("NODE: node size are not equal");
        if (nd.isLeaf() != n.isLeaf()) throw new Exception("NODE: isLeaf are not equal");
        if (!nd.getKeys().equals(n.getKeys())) throw new Exception("NODE: keys are not equal");
        if (!nd.getPointers().equals(n.getPointers())) throw new Exception("NODE: pointers are not equal");
    }
}