public class Main {
    // the main method is just for testing
    public static void main(String[] args) {

                int numWorkers = 1;
                Threads hashtable = new Threads();
                Thread[] workerThreads = new Thread[numWorkers];
        
                // Pre-populating hashtable to test deletion
                for (int i = 0; i < 100; i ++) 
                    hashtable.hashtable.insert(String.valueOf(i), i);
        
                System.out.println("Before");
                hashtable.hashtable.printTable();

                try {
                    for (int i = 0; i < numWorkers; i++) {
                        workerThreads[i] = new Thread(hashtable);
                        workerThreads[i].start();
                    }
                    for (int j = 0; j < numWorkers; j++) {
                        workerThreads[j].join();
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                System.out.println("After");
                hashtable.hashtable.printTable();
                
    }
}