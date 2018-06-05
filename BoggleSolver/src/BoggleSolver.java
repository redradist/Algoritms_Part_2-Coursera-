import java.util.HashSet;
import java.util.Set;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.TrieST;

public class BoggleSolver {
    private final TrieST<Boolean> dictionary = new TrieST<>();

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        for (String str : dictionary) {
            this.dictionary.put(str, Boolean.TRUE);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        Set<String> list = new HashSet<>();
        for (int i = 0; i < board.rows(); ++i) {
            for (int j = 0; j < board.cols(); ++j) {
                boolean[][] marked = createMarkTable(board.rows(), board.cols());
                list.addAll(createWordsFrom(new StringBuilder(), marked, i, j, board));
            }
        }
        return list;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        int score = 0;
        boolean isExist = dictionary.contains(word);
        if (isExist) {
            switch (word.length()) {
                case 0:
                    score = 0;
                    break;
                case 1:
                    score = 0;
                    break;
                case 2:
                    score = 0;
                    break;
                case 3:
                    score = 1;
                    break;
                case 4:
                    score = 1;
                    break;
                case 5:
                    score = 2;
                    break;
                case 6:
                    score = 3;
                    break;
                case 7:
                    score = 5;
                    break;
                default:
                    score = 11;
                    break;
            }
        }
        return score;
    }

    private Set<String> createWordsFrom(StringBuilder prefix,
                                        boolean[][] marked,
                                        final int rowIndex,
                                        final int columnIndex,
                                        final BoggleBoard board) {
        Set<String> list = new HashSet<>();
        marked[rowIndex][columnIndex] = true;
        prefix.append(board.getLetter(rowIndex, columnIndex));

        String foundWord = dictionary.longestPrefixOf(prefix.toString());
        if (foundWord != null && foundWord.length() >= 3) {
            list.add(foundWord);
        }

        if (board.getLetter(rowIndex, columnIndex) == 'Q') {
            String foundWord2 = dictionary.longestPrefixOf(prefix.toString()+'U');
            if (foundWord2 != null && foundWord2.length() >= 3) {
                list.add(foundWord2);
            }
        }

        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if (0 <= rowIndex+i && rowIndex+i < board.rows() &&
                    0 <= columnIndex+j && columnIndex+j < board.cols()) {
                    if (!marked[rowIndex+i][columnIndex+j]) {
                        list.addAll(createWordsFrom(new StringBuilder(prefix.toString()), copyNestedArray(marked), rowIndex+i, columnIndex+j, board));
                        if (board.getLetter(rowIndex, columnIndex) == 'Q') {
                            list.addAll(createWordsFrom(new StringBuilder(prefix.toString()+'U'), copyNestedArray(marked), rowIndex+i, columnIndex+j, board));
                        }
                    }
                }
            }
        }
        return list;
    }

    private boolean[][] copyNestedArray(final boolean[][] array) {
        boolean[][] newArray = new boolean[array.length][array[0].length];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                newArray[i][j] = array[i][j];
            }
        }
        return newArray;
    }

    private boolean[][] createMarkTable(final int rows, final int columns) {
        boolean[][] marked = new boolean[rows][columns];
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < columns; ++j) {
                marked[i][j] = false;
            }
        }
        return marked;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
