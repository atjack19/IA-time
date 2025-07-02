import java.util.ArrayList;
import java.util.List;

public class BinarySearchTree {
    private BinaryTreeNode root;
    private BinaryTreeNodeString stringRoot;
    private List<String> searchResults;

    public BinarySearchTree() {
        root = null;
        stringRoot = null;
        searchResults = new ArrayList<>();
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

    public void add(String v) {
        BinaryTreeNodeString leaf = new BinaryTreeNodeString(v);

        //trivial case tree is empty
        if (stringRoot == null) {
            stringRoot = leaf;
            return;
        }

        //for all others find the correct point of insertion
        BinaryTreeNodeString current = stringRoot;
        boolean done = false;
        while (!done) {
            if (v.compareToIgnoreCase(current.getValue()) <= 0) { //less than or equal to
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

    private void reverseTraverseInOrder(BinaryTreeNode startingNode) {
        if (startingNode == null) { //base case for recursion
            return;
        }
        //in-order traversal
        //traverse right if possible
        reverseTraverseInOrder(startingNode.getRight());
        //display node value
        System.out.print(startingNode.getValue() + ", ");
        //traverse left if possible
        reverseTraverseInOrder(startingNode.getLeft());
    }

    private void traverseInOrder(BinaryTreeNodeString startingNode) {
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

    private void reverseTraverseInOrder(BinaryTreeNodeString startingNode) {
        if (startingNode == null) { //base case for recursion
            return;
        }
        //in-order traversal
        //traverse right if possible
        reverseTraverseInOrder(startingNode.getRight());
        //display node value
        System.out.print(startingNode.getValue() + ", ");
        //traverse left if possible
        reverseTraverseInOrder(startingNode.getLeft());
    }

    public void display() {
        if (root == null) {
            traverseInOrder(stringRoot);
        } else {
            traverseInOrder(root);
        }
        System.out.println();
    }

    public void reverseDisplay() {
        if (root == null) {
            reverseTraverseInOrder(stringRoot);
        } else {
            reverseTraverseInOrder(root);
        }
        System.out.println();
    }

    public List<String> searchFor(String searchCriteria, String searchItem) {
        searchResults.clear();
        searchItem = searchItem.toLowerCase();
        switch (searchCriteria.toLowerCase()) {
            case "name":
                searchFor("name", searchItem, stringRoot);
                break;
        }
        return new ArrayList<>(searchResults);
    }

    private void searchFor(String searchCriteria, String searchItem, BinaryTreeNodeString startingNode) {
        if (startingNode == null) {
            return;
        }

        String nodeValue = startingNode.getValue().toLowerCase();
        
        // Fuzzy search: check if the search term is contained in the node value
        if (nodeValue.contains(searchItem)) {
            searchResults.add(startingNode.getValue());
        }
        
        // Continue searching both left and right subtrees for more matches
        searchFor(searchCriteria, searchItem, startingNode.getLeft());
        searchFor(searchCriteria, searchItem, startingNode.getRight());
    }

    // Method to get all values in sorted order
    public List<String> getAllValues() {
        List<String> values = new ArrayList<>();
        collectValues(stringRoot, values);
        return values;
    }

    private void collectValues(BinaryTreeNodeString node, List<String> values) {
        if (node == null) {
            return;
        }
        collectValues(node.getLeft(), values);
        values.add(node.getValue());
        collectValues(node.getRight(), values);
    }
}
