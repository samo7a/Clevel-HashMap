import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
public class ClevelHashTable implements Runnable {
    // the hash table will have 2 levels, and an isResizing flag
    boolean isResizing;
    public Bucket[] topLevel; // array of buckets
    public Bucket[] bottomLevel;
    int bottomSize; // size of the bottom level
    int topSize;
    int[] keys;
    ReentrantLock lock = new ReentrantLock();
    // constructor
    public ClevelHashTable() {
        bottomSize = 4; // initial size is 4, will be doubled in each resizing
        topSize = bottomSize * 2;
        isResizing = false; // the hash table is not resizing, we are just initializing it
        topLevel = new Bucket[topSize]; // the top level array is twice the size of the bottom one
        bottomLevel = new Bucket[bottomSize];
        // store the 4 generated hash keys of the value
        keys = new int[4];
        for (int i = 0; i < bottomSize; i++) { // initializing each element in the array
            topLevel[i] = null;
            topLevel[i + bottomSize] = null;
            bottomLevel[i] = null;
        }
    }

    public void insert(String key, Integer value) {
        // TODO
        // calls worker thread
        lock.lock();
        // hash here
        this.hash(key, value);
        // get the key from hashing, assign 0 for now
        // insert to bottom level
        if (Bucket.count(this.topLevel[keys[0]]) < 8) {
            this.topLevel[keys[0]] = Bucket.insertTree(key, value, this.topLevel[keys[0]]);
        } else if (Bucket.count(this.topLevel[keys[1]]) < 8) {
            this.topLevel[keys[1]] = Bucket.insertTree(key, value, this.topLevel[keys[1]]);
        } else if (Bucket.count(this.bottomLevel[keys[2]]) < 8) {
            this.bottomLevel[keys[2]] = Bucket.insertTree(key, value, this.bottomLevel[keys[2]]);
        } else if (Bucket.count(this.bottomLevel[keys[3]]) < 8) {
            this.bottomLevel[keys[3]] = Bucket.insertTree(key, value, this.bottomLevel[keys[3]]);
        } else {
            // resize and try to insert again
            this.resize();
            this.insert(key, value);
        }
        lock.unlock();
    }

    public int search(String key, Integer value) {
        // TODO
        this.hash(key, value);
        // search bottom level
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

    public void delete(String key, Integer value) {
        // TODO
        this.hash(key, value);
        int index = this.search(key, value);
        System.out.println("index = " + index);
        boolean topOrBottom = true; // true for topLevel, false for bottomLevel
        if (index == -1)
            return;
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == index) {
                if (i <= 1) {
                    topOrBottom = true;
                    break;
                } else {
                    topOrBottom = false;
                    break;
                }

            }
        }
        if (topOrBottom) { // if true, find in top level and delete it
            this.topLevel[index] = Bucket.deleteTree(this.topLevel[index], key, value);
            System.out.println("Deleting the node at top level");
            return;
        }
        System.out.println("Deleting the node at bottom level");
        this.bottomLevel[index] = Bucket.deleteTree(this.bottomLevel[index], key, value);
        return;

    }

    public void resize() {
        // indicate the bucket is resizing
        isResizing = true;
        // get the new size for the bottom level
        // I assume the size would be doubled based on the discord chat
        bottomSize *= 2;
        topSize *= 2;
        Bucket[] newLevel = new Bucket[topSize];
        // go thru the current bottom level to rehash every element
        for (Bucket root : bottomLevel) {
            // rehash every node in the tree
            reHashNode(root, newLevel);
        }

        // bottomLevel = new Bucket[size];
        // assign the current top level as bottom
        this.bottomLevel = topLevel;
        // just googled it, doing this will wipe off every current element in the old
        // array and
        // create a brand new empty array with the size
        // topLevel = new Bucket[size * 2];

        // assign new Level as the top
        this.topLevel = newLevel;
        // finish resizing
        isResizing = false;
    }

    public void reHashNode(Bucket root, Bucket[] array) {
        if (root == null)
            return;
        // hashing the new index for the key in the root
        hash(root.key, root.value);
        // insert node value into new position in the array
        insertRehash(root.key, root.value, array);
        if (Bucket.count(root) < 8)
            array[keys[0]] = Bucket.insertTree(root.key, root.value, array[keys[0]]);
        else
            array[keys[1]] = Bucket.insertTree(root.key, root.value, array[keys[1]]);
        // traverse left & right
        reHashNode(root.left, array);
        reHashNode(root.right, array);

    }

    public void insertRehash(String key, Integer value, Bucket[] newLevel) {

        // get the key from hashing, assign 0 for now
        // insert to bottom level
        if (Bucket.count(newLevel[keys[0]]) < 8) {
            newLevel[keys[0]] = Bucket.insertTree(key, value, newLevel[keys[0]]);
        } else if (Bucket.count(newLevel[keys[1]]) < 8) {
            newLevel[keys[1]] = Bucket.insertTree(key, value, newLevel[keys[1]]);
        } else if (Bucket.count(this.topLevel[keys[2]]) < 8) {
            this.topLevel[keys[2]] = Bucket.insertTree(key, value, this.topLevel[keys[2]]);
        } else if (Bucket.count(this.topLevel[keys[3]]) < 8) {
            this.topLevel[keys[3]] = Bucket.insertTree(key, value, this.topLevel[keys[3]]);
        } else {
            // resize and try to insert again
            this.resize();
            this.insert(key, value);
        }

    }

    public int update(String key, Integer value) {
        // Bucket deleteTree(Bucket root, String key, int value)
        // Bucket test = Bucket.deleteTree(this.bottomLevel[], key, value);
        // if (test == null) {
        //     Bucket.deleteTree(this.bottomLevel[0], key, value);
        // }
        this.delete(key, value);
        this.insert(key, value);
        return value;
    }

    public void hash(String key, Integer value) {
        // so that we can get a unique hashkey and avoid duplicates
        int num = key.hashCode() + value.hashCode();
        if (num < 0)
            num *= -1;


        this.keys[0] = num % (topSize);
        if (this.keys[0] >= topSize / 2)
            this.keys[1] = this.keys[0] - topSize / 2;
        else
            this.keys[1] = keys[0] + topSize / 2;

        this.keys[2] = num % bottomSize;
        if (this.keys[2] >= bottomSize / 2)
            this.keys[3] = this.keys[2] - bottomSize / 2;
        else
            this.keys[3] = keys[2] + bottomSize / 2;
    }

    public void printTable() {
        System.out.println("Top Array:");
        for (int i = 0; i < topSize; i++) {
            System.out.print("    Tree no. " + i + ": ");
            Bucket.printTree(this.topLevel[i]);
            System.out.println();
        }
        System.out.println();
        System.out.println("Bottom Array:");
        for (int i = 0; i < bottomSize; i++) {
            System.out.print("    Tree no. " + i + ": ");
            Bucket.printTree(this.bottomLevel[i]);
            System.out.println();
        }
    }
    
    public int assignOperation() {       
        Random rndm = new Random();
        return rndm.nextInt(100) + 8;
    }
    
    @Override
    public void run() {
        //System.out.println("hello I am a thread!");
        for (int i = 0; i < 50; i++){
            this.insert(Integer.valueOf(i).toString(),assignOperation());
        }
    }
}
