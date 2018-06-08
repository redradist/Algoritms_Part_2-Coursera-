import java.util.HashSet;
import java.util.Set;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.TST;
import edu.princeton.cs.algs4.TrieST;

public class BoggleSolver {
    private final TST<Boolean> dictionary = new TST<>();

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        for (String str : dictionary) {
            for (int endString = 1; endString <= str.length()-1; ++endString) {
                String subword = str.substring(0, endString);
                if (!this.dictionary.contains(subword)) {
                    this.dictionary.put(str.substring(0, endString), Boolean.FALSE);
                }
            }
            this.dictionary.put(str, Boolean.TRUE);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        Set<String> list = new HashSet<>();
        boolean[][] marked = new boolean[board.rows()][board.cols()];
        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < board.rows(); ++i) {
            for (int j = 0; j < board.cols(); ++j) {
                createWordsFrom(list, prefix, marked, i, j, board);
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

    private void createWordsFrom(Set<String> list,
                                 StringBuilder prefix,
                                 boolean[][] marked,
                                 final int rowIndex,
                                 final int columnIndex,
                                 final BoggleBoard board) {
        marked[rowIndex][columnIndex] = true;
        prefix.append(board.getLetter(rowIndex, columnIndex));

        String foundWordByPrefix;
        String foundWordByExPrefix;

        if (board.getLetter(rowIndex, columnIndex) != 'Q') {
            foundWordByPrefix = dictionary.longestPrefixOf(prefix.toString());
            if (foundWordByPrefix != null) {
                if (dictionary.get(foundWordByPrefix) &&
                    foundWordByPrefix.length() >= 3) {
                    list.add(foundWordByPrefix);
                }
                if (foundWordByPrefix.length() < prefix.length()) {
                    prefix.deleteCharAt(prefix.length()-1);
                    marked[rowIndex][columnIndex] = false;
                    return;
                }
            }
            for (int i = -1; i <= 1; ++i) {
                for (int j = -1; j <= 1; ++j) {
                    if (0 <= rowIndex+i && rowIndex+i < board.rows() &&
                        0 <= columnIndex+j && columnIndex+j < board.cols()) {
                        if (!marked[rowIndex+i][columnIndex+j]) {
                            createWordsFrom(list, prefix, marked, rowIndex+i, columnIndex+j, board);
                        }
                    }
                }
            }
        } else {
            prefix.append('U');
            foundWordByExPrefix = dictionary.longestPrefixOf(prefix.toString());
            if (foundWordByExPrefix != null) {
                if (foundWordByExPrefix.length() >= 3) {
                    if (dictionary.get(foundWordByExPrefix)) {
                        list.add(foundWordByExPrefix);
                    }
                    if (foundWordByExPrefix.length() < prefix.length()) {
                        prefix.deleteCharAt(prefix.length()-1);
                        prefix.deleteCharAt(prefix.length()-1);
                        marked[rowIndex][columnIndex] = false;
                        return;
                    }
                }
            }
            for (int i = -1; i <= 1; ++i) {
                for (int j = -1; j <= 1; ++j) {
                    if (0 <= rowIndex+i && rowIndex+i < board.rows() &&
                        0 <= columnIndex+j && columnIndex+j < board.cols()) {
                        if (!marked[rowIndex+i][columnIndex+j]) {
                            createWordsFrom(list, prefix, marked, rowIndex+i, columnIndex+j, board);
                        }
                    }
                }
            }
            prefix.deleteCharAt(prefix.length()-1);
        }

        prefix.deleteCharAt(prefix.length()-1);
        marked[rowIndex][columnIndex] = false;
    }

    public static void main(String[] args) {
        long startTime = System.nanoTime();
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
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds.
        StdOut.println("duration = " + duration);
    }
}
