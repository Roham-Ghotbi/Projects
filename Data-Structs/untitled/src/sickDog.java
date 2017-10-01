/**
 * Created by Roham on 8/26/2017.
 */
public class sickDog extends Dog{
    public final static int numberOfLegs = 3;

    public sickDog(int size) {
        super(size);
    }
    public sickDog(int size, int age) {
        super(size, age);
    }

    public void test() {
        for ( int i = 0 ; i < 5; i++) {
            System.out.print(i);
        }
    }
}
