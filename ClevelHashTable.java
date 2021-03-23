
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
import java.util.concurrent.atomic.AtomicReference;

/**
 * Unique implementation of a Clevel hash table The hash table has 2 levels,
 * when doing the basic operations (search, delete and insert) the hash function
 * will provide us with 4 locations to insert in. If none of these locations are
 * empty, a resize opreation will be performed and the thread will try to
 * re-insert agian. This data structure is thread safe; multiple threads could
 * perform their basic crud operations at the same time.
 *
 */

public class ClevelHashTable implements Runnable {
    AtomicBoolean isResizing;
    AtomicReference<Bucket[]> newLevel;
    AtomicReference<Bucket[]> topLevel;
    AtomicReference<Bucket[]> bottomLevel;
    AtomicInteger newSize;
    AtomicInteger topSize;
    AtomicInteger bottomSize;
    ReentrantLock lock = new ReentrantLock();

    /**
     * Constructor to create an empty hash table
     */
    public ClevelHashTable() {
        newSize = new AtomicInteger(16);
        topSize = new AtomicInteger(8);
        bottomSize = new AtomicInteger(4);
        isResizing = new AtomicBoolean(false);
        newLevel = new AtomicReference<>(new Bucket[newSize.get()]);
        topLevel = new AtomicReference<>(new Bucket[topSize.get()]);
        bottomLevel = new AtomicReference<>(new Bucket[bottomSize.get()]);
    }

    /**
     * Inserting an item to the hashtable
     * 
     * @param key   The key of the item
     * @param value The value you want to insert
     * @return false if the insert operation failed or true if the insert was
     *         successful
     */
    public boolean insert(String key, Integer value) {
        if (search(key) >= 0)
            return false;
        if (!isResizing.get()) {
            AtomicReference<Bucket[]> local = this.newLevel;
            int[] keys = this.hash(key);
            // if (local.compareAndSet(local.get(), this.newLevel.get()))

            if (Bucket.count(this.topLevel.get()[keys[2]]) < 8) {
                if (local.compareAndSet(this.newLevel.get(), this.newLevel.get())) {
                    this.topLevel.get()[keys[2]] = Bucket.insertTree(key, value, this.topLevel.get()[keys[2]]);
                    return true;
                }
                return insert(key, value);
            } else if (Bucket.count(this.topLevel.get()[keys[3]]) < 8) {
                if (local.compareAndSet(this.newLevel.get(), this.newLevel.get())) {
                    this.topLevel.get()[keys[3]] = Bucket.insertTree(key, value, this.topLevel.get()[keys[3]]);
                    return true;
                }
                return insert(key, value);
            } else if (Bucket.count(this.bottomLevel.get()[keys[4]]) < 8) {
                if (local.compareAndSet(this.newLevel.get(), this.newLevel.get())) {
                    this.bottomLevel.get()[keys[4]] = Bucket.insertTree(key, value, this.bottomLevel.get()[keys[4]]);
                    return true;
                }
                return insert(key, value);
            } else if (Bucket.count(this.bottomLevel.get()[keys[5]]) < 8) {
                if (local.compareAndSet(this.newLevel.get(), this.newLevel.get())) {
                    this.bottomLevel.get()[keys[5]] = Bucket.insertTree(key, value, this.bottomLevel.get()[keys[5]]);
                    return true;
                }
                return insert(key, value);
            } else {
                if (!this.isResizing.get()) {
                    this.isResizing.set(true);
                    Thread resize = new Thread(this);
                    resize.start();
                    return this.insert(key, value);
                } else
                    return this.insert(key, value);

            }

        } else {
            AtomicReference<Bucket[]> local = this.newLevel;
            int[] keys = this.hash(key);
            // if (local.compareAndSet(local.get(), this.newLevel.get()))

            if (Bucket.count(this.newLevel.get()[keys[0]]) < 8) {
                if (local.compareAndSet(this.newLevel.get(), this.newLevel.get())) {
                    this.newLevel.get()[keys[0]] = Bucket.insertTree(key, value, this.newLevel.get()[keys[0]]);
                    return true;
                }
                return insert(key, value);
            } else if (Bucket.count(this.newLevel.get()[keys[1]]) < 8) {
                if (local.compareAndSet(this.newLevel.get(), this.newLevel.get())) {
                    this.newLevel.get()[keys[1]] = Bucket.insertTree(key, value, this.newLevel.get()[keys[1]]);
                    return true;
                }
                return insert(key, value);
            }

            return false;
        }
    }

