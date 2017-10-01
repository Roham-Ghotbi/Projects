package db;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Arrays;

public class Database {

    private Map<String, Table> tables;

    public Database() {
        this.tables = new HashMap<>();
    }

    private String create(String name, String[] colNames, String[] colTypes) {
        Table table = new Table(colNames, colTypes);
        tables.put(name, table);

        return "";
    }

    private String createSelect(String name, String table) {
        try {
            InputStream tableStream = new ByteArrayInputStream(table.getBytes());
            BufferedReader reader = new BufferedReader(new InputStreamReader(tableStream));

            String line = reader.readLine();

            String skips = "[ ,]+";
            String[] firstRow = line.split(skips);
            String[] colNames = new String[firstRow.length / 2];
            String[] colTypes = new String[firstRow.length / 2];

            if (firstRow.length % 2 != 0) {
                return "ERROR: Corrupted table!";
            }
            int k = 0;
            for (int i = 0; i < firstRow.length; i += 2) {
                colNames[k] = firstRow[i];
                k += 1;
            }
            int j = 0;
            for (int i = 1; i < firstRow.length; i += 2) {
                colTypes[j] = firstRow[i];
                j += 1;
            }

            this.create(name, colNames, colTypes);

            while ((line = reader.readLine()) != null) {
                String[] row = line.split(skips);

                this.insert(name, row);
            }

            reader.close();

        } catch (IOException e) {
            return "ERROR: " + e.getMessage();
        }

        return "";
    }

    private String load(String tableName) {

        try {
            FileReader file = new FileReader(tableName + ".tbl");
            BufferedReader reader = new BufferedReader(file);

            String line = reader.readLine();

            String skips = "[ ,]+";
            String[] firstRow = line.split(skips);
            String[] colNames = new String[firstRow.length / 2];
            String[] colTypes = new String[firstRow.length / 2];

            if (firstRow.length % 2 != 0) {
                return "ERROR: Corrupted table!";
            }
            int k = 0;
            for (int i = 0; i < firstRow.length; i += 2) {
                colNames[k] = firstRow[i];
                k += 1;
            }
            int j = 0;
            for (int i = 1; i < firstRow.length; i += 2) {
                colTypes[j] = firstRow[i];
                j += 1;
            }

            this.create(tableName, colNames, colTypes);

            while ((line = reader.readLine()) != null) {
                String[] row = line.split(skips);

                String state = this.insert(tableName, row);
                if (state.length() >= 3) {
                    if (state.substring(0, 3).equals("ERR")) {
                        drop(tableName);
                        return state;
                    }
                }
            }

            reader.close();
            file.close();

        } catch (IOException | NullPointerException e) {
            return "ERROR: " + e.getMessage();
        }
        return "";
    }

    private String store(String tableName) {
        try {
            if (tables.containsKey(tableName)) {
                FileWriter file = new FileWriter(tableName + ".tbl");
                PrintWriter writer = new PrintWriter(file);
                writer.print(tables.get(tableName).print());
                writer.close();
            } else {
                return "ERROR: Requested table does not exist!";
            }
        } catch (IOException e) {
            return e.getMessage();
        }

        return "";
    }

    private String drop(String tableName) {
        if (tables.containsKey(tableName)) {
            tables.remove(tableName);
            return "";
        } else {
            return "ERROR: Table does not Exist!";
        }
    }

    private String insert(String tableName, String[] literals) {
        String element;
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 0; i < literals.length; i++) {
            if (literals[i].charAt(0) == '\'') {
                element = literals[i];

                while (literals[i].charAt(literals[i].length() - 1) != '\'') {
                    i += 1;
                    element += (" " + literals[i]);
                }
                temp.add(element);
            } else {
                temp.add(literals[i]);
            }
        }
        literals = temp.toArray(new String[temp.size()]);

        if (!tables.containsKey(tableName)) {
            return "ERROR: Table does not Exist, Try again!";
        } else if (literals.length != this.tables.get(tableName).getHeaders().length) {
            return "ERROR: Corrupted table!";
        }
        Table table = this.tables.get(tableName);
        String[] headers = table.getHeaders();
        String[] types = table.getColumnsTypes();

