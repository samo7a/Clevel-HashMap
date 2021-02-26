// the bucket that will hold the value, the key, and the right and left pointers (aka: references)
public class Bucket {
    public int key;
    public int value;
    Bucket left;
    Bucket right;
    final static int LIMIT = 8;

    // constructors
    public Bucket(int key, int value) {
        this.key = key;
        this.value = value;
        left = null;
        right = null;
    }

    public static Bucket insertTree(int key, int value, Bucket root) {
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

    public static Bucket searchTree(Bucket root, int value, int key) {
        if (root != null) { // if the root exists
            if (root.value == value && root.key == key) // if the root's value equals the searched item's value, return
                                                        // the root
                return root;
            if (root.value > value) // if the root's value is larger than the searched item's value, search the left
                                    // node
                return searchTree(root.left, value, key);
            else
                return searchTree(root.right, value, key); // if the root's value is smaller than the searched item's
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

    public static Bucket deleteTree(Bucket root, int value, int key) {
        int saveVal;
        Bucket newDelNode;
        Bucket delNode = searchTree(root, value, key);
        Bucket parent = parent(root, delNode);
        if (isLeaf(delNode)) {
            if (parent == null)
                return root;
            if (value < parent.value)
                parent.left = null;
            else
                parent.right = null;
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
        deleteTree(root, saveVal, key);
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

    // printing inorder for Testing
    public static void printTree(Bucket root) {
        if (root != null) {
            printTree(root.left);
            System.out.printf("%d ", root.value);
            printTree(root.right);
        }
    }

    public static int count(Bucket root) {
        if (root == null)
            return 0;
        return 1 + count(root.left) + count(root.right);
    }
}
