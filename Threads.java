import java.util.concurrent.atomic.AtomicInteger;

public class Threads implements Runnable {

    ClevelHashTable hashtable;
    AtomicInteger numCtr = new AtomicInteger(0);

    public Threads() {
        hashtable = new ClevelHashTable();
    }

    @Override
    public void run() {
            int index = 1;
            int num = numCtr.get();
            // int num = rndm.nextInt(100);
            while (numCtr.get() < 100){
                num = numCtr.getAndIncrement();
            switch (index) {
            case 0:
                if (this.hashtable.insert(String.valueOf(num), num))
                    System.out.println("Inserting " + num + " ........ Done");
                else
                    System.out.println("Inserting " + num + " ........ Failed");
                break;
            case 1:
                // If a value is currently present then try to delete
            if (this.hashtable.search(String.valueOf(num)) >= 0) {
                if(this.hashtable.delete(String.valueOf(num)))
//                     // Successful means current thread could delete the value
                    System.out.println(Thread.currentThread().getId() + ": Deleting " + num + " ........ Successful");
                else
//                     // Failed means current thread could not delete the value; implies that another thread may have deleted it
                    System.out.println(Thread.currentThread().getId() + ": Deleting " + num + " ........ Failed");
            }
            else
                System.out.println(Thread.currentThread().getId()+ ": " + num + " could not be found for deletion");

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