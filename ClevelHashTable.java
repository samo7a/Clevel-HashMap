
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
    volatile AtomicBoolean isResizing;
    volatile AtomicReference<Bucket[]> newLevel;
    volatile AtomicReference<Bucket[]> topLevel;
    volatile AtomicReference<Bucket[]> bottomLevel;
    AtomicInteger newSize;
    AtomicInteger topSize;
    AtomicInteger bottomSize;
    // volatile ReentrantLock lock = new ReentrantLock();

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
                    this.resize();
                    return this.insert(key, value);
                } else
                    return this.insertRehash(key, value);
            }

        } else {
            return insertRehash(key, value);
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
        } else if (Bucket.count(newLevel.get()[keys[1]]) < 8) {
            if (local.compareAndSet(this.newLevel.get(), this.newLevel.get())) {
                newLevel.get()[keys[1]] = Bucket.insertTree(key, value, newLevel.get()[keys[1]]);
                return true;
            }
        }
        return false;

    }

    // resize the hashtable
    public void resize() {
        System.out.println("***************Resizing****************");
        for (Bucket root : this.bottomLevel.get()) {
            reHashNode(root);
        }
        // lock.lock();
        this.bottomSize = this.topSize;
        this.topSize = this.newSize;
        this.newSize = new AtomicInteger(this.newSize.get() * 2);

        this.bottomLevel.set(this.topLevel.get());
        this.topLevel.set(this.newLevel.get());
        this.newLevel = new AtomicReference<>(new Bucket[newSize.get()]);
        // lock.unlock();
        this.isResizing.set(false);
    }

    // rehash all the nodes in that root to the new level
    private void reHashNode(Bucket root) {
        if (root == null)
            return;
        // The if block below is part of the implementation of logical deletion    
        // if (root.isMarked.get()) {
        //     reHashNode(root.left);
        //     reHashNode(root.right);
        // }
        insertRehash(root.key, root.value);
        reHashNode(root.left);
        reHashNode(root.right);
    }

    /**
     * Search in the hashtable if that key-value pair exist
     * 
     * @param key   The key of the item
     * @param value The value associated with it
     * @return the index of that key-value pair or -1 if not found
     */
    public int search(String key) {
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

    /**
     * Delete a key-value pair from the hashtable
     * 
     * @param key   The key of the item that is to be deleted
     * @param value The value associated with the key
     */
    public boolean delete(String key) {
        // if (this.search(key) < 0)
        // return false;
        AtomicReference<Bucket[]> localNew = this.newLevel;
        AtomicReference<Bucket[]> localTop = this.topLevel;
        AtomicReference<Bucket[]> localBottom = this.bottomLevel;
        int[] keys = this.hash(key);

        if (localBottom.compareAndSet(this.bottomLevel.get(), this.bottomLevel.get()))
            this.bottomLevel.get()[keys[5]] = Bucket.deleteTree(this.bottomLevel.get()[keys[5]], key);
        if (localBottom.compareAndSet(this.bottomLevel.get(), this.bottomLevel.get()))
            this.bottomLevel.get()[keys[4]] = Bucket.deleteTree(this.bottomLevel.get()[keys[4]], key);

        if (localTop.compareAndSet(this.topLevel.get(), this.topLevel.get()))
            this.topLevel.get()[keys[3]] = Bucket.deleteTree(this.topLevel.get()[keys[3]], key);
        if (localTop.compareAndSet(this.topLevel.get(), this.topLevel.get()))
            this.topLevel.get()[keys[2]] = Bucket.deleteTree(this.topLevel.get()[keys[2]], key);

        if (localNew.compareAndSet(this.newLevel.get(), this.newLevel.get()))
            this.newLevel.get()[keys[1]] = Bucket.deleteTree(this.newLevel.get()[keys[1]], key);
        if (localNew.compareAndSet(this.newLevel.get(), this.newLevel.get()))
            this.newLevel.get()[keys[0]] = Bucket.deleteTree(this.newLevel.get()[keys[0]], key);

        return (this.search(key) < 0);
    }

    // Hashing function
    public int[] hash(String key) {
        AtomicReference<Bucket[]> local = this.newLevel;
        int num = (key.hashCode() * 31) & 0x7fffffff;
        int[] keys = new int[6];
        int newSizeTemp = this.newSize.get();
        int topSizeTemp = this.topSize.get();
        int bottomSizeTemp = this.bottomSize.get();

        keys[0] = num % (newSizeTemp / 2);
        keys[1] = keys[0] + newSizeTemp / 2;
        keys[2] = num % (topSizeTemp / 2);
        keys[3] = keys[2] + topSizeTemp / 2;
        keys[4] = num % (bottomSizeTemp / 2);
        keys[5] = keys[4] + bottomSizeTemp / 2;

        if (local.compareAndSet(this.newLevel.get(), this.newLevel.get()) && newSizeTemp == this.newSize.get()
                && topSizeTemp == this.topSize.get() && bottomSizeTemp == this.bottomSize.get()) {
            return keys;
        }
        System.out.println("reference changed");
        return hash(key);
    } // && !lock.isLocked()

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