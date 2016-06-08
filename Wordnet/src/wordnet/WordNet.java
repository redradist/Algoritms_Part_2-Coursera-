/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wordnet;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdOut;

/**
 *
 * @author REDRADIST
 */
public class WordNet {
    Digraph                        _graph;
    ST<String, Bag<Integer>>       _snouns;
    ST<Integer, Bag<String>>       _inouns;
    
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
    
    private void putIndex(int index, String word)
    {
        if (_inouns.contains(index))
        {
           Bag<String> synonyms = _inouns.get(index);
           synonyms.add(word);
           _inouns.put(index, synonyms);
        }
        else
        {
           Bag<String> synonyms = new Bag<>();
           synonyms.add(word);
           _inouns.put(index, synonyms);
        }
    }
    
   // constructor takes the name of the two input files
   public WordNet(String synsets, String hypernyms)
   {
       In _synsets = new In(synsets);
       String[] synset_lines = _synsets.readAllLines();
       int synset_length = synset_lines.length;
       _snouns = new ST<String, Bag<Integer>>();
       _inouns = new ST<Integer, Bag<String>>();
       for (String line : synset_lines)
       {
           int index = Integer.parseInt(
                        line.substring(0, line.indexOf(',')));
           int startIndex = line.indexOf(',')+1;
           int endIndex = line.indexOf(',',startIndex);
           String word;
           for (int newIndex = line.indexOf(' ',startIndex); 
                    newIndex < endIndex && newIndex >= 0;
                    startIndex = newIndex + 1, 
                    newIndex = line.indexOf(' ',startIndex))
           {
               word = line.substring(startIndex, newIndex);
               putIndex(index, word);
               putString(word, index);
           }
           word = line.substring(startIndex, endIndex);
           putIndex(index, word);
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
                if (rootIndex == 27899)
                    StdOut.println("27899 is found");
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
       
       for (Integer index : _snouns.get("house"))
       {
           StdOut.println("house : "+index);
       }
       
       for (String index : _inouns.get(13400))
       {
           StdOut.println("13400 : "+index);
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
       return _snouns.contains(word);
   }

   // distance between nounA and nounB (defined below)
   public int distance(String nounA, String nounB)
   {
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
        String result = new String();
        SAP sap = new SAP(_graph);
        if (_snouns.get(nounA) != null && _snouns.get(nounB) != null)
        {
            int ancestor;
            ancestor = sap.ancestor(_snouns.get(nounA), _snouns.get(nounB));
            if (ancestor != -1)
            {
                for (String word : _inouns.get(ancestor))
                {
                    result += word;
                }
            }
            return result;
        }
        else
        {
            return result;
        }
   }

   // do unit testing of this class
   public static void main(String[] args)
   {
       
   }
}
