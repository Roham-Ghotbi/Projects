package db;

import static org.junit.Assert.*;

import java.util.ArrayList;

public class Column<T> {
    private String type;
    private String header;
    private ArrayList<Integer> naN;     //for later use not now(After Extra Credit)
    private ArrayList<Integer> noValue;
    private ArrayList<T> column;

    Column(String type, String header) {
        this.type = type;
        this.header = header;
        this.column = new ArrayList<>();
        this.naN = new ArrayList<>();
        this.noValue = new ArrayList<>();
    }

    void addElements(T[] elements) {
        for (T elem : elements) {
            this.addElement(elem);
        }
    }

    void addElement(T element) {
        column.add(element);
    }

    T get(int index) {
        return column.get(index);
    }

    Column getCopy() {
        Column copy = new Column(this.type, this.header);

        for (T elem : (ArrayList<T>)column.clone()) {
            copy.addElement(elem);
        }

        copy.noValue = this.noValue;
        copy.naN = this.naN;

        return copy;
    }

    int size() {
        return column.size();
    }

    String getType() {
        return this.type;
    }

    ArrayList<T> getRows(int[] index) {
        ArrayList<T> elements = new ArrayList<>();
        for (int i : index) {

            elements.add(column.get(i));
        }

        return elements;
    }

    void deleteElement(int index) {
        this.column.remove(index);
    }

    String getHeader() {
        return this.header;
    }

    Column add(String name, Column column2) {
        String typeColumn = "int";
        if ((this.type.equals("float") && column2.type.equals("int"))
                || (this.type.equals("int") && column2.type.equals("float"))) {

            typeColumn = "float";

        } else {
            typeColumn = this.type;
        }
        Column newColumn = new Column(typeColumn, name);
        try {
            for (int i = 0; i < this.column.size(); i++) {
                if (this.naN.contains(i) || column2.naN.contains(i)) {
                    newColumn.naN.add(i);
                    newColumn.addElement(null);
                } else {
                    if (typeColumn.equals("string")) {
                        String str1 = this.get(i).toString();
                        String str2 = column2.get(i).toString();
                        newColumn.addElement(str1.substring(0, str1.length() - 1)
                                + str2.substring(1));
                    } else if (typeColumn.equals("float")) {

                        if (this.type.equals("int")) {
                            Integer intNum = (Integer) this.get(i);
                            Float num1 = (float) intNum;
                            newColumn.addElement(num1 + (Float) column2.get(i));
                        } else if (column2.type.equals("int")) {
                            Integer intNum = (Integer) column2.get(i);
                            Float num2 = (float) intNum;
                            newColumn.addElement((Float) this.get(i) + num2);
                        } else {
                            newColumn.addElement((Float) this.get(i) + (Float) column2.get(i));
                        }

                    } else {
                        newColumn.addElement((Integer) this.get(i) + (Integer) column2.get(i));
                    }
                }
            }
            return newColumn;
        } catch (ClassCastException e) {
            return null;
        }
    }

    Column subtract(String name, Column column2) {
        String typeColumn = "int";
        if (this.type.equals("float") || column2.type.equals("float")) {
            typeColumn = "float";
        }
        Column newColumn = new Column(typeColumn, name);
        try {
            for (int i = 0; i < this.column.size(); i++) {
                if (this.naN.contains(i) || column2.naN.contains(i)) {
                    newColumn.naN.add(i);
                    newColumn.addElement(null);
                } else {
                    if (typeColumn.equals("float")) {

                        if (this.type.equals("int")) {
                            Integer intNum = (Integer) this.get(i);
                            Float num1 = (float) intNum;
                            newColumn.addElement(num1 - (Float) column2.get(i));
                        } else if (column2.type.equals("int")) {
                            Integer intNum = (Integer) column2.get(i);
                            Float num2 = (float) intNum;
                            newColumn.addElement((Float) this.get(i) - num2);
                        } else {
                            newColumn.addElement((Float) this.get(i) - (Float) column2.get(i));
                        }

                    } else {
                        newColumn.addElement((Integer) this.get(i) - (Integer) column2.get(i));
                    }
                }
            }

            return newColumn;
        } catch (ClassCastException e) {
            return null;
        }
    }

    Column mult(String name, Column column2) {
        String typeColumn = "int";
        if (this.type.equals("float") || column2.type.equals("float")) {
            typeColumn = "float";
        }
        Column newColumn = new Column(typeColumn, name);
        try {
            for (int i = 0; i < this.column.size(); i++) {
                if (this.naN.contains(i) || column2.naN.contains(i)) {
                    newColumn.naN.add(i);
                    newColumn.addElement(null);
                } else {
                    if (typeColumn.equals("float")) {

                        if (this.type.equals("int")) {
                            Integer intNum = (Integer) this.get(i);
                            Float num1 = (float) intNum;
                            newColumn.addElement(num1 * (Float) column2.get(i));
                        } else if (column2.type.equals("int")) {
                            Integer intNum = (Integer) column2.get(i);
                            Float num2 = (float) intNum;
                            newColumn.addElement((Float) this.get(i) * num2);
                        } else {
                            newColumn.addElement((Float) this.get(i) * (Float) column2.get(i));
                        }

                    } else {
                        newColumn.addElement((Integer) this.get(i) * (Integer) column2.get(i));
                    }
                }
            }

            return newColumn;
        } catch (ClassCastException e) {
            return null;
        }
    }

