public class Palindrome {
    public static Deque<Character> wordToDeque(String word) {
        ArrayDequeSolution<Character> list = new ArrayDequeSolution<>();
        for (int i = 0; i < word.length(); i++) {
            list.addLast(word.charAt(i));
        }
        return list;
    }

    public static boolean isPalindrome(String word) {
        if (word.length() == 0 || word.length() == 1) {
            return true;
        } else if (word.charAt(0) == (word.charAt(word.length() - 1))) {

            return isPalindrome(word.substring(1, word.length() - 1));

        } else {
            return false;
        }
    }
    public static boolean isPalindrome(String word, CharacterComparator cc) {
        if (word.length() == 0 || word.length() == 1) {
            return true;
        } else if (cc.equalChars(word.charAt(0), (word.charAt(word.length() - 1)))) {

            return isPalindrome(word.substring(1, word.length() - 1), cc);

        } else {
            return false;
        }
    }
}
