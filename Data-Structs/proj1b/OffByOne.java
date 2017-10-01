public class OffByOne implements CharacterComparator {
    @Override
    public boolean equalChars(char elem1, char elem2) {
        return Math.abs((elem1 - elem2)) == 1;
    }
}