    Column division(String name, Column column2) {
        String typeColumn = "int";
        if (this.type.equals("float") || column2.type.equals("float")) {
            typeColumn = "float";
        }
        Column newColumn = new Column(typeColumn, name);
        try {
            for (int i = 0; i < this.column.size(); i++) {
                if (this.naN.contains(i) || column2.naN.contains(i)) {
                    newColumn.naN.add(i);
                    newColumn.addElement(null);
                } else {
                    if (typeColumn.equals("float")) {
                        try {

                            if (this.type.equals("int")) {
                                Integer intNum = (Integer) this.get(i);
                                Float num1 = (float) intNum;
                                if ((Float) column2.get(i) != 0.0) {
                                    newColumn.addElement(num1 / (Float) column2.get(i));
                                } else {
                                    newColumn.naN.add(i);
                                    newColumn.addElement(null);
                                }
                            } else if (column2.type.equals("int")) {
                                Integer intNum = (Integer) column2.get(i);
                                Float num2 = (float) intNum;
                                if (num2 != 0.0) {
                                    newColumn.addElement((Float) this.get(i) / num2);
                                } else {
                                    newColumn.naN.add(i);
                                    newColumn.addElement(null);
                                }
                            } else {
                                if ((Float) column2.get(i) != 0.0) {
                                    newColumn.addElement((Float) this.get(i)
                                                            / (Float) column2.get(i));
                                } else {
                                    newColumn.naN.add(i);
                                    newColumn.addElement(null);
                                }
                            }

                        } catch (ArithmeticException e) {
                            newColumn.addElement(null);
                            newColumn.makeNaN(i);
                        }
                    } else {
                        try {
                            newColumn.addElement((Integer) this.get(i) / (Integer) column2.get(i));
                        } catch (ArithmeticException e) {
                            newColumn.addElement(null);
                            newColumn.makeNaN(i);
                        }
                    }
                }
            }


            return newColumn;
        } catch (ClassCastException e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Column<T> column1 = (Column<T>) o;

        if (type != null ? !type.equals(column1.type) : column1.type != null) {
            return false;
        }
        if (header != null ? !header.equals(column1.header) : column1.header != null) {
            return false;
        }
        if (this.size() != column1.size()) {
            return false;
        } else {
            for (int i = 0; i < this.size(); i++) {
                if (!column.get(i).equals(column1.get(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    ArrayList<Pair> indicesConnector(Column column2, ArrayList<Pair> sameElems) {
        ArrayList<Pair> indices = new ArrayList<>();

        if (sameElems != null && sameElems.size() != 0) {
            for (Pair pair : sameElems) {
                if (this.column.get(pair.first()).equals(column2.get(pair.second()))) {
                    indices.add(pair);
                }
            }
        } else {
            for (int i = 0; i < this.column.size(); i++) {
                for (int j = 0; j < column2.size(); j++) {
                    if (column.get(i).equals(column2.get(j))) {
                        indices.add(new Pair(i, j));
                    }
                }
            }
        }
        return indices;
    }

    void makeNoVal(int index) {
        noValue.add(index);
    }

    void makeNaN(int index) {
        naN.add(index);
    }

    boolean isNaN(int index) {
        return naN.contains(index);
    }

    boolean isNoVal(int index) {
        return noValue.contains(index);
    }

    //                                           ****** TESTED WORKS FINE ******


    public static void tester() {
        Column<String> column1 = new Column<>("String", "Names");
        Column<String> column2;
        Column<Integer> column3 = new Column<>("int", "Age");
        Column<Integer> column4;
        Column<Integer> column8 = new Column<>("int", "Age");

        Column<Float> column5 = new Column<>("float", "Grade");
        Column<Float> column6;
        ArrayList<Pair> indices = new ArrayList<>();

        ArrayList<String> oddNames = new ArrayList<>();
        oddNames.add("Roham");
        oddNames.add("Ronaldo");
        oddNames.add("Doodool Tala");

        int[] odds = {0, 2, 4};
        int[] oddsAndLast = {0, 2, 4, 5};

        String[] names = {"Roham", "Adel", "Ronaldo", "Oscar", "Doodool Tala"};
        Integer[] ages = {20, 19, 32, 18, 88};
        Integer[] ages2 = {20, 32, 45, 56, 73, 18};
        Float[] grades = {60.5f, 73.5f, 99.75f, 78f, 33.56f};

        column1.addElements(names);
        column3.addElements(ages);
        column5.addElements(grades);
        column8.addElements(ages2);

        indices = column8.indicesConnector(column3, null);

        System.out.println(indices);

        assertEquals(column1, column1);
        assertEquals(column3, column3);
        assertEquals(column5, column5);

        assertEquals(column1, column1.getCopy());
        assertEquals(column3, column3.getCopy());
        assertEquals(column5, column5.getCopy());

        assertEquals("String", column1.getType());
        assertEquals("int", column3.getType());
        assertEquals("float", column5.getType());

        assertEquals("Names", column1.getHeader());
        assertEquals("Age", column3.getHeader());
        assertEquals("Grade", column5.getHeader());

        assertEquals(5, column1.size());
        assertNotEquals(4, column3.size());

        column2 = column1.getCopy();
        column4 = column3.getCopy();
        column6 = column5.getCopy();

        column2.addElement("Fereydun");
        column4.addElement(45);
        column6.addElement(100.0f);

        assertNotEquals(column1, column2);
        assertNotEquals(column3, column4);
        assertNotEquals(column5, column6);

        assertEquals(6, column2.size());
        assertNotEquals(13, column3.size());

        assertEquals(oddNames, column1.getRows(odds));
        assertNotEquals(oddNames, column2.getRows(oddsAndLast));

    }

    public static void main(String[] args) {

        tester();
    }
}
