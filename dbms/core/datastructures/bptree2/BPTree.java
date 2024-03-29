package core.datastructures.bptree2;

import core.descriptive.Attribute;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BPTree<E extends Comparable<E>> {

    static byte KEY_TYPE = Attribute.T_INT;

    private int leafSize;
    private int internalSize;
    private Node<E> root;
    private int size;
    private NativeMethods<E> nm;
    private LinkedList<Node<E>> buffer;
    private int bufferboolSize;

    public BPTree(int leafSize, int internalSize, int bufferbool, byte KEY_TYPE) {
        super();
        this.leafSize = leafSize;
        this.internalSize = internalSize;
        this.root = new Node<E>(leafSize, true);
        nm = new NativeMethods<E>();
        buffer = new LinkedList<Node<E>>();
        this.bufferboolSize = bufferbool;

        this.KEY_TYPE = KEY_TYPE;
    }

    public static void main(String[] args) throws Exception {
        BPTree<Integer> t = new BPTree<Integer>(4, 4, 3, Attribute.T_TEXT);
        ArrayList<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < 50; i++) {
            keys.add(i);
        }
        ArrayList<Object> pointers = new ArrayList<Object>();
        for (int i = 0; i < 50; i++) {
            pointers.add(i + "");
        }
        t.insertBulk(keys, pointers);
        t.commit();
//        System.out.println("===========================================");
        //System.out.println(t.search(10).getKeys());
//        BPTree<Integer> tr = new BPTree<Integer>(2, 2);
//        int temp = 0;
//        for (int i = 0; i < 100; i++) {
//            tr.insertNode((int) (Math.random() * 100), i + "");
//            tr.commit();
//            System.out.println("==========================================");
//        }
//        tr.delete(2);
//        tr.commit();
//        tr.insertNode(10, "3");
//        tr.insertNode(8, "5");
//        tr.insertNode(15, "15");
//        tr.insertNode(19, "27");
//        tr.insertNode(8, "30");
//        tr.insertNode(30, "45");
//        tr.insertNode(37, "22");
//        tr.insertNode(38, "17");
//        tr.insertNode(100, "19");
////        tr.insertNode(20, "20");
////        tr.insertNode(28, "28");
////        tr.insertNode(29, "29");
////        tr.insertNode(40, "40");
////        tr.insertNode(47, "47");
////        tr.insertNode(23, "23");
////        tr.insertNode(24, "24");
////        tr.insertNode(100, "100");
////        tr.insertNode(77, "77");
////        tr.insertNode(60, "60");
////        tr.insertNode(36, "36");
////        tr.insertNode(37, "37");
//        tr.delete(100);
//        tr.delete(38);
//        tr.delete(37);
////        tr.delete(28);
////        tr.delete(15);
////        tr.delete(19);
////        tr.delete(27);
////        tr.delete(47);
////        tr.delete(40);
////        tr.delete(23);
////        tr.delete(17);
////        tr.delete(5);
////        tr.delete(3);
////        tr.insertNode(45, "45");
////        tr.delete(24);
////        tr.delete(29);
////        tr.delete(30);
////        tr.delete(36);
////        tr.delete(45);
////        tr.delete(60);
////        tr.insertNode(33, "33");
////        tr.delete(77);
////        tr.delete(100);
////        tr.delete(100);
//        System.out.println(tr.commit());

    }
