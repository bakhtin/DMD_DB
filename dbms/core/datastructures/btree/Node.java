package core.datastructures.btree;

import java.util.Arrays;

class Node<E extends Comparable<E>> {
    final int minKeyCount;
    private final int maxKeyCount;

    // Use non-generic containers, we'll make sure only the right types end up into them
    final Object[] keys;
    final Node<?>[] children;

    char label;
    int keyCount;

    Node(int minKeyCount) {
        this.minKeyCount = minKeyCount;
        this.maxKeyCount = 2 * minKeyCount;

        this.keys = new Object[maxKeyCount + 1];
        this.children = new Node<?>[maxKeyCount + 2];

        this.keyCount = 0;
    }

    Node(int minKeyCount, KeyAndChildren<E> data) {
        this(minKeyCount);
        insertKey(data.key, 0);
        insertChild(data.left, 0);
        insertChild(data.right, 1);
    }

    boolean contains(E e) {
        checkNotNull(e);
        int firstHigher = firstHigher(e);
        if (e.equals(keys(firstHigher))) return true;
        Node<E> targetChild = leftChild(firstHigher);
        return targetChild != null && targetChild.contains(e);
    }

    AddResult<E> add(E e) {
        checkNotNull(e);
        int firstHigher = firstHigher(e);
        if (e.equals(keys(firstHigher))) return AddResult.alreadyExisted();
        Node<E> targetChild = leftChild(firstHigher);
        boolean added;
        if (targetChild == null) {
            assert (this.isLeaf());
            insertKey(e, firstHigher);
            added = true;
        } else {
            AddResult<E> childResult = targetChild.add(e);
            added = childResult.added;
            KeyAndChildren<E> childSplit = childResult.split;
            if (childSplit != null) {
                insertKeyAndRightChild(childSplit.key, childSplit.right, firstHigher);
                replaceChild(childSplit.left, firstHigher);
            }
        }
        KeyAndChildren<E> mySplit = splitIfNecessary();
        return new AddResult<>(added, mySplit);
    }

    private KeyAndChildren<E> splitIfNecessary() {
        if (this.keyCount <= maxKeyCount) return null;

        assert keyCount == maxKeyCount + 1;
        int middle = this.keyCount / 2;
        E median = keys(middle);

        Node<E> left = new Node<>(minKeyCount);
        for (int i = 0; i < middle; i++) {
            left.keys[i] = this.keys[i];
            left.children[i] = this.children[i];
            left.keyCount += 1;
        }
        left.children[middle] = this.children[middle];

        Node<E> right = new Node<>(minKeyCount);
        for (int i = middle + 1; i < this.keyCount; i++) {
            right.keys[i - middle - 1] = this.keys[i];
            right.children[i - middle - 1] = this.children[i];
            right.keyCount += 1;
        }
        right.children[keyCount - middle - 1] = this.children[keyCount];

        return new KeyAndChildren<>(median, left, right);
    }

    public boolean remove(E e) {
        checkNotNull(e);
        int firstHigher = firstHigher(e);
        if (e.equals(keys(firstHigher))) {
            Node<E> leftChild = leftChild(firstHigher);
            Node<E> rightChild = rightChild(firstHigher);
            if (leftChild == null && rightChild == null) {
                assert (this.isLeaf());
                removeKey(firstHigher);
                return true;
            }
            Node<E> rightMostLeafLeft = leftChild == null ? null : leftChild.findRightMostLeaf();
            if (rightMostLeafLeft != null && rightMostLeafLeft.canGiveKey()) {
                swapKey(firstHigher, rightMostLeafLeft, rightMostLeafLeft.keyCount - 1);
                boolean removed = leftChild.remove(e);
                rebalanceIfNeeded(firstHigher);
                return removed;
            }
            Node<E> leftMostLeafRight = rightChild == null ? null : rightChild.findLeftMostLeaf();
            if (leftMostLeafRight != null) {
                int i = 0;
                swapKey(firstHigher, leftMostLeafRight, i);
                boolean removed = rightChild.remove(e);
                rebalanceIfNeeded(firstHigher + 1);
                return removed;
            }
            assert false : "Tree was not balanced before removal";
            return false;
        } else {
            Node<E> targetChild = leftChild(firstHigher);
            if (targetChild == null) return false;
            else {
                boolean removed = targetChild.remove(e);
                rebalanceIfNeeded(firstHigher);
                return removed;
            }
        }
    }

