package db;

import static org.junit.Assert.*;

class Pair {
    private int[] pair;

    @Override
    public String toString() {
        int first = this.first();
        int second = this.second();
        String elem = "< " + first + " " + second + " >";
        return elem;
    }

    Pair(int num1, int num2) {
        pair = new int[2];
        pair[0] = num1;
        pair[1] = num2;
    }

    int[] get() {
        return pair;
    }

    int first() {
        return pair[0];
    }

    int second() {
        return pair[1];
    }

    @Override
    public boolean equals(Object obj) {
        Pair pair2 = (Pair) obj;
        return (this.first() == pair2.first() && this.second() == pair2.second());
    }


    //                            ****** TESTED WORKS FINE ******

    /*@Test
    public static void tester() {

        Pair pair1 = new Pair(1, 2);
        Pair pair2 = new Pair(1,2);
        Pair pair3 = new Pair(1,4);
        Pair pair4 = new Pair(5, 6);
        int num1 = 5;
        int num2 = 6;
        int num3 = 1;
        int num4 = 2;

        assertEquals(pair1, pair2);
        assertNotEquals(pair1, pair3);
        assertNotEquals(pair3, pair4);
        assertEquals(pair1.first(), num3);
        assertEquals(pair4.second(), num2);


    }

    public static void main(String[]args) {
        tester();
    }*/
}
