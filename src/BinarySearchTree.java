
public class BinarySearchTree {
    private BinaryTreeNode root;

    public BinarySearchTree() {
        root = null;
    }
    //insert a new node preserving the BST property
    public void add(int v) {
        BinaryTreeNode leaf = new BinaryTreeNode(v);

        //trivial case tree is empty
        if (root == null) {
            root = leaf;
            return;
        }

        //for all others find the correct point of insertion
        BinaryTreeNode current = root;
        boolean done = false;
        while (!done) {
            if (v <= current.getValue()) {
                if (current.getLeft() == null) {
                    //we've arrived
                    current.setLeft(leaf);
                    done = true;
                } else {
                    current = current.getLeft();
                }
            } else {
                if (current.getRight() == null) {
                    //we've arrived
                    current.setRight(leaf);
                    done = true;
                } else {
                    current = current.getRight();
                }
            }
        }
    }

    public void add(String term) { //
        BinaryTreeNode leaf = new BinaryTreeNode(term);

        //trivial case tree is empty
        if (root == null) {
            root = leaf;
            return;
        }

        //for all others find the correct point of insertion
        BinaryTreeNode current = root;
        boolean done = false;
        while (!done) {
            if (term <= current.getValue()) {
                if (current.getLeft() == null) {
                    //we've arrived
                    current.setLeft(leaf);
                    done = true;
                } else {
                    current = current.getLeft();
                }
            } else {
                if (current.getRight() == null) {
                    //we've arrived
                    current.setRight(leaf);
                    done = true;
                } else {
                    current = current.getRight();
                }
            }
        }
    }


    private void traverseInOrder(BinaryTreeNode startingNode) {
        if (startingNode == null) { //base case for recursion
            return;
        }
        //in-order traversal
        //traverse left if possible
        traverseInOrder(startingNode.getLeft());
        //display node value
        System.out.print(startingNode.getValue() + ", ");
        //traverse right if possible
        traverseInOrder(startingNode.getRight());
    }

    public void display() {
        traverseInOrder(root);
        System.out.println();
    }
}
