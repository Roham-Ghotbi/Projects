public class ArrayDeque<Item> implements Deque<Item>{
    private Item[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    private int mod(int x, int y) {     //with help of internet
        int result = x % y;
        if (result < 0) {
            result += y;
        }
        return result;
    }

    public ArrayDeque() {
        this.items = (Item[]) new Object[8];
        this.size = 0;
        this.nextFirst = 7;
        this.nextLast = 0;
    }

    private void resize(int change) {
        Item[] temp = (Item[]) new Object[change];
        int j = 0;
        int start = mod(nextFirst + 1, items.length);
        for (int i = 0; i < size; i++) {
            temp[j] = items[start];
            j += 1;
            start = mod(start + 1, items.length);
        }

        items = temp;
        nextFirst = change - 1;
        nextLast = size;
    }
    private void sizeCut() {
        if ((double) (size) / items.length <= 0.25 && items.length > 16) {
            resize(this.items.length);
            Item[] temp = (Item[]) new Object[items.length / 2];
            System.arraycopy(items, 0, temp, 0, this.size);
            this.items = temp;
            this.nextFirst = this.items.length - 1;
            this.nextLast = size;

        }
    }
    public void addFirst(Item item) {
        if (size == items.length) {
            resize(2 * items.length);
        }

        this.items[nextFirst] = item;
        nextFirst = mod(nextFirst - 1, items.length);
        size += 1;

    }
    public void addLast(Item item) {
        if (size == items.length) {
            resize(2 * items.length);
        }

        this.items[nextLast] = item;
        nextLast = mod(nextLast + 1, items.length);
        size += 1;
    }
    public boolean isEmpty() {
        return this.size == 0;
    }
    public int size() {
        return this.size;
    }
    public void printDeque() {
        int start = mod(nextFirst + 1, items.length);
        for (int i = 0; i < items.length; i++) {
            if (items[start] != null) {
                System.out.print(items[start] + " ");
            }
            start = mod(start + 1, items.length);
        }
        System.out.println(" ");
    }
    public Item removeFirst() {
        Item element;
        if (size == 0) {
            return null;
        } else {
            nextFirst = mod(nextFirst + 1, items.length);
            element = items[nextFirst];
            items[nextFirst] = null;
            size -= 1;
            sizeCut();
            return element;
        }
    }
    public Item removeLast() {
        Item element;
        if (size == 0) {
            return null;
        } else {
            nextLast = mod(nextLast - 1, items.length);
            element = items[nextLast];
            items[nextLast] = null;
            size -= 1;
            sizeCut();
            return element;
        }
    }
    public Item get(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        int start = mod(nextFirst + 1, items.length);
        int x = mod(index + start, items.length);
        return items[x];
    }
}
