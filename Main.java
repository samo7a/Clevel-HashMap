public class Main {
    // the main method is just for testing
    public static void main(String[] args) {

        Threads hashtable = new Threads();
        
        for (int i = 0; i < 300; i++) {
            Thread insert = new Thread (hashtable);
            insert.start();
        }
        
        hashtable.hashtable.printTable();

        
        

    }
}