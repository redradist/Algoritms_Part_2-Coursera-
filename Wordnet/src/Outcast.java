/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 *
 * @author REDRADIST
 */
public class Outcast {
   private final WordNet _wordnet;
   public Outcast(WordNet wordnet)         // constructor takes a WordNet object
   {
       if (wordnet == null)
       {
           throw new NullPointerException();
       }
       
       _wordnet = wordnet;
   }
   
   public String outcast(String[] nouns)   // given an array of WordNet nouns, return an outcast
   {
       if (nouns == null)
       {
           throw new NullPointerException();
       }
       
       long[] distance = new long[nouns.length];
       int k = 0;
       for (String fnoun : nouns)
       {
           distance[k] = -1;
           for (String snoun : nouns)
           {
               if (fnoun != snoun)
               {
                   if (_wordnet.distance(fnoun, snoun) > 0)
                   {
                       distance[k] += _wordnet.distance(fnoun, snoun);
                   }
               }
           }
           k++;
       }
       int i_max = 0;
       long distance_max = -1;
       for (int i = 0; i < distance.length; i++)
       {
           if (distance_max == -1 ||
               distance[i] > distance_max)
           {
               i_max = i;
               distance_max = distance[i];
           }
       }
       
       return nouns[i_max];
   }
   
   public static void main(String[] args) {
    WordNet wordnet = new WordNet(args[0], args[1]);
    Outcast outcast = new Outcast(wordnet);
    for (int t = 2; t < args.length; t++) {
        In in = new In(args[t]);
        String[] nouns = in.readAllStrings();
        StdOut.println(args[t] + ": " + outcast.outcast(nouns));
    }
}
}