    private void rebalanceIfNeeded(int childIndex) {
        Node<E> child = children(childIndex);
        assert child != null;
        if (!child.isUnderfilled()) return;
        assert child.keyCount == minKeyCount - 1;
        Node<E> leftSibling = childIndex == 0 ? null : children(childIndex - 1);
        Node<E> rightSibling = childIndex == keyCount ? null : children(childIndex + 1);
        if (leftSibling != null && leftSibling.canGiveKey()) {
            E parentKey = keys(childIndex - 1);
            KeyAndChildren<E> lastLeft = leftSibling.removeLastKeyAndItsRightChild();
            this.keys[childIndex - 1] = lastLeft.key;
            child.insertFirstKeyAndItsLeftChild(parentKey, lastLeft.right);
        } else if (rightSibling != null && rightSibling.canGiveKey()) {
            E parentKey = keys(childIndex);
            KeyAndChildren<E> firstRight = rightSibling.removeFirstKeyAndItsLeftChild();
            keys[childIndex] = firstRight.key;
            child.appendKeyAndRightChild(parentKey, firstRight.left);
        } else if (leftSibling != null) {
            E parentKey = keys(childIndex - 1);
            leftSibling.appendKeyAndRightChild(parentKey, child.children(0));
            leftSibling.appendAllKeysAndRightChildrenOf(child);
            this.removeKeyAndItsRightChild(childIndex - 1);
        } else if (rightSibling != null) {
            E parentKey = keys(childIndex);
            child.appendKeyAndRightChild(parentKey, rightSibling.children(0));
            child.appendAllKeysAndRightChildrenOf(rightSibling);
            this.removeKeyAndItsRightChild(childIndex);
        } else {
            assert false : "Tree was not balanced before removal";
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Node<?>) {
            Node<?> that = (Node<?>) other;
            return this.minKeyCount == that.minKeyCount &&
                    Arrays.deepEquals(this.keys, that.keys) &&
                    Arrays.deepEquals(this.children, that.children);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + minKeyCount;
        for (Object key : keys)
            if (key != null) hashCode = 31 * hashCode + key.hashCode();
        for (Node<?> child : children)
            if (child != null) hashCode = 31 * hashCode + child.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return Nodes.toString(this);
    }

    @SuppressWarnings("unchecked")
    private E keys(int i) {
        return (E) keys[i];
    }

    @SuppressWarnings("unchecked")
    Node<E> children(int i) {
        return (Node<E>) children[i];
    }

    private void insertKey(E key, int index) {
        for (int i = index; i < keys.length; i++) {
            E tmp = keys(i);
            keys[i] = key;
            key = tmp;
        }
        assert key == null : "Lost last key!";
        keyCount += 1;
    }

    private E removeKey(int index) {
        E key = keys(index);
        for (int i = index; i < keys.length - 1; i++) keys[i] = keys(i + 1);
        keys[keys.length - 1] = null;
        keyCount -= 1;
        return key;
    }

    /** warning: does not update {@code keyCount} */
    private void insertChild(Node<E> child, int index) {
        for (int i = index; i < children.length; i++) {
            Node<E> tmp = children(i);
            children[i] = child;
            child = tmp;
        }
        assert child == null : "Lost last child!";
    }

    /** warning: does not update {@code keyCount} */
    private Node<E> removeChild(int index) {
        Node<E> child = children(index);
        for (int i = index; i < children.length - 1; i++) children[i] = children(i + 1);
        children[children.length - 1] = null;
        return child;
    }

    /**
     * Returns the index of the first key that is greater than or equal to a given value.
     * If all keys are smaller, return the index after the last key.
     */
    private int firstHigher(E e) {
        for (int i = 0; i < keyCount; i++) {
            E key = keys(i);
            if (key == null || e.compareTo(key) <= 0) return i;
        }
        return keyCount;
    }

    private Node<E> leftChild(int keyIndex) {
        return children(keyIndex);
    }

    private Node<E> rightChild(int keyIndex) {
        return children(keyIndex + 1);
    }

    private void insertKeyAndRightChild(E key, Node<E> right, int index) {
        insertKey(key, index);
        insertChild(right, index + 1);
    }

    private void swapKey(int myKeyIndex, Node<E> otherNode, int otherKeyIndex) {
        E tmp = otherNode.keys(otherKeyIndex);
        otherNode.keys[otherKeyIndex] = keys(myKeyIndex);
        keys[myKeyIndex] = tmp;
    }

    private void replaceChild(Node<E> child, int index) {
        children[index] = child;
    }

    void replaceLastChild(Node<E> child) {
        replaceChild(child, keyCount);
    }

    private Node<E> findRightMostLeaf() {
        Node<E> rightMostChild = null;
        for (int i = keyCount; i >= 0; i--) {
            Node<E> child = children(i);
            if (child != null) {
                rightMostChild = child;
                break;
            }
        }
        return rightMostChild == null ? this : rightMostChild.findRightMostLeaf();
    }

    private Node<E> findLeftMostLeaf() {
        Node<E> leftMostChild = null;
        for (int i = 0; i < keyCount; i++) {
            Node<E> child = children(i);
            if (child != null) {
                leftMostChild = child;
                break;
            }
        }
        return leftMostChild == null ? this : leftMostChild.findLeftMostLeaf();
    }

    private KeyAndChildren<E> removeLastKeyAndItsRightChild() {
        int last = keyCount - 1;
        E key = removeKey(last);
        Node<E> right = removeChild(last + 1);
        return new KeyAndChildren<>(key, null, right);
    }

    KeyAndChildren<E> removeLastKeyWithBothChildren() {
        int last = keyCount - 1;
        E key = removeKey(last);
        Node<E> right = removeChild(last + 1);
        Node<E> left = removeChild(last);
        return new KeyAndChildren<>(key, left, right);
    }

    E removeLastKey() {
        assert children(keyCount) == null : "Key to remove has a right child";
        return removeKey(keyCount - 1);
    }

    private void insertFirstKeyAndItsLeftChild(E key, Node<E> child) {
        insertKey(key, 0);
        insertChild(child, 0);
    }

    private KeyAndChildren<E> removeFirstKeyAndItsLeftChild() {
        E key = removeKey(0);
        Node<E> left = removeChild(0);
        return new KeyAndChildren<>(key, left, null);
    }

    private void appendKeyAndRightChild(E key, Node<E> child) {
        int afterLast = keyCount;
        insertKey(key, afterLast);
        insertChild(child, afterLast + 1);
    }

    void appendKeyAndLeftChild(E key, Node<E> child) {
        int afterLast = keyCount;
        insertKey(key, afterLast);
        insertChild(child, afterLast);
    }

    void appendKey(E key) {
        insertKey(key, keyCount);
    }

    private void removeKeyAndItsRightChild(int i) {
        removeKey(i);
        removeChild(i + 1);
    }

    private void appendAllKeysAndRightChildrenOf(Node<E> child) {
        for (int i = 0; i < child.keyCount; i++)
            appendKeyAndRightChild(child.keys(i), child.children(i + 1));
    }

    private boolean canGiveKey() {
        return keyCount > minKeyCount;
    }

    private boolean isUnderfilled() {
        return keyCount < minKeyCount;
    }

    boolean isOverfilled() {
        return keyCount == maxKeyCount + 1;
    }

    private boolean isLeaf() {
        for (Node<?> child : children) if (child != null) return false;
        return true;
    }

    private void checkNotNull(E e) {
        if (e == null) throw new IllegalArgumentException("Element must not be null");
    }
}