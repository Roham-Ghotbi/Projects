/**
 * Class for doing Radix sort
 *
 * @author Akhil Batra
 * @version 1.4 - April 14, 2016
 *
 **/
public class RadixSort
{

    /**
     * Does Radix sort on the passed in array with the following restrictions:
     *  The array can only have ASCII Strings (sequence of 1 byte characters)
     *  The sorting is stable and non-destructive
     *  The Strings can be variable length (all Strings are not constrained to 1 length)
     *
     * @param asciis String[] that needs to be sorted
     *
     * @return String[] the sorted array
     **/
    public static String[] sort(String[] asciis)
    {
        String asciSorted[] = new String[asciis.length];
        System.arraycopy(asciis, 0, asciSorted, 0, asciis.length);

        sortHelper(asciSorted, 0, asciis.length, 0);
        return asciSorted;
    }

    /**
     * Radix sort helper function that recursively calls itself to achieve the sorted array
     *  destructive method that changes the passed in array, asciis
     *
     * @param asciis String[] to be sorted
     * @param start int for where to start sorting in this method (includes String at start)
     * @param end int for where to end sorting in this method (does not include String at end)
     * @param index the index of the character the method is currently sorting on
     *
     **/
    private static void sortHelper(String[] asciis, int start, int end, int index)
    {
        if (start + 1 >= end) {
            return;
        }
        int counter[] = new int[258];

        for (int i = start; i < end; i++) {
            int position;
            if (index < asciis[i].length()) {
                position = asciis[i].charAt(index) + 2;
            } else {
                position = 1;
            }
            counter[position] += 1;
        }

        for ( int i = 0; i < counter.length - 1; i++) {
            counter[i + 1] += counter[i];
        }

        String result[] = new String[asciis.length];
        for (int i = start; i < end; i++) {

            int position;
            if (index < asciis[i].length()) {
                position = asciis[i].charAt(index) + 1;
            } else {
                position = 0;
            }
            result[counter[position]] = asciis[i];
            counter[position] += 1;
        }

        System.arraycopy(result, 0, asciis, start, end - start);
        for (int i = 0; i < counter.length - 1; i++) {
            sortHelper(asciis, start + counter[i], start + counter[i + 1], index + 1);
        }
    }
}
