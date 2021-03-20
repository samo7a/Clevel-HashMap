
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
 * Unique implementation of  a Clevel hash table
 * The hash table has 2 levels, when doing the basic operations (search, delete and insert) the hash function 
 * will provide us with 4 locations to insert in. If none of these locations are empty, a resize opreation will be
 * performed and the thread will try to re-insert agian.
 * This data structure is thread safe; multiple threads could perform their basic crud operations at the same time.
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
     * Constructor to create an empty hash table
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
     * Inserting an item to the hashtable
     * @param key The key of the item 
     * @param value The value you want to insert
     * @return false if the insert operation failed or true if the insert was successful
     */
    public boolean insert(String key, Integer value) {
        int[] keys = this.hash(key);
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
                this.newLevelSize *= 2;
                resize.start();
                return this.insert(key, value);
            }
        }
    }

    /**
     * Search in the hashtable if that key-value pair exist
     * @param key The key of the item
     * @param value The value associated with it
     * @return the index of that key-value pair or -1 if not found
     */
    public int search(String key) {
        if (this.isResizing.get()){
            int[] keys = this.hash(key);
            Bucket current = Bucket.searchTree(this.topLevel[keys[2]], key) ;
            if (current != null)
                return current.value;
            current = Bucket.searchTree(this.topLevel[keys[3]], key);
            if (current != null)
                return current.value;
            while (this.isResizing.get());
            keys = this.hash(key);
            current = Bucket.searchTree(this.topLevel[keys[2]], key) ;
            if (current != null)
                return current.value;
            current = Bucket.searchTree(this.topLevel[keys[3]], key);
            if (current != null)
                return current.value;
            return -1;
        } else {
            int[] keys = this.hash(key);
            Bucket current = Bucket.searchTree(this.bottomLevel[keys[2]], key);
            if (current != null)
                return current.value;
            current = Bucket.searchTree(this.bottomLevel[keys[3]], key);
            if (current != null)
                return current.value;
            current = Bucket.searchTree(this.topLevel[keys[0]], key) ;
            if (current != null)
                return current.value;
            current = Bucket.searchTree(this.topLevel[keys[1]], key);
            if (current != null)
                return current.value;
            return -1;
        }
    }

    public int getIndex(String key) {
        int[] keys = this.hash(key); // another hash
        // search bottom level (bottom up search)
        if (Bucket.searchTree(this.bottomLevel[keys[2]], key) != null)
            return keys[2];
        if (Bucket.searchTree(this.bottomLevel[keys[3]], key) != null)
            return keys[3];
        if (Bucket.searchTree(this.topLevel[keys[0]], key) != null)
            return keys[0];
        if (Bucket.searchTree(this.topLevel[keys[1]], key) != null)
            return keys[1];
        return -1; // returns -1 if not found
    }
    /**
     * Delete a key-value pair from the hashtable
     * @param key The key of the item that is to be deleted
     * @param value The value associated with the key
     */
    public void delete(String key) {
        if (!this.isResizing.get()){
            int[] keys = this.hash(key);
        int index = this.getIndex(key);
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
            this.topLevel[index] = Bucket.deleteTree(this.topLevel[index], key);
            return;
        }
        this.bottomLevel[index] = Bucket.deleteTree(this.bottomLevel[index], key);
        return;
        }
        else {
            // TODO: concurrent delete

        }
    }

    // resize the hashtable
    private void resize() {
        for (Bucket root : bottomLevel) {
            reHashNode(root, this.newLevel);
        }
        this.bottomLevel = this.topLevel;
        this.topLevel = this.newLevel;
        this.newLevel = new Bucket[newLevelSize];
        this.isResizing.set(false);
    }

    // rehash all the nodes in that root to the new level
    private void reHashNode(Bucket root, Bucket[] newLevel) {
        if (root == null)
            return;
        insertRehash(root.key, root.value);
        reHashNode(root.left, newLevel);
        reHashNode(root.right, newLevel);
    }

   // Rehash and insert the key-value pair into the new level while the hashtable is resizing
    private boolean insertRehash(String key, Integer value) {
        int[] keys = this.hash(key);
        if (Bucket.count(newLevel[keys[0]]) < 8) {
            newLevel[keys[0]] = Bucket.insertTree(key, value, newLevel[keys[0]]);
            return true;
        } else if (Bucket.count(newLevel[keys[1]]) < 8) {
            newLevel[keys[1]] = Bucket.insertTree(key, value, newLevel[keys[1]]);
            return true;
        } else
            return false;
    }

    // Hashing function
    private int[] hash(String key) {
        int[] keys = new int[4];
        int num = key.hashCode();// + value.hashCode();
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
     * Prints the hash table
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
        System.out.println();
    }

    @Override
    public void run() {
        this.resize();
    }
}
