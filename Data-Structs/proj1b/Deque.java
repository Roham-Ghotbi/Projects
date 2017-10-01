public interface Deque<Item> {
    void addFirst(Item list);
    void addLast(Item list);
    boolean isEmpty();
    int size();
    void printDeque();
    Item removeFirst();
    Item removeLast();
    Item get(int index);
}
