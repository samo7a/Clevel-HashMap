public class ClevelHashTable {
    // the hash table will have 2 levels, and an isResizing flag
    boolean isResizing;
    Bucket[] topLevel; // array of buckets
    Bucket[] bottomLevel;
    int size;

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
        // TODO

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
