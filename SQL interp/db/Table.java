package db;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Table {
    private int numRows = 0;
    private ArrayList<String> headers;
    private int numColumns = 0;
    private Map<String, Column> table;


    Table() {
        this.table = new HashMap<>();
        headers = new ArrayList<>();
    }

    Table(String[] colNames, String[] colTypes) {
        this.table = new HashMap<>();
        this.headers = new ArrayList<>();
        for (int i = 0; i < colNames.length; i++) {

            String type = colTypes[i];
            headers.add(colNames[i]);
            numColumns += 1;
            if (type.equals("string")) {
                table.put(colNames[i], new Column<String>(type, colNames[i]));
            } else if (type.equals("int")) {
                table.put(colNames[i], new Column<Integer>(type, colNames[i]));
            } else if ((type.equals("float"))) {
                table.put(colNames[i], new Column<Float>(type, colNames[i]));
            } else {
                System.err.println("Malformed Column!");
            }

        }

        numRows = table.get(colNames[0]).size();

    }

    int getNumRows() {
        return this.numRows;
    }

    void setNumRows() {
        this.numRows += 1;
    }

    int getNumColumns() {
        return this.numColumns;
    }

    void addColumn(String header, Column column) {
        headers.add(header);
        table.put(header, column);
        numColumns += 1;
        numRows = column.size();
    }

    private Table copy() {
        Table copy = new Table();
        for (String key : this.headers) {
            copy.addColumn(key, this.getColumn(key));
        }

        return copy;
    }

    String[] getHeaders() {
        return this.headers.toArray(new String[this.headers.size()]);
    }

    private ArrayList<String> sameHeadFinder(Table table2) {
        ArrayList<String> sameHeads = new ArrayList<>();

        for (String head1 : this.headers) {
            for (String head2 : table2.headers) {   //Finding the same Headers
                if (head1.equals(head2)) {
                    sameHeads.add(head1);
                }
            }
        }

        return sameHeads;
    }

    private ArrayList<String> joinHeadMerger(Table table2, ArrayList<String> sameHeads) {
        ArrayList<String> mergedHeads = new ArrayList<>();
        mergedHeads.addAll(sameHeads);

        for (String head : this.headers) {
            if (!mergedHeads.contains(head)) {
                mergedHeads.add(head);
            }
        }

        for (String head : table2.headers) {
            if (!mergedHeads.contains(head)) {
                mergedHeads.add(head);
            }
        }

        return mergedHeads;
    }

    private ArrayList<String> joinTypeMerger(Table table2, ArrayList<String> mergedHeads) {
        ArrayList<String> types = new ArrayList<>();
        for (String head : mergedHeads) {
            if (this.table.containsKey(head)) {
                types.add(this.getColumnType(head));
            } else {
                types.add(table2.getColumnType(head));
            }
        }
        return types;
    }

    private static int[] joinPairsFirst(Pair[] pairs) {
        int[] pairsFirst = new int[pairs.length];
        for (int i = 0; i < pairs.length; i++) {
            pairsFirst[i] = pairs[i].first();
        }
        return pairsFirst;
    }

    private static int[] joinPairsSecond(Pair[] pairs) {
        int[] pairsSecond = new int[pairs.length];
        for (int i = 0; i < pairs.length; i++) {
            pairsSecond[i] = pairs[i].second();
        }
        return pairsSecond;
    }

    static Table join(Table[] tables) {

        Table table1 = tables[0].copy();
        Table newTable = new Table();

        for (int i = 0; i < tables.length; i++) {
            ArrayList<String> sameHeads = table1.sameHeadFinder(tables[i]);
            ArrayList<String> newTableHeaders = table1.joinHeadMerger(tables[i], sameHeads);
            ArrayList<String> newTableTypes = table1.joinTypeMerger(tables[i], newTableHeaders);

            if (table1.equals(tables[i])) {          // check to see if tables are exactly same
                newTable = table1;
            } else if (sameHeads.size() == 0) {     //cartesian multiplication case
                newTable = concatenate(table1, tables[i]);
            } else {
                String[] colHeaders =
                        newTableHeaders.toArray(new String[newTableHeaders.size()]);
                String[] colTypes = newTableTypes.toArray(new String[newTableTypes.size()]);
                String[] sameColsHeads =
                        sameHeads.toArray(new String[sameHeads.size()]);
                newTable = new Table(colHeaders, colTypes);


                Pair[] sameElements = sameValueIndexFinder(sameColsHeads, table1, tables[i]);

                int[] first = joinPairsFirst(sameElements);
                int[] second = joinPairsSecond(sameElements);

                for (String head : table1.getHeaders()) {
                    for (int index : first) {
                        newTable.getColumn(head).addElement(table1.getColumn(head).get(index));
                        if (table1.getColumn(head).isNaN(index)) {
                            newTable.getColumn(head).makeNaN(index);
                        } else if (table1.getColumn(head).isNoVal(index)) {
                            newTable.getColumn(head).makeNoVal(index);
                        }
                    }
                }

                for (String head : newTable.getHeaders()) {
                    if (tables[i].table.containsKey(head) && !table1.table.containsKey(head)) {
                        for (int index : second) {
                            Column col = tables[i].getColumn(head);
                            newTable.getColumn(head).addElement(col.get(index));
                            if (col.isNaN(index)) {
                                newTable.getColumn(head).makeNaN(index);
                            } else if (col.isNoVal(index)) {
                                newTable.getColumn(head).makeNoVal(index);
                            }
                        }
                    }

                }

            }
            table1 = newTable;
        }

        String key = newTable.getHeaders()[0];   //just to update the numRows and numColumns
        newTable.numRows = newTable.getColumn(key).size();
        newTable.numColumns = newTable.getHeaders().length;

        return newTable;
    }

    private static Table concatenate(Table table1, Table table2) {
        ArrayList<String> headers = table1.joinHeadMerger(table2, new ArrayList<>());
        ArrayList<String> types = table1.joinTypeMerger(table2, headers);

        String[] colHeaders =
                headers.toArray(new String[headers.size()]);
        String[] colTypes = types.toArray(new String[types.size()]);

        Table newTable = new Table(colHeaders, colTypes);
        for (int i = 0; i < table1.numRows; i++) {
            for (int j = 0; j < table2.numRows; j++) {
                for (String head1 : table1.headers) {
                    newTable.getColumn(head1).addElement(table1.getColumn(head1).get(i));
                    if (table1.getColumn(head1).isNaN(i)) {
                        newTable.getColumn(head1).makeNaN(i);
                    } else if (table1.getColumn(head1).isNoVal(i)) {
                        newTable.getColumn(head1).makeNoVal(i);
                    }
                }
                for (String head2 : table2.headers) {
                    newTable.getColumn(head2).addElement(table2.getColumn(head2).get(j));
                    if (table2.getColumn(head2).isNaN(j)) {
                        newTable.getColumn(head2).makeNaN(j);
                    } else if (table2.getColumn(head2).isNoVal(j)) {
                        newTable.getColumn(head2).makeNoVal(j);
                    }
                }
            }
        }

        String key = newTable.getHeaders()[0];   //just to update the numRows and numColumns
        newTable.numRows = newTable.getColumn(key).size();
        newTable.numColumns = newTable.getHeaders().length;

        return newTable;
    }

    boolean hasColumn(String key) {
        return this.table.containsKey(key);
    }

    Column getColumn(String key) {
        return this.table.get(key);
    }

    String getColumnType(String key) {
        return this.getColumn(key).getType();
    }

    String[] getColumnsTypes() {

        String[] types = new String[headers.size()];

        for (int i = 0; i < headers.size(); i++) {
            types[i] = this.getColumnType(headers.get(i));
        }

        return types;
    }


    void deleteRow(int rowNum) {

        for (String key : this.headers) {
            this.getColumn(key).deleteElement(rowNum);
        }
        numRows -= 1;
    }
    
    void deleteColumn(String header) {
        this.table.remove(header);
        this.headers.remove(header);
        numColumns -= 1;
    }

    void reorder(ArrayList<String> heads) {
        headers = heads;
    }
    
    private static Pair[] sameValueIndexFinder(String[] colHeaders, Table table1, Table table2) {

        ArrayList<Pair> indices = new ArrayList<>();
        for (String head : colHeaders) {
            if (table1.table.containsKey(head) && table2.table.containsKey(head)) {
                indices = table1.getColumn(head).indicesConnector(table2.getColumn(head), indices);
            }
        }

        return indices.toArray(new Pair[indices.size()]);
    }

    String print() {
        String header = "";
        String rest = "";

        for (String key : this.headers) {
            header += key + " " + getColumn(key).getType() + ",";
        }
        header = header.substring(0, header.length() - 1) + "\n";

        for (int i = 0; i < numRows; i++) {
            for (String key : this.headers) {
                if (!getColumn(key).isNoVal(i) && !getColumn(key).isNaN(i)) {
                    if (getColumn(key).getType().equals("string")) {
                        rest += getColumn(key).get(i) + ","; //we add if statements to make
                    } else if (getColumn(key).getType().equals("int")) { //it work with NaN & NoVal
                        rest += getColumn(key).get(i) + ",";
                    } else if (getColumn(key).getType().equals("float")) {
                        rest += String.format("%.03f", getColumn(key).get(i)) + ",";
                    }
                } else if (getColumn(key).isNaN(i)) {
                    rest += "NaN,";
                } else {
                    rest += "NOVALUE,";
                }
            }

            rest = rest.substring(0, rest.length() - 1) + "\n";
        }
        return header + rest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Table table1 = (Table) o;
        String[] head1 = table1.getHeaders();
        String[] head = this.getHeaders();

        if (numRows != table1.numRows) {
            return false;
        }
        if (numColumns != table1.numColumns) {
            return false;
        }
        if (head.length == head1.length) {
            for (int i = 0; i < head.length; i++) {
                if (!this.getColumn(head[i]).equals(table1.getColumn(head1[i]))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }

    }


    //                                   ****** TESTED WORKS FINE ******



   /* public static void tester() {
        Column <String> column1 = new Column<>("string", "Names");
        Column <Integer> column2 = new Column<>("int", "Age");
        Column <Float> column3 = new Column<>("float", "Grade");

        String[] names = {"Roham", "Adel", "Ronaldo", "Oscar", "Doodool Tala"};
        Integer[] ages = {20, 19, 32, 18, 19};
        Float[] grades = {60.5f, 73.5f, 99.75f, 78f, 33.56f};

        column1.addElements(names);
        column2.addElements(ages);
        column3.addElements(grades);

        Table table1 = new Table();
        Table table2;
        Table table3 = new Table();

        table1.addColumn(column1.getHeader(), column1);
        table1.addColumn(column2.getHeader(), column2);
        table1.addColumn(column3.getHeader(), column3);

        table2 = table1.copy();

        assertEquals(3, table1.getNumColumns());
        assertEquals(5, table1.getNumRows());
        assertEquals(3, table2.getNumColumns());
        assertEquals(5, table2.getNumRows());

        assertEquals(column2, table2.getColumn("Age"));

        Column <Integer> X1 = new Column<>("int", "x");
        Column <Integer> Y1 = new Column<>("int", "y");
        Column <Integer> X2 = new Column<>("int", "x");
        Column <Integer> Z2 = new Column<>("int", "z");
        Column <Integer> X4 = new Column<>("int", "x");
        Column <Integer> Y4 = new Column<>("int", "y");
        Column <Integer> X5 = new Column<>("int", "x");
        Column <Integer> Z5 = new Column<>("int", "z");
        Column <Integer> X7 = new Column<>("int", "x");
        Column <Integer> Y7 = new Column<>("int", "y");
        Column <Integer> Z7 = new Column<>("int", "z");
        Column <Integer> W7 = new Column<>("int", "w");
        Column <Integer> W8 = new Column<>("int", "w");
        Column <Integer> B8 = new Column<>("int", "c");
        Column <Integer> Z8 = new Column<>("int", "z");
        Column <Integer> X10 = new Column<>("int", "x");
        Column <Integer> Y10 = new Column<>("int", "y");
        Column <Integer> X11 = new Column<>("int", "x");
        Column <Integer> Z11 = new Column<>("int", "z");
        Column <Integer> X12 = new Column<>("int", "x");
        Column <Integer> Y12 = new Column<>("int", "y");
        Column <Integer> A13 = new Column<>("int", "a");
        Column <Integer> B13 = new Column<>("int", "b");

        Integer[] x1 = {2,8,13};
        Integer[] y1 = {5,3,7};
        Integer[] x2 = {2,8,10,11};
        Integer[] z2 = {4,9,1,1};
        Integer[] x4 = {1,2,3};
        Integer[] y4 = {4,5,6};
        Integer[] x5 = {1,7,1,1};
        Integer[] z5 = {7,7,9,11};
        Integer[] x7 = {1,7,1};
        Integer[] y7 = {7,7,9};
        Integer[] z7 = {2,4,9};
        Integer[] w7 = {10,1,1};
        Integer[] w8 = {1,7,1,1};
        Integer[] b8 = {7,7,9,11};
        Integer[] z8 = {4,3,6,9};
        Integer[] x10 = {1,7,1};
        Integer[] y10 = {7,7,9};
        Integer[] x11 = {3,4,5};
        Integer[] z11 = {8,9,10};
        Integer[] x12 = {1,7,1};
        Integer[] y12 = {7,7,9};
        Integer[] a13 = {3,4,5};
        Integer[] b13 = {8,9,10};

        X1.addElements(x1);
        Y1.addElements(y1);
        X2.addElements(x2);
        Z2.addElements(z2);
        X4.addElements(x4);
        Y4.addElements(y4);
        X5.addElements(x5);
        Z5.addElements(z5);
        X7.addElements(x7);
        Y7.addElements(y7);
        Z7.addElements(z7);
        W7.addElements(w7);
        W8.addElements(w8);
        B8.addElements(b8);
        Z8.addElements(z8);
        X10.addElements(x10);
        Y10.addElements(y10);
        X11.addElements(x11);  // I have created all the columns from the examples in the lab
        Z11.addElements(z11);  // all U have to do now is to create the tables based on the number
        X12.addElements(x12);  //each number means a different table like all elements with num 13
        Y12.addElements(y12);  // belong to table 13 with the same order they are defined
        A13.addElements(a13);  // then use created tables to check join
        B13.addElements(b13);

        Table t1 = new Table();
        Table t2 = new Table();
        Table t3;
        Table t4 = new Table();
        Table t5 = new Table();
        Table t6;
        Table t7 = new Table();
        Table t8 = new Table();
        Table t9;
        Table t10 = new Table();
        Table t11 = new Table();
        Table t15;
        Table t12 = new Table();
        Table t13 = new Table();
        Table t14;

        t1.addColumn("x", X1);
        t1.addColumn("y", Y1);

        t2.addColumn("x", X2);
        t2.addColumn("z", Z2);

        t4.addColumn("x", X4);
        t4.addColumn("y", Y4);

        t5.addColumn("x", X5);
        t5.addColumn("z", Z5);

        t7.addColumn("x", X7);
        t7.addColumn("y", Y7);
        t7.addColumn("z", Z7);
        t7.addColumn("w", W7);

        t8.addColumn("w", W8);
        t8.addColumn("b", B8);
        t8.addColumn("z", Z8);

        t10.addColumn("x", X10);
        t10.addColumn("y", Y10);

        t11.addColumn("x", X11);
        t11.addColumn("z", Z11);

        t12.addColumn("x", X12);
        t12.addColumn("y", Y12);

        t13.addColumn("a", A13);
        t13.addColumn("b", B13);

        String[] common = new String[2];
        common[0] = "z";
        common[1] = "w";

        Pair[] pairs = sameValueIndexFinder(common,t7, t8);


        Table[] tables1 = new Table[2];
        Table[] tables2 = new Table[2];
        Table[] tables3 = new Table[2];
        Table[] tables4 = new Table[2];
        Table[] tables5 = new Table[2];

        tables1[0] = t1;
        tables1[1] = t2;

        tables2[0] = t4;
        tables2[1] = t5;

        tables3[0] = t7;
        tables3[1] = t8;

        tables4[0] = t10;
        tables4[1] = t11;

        tables5[0] = t12;
        tables5[1] = t13;


        t3 = join(tables1);
        t6 = join(tables2);
        t9 = join(tables3);
        t15 = join(tables4);
        t14 = join(tables5);

        System.out.println(t1.print());
        System.out.println("");
        System.out.println(t2.print());
        System.out.println("");
        System.out.println(t4.print());
        System.out.println("");
        System.out.println(t5.print());
        System.out.println("");
        System.out.println(t7.print());
        System.out.println("");
        System.out.println(t8.print());
        System.out.println("");
        System.out.println(t10.print());
        System.out.println("");
        System.out.println(t11.print());
        System.out.println("");
        System.out.println(t12.print());
        System.out.println("");
        System.out.println(t13.print());
        System.out.println("");
        System.out.println(t3.print());
        System.out.println("");
        System.out.println(t6.print());
        System.out.println("");
        System.out.println(t9.print());
        System.out.println("");
        System.out.println(t15.print());
        System.out.println("");
        System.out.println(t14.print());

    }

    public static void main(String[]args) {
        tester();
    }*/
}
