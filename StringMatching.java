/*
CSC526 : Assignment 3 - String Matching
This is my own work. JRK. 2/26/2018
This program will run the Z algorithm and KMP algorithm for a given
pattern and text.

Note to grader: The Text and Pattern are represented/contained in a 2D array,
where row i=0 is Text and row i=1 is Pattern. Null will mark places where
the pattern has shifted away from.

Sources: 
https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
https://docs.oracle.com/javase/7/docs/api/java/util/regex/Matcher.html
https://docs.oracle.com/javase/7/docs/api/java/util/regex/MatchResult.html
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import java.util.StringTokenizer;
import java.util.function.Predicate;

public class StringMatching {

    protected ArrayList<String> text = null;
    protected ArrayList<String> pattern = null;
    protected ArrayList<String> patternSaved = null;
    protected ArrayList<String> textSaved = null;
    protected int[] zArray = null;
    protected final String REGEX = "ACACAGT";
    protected final String textStr = "ACAT ACGACACAGT";
    StringBuilder strBuilder = null;
    String[][] matrix = null;

    public StringMatching() {
        this.text = new ArrayList<>();
        this.pattern = new ArrayList<>();
        this.patternSaved = new ArrayList<>();
        this.textSaved = new ArrayList<>();
        this.strBuilder = new StringBuilder(REGEX);
    }

    protected void initializeTextAndPattern() {
        this.text.add("A");
        this.text.add("C");
        this.text.add("A");
        this.text.add("T");
        this.text.add(" ");
        this.text.add("A");
        this.text.add("C");
        this.text.add("G");
        this.text.add("A");
        this.text.add("C");
        this.text.add("A");
        this.text.add("C");
        this.text.add("A");
        this.text.add("G");
        this.text.add("T");

        this.textSaved.addAll(text);

        this.pattern.add("A");
        this.pattern.add("C");
        this.pattern.add("A");
        this.pattern.add("C");
        this.pattern.add("A");
        this.pattern.add("G");
        this.pattern.add("T");

        this.patternSaved.addAll(pattern);
    }

    protected void z() {
        System.out.println("----------------------Running Z algorithm-------------------------------");
        System.out.println("***Note to grader***,  '-1' is used instead of 'x' to fill z[] at index 0\n");
        //concat P$T without the '$' included.
        ArrayList p$t = this.pattern;
        p$t.addAll(this.text);

        ArrayList temp = this.pattern;
        Boolean b = false;

        System.out.print("Pattern$Text: ");
        for (int i = 0; i < this.pattern.size(); i++) {
            System.out.print(this.pattern.get(i));
        }

        this.zArray = new int[this.pattern.size()];

        int i = 0;
        int m = 0;
        int l = 0;
        int count = 0;
        int index = 0;
        // '-1' instead of 'x'
        zArray[0] = -1;
        while (m < this.pattern.size()) {
            i = 0;

            int store = 0;
            for (int j = i + 1 + l; j < this.pattern.size(); j++) {
                store = j;
                while (p$t.get(i).equals(temp.get(store)) && store != this.pattern.size() - 1) {
                    count++;
                    i++;
                    store++;
                }
                if (p$t.get(i).equals(temp.get(store)) && store == this.pattern.size() - 1) {
                    count++;
                }
                if (count != 0) {
                    this.zArray[j] = count;
                    if (count == this.patternSaved.size()) {
                        index = j;
                    }
                } else {
                    this.zArray[j] = 0;
                }
                i = 0;
                l++;
                count = 0;
            }
            m++;
        }
        System.out.print("\n Z[] array: ");
        for (int j = 0; j < this.zArray.length; j++) {
            System.out.print(zArray[j] + ", ");
            if (j + 1 == this.zArray.length - 1) {
                System.out.println(zArray[j + 1]);
                break;
            }
        }

        for (int j = 0; j < zArray.length; j++) {
            if (zArray[j] == this.patternSaved.size()) {
                System.out.print("\nYES, Pattern : ");
                b = true;
                for (int k = 0; k < this.patternSaved.size(); k++) {
                    System.out.print(this.patternSaved.get(k) + " ");
                }
                System.out.println(" present in Text[] at index : " + index);
            }
        }
        if (!b) {
            System.out.println("NO, Pattern is not present at any index.");
        }
    }

    /*
    Build the prefix table. initializePrefixTable() acts as a workhorse function
    to handle the recursion of recurseForPrefixSuffix(). Base case: When the 
    iterator l = (size of pattern - 1).
     */
    protected void initializePrefixTable() {
        System.out.println("\n----------------------Recursively Initialize Prefix Table-----------------------------");
        ArrayList<String> pref = new ArrayList();
        ArrayList<String> suff = new ArrayList();
        List<String> t1 = new ArrayList();
        List<String> t2 = new ArrayList();
        ArrayList<Integer> prefixTable = new ArrayList();
        List<Integer> mList = new ArrayList();

        System.out.print("Text: ");
        for (int i = 0; i < this.textSaved.size(); i++) {
            System.out.print(this.textSaved.get(i) + " ");
        }
        System.out.println("");
        System.out.print("Pattern: ");
        for (int i = 0; i < this.patternSaved.size(); i++) {
            System.out.print(this.patternSaved.get(i) + " ");
        }
        System.out.println("\n");

        int i = 0;
        int j = 1;
        int l = 0;
        int k = 0;
        //initialize prefix table with 0.
        prefixTable.add(0);
        List<Integer> pTable = recurseForPrefixSuffix(i, j, l, k, pref, suff, t1, t2, prefixTable, mList);
        if (!pTable.isEmpty()) {
            pref.clear();

            int indexFound = this.KMP(pTable);

            System.out.println("Index [i][j] of full match = [1][" + indexFound + "]\n");
            System.out.println("FINAL RESULT: ");
            String str = "";
            int resize = 0;
            for (int m = 0; m < 2; m++) {
                for (int n = 0; n < this.textSaved.size(); n++) {
                    str = this.matrix[m][n];
                    if (str != null) {
                        if (str.equals(" ")) {
                            System.out.print("m[" + m + "][" + n + "]: 'SPACE', ");
                        } else {
                            System.out.print("m[" + m + "][" + n + "]: '" + str + "', ");
                        }
                    } else if (str == null) {
                        System.out.print("m[" + m + "][" + n + "]: null, ");
                    }
                }
                resize = this.textSaved.size() - this.patternSaved.size();
                System.out.println("");
            }
        } else {
            System.exit(1);
        }
    }

    public List<Integer> recurseForPrefixSuffix(int i, int j, int l, int k, ArrayList<String> pre, ArrayList<String> suf, List<String> t1, List<String> t2, ArrayList<Integer> pTable, List<Integer> mList) {
        k = l;
        if (l == this.patternSaved.size() - 1) {
            return pTable;
        } else {
            pre.add(l, strBuilder.substring(i, l + 1));
            suf.add(l - k, String.valueOf(strBuilder.charAt(l + 1)));
            while (k != 0) {
                suf.add(strBuilder.substring(k, j + 1));
                k--;
            }
            System.out.println("prefix : " + pre.toString());
            System.out.println("suffix : " + suf.toString());

            t1.addAll(pre);
            t2.addAll(suf);
            pre.retainAll(suf);
            if (!pre.isEmpty()) {
                for (String s : pre) {
                    System.out.println("Intersection: " + s);
                    mList.add(s.length());
                }
                Collections.sort(mList);

                pTable.add(mList.get(mList.size() - 1));
                System.out.println("Prefix Table : " + pTable.toString() + "\n");
            } else {
                pTable.add(0);
                System.out.println("Prefix Table : " + pTable.toString() + "\n");
            }
            mList.clear();
            pre.clear();
            pre.addAll(t1);
            t1.clear();
            t2.clear();
            suf.clear();

            return recurseForPrefixSuffix(i, j + 1, l + 1, k, pre, suf, t1, t2, pTable, mList);
        }
    }

    public void displayTable(int i, int p) {
        String temp1 = "1", temp2 = "2", temp3 = "3", temp4 = "4", temp5 = "5", temp6 = "6", temp7 = "7";
        String a1 = "A", c2 = "C", a3 = "A", c4 = "C", a5 = "A", g6 = "G", t7 = "T";
        String p1 = "0", p2 = "0", p3 = "1", p4 = "2", p5 = "3", p6 = "0", p7 = "0";
        String i1 = String.valueOf(i);
        String pp = String.valueOf(p);
        if (i1.equals("1")) {
            temp1 = "".concat("[").concat(i1.concat("]*"));
            a1 = "  ".concat(a1);
            c2 = " ".concat(c2);
            p1 = "  ".concat(p1).concat(" ");
        } else if (i1.equals("2")) {
            temp2 = "".concat("[").concat(i1.concat("]*"));
            c2 = "  ".concat(c2);
            a3 = " ".concat(a3);
            p2 = "  ".concat(p2).concat(" ");
        } else if (i1.equals("3")) {
            temp3 = "".concat("[").concat(i1.concat("]*"));
            a3 = " ".concat(a3);
            c4 = "  ".concat(c4);
            p3 = " ".concat(p3).concat("  ");
        } else if (i1.equals("4")) {
            temp4 = "".concat("[").concat(i1.concat("]*"));
            c4 = "  ".concat(c4);
            a5 = " ".concat(a5);
            p4 = "  ".concat(p4).concat(" ");
        } else if (i1.equals("5")) {
            temp5 = "".concat("[").concat(i1.concat("]*"));
            a5 = " ".concat(a5);
            g6 = "  ".concat(g6);
            p5 = " ".concat(p5).concat("  ");
        } else if (i1.equals("6")) {
            temp6 = "".concat("[").concat(i1.concat("]*"));
            g6 = " ".concat(g6);
            t7 = "  ".concat(t7);
            p6 = " ".concat(p6).concat("  ");
        } else if (i1.equals("7")) {
            temp7 = "".concat("[").concat(i1.concat("]*"));
            t7 = " ".concat(t7);
            p7 = " ".concat(p7).concat("  ");
        }
        System.out.print("      :i " + temp1 + "  " + temp2 + "  " + temp3 + "  " + temp4 + "  " + temp5 + "  " + temp6 + "  " + temp7 + "\n");
        System.out.print("Pattern: " + a1 + "  " + c2 + "  " + a3 + "  " + c4 + "  " + a5 + "  " + g6 + "  " + t7 + "\n");
        System.out.print(" Prefix: " + p1 + "  " + p2 + "  " + p3 + "  " + p4 + "  " + p5 + "  " + p6 + "  " + p7 + "\n");
        System.out.println("");
    }

    public void displayMatrix() {
        String str = "";
        int resize = 0;
        for (int m = 0; m < 2; m++) {
            for (int n = 0; n < this.textSaved.size(); n++) {
                str = this.matrix[m][n];
                if (str != null) {
                    if (str.equals(" ")) {
                        System.out.print("m[" + m + "][" + n + "]: 'SPACE', ");
                    } else {
                        System.out.print("m[" + m + "][" + n + "]: '" + str + "', ");
                    }
                } else if (str == null) {
                    System.out.print("m[" + m + "][" + n + "]: null, ");
                }
            }
            resize = this.textSaved.size() - this.patternSaved.size();
            System.out.println("");
        }
    }

    public int KMP(List<Integer> pTable) {
        System.out.println("-----------------------Running KMP Algorithm------------------------------------");
        this.displayTable(1, 1);

        this.matrix = new String[2][this.textSaved.size() + 1];

        for (int j = 0; j < this.patternSaved.size(); j++) {
            matrix[1][j] = this.patternSaved.get(j);
        }
        for (int j = 0; j < this.textSaved.size(); j++) {
            matrix[0][j] = this.textSaved.get(j);
        }
        for (int j = 0; j < this.textSaved.size(); j++) {
            if (matrix[0][j].equals(" ")) {
                System.out.print("m[0][" + j + "]=" + "'SPACE'" + "; ");
            } else {
                System.out.print("m[0][" + j + "]='" + matrix[0][j] + "'; ");
            }
        }
        for (int j = 7; j < 15; j++) {
            matrix[1][j] = null;
        }
        System.out.println("");
        for (int i = 0; i < 7; i++) {
            System.out.print("m[1[" + i + "]='" + matrix[1][i] + "'; ");
        }
        for (int i = 7; i < 15; i++) {
            System.out.print("m[1][" + i + "]='" + matrix[1][i] + "'; ");
        }
        System.out.println("");

        int i = 1, j = 0, m = 0, k = 0, mm = 0, l = 0, kk = 0, p = 0, z = 0;
        String s1 = "";
        String s2 = "";
        while (j < this.textSaved.size()) { //m < this.patternSaved.size() && j <= this.textSaved.size()
            s1 = matrix[i][j];
            s2 = matrix[i - 1][j];
            if (!s1.equals(s2)) {
                k = j;
                while (!matrix[i][k].equals(matrix[i - 1][j]) && k != 0 && matrix[i][k - 1] != null) {
                    k--;
                    if (matrix[i - 1][j].equals(matrix[i][k])) {
                        mm = (j - k);
                        l = j;
                        while (mm != -1) {
                            matrix[i][l] = matrix[i][k];
                            l++;
                            k++;
                            mm--;
                        }
                        break;
                    }
                }
                //next while() executes if there is NO match at first try
            }
            //Equal situation
            //z = index of full pattern match, r = increment to count against length of pattern
            z = j;
            int r = 0;
            while (matrix[i][j].equals(matrix[i - 1][j])) {
                System.out.println("'" + matrix[i][j] + "' matches " + "'" + matrix[i - 1][j] + "'");
                j++;
                r++;
                if (matrix[i][j] == null) {
                    System.out.println("Full pattern match reached at index j = [1][" + z + "]");
                    return z;
                }
            }
            if (r == this.patternSaved.size()) {
                System.out.println("Full pattern match reached at index j = [1][" + z + "]");
            } else {
                r = 0;
            }
            //end equal situation
            if (!matrix[1][j].equals(matrix[0][j]) && (!matrix[1][j].equals(" "))) {
                System.out.println("'" + matrix[1][j] + "' mismatches with " + "'" + matrix[0][j] + "'");
            } else if (!matrix[1][j].equals(matrix[0][j]) && !(matrix[1][j].equals(" "))) {
                System.out.println("'SPACE' mismatches with " + matrix[0][j]);
            }
            int jj = j; //store mismatch pos
            int c = (jj + 1) - kk; //adjust pTable val for crazy i starting off as =1
            p = pTable.get(c - 1); //index (KMP) i = c, value = 
            c = 0;

            if (p == 0) {
                c = 1;
            } else {
                while (p > 0) {
                    c++;
                    p--;
                }
            }
            //fast forward to end+1
            while (matrix[i][j] != null) {
                j++;
            }
            //now at null
            //shift c times, make sure ur at null pos at end to fill
            if (matrix[i][j] == null) {
                System.out.println("matrix[1][" + j + "] = " + matrix[i][j] + " so at the end + 1, rewind back 1 pos..."); //go back 1
                j--;
                while (c != 0) {
                    System.out.println("Shifting right (decrement - " + c + ") times.");
                    kk++;
                    int n = 0;
                    while (n < this.patternSaved.size()) { //shifts whole thing right by 1
                        matrix[i][j + 1] = matrix[i][j];
                        j--;
                        n++;
                    }
                    j++;
                    matrix[i][j] = null;
                    j++;
                    //fast forward 'till at end + 1
                    while (matrix[i][j] != null) {
                        j++;
                    }
                    //now at end + 1 = null, (end + 1) - 1 = at end
                    if (matrix[i][j] == null) {
                        j--;
                        c--;
                    }
                }
            }
            //Done shifting, rewind to repeat entire process.
            while (matrix[i][j] != null) {
                j--;
            }
            if (matrix[i][j] == null) {
                j++;
            }
            System.out.println("\n");
            this.displayMatrix();
            //now j at start index of shifted pattern
            m++;
            System.out.println("\nMaking another PASS -  Pattern's ACTUAL start index = " + j + ", and KMP-wise index is now = 1\n");
        } //end main loop
        return -1;
    }

    public static void main(String[] args) {
        StringMatching sm = new StringMatching();
        sm.initializeTextAndPattern();
        sm.z();
        sm.initializePrefixTable(); //runs KMP()
    }
}
