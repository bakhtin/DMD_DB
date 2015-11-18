package core.datastructures.btree;

class KeyAndChildren<E extends Comparable<E>> {

    final E key;
    final Node<E> left;
    final Node<E> right;

    KeyAndChildren(E key, Node<E> left, Node<E> right) {
        this.key = key;
        this.right = right;
        this.left = left;
    }
}
