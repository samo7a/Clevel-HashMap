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
        for (int j = 0; j < 1000000; j++) {
            ClevelHashTable hashtable = new ClevelHashTable();
            // int[] hash = hashTable.hash(String.valueOf(2));
            // for (int i = 0; i < hash.length; i++) System.out.println(hash[i]);
            for (int i = 0; i < 3000; i++) {
                if (!hashtable.insert(String.valueOf(i), i)) {
                    throw new IllegalAccessError();
                    // System.exit(0);
                }

                // // System.out.println("Inserting " + i + " ........ Failed");
            }
            // // hash = hashTable.hash(String.valueOf(2));
            // // for (int i = 0; i < hash.length; i++) System.out.println(hash[i]);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // hashtable.hashtable.printTable();
            // hashtable.printTable();
            System.out.println(j);
            for (int i = 0; i < 100; i++) {
                if (hashtable.search(String.valueOf(i)) < 0) {
                    // System.out.println("Searching for " + i + " ........ XXXXXXXXX");

                    throw new IllegalAccessError();
                    // System.exit(0);

                }
                // else
                // System.out.println("Searching for " + i + " ........ found");

                // System.out.println("not found " + i);
            }
        }
        // hashTable.resize();
        // hash = hashTable.hash(String.valueOf(2));
        // for (int i = 0; i < hash.length; i++) System.out.println(hash[i]);

    }
}