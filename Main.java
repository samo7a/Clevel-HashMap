public class Main {
    // the main method is just for testing
    public static void main(String[] args) {
        ClevelHashTable context = new ClevelHashTable(); // create a new hashtable
        System.out.println(context.isResizing); // testing the value of resizing
        for (int i = 0; i < context.size; i++) { // testing the initial values of the hash table
            System.out.printf("Bottom Level Array: Index %d has a bucket of key = %d, value = %d%n", i,
                    context.bottomLevel[i].key, context.bottomLevel[i].value);
        }
        for (int i = 0; i < context.size * 2; i++) {
            System.out.printf("Top Level Array: Index %d has a bucket of key = %d, value = %d%n", i,
                    context.topLevel[i].key, context.topLevel[i].value);
        }
    }
}