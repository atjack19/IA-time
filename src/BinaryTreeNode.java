public class BinaryTreeNode {
    private int value;
    private String term;
    private BinaryTreeNode left;
    private BinaryTreeNode right;

    public BinaryTreeNode(int v) {
        value = v;
        left = null;
        right = null;
    }

    public BinaryTreeNode(String t) {
        term = t;
        left = null;
        right = null;
    }

    public int getValue() {
        if (value == null) {
            return term;
        } else {
            return value;
        }
    }

    public BinaryTreeNode getLeft() {
        return left;
    }

    public BinaryTreeNode getRight() {
        return right;
    }

    public void setRight(BinaryTreeNode right) {
        this.right = right;
    }

    public void setLeft(BinaryTreeNode left) {
        this.left = left;
    }

    public void setValue(int value) {
        this.value = value;
    }
}