public class Main {
    // the main method is just for testing
    public static void main(String[] args) {

        // Threads hashtable = new Threads();

        // for (int i = 0; i < 1000; i++) {
        // Thread worker = new Thread (hashtable);
        // worker.start();
        // try {
        // worker.join();
        // } catch (Exception e) {
        // System.out.println(e.getMessage());
        // }
        // }
        // hashtable.hashtable.printTable();

        ClevelHashTable hashTable = new ClevelHashTable();
        // int[] hash = hashTable.hash(String.valueOf(2));
        // for (int i = 0; i < hash.length; i++) System.out.println(hash[i]);
        for (int i = 0; i < 50; i++) {
            if (!hashTable.insert(String.valueOf(i), i))
                System.out.println("Inserting " + i + " ........ Failed");
        }
        // hash = hashTable.hash(String.valueOf(2));
        // for (int i = 0; i < hash.length; i++) System.out.println(hash[i]);
        hashTable.printTable();
        for (int i = 0; i < 50; i++) {
            if (hashTable.search(String.valueOf(i)) < 0)
                System.out.println("not found " + i);
        }
        // hashTable.resize();
        // hash = hashTable.hash(String.valueOf(2));
        // for (int i = 0; i < hash.length; i++) System.out.println(hash[i]);

    }
}