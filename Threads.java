import java.util.Random;
public class Threads implements Runnable{





    ClevelHashTable hashtable;
    public Threads () {
        hashtable = new ClevelHashTable();
    }




    @Override
    public void run(){
        Random rndm = new Random();
        int index = rndm.nextInt(1);
        int num = rndm.nextInt(100);
        switch (index) {
            case 0:
                if (this.hashtable.insert( String.valueOf(num) , num))
                    System.out.println("Inserting " + num + " ........ Done");
                else
                    System.out.println("Inserting " + num + " ........ Failed");
                break;
            case 1:
                this.hashtable.delete( String.valueOf(num));
                break;
        
            default:
                if (this.hashtable.search(String.valueOf(num)) > 0)
                    System.out.println("Searching for " + num + " ........ found");
                else 
                    System.out.println("Searching for " + num + " ........ XXXXXXXXX");
                break;
        }
    }
}