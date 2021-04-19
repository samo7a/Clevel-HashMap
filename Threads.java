import java.util.Random;

public class Threads implements Runnable {

    ClevelHashTable hashtable;

    public Threads(int numWorkers) {
        hashtable = new ClevelHashTable();
    }

    @Override
    public void run() {
        Random rndm = new Random();
        // int index = rndm.nextInt(3);
        int index = 1;
        int num = rndm.nextInt(100);
        switch (index) {
        case 0:
            if (this.hashtable.insert(String.valueOf(num), num))
                System.out.println("Inserting " + num + " ........ Done");
            else
                System.out.println("Inserting " + num + " ........ Failed");
            break;
        case 1:
            if (this.hashtable.search(String.valueOf(num)) >= 0) {
                if(this.hashtable.delete(String.valueOf(num)))
                    System.out.println("Deleting " + num + " ........ Done");
                else
                    System.out.println("Deleting " + num + " ........ Failed");
            }
            else
                System.out.println(num + " could not be found for deletion");

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