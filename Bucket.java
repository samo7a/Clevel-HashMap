// the bucket that will hold the value, the key, and the right and left pointers (aka: references)
public class Bucket {
    public String key;
    public int value;
    Bucket left;
    Bucket right;
    static final int LIMIT = 8; // limit the number of nodes in a tree

    // constructors
    public Bucket(String key, int value) {
        this.key = key;
        this.value = value;
        this.left = null;
        this.right = null;
    }

    public static Bucket insertTree(String key, int value, Bucket root) {
        Bucket temp = new Bucket(key, value); // create a bucket with a the key and the value
        if (root == null) // if root is null
            return temp; // return the new bucket to be the root
        if (root.value == value) // if the root's value equal the inserted value
            return root; // return the root and do nothing (do not allow dublicates).
        if (value < root.value) { // if the new value to be inserted is less than root's value
            if (root.left != null) // and the left node is not null
                root.left = insertTree(key, value, root.left); // try to insert the value in the left branch
            else // else if the left node is null
                root.left = temp; // let the left node to be the new node
        } else { // if the new value to be inserted is larger than the root's value
            if (root.right != null) // do the same thing but for the right branch
                root.right = insertTree(key, value, root.right);
            else
                root.right = temp;
        }
        return root; // return the root at the end
    }

    public static Bucket searchTree(Bucket root, String key, int value) {
        if (root != null) { // if the root exists
            if (root.value == value && root.key.equals(key)) // if the root's value equals the searched item's value,
                                                             // return
                // the root
                return root;
            if (root.value > value) // if the root's value is larger than the searched item's value, search the left
                                    // node
                return searchTree(root.left, key, value);
            else
                return searchTree(root.right, key, value); // if the root's value is smaller than the searched item's
                                                           // value,
            // search the right node
        } else // if the root does not exist, return null
            return null;
    }

    // not complete, need more information to implement it.
    // public Bucket updateTree(int key, int value, Bucket root) {
    // Bucket isFound = searchTree(root, value);
    // if (isFound != null)
    // isFound.value = value;
    // else
    // root = insertTree(key, value, root);
    // return root;
    // }

    public static Bucket deleteTree(Bucket root, String key, int value) {
        int saveVal; // var to save a value
        Bucket newDelNode; // intermediate node that helps in deleting
        Bucket delNode = searchTree(root, key, value); // search and check if the node we want to delete it is in the
                                                       // tree
        Bucket parent = parent(root, delNode); // find the parent node of the deleted node
        if (isLeaf(delNode)) { // if the deleted node is leaf
            if (parent == null) // if it has no parent, therefore it does not exits
                return null;
            if (value < parent.value) // if it has a parent, and the value of it is less than the parent's value
                parent.left = null; // delete it by pointing the parent's left branch to null
            else // if the value is greater than the parent's value.
                parent.right = null; // point the right branch to null
            return root;
        }
        if (hasOnlyLeftChild(delNode)) {
            if (parent == null)
                return delNode.left;
            if (value < parent.value)
                parent.left = parent.left.left;
            else
                parent.right = parent.right.left;
            return root;
        }
        if (hasOnlyRightChild(delNode)) {
            if (parent == null)
                return delNode.right;
            if (value < parent.value)
                parent.left = parent.left.right;
            else
                parent.right = parent.right.right;
            return root;
        }
        newDelNode = minVal(delNode.right);
        saveVal = newDelNode.value;
        deleteTree(root, key, saveVal);
        delNode.value = saveVal;
        return root;
    }

    // finds the parent of the node
    public static Bucket parent(Bucket root, Bucket node) {
        if (root == null || node == null)
            return null;
        if (root.left == node || root.right == node) // return the root if the node equals to the left or the right
                                                     // branch
            return root;
        if (node.value < root.value) // go left
            return parent(root.left, node);
        if (node.value > root.value) // go right
            return parent(root.right, node);
        else
            return null;
    }

    // return the smallest value in the tree
    private static Bucket minVal(Bucket root) {
        if (root.left == null)
            return root;
        else // we are checking only the left branch as it smaller than the right branch
            return minVal(root.left);
    }

    // check if the node is a leaf node (left and right are nulls)
    private static boolean isLeaf(Bucket node) {
        if (node == null)
            return true;
        return (node.left == null && node.right == null);
    }

    // return true if the node has only one left child (right is null, left is
    // not null)
    private static boolean hasOnlyLeftChild(Bucket node) {
        return (node.left != null && node.right == null);
    }

    // return true if the node has only one right child (right is not null, left is
    // null)
    private static boolean hasOnlyRightChild(Bucket node) {
        return (node.left == null && node.right != null);
    }

    // printing preorder for Testing
    public static void printTree(Bucket root) {
        if (root != null) {
            System.out.printf("(key:%s , value: %d)",root.key, root.value);
            printTree(root.left);   
            printTree(root.right);
        }
    }
    // counts how many nodes are there in a tree
    public static int count(Bucket root) {
        if (root == null)
            return 0;
        return 1 + count(root.left) + count(root.right);
    }
}
