public class Main {
    // the main method is just for testing
    public static void main(String[] args) {

        ClevelHashTable hashTable = new ClevelHashTable();

        hashTable.printTable();

        for (int i = 0; i < 96; i++) {
            hashTable.insert("h", i);
        }
        hashTable.insert("UNIQUE1", 999999999);
        hashTable.insert("UNIQUE2", 111111111);
        hashTable.printTable();

    }
}