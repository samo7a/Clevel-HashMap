// the bucket that will hold the value and the key
public class Bucket {
    int key;
    int value;

    // constructors
    public Bucket() {
        key = 0; // initial values, will be changed later
        value = 0;
    }

    public Bucket(Integer key, Integer value) {
        this.key = key;
        this.value = value;
    }
}
