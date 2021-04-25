/**
 * @author Ahmed Elshetany
 * @author Ankita Tripathi
 * @author Jacob Bostwick
 * @author Micah Renfrow
 * @author Quynh Nguyen
 * @version 1.0
 */

public class Main {
    // The main method calls the threads for testing
    public static void main(String[] args) {
        // Prepopulate hashtable with 1000 values and calls random functions with 16 threads with random numbers
        int numWorkers = 16;
        Threads hashtable = new Threads();
        Thread[] workerThreads = new Thread[numWorkers];

        // Prepopulating the hashtable with values 0 - 1000
        for (int i = 0; i < 1000; i ++) 
            hashtable.hashtable.insert(String.valueOf(i), i);
        
        // Creating the threads and calling them
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

        // With the work done, the table is printed out
        hashtable.hashtable.printTable();
    }
}