import java.util.ArrayList;

public class Main {
    ClevelHashTable hashtable;
    ArrayList<Threads> threads;
    ArrayList<Thread> th;
    static Main manage;

    // callResizer is used to call the thread that will perform the resizing.
    // This method has not been tested yet. Need to check how wait() and notify() works.
    public void callResizer(boolean shouldResize){
        if (shouldResize) {
            // start resizing thread (4th thread in list)
            System.out.println("Calling resize()");
            manage.th.get(3).notify(); 
        }
    }
    // the main method is just for testing
    public static void main(String[] args) {
        // for passing in the instance to the threads
        manage = new Main();
        manage.hashtable = new ClevelHashTable(); // create a new hashtable
        String aToZ = "ABCDEFGHIJKLMNOPQRSTVWXYabcdefghijklmnopqrstuvwxyz";     
        // hashtable.bottomLevel[0] = Bucket.insertTree("hello10", 10, hashtable.bottomLevel[0]);
        // hashtable.bottomLevel[0] = Bucket.insertTree(ClevelHashTable.generateRandom(aToZ), 11, hashtable.bottomLevel[0]);
        // hashtable.bottomLevel[0] = Bucket.insertTree("hello12", 12, hashtable.bottomLevel[0]);
        // hashtable.bottomLevel[0] = Bucket.insertTree(ClevelHashTable.generateRandom(aToZ), 12, hashtable.bottomLevel[0]);
        // hashtable.bottomLevel[0] = Bucket.insertTree(ClevelHashTable.generateRandom(aToZ), 13, hashtable.bottomLevel[0]);
        // hashtable.bottomLevel[0] = Bucket.insertTree(ClevelHashTable.generateRandom(aToZ), 14, hashtable.bottomLevel[0]);
        // hashtable.bottomLevel[0] = Bucket.insertTree(ClevelHashTable.generateRandom(aToZ), 15, hashtable.bottomLevel[0]);
        // hashtable.bottomLevel[0] = Bucket.insertTree(ClevelHashTable.generateRandom(aToZ), 16, hashtable.bottomLevel[0]);
        // hashtable.bottomLevel[0] = Bucket.insertTree(ClevelHashTable.generateRandom(aToZ), 17, hashtable.bottomLevel[0]);
        // hashtable.bottomLevel[0] = Bucket.insertTree(ClevelHashTable.generateRandom(aToZ), 18, hashtable.bottomLevel[0]);


        // manage.hashtable.printTable();
        // System.out.println();

        // manage.hashtable.bottomLevel[0] = Bucket.deleteTree(manage.hashtable.bottomLevel[0], "hello12", 12);
        // manage.hashtable.bottomLevel[0] = Bucket.deleteTree(manage.hashtable.bottomLevel[0], "hello10", 10);
        // manage.hashtable.bottomLevel[0] = Bucket.deleteTree(manage.hashtable.bottomLevel[0], "hello12", 12);

        // Bucket result = Bucket.searchTree(manage.hashtable.bottomLevel[0], "hello12", 12);
        // if (result == null)  
        //     System.out.println("not found"); 
        // else 
        //     System.out.println(result.key + " "  + result.value);

        
        // hashtable.insert("hello", 1);
        // hashtable.insert("hello3", 2);
        // hashtable.insert("hello44", 3);
        // hashtable.insert("hello4", 4);
        // hashtable.insert("hello55", 5);
        // hashtable.insert("hello6", 6);
        // hashtable.insert("hello65", 7);
        // hashtable.insert("hello77", 8);
        
        
		// array of runnable Java thread
        manage.th = new ArrayList<>();
        // array of Threads(worker thread)
        manage.threads = new ArrayList<>(); 
		for (int i = 0; i < 4; i++) {
            // create a Threads object and pass in the Main obj to manage
            // make the 4th thread the designated resize thread for now
            Threads thread = new Threads(manage, i < 3 ? true : false, i);
            // add that object to the list of Threads
            manage.threads.add(thread);
            //add the new Threads(worker thread) to the array of thread so it can run
           manage.th.add( new Thread(thread));
            
        } 
        //starting the threads
		for (int i = 0; i < 4; i++) manage.th.get(i).start();
        // print out the type of each thread
		for (int i = 0; i < 4; i++){
            System.out.println("Thread " + i + " type: " + manage.threads.get(i).threadType);
        }
		// joining the threads
		for (int i = 0; i < 4; i++) {
			try {
				manage.th.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

        manage.hashtable.printTable();
        System.out.println();
        
        System.out.println(Bucket.count(manage.hashtable.bottomLevel[0]));
        

        // System.out.println(hashtable.search("hello", 4));
        //BlockingQueue q = new BlockingQueue<E>();
    }
}