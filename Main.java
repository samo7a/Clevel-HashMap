import java.util.ArrayList;
import java.util.Random;
public class Main {
    // the main method is just for testing
    public static void main(String[] args) {
        ClevelHashTable hashtable = new ClevelHashTable(); // create a new hashtable
        
        hashtable.insert("hello", 1);
        hashtable.insert("hello3", 2);
        hashtable.insert("hello44", 3);
        hashtable.insert("hello4", 4);
        hashtable.insert("hello55", 5);
        hashtable.insert("hello6", 6);
        hashtable.insert("hello65", 7);
        hashtable.insert("hello77", 8);
        
        hashtable.printTable();
        System.out.println();

        ArrayList<Thread> threads = new ArrayList<>(); 
		for (int i = 0; i < 4; i++) threads.add(new Thread(hashtable)); 
        //starting the threads
		for (int i = 0; i < 4; i++) threads.get(i).start();
		
		// joining the threads
		for (int i = 0; i < 4; i++) {
			try {
				threads.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

        hashtable.printTable();
        System.out.println();

        // Random rndm = new Random();
        // int job = rndm.nextInt(3);
        // int person = rndm.nextInt(3);
        // if (job == 0) System.out.println("Insert");
        // else if (job == 1) System.out.println("Search");
        // else System.out.println("Delete");

        // if (person == 0) System.out.println("Q");
        // else if (job == 1) System.out.println("Ah");
        // else System.out.println("Ak");


    }
}