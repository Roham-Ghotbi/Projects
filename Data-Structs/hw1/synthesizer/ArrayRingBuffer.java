// Make sure to make this class a part of the synthesizer package
package synthesizer;
import java.util.Iterator;

//: Make sure to make this class and all of its methods public
//: Make sure to make this class extend AbstractBoundedQueue<t>
public class ArrayRingBuffer<T> extends AbstractBoundedQueue<T>  {
    /* Index for the next dequeue or peek. */
    private int first;            // index for the next dequeue or peek
    /* Index for the next enqueue. */
    private int last;
    /* Array for storing the buffer data. */
    private T[] rb;

    private int mod(int num1, int num2) {        // just a different mod which will always give
        if (num1 % num2 >= 0) {                  // you a positive answer
            return num1 % num2;
        } else {
            return (num1 % num2) + num2;
        }
    }

    /**
     * Create a new ArrayRingBuffer with the given capacity.
     */
    public ArrayRingBuffer(int capacity) {
        // : Create new array with capacity elements.
        //       first, last, and fillCount should all be set to 0.
        //       this.capacity should be set appropriately. Note that the local variable
        //       here shadows the field we inherit from AbstractBoundedQueue, so
        //       you'll need to use this.capacity to set the capacity.
        this.fillCount = 0;
        this.last = 0;
        this.first = 0;
        this.capacity = capacity;
        this.rb = (T[]) new Object [this.capacity];
    }

    /**
     * Adds x to the end of the ring buffer. If there is no room, then
     * throw new RuntimeException("Ring buffer overflow"). Exceptions
     * covered Monday.
     */

    public void enqueue(T x) {
        // : Enqueue the item. Don't forget to increase fillCount and update last.
        if (fillCount == capacity) {
            throw new RuntimeException("Ring Buffer Overflow");
        } else {
            fillCount += 1;
            rb[last] = x;
            last = mod(last + 1, capacity);
        }

    }


    /**
     * Dequeue oldest item in the ring buffer. If the buffer is empty, then
     * throw new RuntimeException("Ring buffer underflow"). Exceptions
     * covered Monday.
     */
    public T dequeue() {
        // : Dequeue the first item. Don't forget to decrease fillCount and update
        if (this.isEmpty()) {
            throw new RuntimeException("Ring Buffer Underflow");

        } else {
            T elem = rb[first];
            rb[first] = null;
            first = mod(first + 1, capacity);
            fillCount -= 1;

            return elem;
        }
    }

    /**
     * Return oldest item, but don't remove it.
     */
    public T peek() {
        // : Return the first item. None of your instance variables should change.
        if (this.isEmpty()) {
            throw new RuntimeException("Ring Buffer Underflow");

        } else {
            return rb[first];
        }
    }
    private class ArrayIterator<T> implements Iterator<T> {
        private int placeHolder;
        private int counter;

        private ArrayIterator() {
            placeHolder = first;
            counter = 0;
        }

        @Override
        public boolean hasNext() {
            return counter < fillCount;
        }

        @Override
        public T next() {
            T elem = (T) rb[placeHolder];
            counter += 1;
            placeHolder = mod(placeHolder + 1, capacity);
            return elem;
        }
    }
    @Override
    public Iterator<T> iterator() {
        return new ArrayIterator();
    }

    // : When you get to part 5, implement the needed code to support iteration.
}
