// the bucket that will hold the value and the key
public class Bucket {
    int key;
    int value;
    Bucket left;
    Bucket right;

    // constructors
    public Bucket(int key, int value) {
        this.key = key;
        this.value = value;
        left = null;
        right = null;
    }

    public static Bucket insertTree(int key, int value, Bucket root) {
        Bucket temp = new Bucket(key, value);
        if (root == null)
            return temp;
        if (value < root.value) {
            if (root.left != null)
                root.left = insertTree(key, value, root.left);
            else
                root.left = temp;
        } else {
            if (root.right != null)
                root.right = insertTree(key, value, root.right);
            else
                root.right = temp;
        }
        return root;
    }

    public static Bucket searchTree(Bucket root, int value) {
        if (root != null) {
            if (root.value == value)
                return root;
            if (root.value > value)
                return searchTree(root.left, value);
            else
                return searchTree(root.right, value);
        } else
            return null;
    }

    public Bucket updateTree(int key, int value, Bucket root) {
        Bucket isFound = searchTree(root, value);
        if (isFound != null)
            isFound.value = value;
        else
            root = insertTree(key, value, root);
        return root;
    }

    public static Bucket deleteTree(Bucket root, int value) {
        int saveVal;
        Bucket newDelNode;
        Bucket delNode = searchTree(root, value);
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
        deleteTree(root, saveVal);
        delNode.value = saveVal;
        return root;
    }

    public static Bucket parent(Bucket root, Bucket node) {
        if (root == null || node == null)
            return null;
        if (root.left == node || root.right == node)
            return root;
        if (node.value < root.value)
            return parent(root.left, node);
        if (node.value > root.value)
            return parent(root.right, node);
        else
            return null;
    }

    private static Bucket minVal(Bucket root) {
        if (root.left == null)
            return root;
        else
            return minVal(root.left);
    }

    private static boolean isLeaf(Bucket node) {
        if (node == null) return true;
        return (node.left == null && node.right == null);
    }

    private static boolean hasOnlyLeftChild(Bucket node) {
        return (node.left != null && node.right == null);
    }

    private static boolean hasOnlyRightChild(Bucket node) {
        return (node.left == null && node.right != null);
    }

    // for Testing
    public static void printTree(Bucket root) {
        if (root != null) {
            printTree(root.left);
            System.out.printf("%d ", root.value);
            printTree(root.right);
        }
    }
}
