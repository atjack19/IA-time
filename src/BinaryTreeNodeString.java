public class BinaryTreeNodeString {
    private String term;
    private BinaryTreeNodeString left;
    private BinaryTreeNodeString right;

    public BinaryTreeNodeString(String t) {
        term = t;
        left = null;
        right = null;
    }

    public String getValue() {
        return term;
    }

    public BinaryTreeNodeString getLeft() {
        return left;
    }

    public BinaryTreeNodeString getRight() {
        return right;
    }

    public void setRight(BinaryTreeNodeString right) {
        this.right = right;
    }

    public void setLeft(BinaryTreeNodeString left) {
        this.left = left;
    }

    public void setValue(String term) {
        this.term = term;
    }
}