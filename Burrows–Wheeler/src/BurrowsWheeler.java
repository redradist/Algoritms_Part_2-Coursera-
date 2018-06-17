import edu.princeton.cs.algs4.*;

import java.util.ArrayList;
import java.util.List;

public class BurrowsWheeler {
    /**
     * apply Burrows-Wheeler transform, reading from standard input and writing to standard output
     */
    public static void transform() {
        if (!StdIn.isEmpty()) {
            String originText = StdIn.readAll();
            CircularSuffixArray array = new CircularSuffixArray(originText);
            for (int index = 0; index < array.length(); ++index) {
                if (array.index(index) == 0) {
                    BinaryStdOut.write(index);
                    break;
                }
            }
            for (int index = 0; index < array.length(); ++index) {
                int sortedIndex = array.index(index);
                if (sortedIndex == 0) {
                    BinaryStdOut.write(originText.charAt(array.length() - 1));
                } else {
                    BinaryStdOut.write(originText.charAt(sortedIndex - 1));
                }
            }
            BinaryStdOut.flush();
        }
    }

    /**
     * apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard output
     */
    public static void inverseTransform() {
        if (!BinaryStdIn.isEmpty()) {
            int numOriggit add sr           inString = BinaryStdIn.readInt();
            BinarySearchST<Character, List<Integer>> st = new BinarySearchST<Character, List<Integer>>();
            int index = 0;
            while (!BinaryStdIn.isEmpty()) {
                char newCh = BinaryStdIn.readChar();
                if (!st.contains(newCh)) {
                    st.put(newCh, new ArrayList<>());
                }
                st.get(newCh).add(index);
                ++index;
            }
            int[] next = new int[index];
            index = 0;
            for (char ch : st.keys()) {
                List<Integer> indexes = st.get(ch);
                StdOut.println(String.format("ch[%c] = %s", ch, indexes.toString()));
                while (indexes.size() > 0) {
                    next[index++] = indexes.remove(0);
                }
            }
            for (int i : next) {
                StdOut.println("i = " + i);
            }
        }
    }

    /**
     * if args[0] is '-', apply Burrows-Wheeler transform
     * if args[0] is '+', apply Burrows-Wheeler inverse transform
     *
     * @param args
     */
    public static void main(String[] args) {
        if (args.length >= 1) {
            if (args[0].compareTo("-") == 0) {
                BurrowsWheeler.transform();
            } else if (args[0].compareTo("+") == 0) {
                BurrowsWheeler.inverseTransform();
            }
        }
    }
}