/** This class outputs all palindromes in the words file in the current directory. */
public class PalindromeFinder {
    public static int[] xify(int[] x){
        int total = 0;
        int i = 0;
        int counter = 0;
        for (int elem: x){
            total += elem;
        }
        int[] array = new int[total];

        while(i < array.length){
            int j = 0;
            while(j < x[counter]){
                array[i] = x[counter];
                j++;
                i++;
            }
            counter++;
        }
        return array;
    }

    public static void main(String[] args) {
        /*int minLength = 4;
        OffByN checker1 = new OffByN(1);
        OffByOne checker2 = new OffByOne();
        In in = new In("words");

        while (!in.isEmpty()) {
            String word = in.readString();
            if (word.length() >= minLength && Palindrome.isPalindrome(word)) {
                System.out.println(word);
            }
            if (word.length() >= minLength && Palindrome.isPalindrome(word, checker1)) {
                System.out.println(word);
            }

            if (word.length() >= minLength && Palindrome.isPalindrome(word, checker2)) {
                System.out.println(word);
            }
        }*/

        int[] x = {3, 2, 1, 4, 10};
        int[] y = xify(x);
        for(int item: y){
            System.out.print(item + " ");
        }
    }
} 
