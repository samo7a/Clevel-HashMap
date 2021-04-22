/**
 * @author Ahmed Elshetany
 * @author Ankita Tripathi
 * @author Jacob Bostwick
 * @author Micah Renfrow
 * @author Quynh Nguyen
 * @version 1.0
 */
import java.util.concurrent.locks.ReentrantLock;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

/**
 * Represents a location in the hashtable in the form of BST
 */
public class Bucket {
    public String key;
    public int value;
    volatile Bucket left;
    volatile Bucket right;
    public AtomicBoolean isMarked;
    private Lock lock;
    private AtomicInteger size;
    static final int LIMIT = 8;

    /**
     * Constructs an empty node for the BST
     * 
     * @param key   The key associated with inserted value
     * @param value Integer Value to be inserted
     */
    public Bucket(String key, int value) {
        this.size = new AtomicInteger(0);
        this.key = key;
        this.value = value;
        this.left = null;
        this.right = null;
        this.isMarked = new AtomicBoolean(false);
        lock = new ReentrantLock();
    }

    /**
     * Insert a new node into the current tree
     * 
     * @param key   The key associated with inserted value
     * @param value Integer Value to be inserted
     * @param root  The root of the tree to insert in
     * @return The root Bucket of the same tree
     */
    public static Bucket insertTree(String key, int value, Bucket root) {
        Bucket temp = new Bucket(key, value);
        if (root == null) {
            temp.size.getAndIncrement();
            return temp;
        }
        if (root.key.equals(key)) {
            // Micah: not sure what to return here but added check for isMarked so we can set it to true
            if (root.isMarked.get())
            {
                root.isMarked.set(false);
                return root;
            }
            return root;
        }
        Bucket current = root;
        Bucket parent = parent(root, current);
        while (current != null) {
            parent = current;
            if (key.compareTo(current.key) < 0)
                current = current.left;
            else if (key.compareTo(current.key) > 0)
                current = current.right;
            if (current != null && current.key.equals(key))
            {
                // Micah: not sure what to return ehre but added check for isMarked so we can set it to true
                if (root.isMarked.get())
                {
                    root.isMarked.set(false);
                    return root;
                }

                return root;
            }
        }
        Bucket grandParent = parent(root, parent);
        if (grandParent != null)
            grandParent.lock();
        try {
            parent.lock();
            try {
                if (!validate(grandParent, parent)) {
                    return root;
                }
                if (key.compareTo(parent.key) > 0) {
                    parent.right = temp;
                    root.size.getAndIncrement();
                } else {
                    parent.left = temp;
                    root.size.getAndIncrement();
                }
                return root;
            } finally {
                parent.unlock();
            }
        } finally {
            if (grandParent != null)
                grandParent.unlock();
        }
    }

    /**
     * Search the tree for the given key
     * 
     * @param key  The key needed to be search
     * @param root The root of the tree
     * @return Null if not found or the bucket with the given key
     */
    public static Bucket searchTree(Bucket root, String key) {
        if (root == null)
            return null;
        if (root.key.equals(key))
            return root;
        Bucket current = root;
        while (current != null) {
            if (key.compareTo(current.key) < 0)
                current = current.left;
            else if (key.compareTo(current.key) > 0)
                current = current.right;
            if (current != null) {
                // Micah: added a check for isMarked since it would normally keep looping otherwise
                if (current.key.equals(key) && !current.isMarked.get())
                    return current;
                else if (current.key.equals(key) && current.isMarked.get())
                    return null;
            }
        }
        return null;
    }

