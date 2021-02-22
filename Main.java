public class Main {
    // the main method is just for testing
    public static void main(String[] args) {
        ClevelHashTable context = new ClevelHashTable(); // create a new hashtable
        System.out.println(context.isResizing); // testing the value of resizing

        // inserting into the tree
        context.bottomLevel[0] = Bucket.insertTree(0, 23, context.bottomLevel[0]);
        context.bottomLevel[0] = Bucket.insertTree(0, 14, context.bottomLevel[0]);
        context.bottomLevel[0] = Bucket.insertTree(0, 25, context.bottomLevel[0]);
        context.bottomLevel[0] = Bucket.insertTree(0, 24, context.bottomLevel[0]);
        context.bottomLevel[0] = Bucket.insertTree(0, 26, context.bottomLevel[0]);
        context.bottomLevel[0] = Bucket.insertTree(0, 13, context.bottomLevel[0]);
        context.bottomLevel[0] = Bucket.insertTree(0, 15, context.bottomLevel[0]);
        context.bottomLevel[0] = Bucket.insertTree(0, 15, context.bottomLevel[0]); // trying to insert a dublicate 
        Bucket.printTree(context.bottomLevel[0]); // printing the tree
        System.out.println();  

        context.bottomLevel[0] = Bucket.deleteTree(context.bottomLevel[0], 25);  // deleting a node that has two children
        Bucket.printTree(context.bottomLevel[0]);
        System.out.println();

        context.bottomLevel[0] = Bucket.deleteTree(context.bottomLevel[0], 25);  // deleting a node that does not exist 
        Bucket.printTree(context.bottomLevel[0]);
        System.out.println();

        context.bottomLevel[0] = Bucket.deleteTree(context.bottomLevel[0], 24); // deleting a node that has only one child
        Bucket.printTree(context.bottomLevel[0]);
        System.out.println();

        context.bottomLevel[0] = Bucket.deleteTree(context.bottomLevel[0], 23); // deleting the root 
        Bucket.printTree(context.bottomLevel[0]);
        System.out.println();

        context.bottomLevel[0] = Bucket.deleteTree(context.bottomLevel[0], 13); // deleting a leaf node
        Bucket.printTree(context.bottomLevel[0]);
        System.out.println();

    }
}