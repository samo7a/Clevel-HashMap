
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

/**
 * 
 */
public class ClevelHashTable implements Runnable {
    AtomicBoolean isResizing;
    public Bucket[] topLevel;
    public Bucket[] bottomLevel;
    public Bucket[] newLevel;
    int bottomSize;
    int topSize;
    int newLevelSize;
    ReentrantLock lock = new ReentrantLock();

    /**
     * 
     */
    public ClevelHashTable() {
        bottomSize = 4;
        topSize = bottomSize * 2;
        newLevelSize = topSize * 2;
        isResizing = new AtomicBoolean(false);
        topLevel = new Bucket[topSize];
        bottomLevel = new Bucket[bottomSize];
        newLevel = new Bucket[newLevelSize];
    }

    /**
     * 
     * @param key
     * @param value
     * @return
     */
    public boolean insert(String key, Integer value) {
        int[] keys = this.hash(key, value);
        if (isResizing.get()) {
            return insertRehash(key, value);
        } else {
            if (Bucket.count(this.topLevel[keys[0]]) < 8) {
                this.topLevel[keys[0]] = Bucket.insertTree(key, value, this.topLevel[keys[0]]);
                return true;
            } else if (Bucket.count(this.topLevel[keys[1]]) < 8) {
                this.topLevel[keys[1]] = Bucket.insertTree(key, value, this.topLevel[keys[1]]);
                return true;
            } else if (Bucket.count(this.bottomLevel[keys[2]]) < 8) {
                this.bottomLevel[keys[2]] = Bucket.insertTree(key, value, this.bottomLevel[keys[2]]);
                return true;
            } else if (Bucket.count(this.bottomLevel[keys[3]]) < 8) {
                this.bottomLevel[keys[3]] = Bucket.insertTree(key, value, this.bottomLevel[keys[3]]);
                return true;
            } else {
                this.isResizing.set(true);
                Thread resize = new Thread(this);
                this.bottomSize *= 2;
                this.topSize *= 2;
                resize.start();
                return this.insert(key, value);
            }
        }
    }

    /**
     * 
     * @param key
     * @param value
     * @return
     */
    public int search(String key, Integer value) {
        int[] keys = this.hash(key, value);
        if (Bucket.searchTree(this.bottomLevel[keys[2]], key, value) != null)
            return keys[2];
        if (Bucket.searchTree(this.bottomLevel[keys[3]], key, value) != null)
            return keys[3];
        if (Bucket.searchTree(this.topLevel[keys[0]], key, value) != null)
            return keys[0];
        if (Bucket.searchTree(this.topLevel[keys[1]], key, value) != null)
            return keys[1];
        return -1;
    }

    /**
     * 
     * @param key
     * @param value
     */
    public void delete(String key, Integer value) {
        int[] keys = this.hash(key, value);
        int index = this.search(key, value);
        boolean topOrBottom = true;
        if (index == -1)
            return;
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == index) {
                if (i <= 1)
                    topOrBottom = true;
                else
                    topOrBottom = false;
                break;
            }
        }
        if (topOrBottom) {
            this.topLevel[index] = Bucket.deleteTree(this.topLevel[index], key, value);
            return;
        }
        this.bottomLevel[index] = Bucket.deleteTree(this.bottomLevel[index], key, value);
        return;

    }

    /**
     * 
     */
    public void resize() {
        for (Bucket root : bottomLevel) {
            reHashNode(root, this.newLevel);
        }
        this.bottomLevel = this.topLevel;
        this.topLevel = this.newLevel;
        this.newLevel = new Bucket[topSize * 2];
        this.isResizing.set(false);
    }

    /**
     * 
     * @param root
     * @param newLevel
     */
    public void reHashNode(Bucket root, Bucket[] newLevel) {
        if (root == null)
            return;
        insertRehash(root.key, root.value);
        reHashNode(root.left, newLevel);
        reHashNode(root.right, newLevel);
    }

    /**
     * 
     * @param key
     * @param value
     * @return
     */
    public boolean insertRehash(String key, Integer value) {
        int[] keys = this.hash(key, value);
        if (Bucket.count(newLevel[keys[0]]) < 8) {
            newLevel[keys[0]] = Bucket.insertTree(key, value, newLevel[keys[0]]);
            return true;
        } else if (Bucket.count(newLevel[keys[1]]) < 8) {
            newLevel[keys[1]] = Bucket.insertTree(key, value, newLevel[keys[1]]);
            return true;
        } else
            return false;
    }

    /**
     * 
     * @param key
     * @param value
     * @return
     */
    public int[] hash(String key, Integer value) {
        int[] keys = new int[4];
        int num = key.hashCode() + value.hashCode();
        if (num < 0)
            num *= -1;

        keys[0] = num % (topSize);
        if (keys[0] >= topSize / 2)
            keys[1] = keys[0] - topSize / 2;
        else
            keys[1] = keys[0] + topSize / 2;

        keys[2] = num % bottomSize;
        if (keys[2] >= bottomSize / 2)
            keys[3] = keys[2] - bottomSize / 2;
        else
            keys[3] = keys[2] + bottomSize / 2;
        return keys;
    }

    /**
     * 
     */
    public void printTable() {
        System.out.println("Top Array:");
        for (int i = 0; i < topSize; i++) {
            System.out.print("    Tree no. " + i + ": {");
            Bucket.inOrderPrint(this.topLevel[i]);
            System.out.println("}");
        }
        System.out.println();
        System.out.println("Bottom Array:");
        for (int i = 0; i < bottomSize; i++) {
            System.out.print("    Tree no. " + i + ": {");
            Bucket.inOrderPrint(this.bottomLevel[i]);
            System.out.println("}");
        }
    }

    @Override
    public void run() {
        this.resize();
    }
}