    /**
     * Delete a given key-value pair from the tree
     * 
     * @param root The root of the tree
     * @param key  The key that is to be deleted
     * @return The root of the tree from which the key-value pair is deleted
     */
    public static Bucket deleteTree(Bucket root, String key) {
        // For logical deletion the following variables are not required: savKey, savVal, newDelNode.
        // String savKey;
        // int savVal;
        // Bucket newDelNode;

        if (root == null)
            return root;
        Bucket current = searchTree(root, key);
        if (current == null)
            return root;
        Bucket parent = parent(root, current);
        if (parent != null)
            parent.lock();

        try {
            current.lock();
            try {

                if (!validate(parent, current)) {
                    return root;
                }
                if (current.key.equals(key)) {
                    current.isMarked.set(true);
                    if (isLeaf(current)) {
                        if (parent == null)
                            return null;
                        if (key.compareTo(parent.key) < 0)
                            parent.left = null;
                        else
                            parent.right = null;
                        root.size.getAndDecrement();
                        return root;
                    }
                    if (hasOnlyLeftChild(current)) {
                        if (parent == null)
                            return current.left;
                        if (key.compareTo(parent.key) < 0)
                            parent.left = parent.left.left;
                        else
                            parent.right = parent.right.left;
                        root.size.getAndDecrement();
                        return root;
                    }
                    if (hasOnlyRightChild(current)) {
                        if (parent == null)
                            return current.right;
                        if (key.compareTo(parent.key) < 0)
                            parent.left = parent.left.right;
                        else
                            parent.right = parent.right.right;
                        root.size.getAndDecrement();
                        return root;
                    }
                    // The following else block is used for logical deletion.
                    // For physical deletion replace the else block with lines 209 - 217.
                    else {
                        root.size.getAndDecrement();
                        return root;
                    }
                    // newDelNode = minVal(current.right);
                    // savKey = newDelNode.key;
                    // savVal = newDelNode.value;
                    // current.isMarked.set(false);
                    // deleteTree(root, savKey);
                    // current.key = savKey;
                    // current.value = savVal;
                    // root.size.getAndDecrement();
                    // return root;
                }
                return root;
            } finally {
                current.unlock();
            }
        } finally {
            if (parent != null)
                parent.unlock();
        }
    }

    /**
     * Find the parent of a node
     * 
     * @param root The root of the tree
     * @param node The current node
     * @return The parent of a current node
     */
    public static Bucket parent(Bucket root, Bucket node) {
        if (root == null || node == null)
            return null;
        if (root.left == node || root.right == node)
            return root;
        if (node.key.compareTo(root.key) < 0)
            return parent(root.left, node);
        if (node.key.compareTo(root.key) > 0)
            return parent(root.right, node);
        else
            return null;
    }

    // Helper function to find the minimum value in the tree
    private static Bucket minVal(Bucket root) {
        if (root.left == null)
            return root;
        else
            return minVal(root.left);
    }

    // Helper function to check if the node doens't have any child
    private static boolean isLeaf(Bucket node) {
        if (node == null)
            return true;
        return (node.left == null && node.right == null);
    }

    // Helper function to check if a node only has a left child
    private static boolean hasOnlyLeftChild(Bucket node) {
        return (node.left != null && node.right == null);
    }

    // Helper function to check if the node only has right child
    private static boolean hasOnlyRightChild(Bucket node) {
        return (node.left == null && node.right != null);
    }

    /**
     * Print the tree in pre-order format
     * 
     * @param root The root of the tree
     */
    public static void preOrderPrint(Bucket root) {
        if (root != null) {
            if (!root.isMarked.get())
                System.out.printf("(%s,%d)", root.key, root.value);
            preOrderPrint(root.left);
            preOrderPrint(root.right);
        }
    }

    /**
     * Print the Tree in in-order format
     * 
     * @param root The root of the tree
     */
    public static void inOrderPrint(Bucket root) {
        if (root != null) {
            inOrderPrint(root.left);
            if (!root.isMarked.get())
                System.out.printf("(%s,%d)", root.key, root.value);
            inOrderPrint(root.right);
        }
    }

    /**
     * Print the tree in post-order format
     * 
     * @param root The root of the tree
     */
    public static void postOrderPrint(Bucket root) {
        if (root != null) {
            postOrderPrint(root.left);
            postOrderPrint(root.right);
            if (!root.isMarked.get())
                System.out.printf("(%s,%d)", root.key, root.value);
        }
    }

    /**
     * Find the number of elements in a tree
     * 
     * @param root The root of the tree
     * @return The number of elements in a tree
     */
    public static int count(Bucket root) {
        if (root == null)
            return 0;
        return Integer.parseInt(root.size.toString());
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    // Validates if the parent points to the current, and both are not marked as
    // logically deleted
    private static boolean validate(Bucket parent, Bucket current) {
        if (parent != null)
            return ((!current.isMarked.get() && !parent.isMarked.get())
                    && (current == parent.left || current == parent.right));
        else
            return (!current.isMarked.get());
    }
}