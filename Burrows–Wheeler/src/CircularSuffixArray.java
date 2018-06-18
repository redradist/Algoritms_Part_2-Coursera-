import edu.princeton.cs.algs4.Quick;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.List;

public class CircularSuffixArray {
    private class CharEntry implements Comparable<CharEntry> {
        private final String orig;
        private final int realIndex;

        CharEntry(String orig, int index) {
            this.orig = orig;
            this.realIndex = index;
        }

        private int getIndex() {
            return (this.realIndex + prefixIndex) % orig.length();
        }

        private char getChar() {
            return orig.charAt(getIndex());
        }

        @Override
        public boolean equals(Object o) {
            return this == o || (o instanceof CharEntry && this.getChar() == ((CharEntry)o).getChar()) ;
        }

        @Override
        public int compareTo(CharEntry o) {
            int result = 0;
            if (!this.equals(o)) {
                if (this.getChar() > o.getChar()) {
                    result = 1;
                } else if (this.getChar() < o.getChar()) {
                    result = -1;

                }
            }
            return result;
        }
    }

    private List<CharEntry> startOfPerfixes = new ArrayList<>();
    private int prefixIndex = 0;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException("String should not be null !!");
        }

        for (int index = 0; index < s.length(); ++index) {
            startOfPerfixes.add(new CharEntry(s, index));
        }
        sortInRange(0, s.length());
    }

    private void sortInRange(final int fromIndex, final int toIndex) {
        CharEntry[] arrayToSort = startOfPerfixes.subList(fromIndex, toIndex)
                                                 .toArray(new CharEntry[toIndex-fromIndex]);
        Quick.sort(arrayToSort);
        for (int index = fromIndex; index < toIndex; ++index) {
            startOfPerfixes.set(index, arrayToSort[index-fromIndex]);
        }
        sortNextLevel(fromIndex, toIndex);
    }

    private void sortNextLevel(final int fromIndex, final int toIndex) {
        if (prefixIndex < (startOfPerfixes.size() - 1)) {
            int startSubIndex = fromIndex;
            while (startSubIndex < toIndex) {
                int endSubIndex = startSubIndex + 1;
                while (endSubIndex < toIndex) {
                    if (startOfPerfixes.get(startSubIndex).getChar() != startOfPerfixes.get(endSubIndex).getChar()) {
                        break;
                    }
                    ++endSubIndex;
                }
                prefixIndex += 1;
                if ((endSubIndex - startSubIndex) > 1) {
                    sortInRange(startSubIndex, endSubIndex);
                    startSubIndex = endSubIndex;
                } else {
                    ++startSubIndex;
                }
                prefixIndex -= 1;
            }
        }
    }

    private void printArray() {
        int originPrefixIndex = prefixIndex;
        StdOut.println("CircularSuffixArray =>");
        for (int index = 0; index < startOfPerfixes.size(); ++index) {
            for (prefixIndex = 0; prefixIndex < startOfPerfixes.size(); ++prefixIndex) {
                StdOut.print(String.format("%c", startOfPerfixes.get(index).getChar()));
                StdOut.print(' ');
            }
            StdOut.print(String.format("%d", startOfPerfixes.get(index).realIndex));
            StdOut.println();
        }
        prefixIndex = originPrefixIndex;
    }

    // length of s
    public int length() {
        return startOfPerfixes.size();
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= startOfPerfixes.size()) {
            throw new IllegalArgumentException(String.format("Argument i should be in range [%d, %d)",
                                                              0, startOfPerfixes.size()));
        }
        return startOfPerfixes.get(i).realIndex;
    }

    // unit testing (required)
    public static void main(String[] args) {
        String orig = "ABRACADABRA!";
        CircularSuffixArray array = new CircularSuffixArray(orig);
        array.printArray();
    }
}
