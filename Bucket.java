/*Binary Search Tree using LazySynchronization from the lectures*/

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

// the bucket that will hold the value, the key, and the right and left pointers (aka: references)
// it will hold also an atomic boolean value for logical deletion.
public class Bucket {
    public String key;
    public int value;
    volatile Bucket left;
    volatile Bucket right;
    public AtomicBoolean isMarked;
    private Lock lock;
    static final int LIMIT = 8; // limit the number of nodes in a tree
    private AtomicInteger size;

    // constructor
    public Bucket(String key, int value) {
        this.size = new AtomicInteger(0); // check the count method to know how this variable is used
        this.key = key;
        this.value = value;
        this.left = null;
        this.right = null;
        this.isMarked = new AtomicBoolean(false);
        lock = new ReentrantLock();
    }

    /* converted the recursive functions to iterative one. */
    /* it was hard to work with concurrentency and recursibley at the same time. */
    /* delete still uses recursion!! */

    public static Bucket insertTree(String key, int value, Bucket root) {
        Bucket temp = new Bucket(key, value); // create a bucket with a the key and the value
        if (root == null) {// if root is null
            temp.size.getAndIncrement(); // increment the size of the root for every successfull insertion
            return temp; // return the new bucket to be the root
        }

        if (root.value == value && root.key.equals(key)) { // if the root's value equal the inserted value
            return root; // return the root and do nothing (do not allow dublicates).
        }
        Bucket current = root; // for traversing the tree
        Bucket parent = parent(root, current);
        // traverse the tree
        while (current.left != null || current.right != null) {
            parent = current;
            if (current.left == null)
                current = current.right;
            else if (current.right == null)
                current = current.left;
            if (current.value == value && current.key.equals(key))
                return root; // if the value exsits, return the root. Don't proceed further

        }
        // locking the parent and the child (current)
        try {
            if (parent != null) // check if it is null or not, we cannot apply a lock on a null object
                parent.lock();
            try {
                current.lock();
                try {
                    // abory if validation failed
                    if (!validate(parent, current)) {
                        return root;
                    }
                    // insert left or right depends on the value of the current node and the
                    // inserted node.
                    if (value > current.value) {
                        current.right = temp;
                    } else {
                        current.left = temp;
                    }
                    return root; // return the root at the end
                    // unlock current and parent
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
        if (root == null) // it is not there
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
                return current; // busted, all values match, and the current is not marked
        }
        return null; // other wise return null

    }

    // complicated!! try to read through it and ask me (Ahmed) if you have any
    // questions
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
                    // delete current logically
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
                        root.size.getAndDecrement(); // decrement the root size by one after each deletion
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
            if (parent != null)
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

    // printing preorder-inorder-postorder for Testing - uncomment only one print statement
    public static void printTree(Bucket root) {
        if (root != null) {
            // if (!root.isMarked.get())
            // System.out.printf("(key:%s , value: %d)",root.key, root.value);
            printTree(root.left);
            if (!root.isMarked.get()) // added this condition, so it will not print the logically deleted nodes
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
        return Integer.parseInt(root.size.toString()); // returns the size of the root only
        // easy to access the root to know the count than to traverse the whole tree
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
