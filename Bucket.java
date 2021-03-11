/*Binary Search Tree using LazySynchronization from the lectures*/

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

// the bucket that will hold the value, the key, and the right and left pointers (aka: references)
public class Bucket {
    public String key;
    public int value;
    volatile Bucket left;
    volatile Bucket right;
    public AtomicBoolean isMarked;
    private Lock lock;
    static final int LIMIT = 8; // limit the number of nodes in a tree
    private AtomicInteger size;

    // constructors
    public Bucket(String key, int value) {
        this.size = new AtomicInteger(0);
        this.key = key;
        this.value = value;
        this.left = null;
        this.right = null;
        this.isMarked = new AtomicBoolean(false);
        lock = new ReentrantLock();
    }

    // converted the recursive function to iterative one.
    // it was hard to work with concurrentency and recursibley at the same time.
    public static Bucket insertTree(String key, int value, Bucket root) {
        Bucket temp = new Bucket(key, value); // create a bucket with a the key and the value
        if (root == null) {// if root is null
            temp.size.getAndIncrement();
            return temp; // return the new bucket to be the root
        }

        if (root.value == value) { // if the root's value equal the inserted value
            return root; // return the root and do nothing (do not allow dublicates).
        }
        Bucket current = root;
        Bucket parent = parent(root, current);

        while (current.left != null || current.right != null) {
            parent = current;
            if (current.left == null)
                current = current.right;
            else if (current.right == null)
                current = current.left;
            if (current.value == value)
                return root;

        }

        try {
            if (parent != null)
                parent.lock();
            try {
                current.lock();
                try {
                    // validate that all locked nodes are still part of the tree and unchanged.
                    if (!validate(parent, current)) {
                        return root;
                    }

                    // create new internal node and attach new leaf and current to it
                    if (value > current.value) {
                        current.right = temp;
                    } else {
                        current.left = temp;
                    }
                    return root;

                } finally {
                    current.unlock();
                }
            } finally {
                if (parent != null)
                    parent.unlock();
            }
        } finally {
            // increment size by one
            root.size.getAndIncrement();
        }
    }

    public static Bucket searchTree(Bucket root, String key, int value) {
        if (root == null)
            return null;
        if (root.value == value && root.key.equals(key)) // if the root's value equals the searched item's value,
                                                         // return root
            return root;

        Bucket current = root;
        Bucket parent = parent(root, current);

        while (current.left != null || current.right != null) {
            parent = current;
            if (current.left == null)
                current = current.right;
            else if (current.right == null)
                current = current.left;
            if (current.key.equals(key) && current.value == value && !current.isMarked.get())
                return current;
        }
        return null;
        // if (current.value == value && !current.isMarked)
        // return current;
        // else
        // return null;

    }

    // public static Bucket deleteTree(Bucket root, String key, int value) {
    // int saveVal; // var to save a value
    // Bucket newDelNode; // intermediate node that helps in deleting
    // Bucket delNode = searchTree(root, key, value); // search and check if the
    // node we want to delete it is in the
    // // tree
    // Bucket parent = parent(root, delNode); // find the parent node of the deleted
    // node
    // if (isLeaf(delNode)) { // if the deleted node is leaf
    // if (parent == null) // if it has no parent, therefore it does not exits
    // return null;
    // if (value < parent.value) // if it has a parent, and the value of it is less
    // than the parent's value
    // parent.left = null; // delete it by pointing the parent's left branch to null
    // else // if the value is greater than the parent's value.
    // parent.right = null; // point the right branch to null
    // return root;
    // }
    // if (hasOnlyLeftChild(delNode)) {
    // if (parent == null)
    // return delNode.left;
    // if (value < parent.value)
    // parent.left = parent.left.left;
    // else
    // parent.right = parent.right.left;
    // return root;
    // }
    // if (hasOnlyRightChild(delNode)) {
    // if (parent == null)
    // return delNode.right;
    // if (value < parent.value)
    // parent.left = parent.left.right;
    // else
    // parent.right = parent.right.right;
    // return root;
    // }
    // newDelNode = minVal(delNode.right);
    // saveVal = newDelNode.value;
    // deleteTree(root, key, saveVal);
    // delNode.value = saveVal;
    // return root;
    // }

    public static Bucket deleteTree(Bucket root, String key, int value) {
        Bucket current = root;
        Bucket parent = parent(root, current);

        while (current.left != null || current.right != null) {
            parent = current;
            if (current.left == null)
                current = current.right;
            else if (current.right == null)
                current = current.left;
            if (current.key.equals(key) && current.value == value)
                break;
        }

        if (parent == null) {
            if (current.key.equals(key) && current.value == value)
                return null;
        } else
            parent.lock();

        try {
            current.lock();
            try {

                if (!validate(parent, current)) {
                    return root;
                }

                if (current.key.equals(key) && current.value == value) {
                    // first logically delete
                    current.isMarked.set(true);

                    int saveVal; // var to save a value
                    Bucket newDelNode; // intermediate node that helps in deleting
                    if (isLeaf(current)) { // if the deleted node is leaf
                        if (parent == null) // if it has no parent, therefore it does not exits
                            return null;
                        if (value < parent.value) // if it has a parent, and the value of it is less than the parent's
                                                  // value
                            parent.left = null; // delete it by pointing the parent's left branch to null
                        else // if the value is greater than the parent's value.
                            parent.right = null; // point the right branch to null
                        root.size.getAndDecrement();
                        return root;
                    }
                    if (hasOnlyLeftChild(current)) {
                        if (parent == null)
                            return current.left;
                        if (value < parent.value)
                            parent.left = parent.left.left;
                        else
                            parent.right = parent.right.left;
                        root.size.getAndDecrement();
                        return root;
                    }
                    if (hasOnlyRightChild(current)) {
                        if (parent == null)
                            return current.right;
                        if (value < parent.value)
                            parent.left = parent.left.right;
                        else
                            parent.right = parent.right.right;
                        root.size.getAndDecrement();
                        return root;
                    }
                    newDelNode = minVal(current.right);
                    saveVal = newDelNode.value;
                    deleteTree(root, key, saveVal);
                    current.value = saveVal;
                    root.size.getAndDecrement();
                    return root;
                }
                return root;
            } finally {
                current.unlock();
            }
        } finally {
            parent.unlock();
        }
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
            // if (!root.isMarked.get())
            // System.out.printf("(key:%s , value: %d)",root.key, root.value);
            printTree(root.left);
            if (!root.isMarked.get())
                System.out.printf("(key:%s , value: %d)", root.key, root.value);
            printTree(root.right);
            // if (!root.isMarked.get())
            // System.out.printf("(key:%s , value: %d)",root.key, root.value);
        }
    }

    // counts how many nodes are there in a tree
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

    private static boolean validate(Bucket parent, Bucket current) {
        // both parent and current are not marked && parent is pointing to the child
        if (parent != null)
            return ((!current.isMarked.get() && !parent.isMarked.get())
                    && (current == parent.left || current == parent.right));
        else
            return true;
    }
}
