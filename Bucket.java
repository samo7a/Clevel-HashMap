
/**
 * @author Ahmed Elshetany
 * @author Ankita Tripathi
 * @author Jacob Bostwick
 * @author Micah Renfrow
 * @author Quynh Nguyen
 */
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

/**
 * 
 */
public class Bucket {
    public String key;
    public int value;
    volatile Bucket left;
    volatile Bucket right;
    public AtomicBoolean isMarked;
    private Lock lock;
    static final int LIMIT = 8;
    private AtomicInteger size;

    /**
     * 
     * @param key
     * @param value
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
     * 
     * @param key
     * @param value
     * @param root
     * @return
     */
    public static Bucket insertTree(String key, int value, Bucket root) {
        Bucket temp = new Bucket(key, value);
        if (root == null) {
            temp.size.getAndIncrement();
            return temp;
        }
        if (root.value == value && root.key.equals(key)) {
            return root;
        }
        Bucket current = root;
        Bucket parent = parent(root, current);
        while (current.left != null || current.right != null) {
            parent = current;
            if (current.left == null)
                current = current.right;
            else if (current.right == null)
                current = current.left;
            if (current.value == value && current.key.equals(key))
                return root;
        }

        if (parent != null)
            parent.lock();
        try {
            current.lock();
            try {
                if (!validate(parent, current)) {
                    return root;
                }
                if (value > current.value) {
                    current.right = temp;
                    root.size.getAndIncrement();
                } else {
                    current.left = temp;
                    root.size.getAndIncrement();
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
     * 
     * @param root
     * @param key
     * @param value
     * @return
     */
    public static Bucket searchTree(Bucket root, String key, int value) {
        if (root == null)
            return null;
        if (root.value == value && root.key.equals(key))
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
    }

    /**
     * 
     * @param root
     * @param key
     * @param value
     * @return
     */
    public static Bucket deleteTree(Bucket root, String key, int value) {
        Bucket current = root;
        if (current == null)
            return null;
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
                    current.isMarked.set(true);

                    int saveVal;
                    Bucket newDelNode;
                    if (isLeaf(current)) {
                        if (parent == null)
                            return null;
                        if (value < parent.value)
                            parent.left = null;
                        else
                            parent.right = null;
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
            if (parent != null)
                parent.unlock();
        }
    }

    /**
     * 
     * @param root
     * @param node
     * @return
     */
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
        if (node == null)
            return true;
        return (node.left == null && node.right == null);
    }

    private static boolean hasOnlyLeftChild(Bucket node) {
        return (node.left != null && node.right == null);
    }

    private static boolean hasOnlyRightChild(Bucket node) {
        return (node.left == null && node.right != null);
    }

    /**
     * 
     * @param root
     */
    public static void preOrderPrint(Bucket root) {
        if (root != null) {
            if (!root.isMarked.get())
                System.out.printf("(key:%s , value: %d)", root.key, root.value);
            preOrderPrint(root.left);
            preOrderPrint(root.right);
        }
    }

    /**
     * 
     * @param root
     */
    public static void inOrderPrint(Bucket root) {
        if (root != null) {
            inOrderPrint(root.left);
            if (!root.isMarked.get())
                System.out.printf("(key:%s , value: %d)", root.key, root.value);
            inOrderPrint(root.right);
        }
    }

    /**
     * 
     * @param root
     */
    public static void postOrderPrint(Bucket root) {
        if (root != null) {
            postOrderPrint(root.left);
            postOrderPrint(root.right);
            if (!root.isMarked.get())
                System.out.printf("(key:%s , value: %d)", root.key, root.value);
        }
    }

    /**
     * 
     * @param root
     * @return
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

    private static boolean validate(Bucket parent, Bucket current) {
        if (parent != null)
            return ((!current.isMarked.get() && !parent.isMarked.get())
                    && (current == parent.left || current == parent.right));
        else
            return true;
    }
}
