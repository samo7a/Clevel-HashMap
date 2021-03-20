import java.util.Random;
public class Threads implements Runnable{





    ClevelHashTable hashtable;
    public Threads () {
        hashtable = new ClevelHashTable();
    }




    @Override
    public void run(){
        Random rndm = new Random();
        int index = rndm.nextInt(2);

        switch (index) {
            case 0:
                this.hashtable.insert( String.valueOf(rndm.nextInt(100)) , rndm.nextInt(100));
                break;
        
            default:
            if (this.hashtable.search(String.valueOf(rndm.nextInt(100))) > 0)
                System.out.println("found");
            else 
                System.out.println("XXXXXXXX");
                break;
        }
    }
}