import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;


public class Main {
    // the main method is just for testing
    public static void main(String[] args) {
        ClevelHashTable hashtable = new ClevelHashTable(); // create a new hashtable
        String aToZ = "ABCDEFGHIJKLMNOPQRSTVWXYabcdefghijklmnopqrstuvwxyz";     
        hashtable.bottomLevel[0] = Bucket.insertTree("hello10", 10, hashtable.bottomLevel[0]);
        hashtable.bottomLevel[0] = Bucket.insertTree(ClevelHashTable.generateRandom(aToZ), 11, hashtable.bottomLevel[0]);
        hashtable.bottomLevel[0] = Bucket.insertTree("hello12", 12, hashtable.bottomLevel[0]);
        hashtable.bottomLevel[0] = Bucket.insertTree(ClevelHashTable.generateRandom(aToZ), 12, hashtable.bottomLevel[0]);
        hashtable.bottomLevel[0] = Bucket.insertTree(ClevelHashTable.generateRandom(aToZ), 13, hashtable.bottomLevel[0]);
        hashtable.bottomLevel[0] = Bucket.insertTree(ClevelHashTable.generateRandom(aToZ), 14, hashtable.bottomLevel[0]);
        hashtable.bottomLevel[0] = Bucket.insertTree(ClevelHashTable.generateRandom(aToZ), 15, hashtable.bottomLevel[0]);
        hashtable.bottomLevel[0] = Bucket.insertTree(ClevelHashTable.generateRandom(aToZ), 16, hashtable.bottomLevel[0]);
        hashtable.bottomLevel[0] = Bucket.insertTree(ClevelHashTable.generateRandom(aToZ), 17, hashtable.bottomLevel[0]);
        hashtable.bottomLevel[0] = Bucket.insertTree(ClevelHashTable.generateRandom(aToZ), 18, hashtable.bottomLevel[0]);


        hashtable.printTable();
        System.out.println();

        hashtable.bottomLevel[0] = Bucket.deleteTree(hashtable.bottomLevel[0], "hello12", 12);
        hashtable.bottomLevel[0] = Bucket.deleteTree(hashtable.bottomLevel[0], "hello10", 10);
        hashtable.bottomLevel[0] = Bucket.deleteTree(hashtable.bottomLevel[0], "hello12", 12);

        Bucket result = Bucket.searchTree(hashtable.bottomLevel[0], "hello12", 12);
        if (result == null)  
            System.out.println("not found"); 
        else 
            System.out.println(result.key + " "  + result.value);
        
        
        // hashtable.insert("hello", 1);
        // hashtable.insert("hello3", 2);
        // hashtable.insert("hello44", 3);
        // hashtable.insert("hello4", 4);
        // hashtable.insert("hello55", 5);
        // hashtable.insert("hello6", 6);
        // hashtable.insert("hello65", 7);
        // hashtable.insert("hello77", 8);
        
        

        // ArrayList<Thread> threads = new ArrayList<>(); 
		// for (int i = 0; i < 4; i++) threads.add(new Thread(hashtable));
        // //starting the threads
		// for (int i = 0; i < 4; i++) threads.get(i).start();
		
		// // joining the threads
		// for (int i = 0; i < 4; i++) {
		// 	try {
		// 		threads.get(i).join();
		// 	} catch (InterruptedException e) {
		// 		e.printStackTrace();
		// 	}
		// }

        hashtable.printTable();
        System.out.println();
        
        System.out.println(Bucket.count(hashtable.bottomLevel[0]));
    

        // System.out.println(hashtable.search("hello", 4));
        BlockingQueue q = new BlockingQueue<E>();
    }
}