package synthesizer;
import org.junit.Test;
import static org.junit.Assert.*;

/** Tests the ArrayRingBuffer class.
 *  @author Josh Hug
 */

public class TestArrayRingBuffer {
    @Test
    public void someTest() {
        ArrayRingBuffer arb = new ArrayRingBuffer(10);
        ArrayRingBuffer <Double> checker1 = new ArrayRingBuffer<>(4);
        assertEquals("It should be empty", true, checker1.isEmpty());
        checker1.enqueue(9.3);    // 9.3
        checker1.enqueue(15.1);   // 9.3  15.1
        checker1.enqueue(31.2);   // 9.3  15.1  31.2
        assertEquals("It should not be full", false, checker1.isFull());
        checker1.enqueue(-3.1);   // 9.3  15.1  31.2  -3.1
        assertEquals("It should be full", true, checker1.isFull());
    }

    /** Calls tests for ArrayRingBuffer. */
    public static void main(String[] args) {

        jh61b.junit.textui.runClasses(TestArrayRingBuffer.class);
    }
} 
