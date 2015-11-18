package core.datastructures.btree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Bulkloader<E extends Comparable<E>> {
    private final int minKeyCount;

    final Node<E> root;

    Bulkloader(int minKeysPerNode, E[] elements) {
        this.minKeyCount = minKeysPerNode;

        List<Node<E>> currentLevel = createLeaves(elements);
        while (currentLevel.size() > 1 || currentLevel.get(0).isOverfilled()) {
            currentLevel = buildUpperLevel(currentLevel);
        }
        root = currentLevel.get(0);
    }

    private List<Node<E>> createLeaves(E[] elements) {
        Arrays.sort(elements);

        List<Node<E>> leaves = new ArrayList<>();
        Node<E> currentNode = newNode();
        leaves.add(currentNode);
        for (E element : elements) {
            if (currentNode.isOverfilled()) {
                currentNode = newNode();
                leaves.add(currentNode);
            }
            currentNode.appendKey(element);
        }
        Node<E> lastNode = currentNode;
        if (lastNode.isOverfilled()) {
            E lastKey = lastNode.removeLastKey();
            Node<E> extraNode = newNode();
            extraNode.appendKey(lastKey);
            leaves.add(extraNode);
        }
        return leaves;
    }

    private List<Node<E>> buildUpperLevel(List<Node<E>> currentLevel) {
        List<Node<E>> upperLevel = new ArrayList<>();
        Node<E> upperNode = newNode();
        upperLevel.add(upperNode);
        for (int i = 0; i < currentLevel.size() - 1; i++) {
            Node<E> currentNode = currentLevel.get(i);
            if (upperNode.isOverfilled()) {
                upperNode = newNode();
                upperLevel.add(upperNode);
            }
            E lastKey = currentNode.removeLastKey();
            upperNode.appendKeyAndLeftChild(lastKey, currentNode);
        }
        Node<E> lastUpperNode = upperNode;
        Node<E> lastCurrentNode = currentLevel.get(currentLevel.size() - 1);
        lastUpperNode.replaceLastChild(lastCurrentNode);

        if (lastUpperNode.isOverfilled()) {
            KeyAndChildren<E> last = lastUpperNode.removeLastKeyWithBothChildren();
            Node<E> extraUpperNode = newNode(last);
            upperLevel.add(extraUpperNode);
        }
        return upperLevel;
    }

    private Node<E> newNode() {
        return new Node<>(minKeyCount);
    }

    private Node<E> newNode(KeyAndChildren<E> keyAndChildren) {
        return new Node<>(minKeyCount, keyAndChildren);
    }
}
