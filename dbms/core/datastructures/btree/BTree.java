package core.datastructures.btree;

public class BTree<E extends Comparable<E>> {
    private final int minKeysPerNode;

    Node<E> root;

    public BTree(int minKeysPerNode) {
        this.minKeysPerNode = minKeysPerNode;
        this.root = new Node<>(minKeysPerNode);
    }

    @SafeVarargs
    public BTree(int minKeysPerNode, E... elements) {
        this.minKeysPerNode = minKeysPerNode;
        this.root = new Bulkloader<>(minKeysPerNode, elements).root;
    }

    BTree(Node<E> root) {
        this.minKeysPerNode = root.minKeyCount;
        this.root = root;
    }

    public boolean contains(E e) {
        return root.contains(e);
    }

    /** @return {code true} if the element was not already present */
    public boolean add(E e) {
        AddResult<E> rootAddResult = root.add(e);
        KeyAndChildren<E> rootSplit = rootAddResult.split;
        if (rootSplit != null) {
            root = new Node<>(minKeysPerNode, rootSplit);
        }
        return rootAddResult.added;
    }

    /** @return {code true} if the datastructures changed as a result of this call */
    @SafeVarargs
    public final boolean addAll(E... es) {
        boolean result = false;
        for (E e : es) result |= add(e);
        return result;
    }

    /** @return {code true} if the element was present */
    public boolean remove(E e) {
        boolean removed = root.remove(e);
        if (root.keyCount < minKeysPerNode) root = root.children(0);
        return removed;
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
