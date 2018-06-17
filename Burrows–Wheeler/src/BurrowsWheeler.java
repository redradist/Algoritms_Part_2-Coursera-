import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

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
                if (sortedIndex != 0) {
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