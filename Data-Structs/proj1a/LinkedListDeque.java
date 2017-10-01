public class LinkedListDeque<Item> implements Deque<Item>{

    private class Node {
        private Item item;
        private Node next;
        private Node prev;

        private Node(Item item, Node next, Node prev) {
            this.item = item;
            this.next = next;
            this.prev = prev;
        }

    }
    private int size;
    private Node sentinel;

    public LinkedListDeque() {
        this.size = 0;
        sentinel = new Node(null, sentinel, sentinel);

    }
    public void addFirst(Item item) {
        Node first = new Node(item, sentinel.next, sentinel);
        if (this.isEmpty()) {
            sentinel.prev = first;
        } else {
            sentinel.next.prev = first;
        }
        sentinel.next = first;

        this.size += 1;
    }
    public void addLast(Item item) {
        Node last = new Node(item, sentinel, sentinel.prev);
        if (this.isEmpty()) {
            sentinel.next = last;
        } else {
            sentinel.prev.next = last;
        }
        sentinel.prev = last;
        this.size += 1;
    }
    public boolean isEmpty() {
        return (this.size == 0);
    }
    public int size() {
        return this.size;
    }
    public void printDeque() {
        int counter = this.size() - 1;
        Node list = sentinel.next;
        while (counter >= 0) {
            System.out.print(list.item + " ");
            list = list.next;
            counter -= 1;
        }
        System.out.println("");
    }
    public Item removeFirst() {
        Item element;

        if (this.size() == 0) {
            return null;
        } else if (this.size() == 1) {
            element = sentinel.next.item;
            sentinel.next = sentinel;
            sentinel.prev = sentinel;
        } else {
            element = sentinel.next.item;        // extracting the item for return
            sentinel.next = sentinel.next.next; //assigning the sec item as first element
            sentinel.next.prev = sentinel;     // changing our new first's prev pointer
        }
        this.size -= 1;
        return element;
    }
    public Item removeLast() {
        Item element;

        if (this.size() == 0) {
            return null;
        } else if (this.size() == 1) {
            element = sentinel.next.item;
            sentinel.next = sentinel;
            sentinel.prev = sentinel;
        } else {
            element = sentinel.prev.item;        // extracting the item for return
            sentinel.prev = sentinel.prev.prev; //assiging item before last as the last item
            sentinel.prev.next = sentinel;
        }
        this.size -= 1;
        return element;
    }
    public Item get(int index) {
        if (size == 0) {
            return null;
        } else if (index >= this.size()) {
            return null;
        }
        Node list = sentinel.next;
        while (index > 0) {
            list = list.next;
            index -= 1;
        }
        return list.item;
    }
    private Item getNext(Node element, int index) {
        if (size == 0) {
            return null;
        } else if (index >= this.size()) {
            return null;
        }
        
        if (index == 0) {
            return element.item;
        } else {
            return getNext(element.next, index - 1);
        }
    }
    public Item getRecursive(int index) {
        return getNext(sentinel.next, index);
    }
}
