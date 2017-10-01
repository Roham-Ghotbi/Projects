import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;



public class Solver {

    /*public static String decrypt(String encrypted_message) {
        int[] key = {-8,-2,-5,-1,-2,-2,-0};       //found the key by checking the last part of the ciphertext(made negative to reverse the cipher)
        String msg = "";
        int i = 0;

        for (char elem : encrypted_message.toCharArray()) {
            int value = elem;
            if(value >= 65 && value <= 90) {         //making sure to only change the alphabets
                if (value + key[i] < 65) {
                    int rev = 26 + key[i];          //practically performing a mod to loop to z from a
                    value += rev;
                    msg += (char)value;
                } else {
                    msg += (char) (value + key[i]);
                }
                i += 1;         //updating the key value
                if(i == 7) {
                    i = 0;
                }
            } else if (value >= 97 && value <= 122) {
                if (value + key[i] < 97) {
                    int rev = 26 + key[i];          //same process for small letters
                    value += rev;
                    msg += (char)value;
                }else {
                    msg += (char) (value + key[i]);
                }

                i += 1;         //updating the key value
                if(i == 7) {
                    i = 0;
                }

            } else {
                msg += elem;
            }

        }
        return msg;
    }*/

    public static class DataBlock implements Comparable<DataBlock>{
        private int year;
        private int month;
        private int day;
        private String action;
        private int count;


        public DataBlock(String data) {
            String noSpace = data.replaceAll("\\s+","");
            this.year = Integer.parseInt(noSpace.substring(0, 4));
            this.month = Integer.parseInt(noSpace.substring(5, 7));
            this.day = Integer.parseInt(noSpace.substring(8, 10));

            int firstComma = noSpace.indexOf(",");
            int secComma = noSpace.lastIndexOf(",");

            this.action = noSpace.substring(firstComma + 1, secComma);          //get the action
            this.count = Integer.parseInt(noSpace.substring(secComma + 1));     //get the count

        }


        public boolean sameMonthAndYear(DataBlock block2) {
            return (this.year == block2.year) && (this.month == block2.month);
        }

        public boolean sameAction(DataBlock block2) {
            return this.action.equals(block2.action);
        }

        @Override
        public int compareTo(DataBlock block2) {            // will be sorted in a way the the latest is the smallest
            if (this.year > block2.year) {
                return -1;
            } else if (this.year < block2.year) {
                return 1;
            } else {            //if they happened in the same year

                if (this.month > block2.month) {
                    return -1;
                } else if (this.month < block2.month) {
                    return 1;
                } else {            //if they happened in the same year and same month


                    if (this.day > block2.day) {
                        return -1;
                    } else if (this.day < block2.day) {
                        return 1;
                    } else {            //if they happened in the exact same date
                        return 0;
                    }
                }
            }
        }
    }


    public static void main(String[]args) {

        String test = "Otjfvknou kskgnl, K mbxg iurtsvcnb ksgq hoz atv. Vje xcxtyqrl vt ujg smewfv vrmcxvtg rwqr ju vhm ytsf elwepuqyez. -Atvt hrqgse, Cnikg";
        String test2 = "Bjj rwkcs dwpyp fwz ovors wxjs vje tcez fqg";
       /* String msg = decrypt(test);*/
       /* System.out.println(msg);*/
       try {
           File text = new File("C:\\Users\\Roham\\Desktop\\input001 (1).txt");

           Scanner scanner = new Scanner(text);
           List elements = new ArrayList<DataBlock>();
           String interval = "";

           if(scanner.hasNextLine()) {     //making sure its not empty
               interval = scanner.nextLine().replaceAll("\\s+", "");
               scanner.nextLine();         //skipping the space
           }

           while (scanner.hasNextLine()) {
               elements.add(new DataBlock(scanner.nextLine()));
           }
           scanner.close();
           Collections.sort(elements);
           //String interval = ("2015-08, 2016-04").replaceAll("\\s+", "");


           int yearS = Integer.parseInt(interval.substring(0, 4));      //beginning year
           int monthS = Integer.parseInt(interval.substring(5, 7));      //beginning month
           int comma = interval.indexOf(",");
           int yearE = Integer.parseInt(interval.substring(comma + 1, comma + 5));      //end year
           int monthE = Integer.parseInt(interval.substring(comma + 7));      //end month



        /*elements.add(new DataBlock("2015-08-15, clicks, 635"));
        elements.add(new DataBlock("2016-03-24, app_installs, 683"));
        elements.add(new DataBlock("2015-04-05, favorites, 763"));
        elements.add(new DataBlock("2016-01-22, favorites, 788"));
        elements.add(new DataBlock("2015-12-26, clicks, 525"));
        elements.add(new DataBlock("2016-06-03, retweets, 101"));
        elements.add(new DataBlock("2015-12-02, app_installs, 982"));
        elements.add(new DataBlock("2016-09-17, app_installs, 770"));
        elements.add(new DataBlock("2015-11-07, impressions, 245"));
        elements.add(new DataBlock("2016-10-16, impressions, 567"));*/

           Collections.sort(elements);

           int SIZE = elements.size();

           for (int i = 0; i < SIZE; i++) {
               DataBlock elem = (DataBlock) elements.get(i);
               List<String> actions = new ArrayList<>();
               Map<String, Integer> counts = new HashMap<>();

               if ((elem.year > yearS && elem.year < yearS) || (elem.year == yearE && elem.month < monthE)
                       || (elem.year == yearS && elem.month >= monthS)) {

                   int year = elem.year;
                   int month = elem.month;
                   String sMonth;
                   int j = i + 1;
                   boolean same = false;

                   actions.add(elem.action);                                //create a list of possible diff actions
                   counts.put(elem.action, elem.count);

                                                          //check to see if we had same action on diff days of same month of the year

                       while (j < SIZE && elem.sameMonthAndYear((DataBlock) elements.get(j))) {
                           DataBlock item = (DataBlock) elements.get(j);    //for the sake of legibility

                           if (actions.contains(item.action)) {       //check to see if it is in the list
                               counts.put(item.action, counts.get(item.action) + item.count);   //updating the value
                           } else {
                               actions.add(item.action);
                               counts.put(item.action, item.count);
                           }
                           same = true;
                           j += 1;
                       }

                       if (j < SIZE && same == true) {
                           i = j - 1;
                       }


                   Collections.sort(actions);

                   if (month < 10) {               //fixing the format of the month for print out
                       sMonth = "0" + month;
                   } else {
                       sMonth = Integer.toString(month);
                   }

                   String result = year + "-" + sMonth + ", ";
                   for (String act : actions) {                        //creating the result string
                       result += ", " + act + ", " + counts.get(act);
                   }

                   System.out.println(result);                         //returning the value

               }

               actions.clear();                         //reseting the values for the next round
               counts.clear();
           }

       } catch (Exception e) {
           System.out.print("NULL");
       }
    }
}