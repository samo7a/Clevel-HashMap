/**
 * @author Ahmed Elshetany
 * @author Ankita Tripathi
 * @author Jacob Bostwick
 * @author Micah Renfrow
 * @author Quynh Nguyen
 * @version 1.0
 */

import java.util.concurrent.TimeUnit;
public class Main {
    // the main method is just for testing
    public static void main(String[] args) {

                // Test 1: Call insert 1 million times with 1,2,4,8,16 threads, to insert a random no between 1 and 900k
                // int numWorkers = 16;
                // Threads hashtable = new Threads();
                // Thread[] workerThreads = new Thread[numWorkers];
        
                // int numOps = hashtable.noops.get();
                // long start = System.currentTimeMillis();
                // try {
                //     for (int i = 0; i < numWorkers; i++) {
                //         workerThreads[i] = new Thread(hashtable);
                //         workerThreads[i].start();
                //     }
                //     for (int j = 0; j < numWorkers; j++) {
                //         workerThreads[j].join();
                //     }
                // } catch (Exception e) {
                //     System.out.println(e.getMessage());
                // }
                // long end = System.currentTimeMillis();
                // long time = end - start;
                // long min = TimeUnit.MILLISECONDS.toMinutes(time)
                //         - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
                // long sec = TimeUnit.MILLISECONDS.toSeconds(time)
                //         - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
                // long ms = TimeUnit.MILLISECONDS.toMillis(time)
                //         - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(time));
                // System.out.println("no of insert operations: " + numOps);
                // System.out.println("no of threads: " + numWorkers);
                // System.out.println("time in ms: " + time);
                // System.out.println("run time: " + min + " mins, " + sec + " secs and " + ms + " ms");

                // Test 2: prepopulate hashtable with 1m values and call search 1m times with 1,2,4,8,16 threads, to search for a random no between 1 and 900k
                // int numWorkers = 16;
                // Threads hashtable = new Threads();
                // Thread[] workerThreads = new Thread[numWorkers];
        
                // int numOps = hashtable.noops.get();
                // for (int i = 0; i < 1000000; i ++) 
                //     hashtable.hashtable.insert(String.valueOf(i), i);

                // long start = System.currentTimeMillis();
                // try {
                //     for (int i = 0; i < numWorkers; i++) {
                //         workerThreads[i] = new Thread(hashtable);
                //         workerThreads[i].start();
                //     }
                //     for (int j = 0; j < numWorkers; j++) {
                //         workerThreads[j].join();
                //     }
                // } catch (Exception e) {
                //     System.out.println(e.getMessage());
                // }
                // long end = System.currentTimeMillis();
                // long time = end - start;
                // long min = TimeUnit.MILLISECONDS.toMinutes(time)
                //         - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
                // long sec = TimeUnit.MILLISECONDS.toSeconds(time)
                //         - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
                // long ms = TimeUnit.MILLISECONDS.toMillis(time)
                //         - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(time));
                // System.out.println("no of search operations: " + numOps);
                // System.out.println("no of threads: " + numWorkers);
                // System.out.println("time in ms: " + time);
                // System.out.println("run time: " + min + " mins, " + sec + " secs and " + ms + " ms");

                // Test 3: prepopulate hashtable with 1m values and call delete 1m times with 1,2,4,8,16 threads, to delete a random no between 1 and 1m
                int numWorkers = 16;
                Threads hashtable = new Threads();
                Thread[] workerThreads = new Thread[numWorkers];
        
                int numOps = hashtable.noops.get();
                for (int i = 0; i < 1000000; i ++) 
                    hashtable.hashtable.insert(String.valueOf(i), i);
                
                long start = System.currentTimeMillis();
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
                long end = System.currentTimeMillis();
                long time = end - start;
                long min = TimeUnit.MILLISECONDS.toMinutes(time)
                        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
                long sec = TimeUnit.MILLISECONDS.toSeconds(time)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
                long ms = TimeUnit.MILLISECONDS.toMillis(time)
                        - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(time));
                System.out.println("no of delete operations: " + numOps);
                System.out.println("no of threads: " + numWorkers);
                System.out.println("time in ms: " + time);
                System.out.println("run time: " + min + " mins, " + sec + " secs and " + ms + " ms");

                
    }
}