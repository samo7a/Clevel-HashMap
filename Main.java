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
        for (int i = 0; i < 95; i++) {
            if (!hashTable.insert(String.valueOf(i), i))
                System.out.println("Inserting " + i + " ........ Failed");
        }
        hashTable.printTable();
        for (int i = 0; i < 95; i++) {
            if (hashTable.search(String.valueOf(i)) < 0)
                System.out.println("not found " + i);
        }

    }
}