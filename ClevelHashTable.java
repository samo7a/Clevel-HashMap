public class ClevelHashTable {
    // the hash table will have 2 levels, and an isResizing flag
    boolean isResizing;
    Bucket[] topLevel; // array of buckets
    Bucket[] bottomLevel;
    int size; // size of the bottom level

    // constructor
    public ClevelHashTable() {
        size = 4; // initial size is 4, will be doubled in each resizing
        isResizing = false; // the hash table is not resizing, we are just initializing it
        topLevel = new Bucket[size * 2]; // the top level array is twice the size of the bottom one
        bottomLevel = new Bucket[size];
        for (int i = 0; i < size; i++) { // initializing each element in the array
            topLevel[i] = null;
            topLevel[i + size] = null;
            bottomLevel[i] = null;
        }
    }

    public int insert(int value) {
        // TODO
       // hash here

        // get the key from hashing, assign 0 for now
        int key = 0;
        // insert to bottom level
       bottomLevel[key] = Bucket.insertTree(key, value, bottomLevel[key]);

       // get the key for top level, assign 0 for now
       int topKey = 0;
       // insert to top level
        topLevel[topKey] = Bucket.insertTree(topKey, value, topLevel[topKey]);
        return value;
    }

    public int search(int value) {
        // TODO
        return value;
    }

    public int delete(int value) {
        // TODO
        return value;
    }

    public void resize() {
        // indicate the bucket is resizing
        isResizing = true;
        // get the new size for the bottom level
        // I assume the size would be doubled based on the discord chat
        size *= 2;
        Bucket[] newLevel = new Bucket[size];
        // call hash function here

        // assign the current top level as bottom
        for(Bucket buc: topLevel) {

        }
        // could be spicy here since we already assign an initial size to topLevel
        topLevel = new Bucket[size];
        //topLevel = newLevel;
        // assign new Level as the top
        for(Bucket i : newLevel) {

        }
        // finish resiziing
        isResizing = false;

    }

    public int update(int value) {
        // TODO
        return value;
    }

    public int hash1() {
        // TODO
        return 0;
    }

    public int hash2() {
        // TODO
        return 0;
    }

    public int hash3() {
        // TODO
        return 0;
    }

    public int hash4() {
        // TODO
        return 0;
    }
}