        try {
            if (literals.length != types.length) {
                return "ERROR: Table and input row length mismatch!";
            }
            for (int i = 0; i < literals.length; i++) {
                if (!literals[i].equals("NOVALUE") && !literals[i].equals("NaN")) {
                    if (types[i].equals("string")) {
                        if (literals[i].charAt(0) != '\'') {
                            throw new IllegalStateException();
                        }
                    } else if (types[i].equals("int")) {
                        Integer.parseInt(literals[i]);
                    } else {
                        Float.parseFloat(literals[i]);
                    }
                }
            }
            for (int i = 0; i < literals.length; i++) {
                if (!literals[i].equals("NOVALUE") && !literals[i].equals("NaN")) {
                    if (table.getColumnType(headers[i]).equals("string")) {
                        table.getColumn(headers[i]).addElement(literals[i]);
                    } else if (table.getColumnType(headers[i]).equals("int")) {
                        table.getColumn(headers[i]).addElement(Integer.parseInt(literals[i]));
                    } else {
                        table.getColumn(headers[i]).addElement(Float.parseFloat(literals[i]));
                    }
                } else if (!literals[i].equals("NOVALUE")) {
                    int size = table.getColumn(headers[i]).size();
                    table.getColumn(headers[i]).addElement(null);
                    table.getColumn(headers[i]).makeNaN(size);

                } else {
                    int size = table.getColumn(headers[i]).size();

                    if (table.getColumnType(headers[i]).equals("string")) {
                        table.getColumn(headers[i]).addElement("\"");
                    } else if (table.getColumnType(headers[i]).equals("int")) {
                        table.getColumn(headers[i]).addElement(0);
                    } else {
                        table.getColumn(headers[i]).addElement(0f);
                    }
                    table.getColumn(headers[i]).makeNoVal(size);
                }
            }

            table.setNumRows();

            return "";

        } catch (IllegalStateException | NumberFormatException | NullPointerException e) {
            return "ERROR: Table and input row Datatype mismatch!";
        }
    }

    private String print(String tableName) {
        return this.tables.get(tableName).print();
    }

    private void itemIntConditionFilter(Table table, String header, String colName,
                                        String comperator, int[] index) {
        if (comperator.equals(">")) {
            while (index[0] < table.getNumRows()) {
                if (!((Integer) table.getColumn(header).get(index[0])
                        > (Integer) table.getColumn(colName).get(index[0]))) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("<")) {
            while (index[0] < table.getNumRows()) {
                if (!((Integer) table.getColumn(header).get(index[0])
                        < (Integer) table.getColumn(colName).get(index[0]))) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("==")) {
            while (index[0] < table.getNumRows()) {
                if (!((Integer) table.getColumn(header).get(index[0])
                        == (Integer) table.getColumn(colName).get(index[0]))) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("!=")) {
            while (index[0] < table.getNumRows()) {
                if (!( table.getColumn(header).get(index[0])
                        !=  table.getColumn(colName).get(index[0]))) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals(">=")) {
            while (index[0] < table.getNumRows()) {
                if (!((Integer) table.getColumn(header).get(index[0])
                        >= (Integer) table.getColumn(colName).get(index[0]))) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("<=")) {
            while (index[0] < table.getNumRows()) {
                if (!((Integer) table.getColumn(header).get(index[0])
                        <= (Integer) table.getColumn(colName).get(index[0]))) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        }

    }

    private void itemFloatConditionFilter(Table table, String header, String colName,
                                          String comperator, int[] index) {
        if (comperator.equals(">")) {
            while (index[0] < table.getNumRows()) {
                if (!((Float) table.getColumn(header).get(index[0])
                        > (Float) table.getColumn(colName).get(index[0]))) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("<")) {
            while (index[0] < table.getNumRows()) {
                if (!((Float) table.getColumn(header).get(index[0])
                        < (Float) table.getColumn(colName).get(index[0]))) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("==")) {
            while (index[0] < table.getNumRows()) {
                if (!((Float) table.getColumn(header).get(index[0])
                        == (Float) table.getColumn(colName).get(index[0]))) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("!=")) {
            while (index[0] < table.getNumRows()) {
                if (!((Float) table.getColumn(header).get(index[0])
                        != (Float) table.getColumn(colName).get(index[0]))) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals(">=")) {
            while (index[0] < table.getNumRows()) {
                if (!((Float) table.getColumn(header).get(index[0])
                        >= (Float) table.getColumn(colName).get(index[0]))) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("<=")) {
            while (index[0] < table.getNumRows()) {
                if (!((Float) table.getColumn(header).get(index[0])
                        <= (Float) table.getColumn(colName).get(index[0]))) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        }

    }

    private void itemStringConditionFilter(Table table, String header, String colName,
                                           String comperator, int[] index) {
        if (comperator.equals(">")) {
            while (index[0] < table.getNumRows()) {
                if (!(((String) table.getColumn(header).get(index[0]))
                        .compareTo((String) table.getColumn(colName).get(index[0])) > 0)) {

                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("<")) {
            while (index[0] < table.getNumRows()) {
                if (!(((String) table.getColumn(header).get(index[0]))
                        .compareTo((String) table.getColumn(colName).get(index[0])) < 0)) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("==")) {
            while (index[0] < table.getNumRows()) {
                if (!(((String) table.getColumn(header).get(index[0]))
                        .compareTo((String) table.getColumn(colName).get(index[0])) == 0)) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("!=")) {
            while (index[0] < table.getNumRows()) {
                if (!(((String) table.getColumn(header).get(index[0]))
                        .compareTo((String) table.getColumn(colName).get(index[0])) != 0)) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals(">=")) {
            while (index[0] < table.getNumRows()) {
                if (!(((String) table.getColumn(header).get(index[0]))
                        .compareTo((String) table.getColumn(colName).get(index[0])) >= 0)) {

                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("<=")) {
            while (index[0] < table.getNumRows()) {
                if (!(((String) table.getColumn(header).get(index[0]))
                        .compareTo((String) table.getColumn(colName).get(index[0])) <= 0)) {

                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        }

    }

    private void intConditionFilter(Table table, String header, int element,
                                    String comperator, int[] index) {

        if (comperator.equals(">")) {
            while (index[0] < table.getNumRows()) {
                if (table.getColumn(header).isNaN(index[0])) {
                    index[0] += 1;
                } else if (!((Integer) table.getColumn(header).get(index[0]) > element)
                        || table.getColumn(header).isNoVal(index[0])) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("<")) {
            while (index[0] < table.getNumRows()) {

                if (table.getColumn(header).isNaN(index[0])
                        || table.getColumn(header).isNoVal(index[0])
                        || !((Integer) table.getColumn(header).get(index[0]) < element)) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("==")) {
            while (index[0] < table.getNumRows()) {
                if (table.getColumn(header).isNaN(index[0])
                        || table.getColumn(header).isNoVal(index[0])
                        || !((Integer) table.getColumn(header).get(index[0]) == element)) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("!=")) {
            while (index[0] < table.getNumRows()) {
                if (!((Integer) table.getColumn(header).get(index[0]) != element)) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals(">=")) {
            while (index[0] < table.getNumRows()) {
                if (table.getColumn(header).isNaN(index[0])) {
                    index[0] += 1;
                } else if (!((Integer) table.getColumn(header).get(index[0]) >= element)
                        || table.getColumn(header).isNoVal(index[0])) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }                               //fixed nan and noval
            }
        } else if (comperator.equals("<=")) {
            while (index[0] < table.getNumRows()) {
                if (table.getColumn(header).isNaN(index[0])
                        || table.getColumn(header).isNoVal(index[0])
                        || !((Integer) table.getColumn(header).get(index[0]) <= element)) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else {
            System.err.println("Wrong Comparator!");
        }

    }

    private void floatConditionFilter(Table table, String header, float element,
                                      String comperator, int[] index) {

        if (comperator.equals(">")) {
            while (index[0] < table.getNumRows()) {
                if (table.getColumn(header).isNaN(index[0])) {
                    index[0] += 1;
                } else if (!((Float) table.getColumn(header).get(index[0]) > element)
                        || table.getColumn(header).isNoVal(index[0])) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("<")) {
            while (index[0] < table.getNumRows()) {

                if (table.getColumn(header).isNaN(index[0])
                        || table.getColumn(header).isNoVal(index[0])
                        || !((Float) table.getColumn(header).get(index[0]) < element)) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("==")) {
            while (index[0] < table.getNumRows()) {
                if (table.getColumn(header).isNaN(index[0])
                        || table.getColumn(header).isNoVal(index[0])
                        || !((Float) table.getColumn(header).get(index[0]) == element)) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("!=")) {
            while (index[0] < table.getNumRows()) {
                if (!((Float) table.getColumn(header).get(index[0]) != element)) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals(">=")) {
            while (index[0] < table.getNumRows()) {
                if (table.getColumn(header).isNaN(index[0])) {
                    index[0] += 1;
                } else if (!((Float) table.getColumn(header).get(index[0]) >= element)
                        || table.getColumn(header).isNoVal(index[0])) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("<=")) {
            while (index[0] < table.getNumRows()) {
                if (table.getColumn(header).isNaN(index[0])
                        || table.getColumn(header).isNoVal(index[0])
                        || !((Float) table.getColumn(header).get(index[0]) <= element)) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else {
            System.err.println("Wrong Comparator!");
        }
    }

    private void stringConditionFilter(Table table, String header, String element,
                                       String comperator, int[] index) {

        if (comperator.equals(">")) {
            while (index[0] < table.getNumRows()) {
                if (/*table.getColumn(header).isNoVal(index[0])
                    ||*/ !(((String) table.getColumn(header).get(index[0]))
                        .compareTo(element) > 0)) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("<")) {
            while (index[0] < table.getNumRows()) {
                if (/*table.getColumn(header).isNoVal(index[0])
                    ||*/ !(((String) table.getColumn(header).get(index[0]))
                        .compareTo(element) < 0)) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("==")) {
            while (index[0] < table.getNumRows()) {
                if (!(((String) table.getColumn(header).get(index[0])).compareTo(element) == 0)) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("!=")) {
            while (index[0] < table.getNumRows()) {
                if (!(((String) table.getColumn(header).get(index[0])).compareTo(element) != 0)) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals(">=")) {
            while (index[0] < table.getNumRows()) {
                if (/*table.getColumn(header).isNoVal(index[0])
                        ||*/ !(((String) table.getColumn(header).get(index[0]))
                        .compareTo(element) >= 0)) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else if (comperator.equals("<=")) {
            while (index[0] < table.getNumRows()) {
                if (/*table.getColumn(header).isNoVal(index[0])
                        || */!(((String) table.getColumn(header).get(index[0]))
                        .compareTo(element) <= 0)) {
                    table.deleteRow(index[0]);
                } else {
                    index[0] += 1;
                }
            }
        } else {
            System.err.println("Wrong Comparator!");
        }

    }

    private void filter(Table newTable, String[] conditions) {
        if (conditions != null) {
            for (int i = 0; i < conditions.length; i += 3) {
                for (int k = 0; k < newTable.getNumColumns(); k++) {
                    if (newTable.getHeaders()[k].equals(conditions[i])) {
                        int[] j = {0};
                        while (j[0] < newTable.getNumRows()) {
                            //for type Column
                            try {
                                if (newTable.getColumn(conditions[i + 2]).getType().equals("int")) {
                                    String colName = conditions[i + 2];

                                    itemIntConditionFilter(newTable, conditions[i],
                                            colName, conditions[i + 1], j);

                                } else if (newTable.getColumn(conditions[i + 2])
                                        .getType().equals("float")) {
                                    String colName = conditions[i + 2];

                                    itemFloatConditionFilter(newTable, conditions[i],
                                            colName, conditions[i + 1], j);

                                } else {
                                    String colName = conditions[i + 2];

                                    itemStringConditionFilter(newTable, conditions[i],
                                            colName, conditions[i + 1], j);

                                }
                            } catch (NullPointerException e) {
                                //for type int
                                if (newTable.getColumn(conditions[i]).getType().equals("int")) {
                                    int element = Integer.parseInt(conditions[i + 2]);
                                    intConditionFilter(newTable, conditions[i],
                                            element, conditions[i + 1], j);

                                    //for type string
                                } else if (newTable.getColumn(conditions[i])
                                        .getType().equals("string")) {
                                    String element = conditions[i + 2];
                                    stringConditionFilter(newTable, conditions[i],
                                            element, conditions[i + 1], j);
                                    //for type float

                                } else if (newTable.getColumn(conditions[i]).getType()
                                        .equals("float")) {
                                    float element = Float.parseFloat(conditions[i + 2]);
                                    floatConditionFilter(newTable, conditions[i],
                                            element, conditions[i + 1], j);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Table arithmeticFilter(Table arithmeticTable, Table newTable,
                                ArrayList<String> arithmetics, ArrayList<String> joinedHeaders,
                                Map<String, String[]> arithmeticMatcher, ArrayList<String> newCols,
                                   ArrayList<String> asNew) {
        if (arithmetics.size() > 0) {

            ArrayList<Column> asColumn = new ArrayList<>();
            Column column = null;
            Column columnTemp1;
            Column columnTemp2;
            for (int i = 0; i < arithmetics.size(); i++) {
                String op = arithmetics.get(i);
                String colName = asNew.get(i);
                String[] columns = arithmeticMatcher.get(op);
                columnTemp1 = newTable.getColumn(columns[0]);
                columnTemp2 = newTable.getColumn(columns[1]);
                if (op.equals("+")) {
                    column = columnTemp1.add(colName, columnTemp2);
                    if (column == null) {
                        return null;
                    }
                } else if (op.equals("-")) {
                    column = columnTemp1.subtract(colName, columnTemp2);
                    if (column == null) {
                        return null;
                    }
                } else if (op.equals("*")) {
                    column = columnTemp1.mult(colName, columnTemp2);
                    if (column == null) {
                        return null;
                    }
                } else {
                    column = columnTemp1.division(colName, columnTemp2);
                    if (column == null) {
                        return null;
                    }
                }
                asColumn.add(column);
            }
            for (String columnName : newCols) {
                if (joinedHeaders.contains(columnName)) {
                    arithmeticTable.addColumn(columnName, newTable.getColumn(columnName));

                } else {
                    Column temp = asColumn.remove(0);
                    arithmeticTable.addColumn(temp.getHeader(), temp);
                }
            }
            return arithmeticTable;
        }
        return newTable;
    }

    private void orderChecker(Table table, ArrayList<String> headsOrdered) {
        table.reorder(headsOrdered);
    }

    private String select(String[] colExps, String[] tableNames, String[] conditions) {
        Table[] tablesArray = new Table[tableNames.length];
        for (int i = 0; i < tableNames.length; i++) {
            tablesArray[i] = tables.get(tableNames[i]);
        }
        Table newTable = new Table();
        try {
            newTable = Table.join(tablesArray);
        } catch (NullPointerException e) {
            return "ERROR: Table Does not Exist!";
        }
        ArrayList<String> joinedHeaders = new ArrayList<>(Arrays.asList(newTable.getHeaders()));
        Table arithmeticTable = new Table();
        ArrayList<String> newCols = new ArrayList<>();
        ArrayList<String> newTypes = new ArrayList<>();
        ArrayList<String> asNewNames = new ArrayList<>();
        ArrayList<String> arithmetics = new ArrayList<>();
        Map<String, String[]> arithmeticMatcher = new HashMap<>();
        if (colExps.length > 1) {
            for (int i = 0; i < colExps.length; i++) {
                String element = colExps[i];
                if (!element.equals("as") && !element.equals("+")
                        && !element.equals("-")
                        && !element.equals("*")
                        && !element.equals("/")) {
                    newCols.add(element);
                } else if (element.equals("+")
                        || element.equals("-") || element.equals("*") || element.equals("/")) {
                    newCols.remove(newCols.size() - 1);
                    String[] temp = {colExps[i - 1], colExps[i + 1]};
                    arithmetics.add(element);
                    arithmeticMatcher.put(element, temp);
                    i += 1;
                } else if (element.equals("as")) {
                    newCols.add(colExps[i + 1]);
                    asNewNames.add(colExps[i + 1]);
                    i += 1;
                }
            }
        }
        newTable = arithmeticFilter(arithmeticTable, newTable, arithmetics,
                joinedHeaders, arithmeticMatcher, newCols, asNewNames);
        if (newTable == null) {
            return "ERROR: Bad Data!";
        }
        filter(newTable, conditions);
        if (newCols.size() > 1) {
            for (String head : newTable.getHeaders()) {
                if (!newCols.contains(head)) {
                    newTable.deleteColumn(head);
                }
            }
            orderChecker(newTable, newCols);
        } else if (newCols.size() == 0) {
            ArrayList<String> colExpres = new ArrayList<>(Arrays.asList(colExps));
            if (!colExpres.contains("*")) {
                for (String head : newTable.getHeaders()) {
                    if (!colExpres.contains(head)) {
                        newTable.deleteColumn(head);
                    }
                }
                orderChecker(newTable, colExpres);
            }
        }
        ArrayList<Integer> indices = new ArrayList<>();
        for (String head : newTable.getHeaders()) {
            int size = newTable.getColumn(head).size();
            for (int i = 0; i < size; i++) {
                if (newTable.getColumn(head).getType().equals("string")) {
                    if (newTable.getColumn(head).get(i).equals("\"")) {
                        indices.add(i);
                    }
                }
            }
        }
        for (int index : indices) {
            newTable.deleteRow(index);
        }
        return newTable.print();
    }

    //                              ******PARSING******


    private static final String REST = "\\s*(.*)\\s*",
            COMMA = "\\s*,\\s*",
            AND = "\\s+and\\s+";


    // Stage 1 syntax, contains the command name.
    private static final Pattern CREATE_CMD = Pattern.compile("create table " + REST),
            LOAD_CMD = Pattern.compile("load " + REST),
            STORE_CMD = Pattern.compile("store " + REST),
            DROP_CMD = Pattern.compile("drop table " + REST),
            INSERT_CMD = Pattern.compile("insert into " + REST),
            PRINT_CMD = Pattern.compile("print " + REST),
            SELECT_CMD = Pattern.compile("select " + REST);

    // Stage 2 syntax, contains the clauses of commands.
    private static final Pattern CREATE_NEW = Pattern.compile("(\\S+)\\s+\\((\\S+\\s+\\S+\\s*"
            + "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
            SELECT_CLS = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+"
                    + "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+"
                    + "([\\w\\s+\\-*/'<>=!.]+?(?:\\s+and\\s+"
                    + "[\\w\\s+\\-*/'<>=!.]+?)*))?"),
            CREATE_SEL = Pattern.compile("(\\S+)\\s+as select\\s+"
                    + SELECT_CLS.pattern()),
            INSERT_CLS = Pattern.compile("(\\S+)\\s+values\\s+(.+?"
                    + "\\s*(?:,\\s*.+?\\s*)*)");

    private String eval(String query) {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            return createTable(m.group(1));
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
            return loadTable(m.group(1));
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            return storeTable(m.group(1));
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            return dropTable(m.group(1));
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            return insertRow(m.group(1));
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            return printTable(m.group(1));
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            return select(m.group(1));
        } else {
            return ("ERROR: Malformed query: " + query);
        }
    }

    private String createTable(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            return createNewTable(m.group(1), m.group(2).split(COMMA));
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            return createSelectedTable(m.group(1), m.group(2), m.group(3), m.group(4));
        } else {
            return ("ERROR: Malformed create: " + expr);
        }

    }

    private String createNewTable(String name, String[] cols) {

        String[] types = new String[cols.length];
        String[] headers = new String[cols.length];

        for (int i = 0; i < cols.length; i++) {
            String[] splited = cols[i].split("\\s+");
            headers[i] = splited[0];
            types[i] = splited[1];
        }
        for (String type : types) {
            if (!type.equals("string") && !type.equals("int") && !type.equals("float")) {
                return "ERROR: Wrong datatypes!";
            }
        }

        create(name, headers, types);
        return " ";
    }

    private String createSelectedTable(String name, String exprs, String tableslist, String conds) {
        return createSelect(name, select(exprs, tableslist, conds));
    }

    private String loadTable(String name) {
        return load(name);
    }

    private String storeTable(String name) {
        return store(name);
    }

    private String dropTable(String name) {

        return drop(name);
    }

    private String insertRow(String expr) {
        Matcher m = INSERT_CLS.matcher(expr);
        if (!m.matches()) {
            return ("ERROR: Malformed insert: " + expr);
        }

        String skips = "[ ,]+";
        String[] literals = m.group(2).split(skips);

        return insert(m.group(1), literals);
    }

    private String printTable(String name) {
        //System.out.printf("You are trying to print the table named %s\
        try {
            return print(name);
        } catch (NullPointerException e) {
            return "ERROR: Table does not exist!";
        }

    }

    private String select(String expr) {
        Matcher m = SELECT_CLS.matcher(expr);
        if (!m.matches()) {
            return ("ERROR: Malformed select: " + expr);
        }

        return select(m.group(1), m.group(2), m.group(3));

    }

    private String select(String exprs, String tableslist, String conds) {
        String skips = "[ ,]+";
        String[] exprsArray1 = exprs.split(skips);
        String[] tablesArray = tableslist.split(skips);
        String[] condsArray1 = null;
        ArrayList<String> condsArray2 = new ArrayList<>();

        ArrayList<String> exprsArray2 = new ArrayList<>(Arrays.asList(exprsArray1));
        for (int x = 0; x < exprsArray2.size(); x++) {
            for (int y = 0; y < exprsArray2.get(x).length(); y++) {
                if (exprsArray2.get(x).length() > 1
                        && (exprsArray2.get(x).charAt(y) == '+'
                        || exprsArray2.get(x).charAt(y) == '-'
                        || exprsArray2.get(x).charAt(y) == '*'
                        || exprsArray2.get(x).charAt(y) == '/')) {
                    exprsArray2.add(x + 1, Character.toString(exprsArray2.get(x).charAt(y)));
                    if (exprsArray2.get(x).substring(y + 1,
                            exprsArray2.get(x).length()).length() > 0) {
                        exprsArray2.add(x + 2,
                                exprsArray2.get(x).substring(y + 1, exprsArray2.get(x).length()));
                    }
                    if (y > 0) {
                        exprsArray2.set(x, exprsArray2.get(x).substring(0, y));
                    }
                    if (y == 0) {
                        exprsArray2.remove(x);
                    }
                }
            }
        }

        if (conds != null) {
            condsArray1 = conds.split(AND);
            for (String cond : condsArray1) {
                String[] temp = cond.split(skips);
                for (String elem : temp) {
                    condsArray2.add(elem);
                }
            }

        }

        String element;
        ArrayList<String> temp = new ArrayList<>();

        for (int i = 0; i < condsArray2.size(); i++) {
            if (condsArray2.get(i).charAt(0) == '\'') {
                element = condsArray2.get(i);

                while (condsArray2.get(i).charAt(condsArray2.get(i).length() - 1) != '\'') {
                    i += 1;
                    element += (" " + condsArray2.get(i));
                }

                temp.add(element);
            } else {
                temp.add(condsArray2.get(i));
            }
        }

        condsArray2 = temp;

        return select(exprsArray2.toArray(new String[exprsArray2.size()]),
                tablesArray, condsArray2.toArray(new String[condsArray2.size()]));
    }

    public String transact(String query) {
        return eval(query);
    }

    @Test
    public void tester() {

    }
}
