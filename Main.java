public class Main {
    // the main method is just for testing
    public static void main(String[] args) {
        ClevelHashTable hashtable = new ClevelHashTable(); // create a new hashtable
        for (int i = 0; i < 96; i++){
            hashtable.insert("aaak", i);
        }
        hashtable.printTable();
        System.out.println();
        //System.out.println(hashtable.search("aaak", 70));
        hashtable.insert("aaak", 96);


        hashtable.printTable();
        System.out.println();
        //System.out.println(hashtable.search("aaak", 1));

    }
}