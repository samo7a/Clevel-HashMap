public class Main {
    // the main method is just for testing
    public static void main(String[] args) {
        Threads hashtable = new Threads();
        for (int i = 0; i < 100; i++) {
            Thread worker = new Thread(hashtable);
            worker.start();
            try {
                worker.join();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        hashtable.hashtable.printTable();

    }
}