//======================================BULK INSERTION=================================================

    // ======================================================================
    // ===========================INSERTION==================================
    @SuppressWarnings("unchecked")
    public void insertNode(E key, Object data) {
        // stack to hold parent
        LinkedList<Node<E>> stack = new LinkedList<Node<E>>();
        Node<E> n = root;
        //sezrching fo the element
        while (!n.isLeaf) {
            stack.push(n);
            // ===================================================
            if (key.compareTo(n.getKeys().get(0)) < 0) {  // if in first pointer
                n = (Node<E>) n.getPointers().get(0);
            } else if (key.compareTo(n.getKeys().get(n.getKeys().size() - 1)) >= 0) {// if in last pointer
                n = (Node<E>) n.getPointers().get(n.getPointers().size() - 1);
            } else {
                for (int i = 0; i < n.getKeys().size() - 1; i++) { // general case
                    if (n.getKeys().size() > 1 && key.compareTo(n.getKeys().get(i)) >= 0 && key.compareTo(n.getKeys().get(i + 1)) < 0) {
                        n = (Node) n.getPointers().get(i + 1);
                        break;
                    }
                }
            }
        }
        // check if the elemnet in the node or not
        for (int i = 0; i < n.getKeys().size(); i++) {
            if (key == n.getKeys().get(i)) {
                return;
            }
        }
        // if node is not full
        if (n.getKeys().size() < leafSize) {
            nm.sortedInsert(key, data, n);
        } else {
            ///    spliting two leaf nodes
            // copying all current node contents in temp node then insert the new element on it
            Node<E> temp = new Node(leafSize, true);
            temp.setKeys(new ArrayList<E>(n.getKeys()));
            temp.setPointers(new ArrayList<Object>(n.getPointers()));
            nm.sortedInsert(key, data, temp);
            Node newNode = new Node(leafSize, true);
            int j = (int) Math.ceil(n.getPointers().size() / (double) 2);
            //take the first half of the temp nde in current node
            n.setKeys(new ArrayList<E>(temp.getKeys().subList(0, j)));
            n.setPointers(new ArrayList<Object>(temp.getPointers().subList(0, j)));
            // next and prev
            if (n.getNextNode() != null) {
                n.getNextNode().setPrevNode(newNode);
            }
            newNode.setNextNode(n.getNextNode());
            n.setNextNode(newNode);
            // copying the rest of temp node in new node
            newNode.setPrevNode(n);
            newNode.setKeys(new ArrayList<E>(temp.getKeys().subList(j, temp.getKeys().size())));
            newNode.setPointers(new ArrayList<Object>(temp.getPointers().subList(j, temp.getPointers().size())));
            // keeping the key that will be inserting in parent node
            key = temp.getKeys().get(j);
            boolean finished = false;
            do {
                // if the parent is null (root case)
                if (stack.isEmpty()) {
                    root = new Node(internalSize, false);
                    ArrayList<Object> point = new ArrayList<Object>();
                    point.add(n);
                    point.add(newNode);
                    ArrayList<E> keys_ = new ArrayList<E>();
                    keys_.add(key);
                    root.setKeys(keys_);
                    root.setPointers(point);
                    finished = true;
                } else {
                    // if there's parent
                    n = stack.pop();
                    // if there's no need for splitting internal
                    if (n.getKeys().size() < internalSize) {
                        nm.sortedInsertInternal(key, newNode, n);
                        finished = true;
                    } else {
                        /* splitting two internal nodes by copying them into new node and insert
                        new elemnet in the temp node then divide it betwwen current node and new node
                         */
                        temp.setLeaf(false);
                        temp.setKeys(new ArrayList<E>(n.getKeys()));
                        temp.setPointers(new ArrayList<Object>(n.getPointers()));

                        nm.sortedInsertInternal(key, newNode, temp);
                        newNode = new Node(internalSize, false);
                        j = (int) Math.ceil(temp.getPointers().size() / (double) 2);

                        n.setKeys(new ArrayList<E>(temp.getKeys().subList(0, j - 1)));
                        n.setPointers(new ArrayList<Object>(temp.getPointers().subList(0, j)));
                        if (n.getNextNode() != null) {
                            n.getNextNode().setPrevNode(newNode);
                        }
                        newNode.setNextNode(n.getNextNode());
                        n.setNextNode(newNode);
                        newNode.setPrevNode(n);
                        newNode.setKeys(new ArrayList<E>(temp.getKeys().subList(j, temp.getKeys().size())));
                        newNode.setPointers(new ArrayList<Object>(temp.getPointers().subList(j, temp.getPointers().size())));

                        key = temp.getKeys().get(j - 1);
                    }
                }
            } while (!finished);
        }
    }

    @SuppressWarnings("unchecked")
    public void insertBulk(ArrayList<E> keys, ArrayList<Object> records) {
        E key;
        boolean firstInsert = true;
        int first = 0;
        int second = 0;
        for (int i = 0; i < Math.ceil(keys.size() / (double) leafSize); i++) {
            // stack to hold parent
            LinkedList<Node<E>> stack = new LinkedList<Node<E>>();
            Node<E> n = root;
            first = second;
            second = second + leafSize;
            if (second > keys.size()) {
                second = keys.size();
            }
            ArrayList<E> newKeys = new ArrayList<E>(keys.subList(first, second));
            ArrayList<Object> newRecords = new ArrayList<Object>(records.subList(first, second));
            // getting the right most elemnet in the tree
            while (!n.isLeaf) {
                stack.push(n);
                n = (Node<E>) n.getPointers().get(n.getPointers().size() - 1);
            }
            // if its the first insert
            if (firstInsert) {
                root.setKeys(newKeys);
                root.setPointers(newRecords);
                firstInsert = false;
            } else {
                //    spliting two leaf nodes
                // copying all current node contents in temp node then insert the new element on it
                Node<E> temp = new Node(leafSize, true);
                temp.setKeys(new ArrayList<E>(n.getKeys()));
                temp.setPointers(new ArrayList<Object>(n.getPointers()));
                temp.getKeys().addAll(newKeys);
                temp.getPointers().addAll(newRecords);
                Node newNode = new Node(leafSize, true);
                int j = (int) Math.ceil(temp.getPointers().size() / (double) 2);
                //take the first half of the temp nde in current node
                n.setKeys(new ArrayList<E>(temp.getKeys().subList(0, j)));
                n.setPointers(new ArrayList<Object>(temp.getPointers().subList(0, j)));
                if (n.getNextNode() != null) {
                    n.getNextNode().setPrevNode(newNode);
                }
                newNode.setNextNode(n.getNextNode());
                n.setNextNode(newNode);
                // copying other elements
                newNode.setPrevNode(n);
                newNode.setKeys(new ArrayList<E>(temp.getKeys().subList(j, temp.getKeys().size())));
                newNode.setPointers(new ArrayList<Object>(temp.getPointers().subList(j, temp.getPointers().size())));
                key = temp.getKeys().get(j);
                boolean finished = false;
                // keeping the key that will be inserting in parent node
                do {
                    // if the parent is null (root case)
                    if (stack.isEmpty()) {
                        root = new Node(internalSize, false);
                        ArrayList<Object> point = new ArrayList<Object>();
                        point.add(n);
                        point.add(newNode);
                        ArrayList<E> keys_ = new ArrayList<E>();
                        keys_.add(key);
                        root.setKeys(keys_);
                        root.setPointers(point);
                        finished = true;
                    } else {
                        // if there's parent
                        n = stack.pop();
                        // if there's no need for splitting internal
                        if (n.getKeys().size() < internalSize) {
                            nm.sortedInsertInternal(key, newNode, n);
                            finished = true;
                        } else {
                            /* splitting two internal nodes by copying them into new node and insert
                            new elemnet in the temp node then divide it betwwen current node and new node
                             */
                            temp.setLeaf(false);
                            temp.setKeys(new ArrayList<E>(n.getKeys()));
                            temp.setPointers(new ArrayList<Object>(n.getPointers()));
                            nm.sortedInsertInternal(key, newNode, temp);
                            newNode = new Node(internalSize, false);
                            j = (int) Math.ceil(temp.getPointers().size() / (double) 2);
                            n.setKeys(new ArrayList<E>(temp.getKeys().subList(0, j - 1)));
                            n.setPointers(new ArrayList<Object>(temp.getPointers().subList(0, j)));
                            if (n.getNextNode() != null) {
                                n.getNextNode().setPrevNode(newNode);
                            }
                            newNode.setNextNode(n.getNextNode());
                            n.setNextNode(newNode);
                            newNode.setPrevNode(n);
                            newNode.setKeys(new ArrayList<E>(temp.getKeys().subList(j, temp.getKeys().size())));
                            newNode.setPointers(new ArrayList<Object>(temp.getPointers().subList(j, temp.getPointers().size())));
                            key = temp.getKeys().get(j - 1);
                        }
                    }
                } while (!finished);
            }
        }
    }

    // ======================================================================
    // =============================SEARCHING================================
    @SuppressWarnings("unchecked")
    public Node<E> search(E key) {
        // secrhing in buffer array to check if the required
        // element on it or not
        for (int i = 0; i < buffer.size(); i++) {
            ArrayList<E> find = buffer.get(i).getKeys();
            if (find.contains(key)) {
                return buffer.get(i);
            }
        }
        // if the elemnet isn't in buffer bool
        Node<E> n = root;
        while (!n.isLeaf) {
            //sezrching fo the element
            if (key.compareTo(n.getKeys().get(0)) < 0) {// if in the first pointer
                n = (Node<E>) n.getPointers().get(0);
            } else if (key.compareTo(n.getKeys().get(n.getKeys().size() - 1)) >= 0) {// if in the last pointer
                n = (Node<E>) n.getPointers().get(n.getPointers().size() - 1);
            } else {
                for (int i = 0; i < n.getKeys().size() - 1; i++) {
                    if (n.getKeys().size() > 1 && key.compareTo(n.getKeys().get(i)) >= 0 && key.compareTo(n.getKeys().get(i + 1)) < 0) {// general case
                        n = (Node) n.getPointers().get(i + 1);
                        break;
                    }
                }
            }
        }
        // adding new node to buffre bool
        for (int i = 0; i < n.getKeys().size(); i++) {
            if (key.compareTo(n.getKeys().get(i)) == 0) {
                if (buffer.size() == bufferboolSize) {
                    buffer.removeFirst();
                    buffer.add(n);
                } else {
                    buffer.add(n);
                }
                return n;
            }
        }
        return null;
    }

    // ======================================================================
    // ==============================DELETION================================
    @SuppressWarnings("unchecked")
    public void delete(E key) {
        LinkedList<Node<E>> stack = new LinkedList<Node<E>>();
        Node<E> n = root;
        //secrching for the required node
        while (!n.isLeaf) {
            stack.push(n);
            // ===================================================
            if (key.compareTo(n.getKeys().get(0)) < 0) {
                n = (Node<E>) n.getPointers().get(0);
            } else if (key.compareTo(n.getKeys().get(n.getKeys().size() - 1)) >= 0) {
                n = (Node) n.getPointers().get(n.getPointers().size() - 1);
            } else {
                for (int i = 0; i < n.getKeys().size(); i++) {
                    if (key.compareTo(n.getKeys().get(i)) >= 0 && key.compareTo(n.getKeys().get(i + 1)) < 0) {
                        n = (Node) n.getPointers().get(i + 1);
                        break;
                    }
                }
            }
        }
        // END OF WHILE
        boolean flag = false;
        for (int i = 0; i < n.getKeys().size(); i++) {
            if (n == root && key == n.getKeys().get(i)) {
                nm.deleteNode(n, key);
                return;
            } else if (key == n.getKeys().get(i)) {
                flag = true;
                break;
            }
        }
        //searching to determine if the element is found in leaf node or not
        if (flag) {
            //if the node isn't under flow
            if (n.getKeys().size() - 1 >= Math.ceil(leafSize / 2.0)) {
                nm.deleteNode(n, key);
                Node<E> parent = stack.peek();
                for (int i = 0; i < parent.getKeys().size(); i++) {
                    if (key.compareTo(parent.getKeys().get(i)) == 0) {
                        parent.getKeys().set(i, n.getKeys().get(0));
                        break;
                    }
                }
            } else {
                // if node is in underflow
                Node<E> parent = stack.peek();
                // determin if the next node is from the same parent or not to borrow from it
                int deter = nm.sameParent(n, stack.peek(), leafSize);
                // if next from the same parent
                if (deter == 1) {
                    // delete the node
                    nm.deleteNode(n, key);
                    // borrow from the next leaf node
                    E element = n.getNextNode().getKeys().remove(0);
                    Object obj = n.getNextNode().getPointers().remove(0);
                    n.getKeys().add(element);
                    n.getPointers().add(obj);
                    for (int i = 0; i < parent.getKeys().size(); i++) {
                        if (element.compareTo(parent.getKeys().get(i)) == 0) {
                            parent.getKeys().set(i, n.getNextNode().getKeys().get(0));
                            break;
                        }
                    }
                    for (int i = 0; i < parent.getKeys().size(); i++) {
                        if (key.compareTo(parent.getKeys().get(i)) == 0) {
                            parent.getKeys().set(i, n.getKeys().get(0));
                            break;
                        }
                    }
                    return;
                } else if (deter == 2) {
                    // borrow from the previous node
                    nm.deleteNode(n, key);
                    E element = n.getPrevNode().getKeys().remove(n.getPrevNode().getKeys().size() - 1);
                    Object obj = n.getPrevNode().getPointers().remove(n.getPrevNode().getPointers().size() - 1);
                    n.getKeys().add(0, element);
                    n.getPointers().add(0, obj);
                    for (int i = 0; i < parent.getKeys().size(); i++) {
                        if (element.compareTo(parent.getKeys().get(i)) == 0) {
                            parent.getKeys().set(i, n.getPrevNode().getKeys().get(n.getPrevNode().getKeys().size() - 1));
                            break;
                        }
                    }
                    for (int i = 0; i < parent.getKeys().size(); i++) {
                        if (key.compareTo(parent.getKeys().get(i)) == 0) {
                            parent.getKeys().set(i, n.getKeys().get(0));
                            break;
                        }
                    }
                    return;
                } else {
                    // there will be merging for internal nodes
                    boolean prevB = true;
                    if (key == n.getKeys().get(0)) {
                        prevB = false;
                    }

                    nm.deleteNode(n, key);
                    int tempKey = 0;
                    int tempPointer = 0;
                    // if the merging will be with the next node
                    // then copying all elemnts of the current node in the next node
                    // dalete the first element from the next node in the parent node
                    if (nm.sameParent2(parent, n)) {
                        Node<E> next = n.getNextNode();
                        if (n.getPrevNode() != null) {
                            n.getPrevNode().setNextNode(next);
                        }
                        if (next != null) {
                            next.setPrevNode(n.getPrevNode());
                        }
                        n.setNextNode(null);
                        n.setPrevNode(null);
                        next.getKeys().addAll(0, n.getKeys());
                        next.getPointers().addAll(0, n.getPointers());
                        for (int i = 0; i < parent.getKeys().size(); i++) {
                            if (next.getKeys().get(n.getKeys().size()).compareTo(parent.getKeys().get(i)) == 0) {
                                tempKey = i;
                                tempPointer = i;
                                break;
                            }
                        }
                        if (tempKey > 0 && parent.getKeys().get(tempKey - 1) == key) {
                            parent.getKeys().set(tempKey - 1, next.getKeys().get(0));
                        }
                    } else {
                        // if the merging will be with the prev node
                        // then copying all elemnts of the node in the prev node
                        // dalete the first element from the current node in the parent node
                        Node<E> prev = n.getPrevNode();
                        if (prev != null) {
                            prev.setNextNode(n.getNextNode());
                        }
                        if (n.getNextNode() != null) {
                            n.getNextNode().setPrevNode(prev);
                        }
                        n.setNextNode(null);
                        n.setPrevNode(null);
                        prev.getKeys().addAll(n.getKeys());
                        prev.getPointers().addAll(n.getPointers());
                        if (prevB) {
                            for (int i = 0; i < parent.getKeys().size(); i++) {
                                if (n.getKeys().get(0).compareTo(parent.getKeys().get(i)) == 0) {
                                    tempKey = i;
                                    tempPointer = i + 1;
                                    break;
                                }
                            }
                        } else {
                            for (int i = 0; i < parent.getKeys().size(); i++) {
                                if (key.compareTo(parent.getKeys().get(i)) == 0) {
                                    tempKey = i;
                                    tempPointer = i + 1;
                                    break;
                                }
                            }
                        }
                    }
                    boolean finished = false;
                    do {
                        // if we get the root
                        if (stack.isEmpty()) {
                            root.getKeys().remove(tempKey);
                            root.getPointers().remove(tempPointer);
                            finished = true;
                        } else {
                            n = stack.pop();
                            //try borrowing from the cebeling
                            if (n.getKeys().size() - 1 >= 1) {
                                n.getKeys().remove(tempKey);
                                n.getPointers().remove(tempPointer);
                                finished = true;
                            } else {
                                // if the root have one cebeling
                                // the tree level will decrease
                                if (n == root) {
                                    n.getKeys().remove(tempKey);
                                    n.getPointers().remove(tempPointer);
                                    if (n.getPointers().size() == 1) {
                                        root = (Node<E>) n.getPointers().get(0);
                                    }
                                    finished = true;
                                } else {
                                    n.getKeys().remove(tempKey);
                                    n.getPointers().remove(tempPointer);
                                    deter = nm.nexOrprev(n, stack.peek(), internalSize);
                                    parent = stack.peek();
                                    // borrowing from next internal node
                                    if (deter == 1) {
                                        int index = -1;
                                        for (int i = 0; i < parent.getPointers().size(); i++) {
                                            if (parent.getPointers().get(i) == n.getNextNode()) {
                                                index = i;
                                                break;
                                            }
                                        }
                                        E tempkey = parent.getKeys().remove(index - 1);
                                        n.getKeys().add(tempkey);
                                        Node<E> tempNext = (Node<E>) n.getNextNode().getPointers().remove(0);
                                        E nextKey = n.getNextNode().getKeys().remove(0);
                                        n.getPointers().add(tempNext);
                                        parent.getKeys().add(index - 1, nextKey);
                                        finished = true;
                                        // boorwing form prev internal node
                                    } else if (deter == 2) {
                                        int index = -1;
                                        for (int i = 0; i < parent.getPointers().size(); i++) {
                                            if (parent.getPointers().get(i) == n) {
                                                index = i;
                                                break;
                                            }
                                        }
                                        E tempkey = parent.getKeys().remove(index - 1);
                                        n.getKeys().add(0, tempkey);
                                        Node<E> tempPrev = (Node<E>) n.getPrevNode().getPointers().remove(n.getPrevNode().getPointers().size() - 1);
                                        E prevKey = n.getPrevNode().getKeys().remove(n.getPrevNode().getKeys().size() - 1);
                                        n.getPointers().add(0, tempPrev);
                                        parent.getKeys().add(index - 1, prevKey);
                                        finished = true;
                                    } else {
                                        // mergae two internal nodes
                                        if (nm.sameParent2(parent, n)) {
                                            for (int i = 0; i < parent.getPointers().size(); i++) {
                                                if (n == parent.getPointers().get(i)) {
                                                    tempKey = i;
                                                    tempPointer = i;
                                                    break;
                                                }
                                            }
                                            Node<E> next = n.getNextNode();
                                            if (n.getPrevNode() != null) {
                                                n.getPrevNode().setNextNode(next);
                                            }
                                            if (next != null) {
                                                next.setPrevNode(n.getPrevNode());
                                            }
                                            next.getKeys().add(0, parent.getKeys().get(tempKey));
                                            next.getKeys().addAll(0, n.getKeys());
                                            next.getPointers().addAll(0, n.getPointers());

                                        } else {
                                            for (int i = 0; i < parent.getPointers().size(); i++) {
                                                if (n == parent.getPointers().get(i)) {
                                                    tempKey = i - 1;
                                                    tempPointer = i;
                                                    break;
                                                }
                                            }
                                            Node<E> prev = n.getPrevNode();
                                            if (prev != null) {
                                                prev.setNextNode(n.getNextNode());
                                            }
                                            if (n.getNextNode() != null) {
                                                n.getNextNode().setPrevNode(prev);
                                            }
                                            prev.getKeys().add(parent.getKeys().get(tempKey));
                                            prev.getKeys().addAll(n.getKeys());
                                            prev.getPointers().addAll(n.getPointers());
                                        }
                                    }
                                }
                            }
                        }
                    } while (!finished);

                }
            }
        } else { // if the elemnet isn't found
            return;
        }
    }

    // ======================================================================
// ===========================GETTERS AND SETTERS========================
    public int getLeafSize() {
        return leafSize;
    }

    public void setLeafSize(int leafSize) {
        this.leafSize = leafSize;
    }

    public int getInternalSize() {
        return internalSize;
    }

    public void setInternalSize(int internalSize) {
        this.internalSize = internalSize;
    }

    public Node<E> getRoot() {
        return root;
    }

    public void setRoot(Node<E> root) {
        this.root = root;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;


    }

    /**
     * Print tree into the file
     */
    @SuppressWarnings("unchecked")
    public List<ByteBuffer> commit() throws Exception {
        LinkedList<ByteBuffer> nodes = new LinkedList<>();
        LinkedList<Node<E>> view = new LinkedList<Node<E>>();
        view.add(root);
        while (!view.isEmpty()) {
            Node<E> e = view.pop();
            nodes.add(e.serialize());
        }

        return nodes;
    }
}