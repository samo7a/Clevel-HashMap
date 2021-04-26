/**
 * @author Ahmed Elshetany
 * @author Ankita Tripathi
 * @author Jacob Bostwick
 * @author Micah Renfrow
 * @author Quynh Nguyen
 * @version 1.0
 */

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Threads implements Runnable {

    ClevelHashTable hashtable;
    AtomicInteger numOps = new AtomicInteger(1000000);

    public Threads() {
        hashtable = new ClevelHashTable();
    }

    @Override
    public void run() {
        // Perform N = numOps random operations using random numbers between 0 - 1000000
        while (numOps.getAndDecrement() > 0) {
            Random rndm = new Random();
            int index = rndm.nextInt(3);
            // int index = 0; // For test 1 in Main.java: to test insertion
            // int index = 2; // For test 2 in Main.java: to test search
            // int index = 1; // For test 3 in Main.java: to test deletion
            int num = rndm.nextInt(1000000);
            switch (index) {
            case 0:
                if (this.hashtable.insert(String.valueOf(num), num))
                    System.out.println("Inserting " + num + " ........ Done");
                else
                    System.out.println("Inserting " + num + " ........ Failed");
                break;
            case 1:
                if (this.hashtable.delete(String.valueOf(num)))
                    System.out.println("deleting " + num + " ........ Done");
                else
                    System.out.println("deleting " + num + " ........ Failed");
                break;

            default:
                if (this.hashtable.search(String.valueOf(num)) > 0)
                    System.out.println("Searching for " + num + " ........ found");
                else
                    System.out.println("Searching for " + num + " ........ XXXXXXXXX");
                break;
            }
        }
    }
}