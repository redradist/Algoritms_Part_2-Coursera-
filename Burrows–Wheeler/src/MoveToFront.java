import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.LinkedList;
import java.util.List;

public class MoveToFront {
    /**
     * apply move-to-front encoding, reading from standard input and writing to standard output
     */
    public static void encode() {
        List<Character> sequence = new LinkedList<>();
        for (int i = 0; i < 256; ++i) {
            sequence.add((char) i);
        }
        while (!BinaryStdIn.isEmpty()) {
            char readCh = BinaryStdIn.readChar();
            char pos = 0;
            for (Character seqCh : sequence) {
                if (readCh == seqCh) {
                    BinaryStdOut.write(pos);
                    sequence.add(0, sequence.remove(pos));
                    break;
                }
                ++pos;
            }
        }
        BinaryStdOut.flush();
    }

    /**
     * apply move-to-front decoding, reading from standard input and writing to standard output
     */
    public static void decode() {
        List<Character> sequence = new LinkedList<>();
        for (int i = 0; i < 256; ++i) {
            sequence.add((char) i);
        }

        while (!BinaryStdIn.isEmpty()) {
            StringBuilder number = new StringBuilder();
            int posCh = BinaryStdIn.readChar();
            int pos = 0;
            for (Character seqCh : sequence) {
                if (pos == posCh) {
                    BinaryStdOut.write(seqCh);
                    break;
                }
                ++pos;
            }
            char foundCh = sequence.remove(pos);
            sequence.add(0, foundCh);
        }
        BinaryStdOut.flush();
    }

    /**
     * if args[0] is '-', apply move-to-front encoding
     * if args[0] is '+', apply move-to-front decoding
     *
     * @param args
     */
    public static void main(String[] args) {
        if (args.length >= 1) {
            if (args[0].compareTo("-") == 0) {
                MoveToFront.encode();
            } else if (args[0].compareTo("+") == 0) {
                MoveToFront.decode();
            }
        }
    }
}
