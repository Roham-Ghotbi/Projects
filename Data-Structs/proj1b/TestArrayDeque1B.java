import static org.junit.Assert.*;
import org.junit.Test;

public class TestArrayDeque1B {
    @Test
    public void arrayTester() {
        StudentArrayDeque<Integer> array1 = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> expected = new ArrayDequeSolution<>();
        OperationSequence message = new OperationSequence();

        for (int i = 0; i < 300; i++) {                         //with a little help from
            double randNum = StdRandom.uniform();           //StudentArrayDequeLauncher
            if (randNum < 0.25) {
                array1.addLast(i);
                expected.addLast(i);
                message.addOperation(new DequeOperation("addLast", i));
                message.addOperation(new DequeOperation("get", i));
                assertEquals(message.toString(), expected.get(expected.size() - 1),
                        array1.get(expected.size() - 1));

            } else if (randNum < 0.5) {
                array1.addFirst(i);
                expected.addFirst(i);
                message.addOperation(new DequeOperation("addFirst", i));
                message.addOperation(new DequeOperation("get", i));
                assertEquals(message.toString(), expected.get(0), array1.get(0));
            } else if (randNum < 0.75) {
                message.addOperation(new DequeOperation("removeFirst"));
                assertEquals(message.toString(), expected.removeFirst(), array1.removeFirst());
            } else {
                message.addOperation(new DequeOperation("removeLast"));
                assertEquals(message.toString(), expected.removeLast(), array1.removeLast());
            }
        }
    }
}
