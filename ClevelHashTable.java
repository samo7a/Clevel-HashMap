public class ClevelHashTable {
    // the hash table will have 2 levels, and an isResizing flag
    boolean isResizing;
    public Bucket[] topLevel; // array of buckets
    public Bucket[] bottomLevel;
    int size; // size of the bottom level
    int[] keys;

    // constructor
    public ClevelHashTable() {
        size = 4; // initial size is 4, will be doubled in each resizing
        isResizing = false; // the hash table is not resizing, we are just initializing it
        topLevel = new Bucket[size * 2]; // the top level array is twice the size of the bottom one
        bottomLevel = new Bucket[size];
        // store the 4 generated hash keys of the value
        keys = new int[4];
        for (int i = 0; i < size; i++) { // initializing each element in the array
            topLevel[i] = null;
            topLevel[i + size] = null;
            bottomLevel[i] = null;
        }
    }

    public void insert(int key, int value) {
        // TODO

        // hash here
        this.hash(key);
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
            this.insert(value, key);
        }

    }

    public int search(int key, int value) {
        // TODO
        this.hash(key);
        // search bottom level
        if (Bucket.searchTree(this.bottomLevel[keys[2]], value, key) != null)
            return keys[2];
        if (Bucket.searchTree(this.bottomLevel[keys[3]], value, key) != null)
            return keys[3];
        if (Bucket.searchTree(this.topLevel[keys[0]], value, key) != null)
            return keys[0];
        if (Bucket.searchTree(this.topLevel[keys[1]], value, key) != null)
            return keys[1];
        return -1;
    }

    public void delete(int key, int value) {
        // TODO
        this.hash(key);
        int index = search(value, key);
        System.out.println("index = " + index);
        boolean topOrBottom = true; // true for topLevel, false for bottomLevel
        if (index == -1)
            return;
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == index) {
                if (i <= 1) {
                    topOrBottom = true;
                    break;
                }    
                else{
                    topOrBottom = false;
                    break;
                }
                    
            }
        }
        if (topOrBottom) { // if true, find in top level and delete it
            this.topLevel[index] = Bucket.deleteTree(this.topLevel[index], value, key);
            System.out.println("Deleting the node at top level");
            return;
        }
        System.out.println("Deleting the node at bottom level");
        this.bottomLevel[index] = Bucket.deleteTree(this.bottomLevel[index], value, key);
        return;

    }

    public void resize() {
        // indicate the bucket is resizing
        isResizing = true;
        // get the new size for the bottom level
        // I assume the size would be doubled based on the discord chat
        this.size *= 2;
        Bucket[] newLevel = new Bucket[size * 2];
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
        hash(root.key);
        // insert node value into new position in the array
        array[keys[0]] = Bucket.insertTree(root.key, root.value, array[keys[0]]);
        // traverse left & right
        reHashNode(root.left, array);
        reHashNode(root.right, array);

    }

    public int update(int value) {
        // TODO
        return value;
    }

    public void hash(Integer key) {
        int num = key.hashCode();
        if (num < 0) num *= -1;
        this.keys[0] = num % (size * 2);
        if (this.keys[0] > size)  this.keys[1] = this.keys[0] - size;
        else this.keys[1] = keys[0] + size;
        this.keys[2] = this.keys[0] / 2;
        this.keys[3] = this.keys[1] / 2;
    }
    public void printTable(){
        System.out.println("Top Array:");
        for (int i = 0; i < size * 2; i++){
            System.out.print("    Tree no. " + i + ": ");
            Bucket.printTree(this.topLevel[i]);
        }

        System.out.println("Bottom Array:");
        for (int i = 0; i < size; i++){
            System.out.print("    Tree no. " + i + ": ");
            Bucket.printTree(this.bottomLevel[i]);
        }
    }
}
