package core.datastructures.btree;

class Nodes {
    static String toString(Node<?> root) {
        labelNodesDepthFirst(root);
        return printNodesDepthFirst(root);
    }

    private static void labelNodesDepthFirst(Node<?> root) {
        label(root, 'A');
    }

    private static void label(Node<?> node, char label) {
        node.label = label;
        for (Node<?> child : node.children)
            if (child != null) label(child, ++label);
    }

    private static String printNodesDepthFirst(Node<?> root) {
        StringBuilder sb = new StringBuilder();
        append(root, sb);
        return sb.toString();
    }

    private static void append(Node<?> node, StringBuilder sb) {
        appendDetails(node, sb);
        for (Node<?> child : node.children)
            if (child != null) append(child, sb);
    }

    private static void appendDetails(Node<?> node, StringBuilder sb) {
        sb.append("Node " + node.label + ":");
        sb.append("\n  keys:");
        for (int i = 0; i < node.keyCount; i++) {
            assert node.keys[i] != null;
            sb.append(" ").append(node.keys[i]);
        }
        sb.append("\n  children:");
        for (int i = 0; i < node.keyCount + 1; i++) {
            Node<?> child = node.children[i];
            sb.append(" ").append(child == null ? '-' : child.label);
        }
        sb.append("\n");
    }
}
