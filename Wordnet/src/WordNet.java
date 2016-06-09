/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;

/**
 *
 * @author REDRADIST
 */
public class WordNet {
    private final Digraph                       _graph;
    private final ST<String, Bag<Integer>>      _snouns;
    private final ST<Integer, String>           _inouns;
    
    private void putString(String word, int index)
    {
        if (_snouns.contains(word))
        {
           Bag<Integer> synonyms = _snouns.get(word);
           synonyms.add(index);
           _snouns.put(word, synonyms);
        }
        else
        {
           Bag<Integer> synonyms = new Bag<>();
           synonyms.add(index);
           _snouns.put(word, synonyms);
        }
    }
    
   // constructor takes the name of the two input files
   public WordNet(String synsets, String hypernyms)
   {
       if (synsets == null || hypernyms == null)
       {
           throw new NullPointerException();
       }
       
       In _synsets = new In(synsets);
       String[] synset_lines = _synsets.readAllLines();
       int synset_length = synset_lines.length;
       _snouns = new ST<>();
       _inouns = new ST<>();
       for (String line : synset_lines)
       {
           int index = Integer.parseInt(
                        line.substring(0, line.indexOf(',')));
           int startIndex = line.indexOf(',')+1;
           int endIndex = line.indexOf(',',startIndex);
           
           String words = line.substring(startIndex, endIndex);
           _inouns.put(index, words);
           
           String word;
           for (int newIndex = line.indexOf(' ',startIndex); 
                    newIndex < endIndex && newIndex >= 0;
                    startIndex = newIndex + 1, 
                    newIndex = line.indexOf(' ',startIndex))
           {
               word = line.substring(startIndex, newIndex);
               putString(word, index);
           }
           word = line.substring(startIndex, endIndex);
           putString(word, index);
       }
       
       In _hypernyms = new In(hypernyms);
       String[] hypernym_lines = _hypernyms.readAllLines();
       _graph = new Digraph(synset_length);
       for (String line : hypernym_lines)
       {
           int startIndex = 0;
           int endIndex = line.indexOf(',', startIndex);
           if (endIndex >= 0)
           {
                int rootIndex = Integer.parseInt(
                   line.substring(startIndex, endIndex));
                startIndex = endIndex+1;
                int vertex;
                while ((endIndex = line.indexOf(',', startIndex)) >= 0)
                {
                    vertex = Integer.parseInt(
                        line.substring(startIndex, endIndex));
                    _graph.addEdge(rootIndex, vertex);
                    startIndex = endIndex+1;
                }
                vertex = Integer.parseInt(
                        line.substring(startIndex));
                _graph.addEdge(rootIndex, vertex);
           }
       }
   }

   // returns all WordNet nouns
   public Iterable<String> nouns()
   {
       return _snouns.keys();
   }

   // is the word a WordNet noun?
   public boolean isNoun(String word)
   {
       if (word == null)
       {
           throw new NullPointerException();
       }
       return _snouns.contains(word);
   }

   // distance between nounA and nounB (defined below)
   public int distance(String nounA, String nounB)
   {
       if (nounA == null || nounB == null)
       {
           throw new NullPointerException();
       }
              
       SAP sap = new SAP(_graph);
       int distance = -1;
       if (_snouns.get(nounA) != null && _snouns.get(nounB) != null)
        {
            distance = sap.length(_snouns.get(nounA), _snouns.get(nounB));
        }
       return distance;
   }

   // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
   // in a shortest ancestral path (defined below)
   public String sap(String nounA, String nounB)
   {
       if (nounA == null || nounB == null)
       {
           throw new NullPointerException();
       }
              
        String result = new String();
        SAP sap = new SAP(_graph);
        if (_snouns.get(nounA) != null && _snouns.get(nounB) != null)
        {
            int ancestor;
            ancestor = sap.ancestor(_snouns.get(nounA), _snouns.get(nounB));
            if (ancestor != -1)
            {
                result += _inouns.get(ancestor);
            }
        }
        return result;
   }

   // do unit testing of this class
   public static void main(String[] args)
   {
       
   }
}
