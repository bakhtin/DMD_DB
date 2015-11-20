package core.datastructures.bptree2;


import core.descriptive.Attribute;
import core.descriptive.Page;
import core.util.Misc;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Node<E extends Comparable> extends Page {

    int number;

    int previous;
    int nextious;

    ArrayList<E> keys;
    ArrayList<Object> pointers;
    boolean isLeaf;
    int nodeSize;
    private Node<E> next;
    private Node<E> prev;

    public Node(int nodeSize, boolean isLeaf) {
        super(0);
        keys = new ArrayList<E>();
        pointers = new ArrayList<Object>();
        this.nodeSize = nodeSize;
        this.isLeaf = isLeaf;
    }

    public static Node deserialize(ByteBuffer b) {
        int number = b.getInt();

        byte type = b.get();

        boolean isLeaf = false;
        if (type == Page.T_LNODE) isLeaf = true;
        else if (type == Page.T_INODE) isLeaf = false;
        // else "Bad deserialization in Node"

        byte keyType = b.get();
        short nodeSize = b.getShort();
        int prev = b.getInt();
        int next = b.getInt();

        Node<Comparable> c = new Node<>(nodeSize, isLeaf);
        c.nextious = next;
        c.previous = prev;

        short keyNum = b.getShort();
        c.keys = new ArrayList<>(nodeSize);
        for (int i = 0; i < keyNum; i++) {
            Comparable k = null;
            if (keyType == Attribute.T_INT) k = b.getInt();
            else if (keyType == Attribute.T_TEXT) k = Misc.parseStr(b);
            else if (keyType == Attribute.T_FLOAT) k = b.getFloat();
            else if (keyType == Attribute.T_SHORT) k = b.getShort();
            //else throw new Exception("Wrong key type");

            c.keys.add(k);
        }

        short pointNum = b.getShort();
        c.pointers = new ArrayList<>(nodeSize);
        for (int i = 0; i < pointNum; i++) {
            Comparable k = b.getInt();
            c.pointers.add(k);
        }

        return c;
    }

    @Override
    public ByteBuffer serialize() throws Exception {
        ByteBuffer buf = ByteBuffer.allocate(Page.pageSize);
        buf.clear();
        buf.putInt(number);
        buf.put(isLeaf ? Page.T_LNODE : Page.T_LNODE);
        buf.put(BPTree.KEY_TYPE);
        buf.putShort((short) nodeSize);
        buf.putInt(prev.number);
        buf.putInt(next.number);

        buf.putShort((short) keys.size()); // number of records
        for (Comparable k : this.keys) {
            if (BPTree.KEY_TYPE == Attribute.T_INT) buf.putInt((Integer) k);
            else if (BPTree.KEY_TYPE == Attribute.T_TEXT) Misc.addStr(buf, (String) k);
            else if (BPTree.KEY_TYPE == Attribute.T_FLOAT) buf.putFloat((Float) k);
            else if (BPTree.KEY_TYPE == Attribute.T_SHORT) buf.putShort((Short) k);
            else throw new Exception("Wrong key type");
        }

        buf.putShort((short) pointers.size()); // number of pointers
        for (Object k : this.pointers) {
            buf.putInt((Integer) k);
        }

        buf.flip();
        return buf;
    }

    public ArrayList<E> getKeys() {
        return keys;
    }

    public void setKeys(ArrayList<E> keys) {
        this.keys = keys;
    }

    public ArrayList<Object> getPointers() {
        return pointers;
    }

    public void setPointers(ArrayList<Object> pointers) {
        this.pointers = pointers;
    }

    public Node<E> getNextNode() {
        return this.next;
    }

    public void setNextNode(Node<E> next) {
        this.next = next;
    }

    public Node<E> getPrevNode() {
        return prev;
    }

    public void setPrevNode(Node<E> prev) {
        this.prev = prev;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public int getNodeSize() {
        return nodeSize;
    }

    public void setNodeSize(int nodeSize) {
        this.nodeSize = nodeSize;
    }
}