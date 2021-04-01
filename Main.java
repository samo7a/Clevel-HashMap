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
        // for (int j = 0; j < 1000000; j++) {
        ClevelHashTable hashtable = new ClevelHashTable();

        // hashtable.bottomLevel.get()[1] = Bucket.insertTree("2", 2,
        // hashtable.bottomLevel.get()[1]);
        // hashtable.bottomLevel.get()[1] = Bucket.insertTree("13", 13,
        // hashtable.bottomLevel.get()[1]);
        // hashtable.bottomLevel.get()[1] = Bucket.insertTree("17", 17,
        // hashtable.bottomLevel.get()[1]);
        // hashtable.bottomLevel.get()[1] = Bucket.insertTree("6", 6,
        // hashtable.bottomLevel.get()[1]);
        // hashtable.bottomLevel.get()[1] = Bucket.insertTree("20", 20,
        // hashtable.bottomLevel.get()[1]);
        // hashtable.bottomLevel.get()[1] = Bucket.insertTree("24", 24,
        // hashtable.bottomLevel.get()[1]);
        // hashtable.bottomLevel.get()[1] = Bucket.insertTree("28", 28,
        // hashtable.bottomLevel.get()[1]);
        // hashtable.bottomLevel.get()[1] = Bucket.insertTree("31", 31,
        // hashtable.bottomLevel.get()[1]);
        // hashtable.bottomLevel.get()[1] =
        // Bucket.deleteTree(hashtable.bottomLevel.get()[1], "2");
        // Bucket.inOrderPrint(hashtable.bottomLevel.get()[1]);
        // System.out.println();
        // Bucket.postOrderPrint(hashtable.bottomLevel.get()[1]);
        // System.out.println();
        // Bucket.preOrderPrint(hashtable.bottomLevel.get()[1]);
        // System.out.println();

        for (int i = 0; i < 100; i++) {
            if (!hashtable.insert(String.valueOf(i), i))
                throw new IllegalAccessError();
        }

        hashtable.printTable();
        // System.out.println(j);
        for (int i = 0; i < 100; i++) {
            if (hashtable.search(String.valueOf(i)) < 0)
                throw new IllegalAccessError();
        }

        for (int i = 0; i < 50; i++) {
            if (!hashtable.delete(String.valueOf(i))){
                System.out.println("deleting for " + i + " ........ XXXXXXXXX");

                throw new IllegalAccessError();
            }
            // System.exit(0);

        }
        hashtable.printTable();
    }
}