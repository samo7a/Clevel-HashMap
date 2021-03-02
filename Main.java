public class Main {
    // the main method is just for testing
    public static void main(String[] args) {
        ClevelHashTable hashtable = new ClevelHashTable(); // create a new hashtable
        hashtable.insert(1, 11);
        hashtable.insert(2, 23);
        hashtable.insert(3, 155);
        hashtable.insert(4, 155);
        hashtable.insert(5, 66);
        //hashtable.delete(2, 10);
        //String num = "jkhkskjheuhfdh33kk388djdjk38";
        int result = hashtable.search(2,10);
        //hashtable.printTable();
        //System.out.println(result);

        

    }
}