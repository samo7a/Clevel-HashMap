import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
public class Threads implements Runnable{

    public AtomicBoolean threadType = new AtomicBoolean(); // to identify worker and background threads
    private ClevelHashTable hashTable;
    private Main manager;
    int ID;
    public Threads(Main main, boolean isWorker, int ID) {
        manager = main;
        hashTable = main.hashtable;
        threadType.set(isWorker); // true for worker; false for background thread
        this.ID = ID;
    }

    public static String generateRandom(String aToZ) {
        Random rand = new Random();
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            int randIndex = rand.nextInt(aToZ.length());
            res.append(aToZ.charAt(randIndex));
        }
        return res.toString();
    }

    @Override
    public void run() {
        // if thread is a worker thread, then perform insert/delete/search
        if (this.threadType.get()) {        
            int noOfAttempts = 100; // # of attempts for that threads
            Random random = new Random();
            boolean insert; //check if insert succeed or not
            int valueBound = 100;
            for(int i = 0; i < noOfAttempts; i++){
                int operation = random.nextInt(3);
                int value = random.nextInt(valueBound);
                String key = generateRandom("ABCDEFGHIJKLMNOPQRSTVWXYabcdefghijklmnopqrstuvwxyz");
                switch (operation) {
                case 0:	
                    insert = hashTable.insert(key, value);
                    // if failed to insert => call Main to resize
                    if(!insert)
                        manager.callResizer(true);
                    break;

                case 1:
                    hashTable.delete(key, value);
                    break;

                default: 
                    hashTable.search(key, value);
                    break;
                }
            }
        }
        else { // else the thread is a resize thread => do resizing
            if (hashTable.isResizing) {
                System.out.println("Thread #" + this.ID + " is calling resize()");
                hashTable.resize();
            }
            // trying to make wait() work, will fix it later
            /*try {
                //hashTable.wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/
        }
    }

}
