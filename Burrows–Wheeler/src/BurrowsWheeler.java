import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.BinarySearchST;

import java.util.ArrayList;
import java.util.List;

public class BurrowsWheeler {
    /**
     * apply Burrows-Wheeler transform, reading from standard input and writing to standard output
     */
    public static void transform() {
        if (!BinaryStdIn.isEmpty()) {
            String originText = BinaryStdIn.readString();
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
            int numOriginString = BinaryStdIn.readInt();
            BinarySearchST<Character, List<Integer>> st = new BinarySearchST<Character, List<Integer>>();
            StringBuilder sortedString = new StringBuilder();
            int index = 0;
            while (!BinaryStdIn.isEmpty()) {
                char newCh = BinaryStdIn.readChar();
                sortedString.append(newCh);
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
                while (indexes.size() > 0) {
                    next[index++] = indexes.remove(0);
                }
            }
            int numInverseChars = 0;
            int indexToHandle = numOriginString;
            while (true) {
                indexToHandle = next[indexToHandle];
                BinaryStdOut.write(sortedString.charAt(indexToHandle));
                ++numInverseChars;
                if (numInverseChars >= next.length) {
                    break;
                }
            }
            BinaryStdOut.flush();
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