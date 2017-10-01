/**
 * Created by Roham on 8/26/2017.
 */
public class Dog  implements Comparable<Dog>{
    private int size;
    private int age;

    public Dog (int size) {
        this.size = size;
        this.age = 1;
    }

    public Dog (int size, int age) {
        this.size = size;
        this.age = age;
    }

    public void bark() {
        System.out.println("Bark Bark !");
    }

    @Override
    public int compareTo(Dog dog) {
        if (this.age == dog.age){
            return 0;
        }

        return this.age < dog.age ? -1 : 1;
    }
}
