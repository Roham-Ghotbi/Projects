/**
 * Created by Roham on 2/21/2017.
 */
public interface Deque<Item> {
    void addFirst(Item x);
    boolean isEmpty();
    void printDeque();
    Item removeFirst();
    void addLast(Item x);
    Item removeLast();
    int size();
    Item get(int index);
 default boolean remove(Item x){
     boolean removed = false;
     if(this.get(0).equals(x)){
         removed = true;
         this.removeFirst();
     } else{
         return this
     }
 }

}
