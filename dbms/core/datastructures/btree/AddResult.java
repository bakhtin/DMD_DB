package core.datastructures.btree;

/** Used to bubble up datastructures modifications when an element was added to a child node. */
class AddResult<E extends Comparable<E>> {
    /** Whether the element was actually added (vs. it already existed) */
    final boolean added;
    /**
     * When a node exceeds the maximum key count as the result of an addition, it is split into two nodes,
     * separated by a key to be inserted in its parent.
     */
    final KeyAndChildren<E> split;

    AddResult(boolean added, KeyAndChildren<E> split) {
        this.added = added;
        this.split = split;
    }

    public static <E extends Comparable<E>> AddResult<E> alreadyExisted() {
        return new AddResult<>(false, null);
    }
}
