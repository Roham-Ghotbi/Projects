public class OffByN implements CharacterComparator {

    private int diff;

    public OffByN(int N) {
        diff = N;
    }
    @Override
    public  boolean equalChars(char elem1, char elem2) {
        return Math.abs((elem1 - elem2)) == diff;
    }
}
