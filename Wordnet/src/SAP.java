/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 *
 * @author REDRADIST
 */
public class SAP {
   private int ancestor = -1;
   private int distance = -1;
   
   private int seq_ancestor = -1;
   private int seq_distance = -1;
   private final Digraph diagraph;
   // constructor takes a digraph (not necessarily a DAG)
   public SAP(Digraph G)
   {
       diagraph = new Digraph(G);
   }

   // length of shortest ancestral path between v and w; -1 if no such path
   public int length(int v, int w)
   {
       ancestor(v, w);
       return distance;
   }

   // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
   public int ancestor(int v, int w)
   {
       BreadthFirstDirectedPaths vpaths
               = new BreadthFirstDirectedPaths(diagraph, v);
       BreadthFirstDirectedPaths wpaths
               = new BreadthFirstDirectedPaths(diagraph, w);
       ancestor = -1;
       distance = -1;
       for (int i = 0; i < diagraph.V(); i++)
       {
           if (vpaths.hasPathTo(i) && wpaths.hasPathTo(i))
           {
               if (distance == -1 || 
                  (vpaths.distTo(i) + wpaths.distTo(i)) < distance)
                {
                    ancestor = i;
                    distance = vpaths.distTo(i) + wpaths.distTo(i);
                }
           }
       }
       return ancestor;
   }

   // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
   public int length(Iterable<Integer> v, Iterable<Integer> w)
   {
       ancestor(v, w);
       return seq_distance;
   }

   // a common ancestor that participates in shortest ancestral path; -1 if no such path
   public int ancestor(Iterable<Integer> v, Iterable<Integer> w)
   {
       seq_ancestor = -1;
       seq_distance = -1;
       for (int _v : v)
       {
           for (int _w : w)
           {
               ancestor(_v, _w);
               if (seq_distance == -1 || 
                  (distance != -1 && distance < seq_distance))
               {
                  seq_ancestor = ancestor;
                  seq_distance = distance;
               }
           }
       }
       return seq_ancestor;
   }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