    // Rehash and insert the key-value pair into the new level while the hashtable
    // is resizing
    private boolean insertRehash(String key, Integer value) {
        AtomicReference<Bucket[]> local = this.newLevel;
        int[] keys = this.hash(key);
        if (Bucket.count(newLevel.get()[keys[0]]) < 8) {
            if (local.compareAndSet(this.newLevel.get(), this.newLevel.get())) {
                newLevel.get()[keys[0]] = Bucket.insertTree(key, value, newLevel.get()[keys[0]]);
                return true;
            }
            // return this.insert(key, value);
        } else if (Bucket.count(newLevel.get()[keys[1]]) < 8) {
            if (local.compareAndSet(this.newLevel.get(), this.newLevel.get())) {
                newLevel.get()[keys[1]] = Bucket.insertTree(key, value, newLevel.get()[keys[1]]);
                return true;
            }
            // return this.insert(key, value);
        }
        return false;

    }

    // resize the hashtable
    private void resize() {
        this.isResizing.set(true);
        System.out.println("***************Resizing****************");
        for (Bucket root : this.bottomLevel.get()) {
            reHashNode(root, this.newLevel.get());
        }
        this.isResizing.set(false);
        this.newSize.set(this.newSize.get() * 2);
        this.topSize.set(this.topSize.get() * 2);
        this.bottomSize.set(this.bottomSize.get() * 2);

        this.bottomLevel.set(this.topLevel.get());
        this.topLevel.set(this.newLevel.get());
        this.newLevel = new AtomicReference<>(new Bucket[newSize.get()]);

    }

    // rehash all the nodes in that root to the new level
    private void reHashNode(Bucket root, Bucket[] newLevel) {
        if (root == null)
            return;
        insertRehash(root.key, root.value);
        reHashNode(root.left, newLevel);
        reHashNode(root.right, newLevel);
    }

    /**
     * Search in the hashtable if that key-value pair exist
     * 
     * @param key   The key of the item
     * @param value The value associated with it
     * @return the index of that key-value pair or -1 if not found
     */
    public int search(String key) {
        int newSizeTemp = this.newSize.get();
        int topSizeTemp = this.topSize.get();
        int bottomSizeTemp = this.bottomSize.get();
        AtomicReference<Bucket[]> local = this.newLevel;
        int[] keys = hash(key);
        Bucket current = Bucket.searchTree(this.bottomLevel.get()[keys[5]], key);
        if (current != null)
            return current.value;
        current = Bucket.searchTree(this.bottomLevel.get()[keys[4]], key);
        if (current != null)
            return current.value;
        current = Bucket.searchTree(this.topLevel.get()[keys[3]], key);
        if (current != null)
            return current.value;
        current = Bucket.searchTree(this.topLevel.get()[keys[2]], key);
        if (current != null)
            return current.value;
        current = Bucket.searchTree(this.newLevel.get()[keys[1]], key);
        if (current != null)
            return current.value;
        current = Bucket.searchTree(this.newLevel.get()[keys[0]], key);
        if (current != null)
            return current.value;
        if (local.compareAndSet(this.newLevel.get(), this.newLevel.get()))
            return -1;

        else
            return this.search(key);
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
     * 
     * @param key   The key of the item that is to be deleted
     * @param value The value associated with the key
     */
    public void delete(String key) {
        if (!this.isResizing.get()) {
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
        } else {
            // TODO: concurrent delete

        }
    }

    // Hashing function
    private int[] hash(String key) {
        AtomicReference<Bucket[]> local = this.newLevel;
        int num = key.hashCode() & 0x7fffffff;
        if (num < 0)
            num *= -1;

        int[] keys = new int[6];
        int newSizeTemp = this.newSize.get();
        int topSizeTemp = this.topSize.get();
        int bottomSizeTemp = this.bottomSize.get();

        keys[0] = num % (newSizeTemp);

        if (keys[0] >= newSizeTemp / 2)
            keys[1] = keys[0] - newSizeTemp / 2;
        else
            keys[1] = keys[0] + newSizeTemp / 2;

        keys[2] = keys[0] / 2;
        keys[3] = keys[1] / 2;
        keys[4] = keys[2] / 2;
        keys[5] = keys[3] / 2;
        if (local.compareAndSet(this.newLevel.get(), this.newLevel.get()))
            return keys;
        // if (newSizeTemp == this.newSize.get() && topSizeTemp == this.topSize.get()
        // && bottomSizeTemp == this.bottomSize.get())
        // return keys;
        else
            return hash(key);
    }

    /**
     * Prints the hash table
     */
    public void printTable() {
        System.out.println("Top Array:");
        for (int i = 0; i < topSize.get(); i++) {
            System.out.print("    Tree no. " + i + ": {");
            Bucket.inOrderPrint(this.topLevel.get()[i]);
            System.out.println("}");
        }
        System.out.println();
        System.out.println("Bottom Array:");
        for (int i = 0; i < bottomSize.get(); i++) {
            System.out.print("    Tree no. " + i + ": {");
            Bucket.inOrderPrint(this.bottomLevel.get()[i]);
            System.out.println("}");
        }
        System.out.println();
    }

    @Override
    public void run() {
        this.resize();
    }
}
