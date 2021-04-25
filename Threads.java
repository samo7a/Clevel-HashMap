/**
 * @author Ahmed Elshetany
 * @author Ankita Tripathi
 * @author Jacob Bostwick
 * @author Micah Renfrow
 * @author Quynh Nguyen
 * @version 1.0
 */

import java.util.Random;

public class Threads implements Runnable {

    ClevelHashTable hashtable;

    public Threads() {
        hashtable = new ClevelHashTable();
    }

    @Override
    public void run() {
        // Chooses a random function with random value
        Random rndm = new Random();
        int index = rndm.nextInt(3);
        int num = rndm.nextInt(1000);

        switch (index) {
        // Insertion function
        case 0:
            if (this.hashtable.insert(String.valueOf(num), num))
                System.out.println("Inserting " + num + " ........ Done");
            else
                System.out.println("Inserting " + num + " ........ Failed");
            break;
        
        // Deletion function
        case 1:
            if (this.hashtable.delete(String.valueOf(num)))
                System.out.println("deleting " + num + " ........ Done");
            else
                System.out.println("deleting " + num + " ........ Failed");
            break;

        // Search function
        default:
            if (this.hashtable.search(String.valueOf(num)) > 0)
                System.out.println("Searching for " + num + " ........ found");
            else
                System.out.println("Searching for " + num + " ........ XXXXXXXXX");
            break;
        }
    }
}