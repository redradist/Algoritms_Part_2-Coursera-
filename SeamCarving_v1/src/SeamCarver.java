
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.IndexMinPQ;
import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;
import java.awt.Color;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author REDRADIST
 */
public class SeamCarver {
    private enum Direction 
    {
        Horizontal,
        Vertical
    }
    
    private class RunTimeGraph
    {
        public double[] distTo = null;
        public DirectedEdge[] edgeTo;

        private RunTimeGraph() {
            this.distTo = null;
            this.edgeTo = null;
        }
    }
    
   private Picture mPicture;
   
   public SeamCarver(Picture picture)
   {
        mPicture = new Picture(picture);
   }
   
   public Picture picture()
   {
       return new Picture(mPicture);
   }
   
   public int width()
   {
       return mPicture.width();
   }
   public int height()
   {
       return mPicture.height();
   }
   
   public double energy(int x, int y)
   {
       int sizeX = mPicture.width(); 
       int sizeY = mPicture.height();
       if (x < 0 || x >= sizeX)
       {
           throw new IndexOutOfBoundsException("Invalid index x");
       }
       else if (y < 0 || y >= sizeY)
       {
           throw new IndexOutOfBoundsException("Invalid index y");
       }
       double energy = 1000;
       if ((x-1 >= 0 && x+1 < mPicture.width()) &&
           (y-1 >= 0 && y+1 < mPicture.height()))
       {
           Color rightColor = mPicture.get(x+1, y);
           Color leftColor = mPicture.get(x-1, y);
           double diffHorizontal = diffEnergy(rightColor, leftColor);
           
           Color upColor = mPicture.get(x, y-1);
           Color downColor = mPicture.get(x, y+1);
           double diffVertical = diffEnergy(upColor, downColor);
           
           energy = Math.sqrt(diffHorizontal+diffVertical);
       }
       return energy;
   }
   
   private double diffEnergy(Color first, Color second)
   {
    double ydeltaBlue = 
        Math.pow(Math.abs(first.getBlue() - second.getBlue()), 2);
    double ydeltaGreen = 
        Math.pow(Math.abs(first.getGreen()- second.getGreen()), 2);
    double ydeltaRed = 
        Math.pow(Math.abs(first.getRed()- second.getRed()), 2);
    return ydeltaBlue + ydeltaGreen + ydeltaRed;
   }
   
    private RunTimeGraph computeGraph(Direction direct)
    {
        int sizeX = mPicture.width(); 
        int sizeY = mPicture.height();
        int vertexes = sizeX * sizeY + 1;
       
        RunTimeGraph graph = new RunTimeGraph();
        graph.distTo = new double[vertexes];
        graph.edgeTo = new DirectedEdge[vertexes];
        for (int v = 0; v < vertexes; v++)
            graph.distTo[v] = Double.POSITIVE_INFINITY;
        
        int sizeTopItems = (direct == Direction.Horizontal) ? sizeY : sizeX;
        // relax vertices in order of distance from s
        IndexMinPQ<Double> pq = new IndexMinPQ<Double>(vertexes);
        for (int item = 0; item < sizeTopItems; ++item)
        {
            graph.distTo[item] = 0.0;
            pq.insert(item, graph.distTo[item]);
        }
        while (!pq.isEmpty()) 
        {
            int v = pq.delMin();
            for (DirectedEdge e : edgesFrom(v, direct))
            {
                if (e != null)
                {
                    int s = e.from();
                    int i = e.to();
                    if (graph.distTo[i] > graph.distTo[s] + e.weight()) {
                        graph.distTo[i] = graph.distTo[s] + e.weight();
                        graph.edgeTo[i] = e;
                        if (pq.contains(i)) pq.decreaseKey(i, graph.distTo[i]);
                        else                pq.insert(i, graph.distTo[i]);
                    }
                }
            }
        }
        
        return graph;
    }
   
   // relax edge e and update pq if changed
    private ArrayList<DirectedEdge> edgesFrom(int vertex, Direction direct)
    {
        int sizeX = mPicture.width(); 
        int sizeY = mPicture.height();
        ArrayList<DirectedEdge> edges = new ArrayList<>();
        if (vertex < sizeY * sizeX)
        {
            int widthRow = (direct == Direction.Horizontal) ? sizeY : sizeX ;
            int penultRow = (direct == Direction.Horizontal) ? sizeX : sizeY ;
            int relativeXIndex = (direct == Direction.Horizontal) ? 
                                  vertex/widthRow : vertex%widthRow;
            int relativeYIndex = (direct == Direction.Horizontal) ? 
                                  vertex%widthRow : vertex/widthRow;
            int energyXIndex = (direct == Direction.Horizontal) ? 
                                sizeX-relativeXIndex-1 : relativeXIndex;
            int energyYIndex = relativeYIndex;
            double energy = energy(energyXIndex, energyYIndex);
            if (vertex >= widthRow * (penultRow-1))
            {
                edges.add(new DirectedEdge(vertex, sizeX * sizeY, energy));
            }
            else 
            {
              int checkIndex = (direct == Direction.Horizontal) ? 
                                relativeYIndex : relativeXIndex;
              if (checkIndex-1 >= 0)
                  edges.add(new DirectedEdge(vertex, vertex+widthRow-1, energy));
              if (checkIndex+1 < widthRow)
                  edges.add(new DirectedEdge(vertex, vertex+widthRow+1, energy));
              edges.add(new DirectedEdge(vertex, vertex+widthRow, energy));
            }
        }
        return edges;
    }
   
   public int[] findHorizontalSeam()
   {
        int sizeX = mPicture.width(); 
        int sizeY = mPicture.height();
        
        RunTimeGraph graph = computeGraph(Direction.Horizontal);
        int[] horizontalSeam = new int[sizeX];
        if (graph.distTo[sizeX * sizeY] < Double.POSITIVE_INFINITY)
        {
            int i = 0;
            for (DirectedEdge e = graph.edgeTo[sizeX * sizeY]; e != null; e = graph.edgeTo[e.from()]) {
                horizontalSeam[i++] = e.from()%sizeY;
            }
        }
        
        fixHorizontal(horizontalSeam);
        return horizontalSeam;
   }
   
   private void fixHorizontal(int[] seam)
   {
       /*  
        * Needed only for class.
        * Meanless, 'cause all items on the board have a value of 1000.
        */
        int sizeX = mPicture.width(); 
        int sizeY = mPicture.height();
        if (sizeX > 1)
        {
            seam[0] = seam[1];
            if (seam[sizeX-2]+1 < sizeY)
                seam[sizeX-1] = seam[sizeX-2]+1;
        }
   }
   
    
   
   public int[] findVerticalSeam()
   {
        int sizeX = mPicture.width(); 
        int sizeY = mPicture.height();
       
        RunTimeGraph graph = computeGraph(Direction.Vertical);
        int[] verticalSeam = new int[sizeY];
        if (graph.distTo[sizeX * sizeY] < Double.POSITIVE_INFINITY)
        {
            int i = sizeY - 1;
            for (DirectedEdge e = graph.edgeTo[sizeX * sizeY]; e != null; e = graph.edgeTo[e.from()]) {
                verticalSeam[i--] = e.from()%sizeX;
            }
        }
        
        fixVertical(verticalSeam);
        return verticalSeam;
   }
   
   private void fixVertical(int[] seam)
   {
       /*  
        * Needed only for class.
        * Meanless, 'cause all items on the board have a value of 1000.
        */
        int sizeY = mPicture.height();
        if (sizeY > 1)
        {
            seam[sizeY-1] = seam[sizeY-2];
            if (seam[1]-1 >= 0)
                seam[0] = seam[1]-1;
        }
   }
   
   public void removeHorizontalSeam(int[] seam)
   {
       int sizeX = mPicture.width();
       int sizeY = mPicture.height();
       if (seam.length != sizeX)
            throw new IllegalArgumentException ();
       Picture temp;
       temp = new Picture(sizeX, sizeY-1);
       int prev = seam[0];
       for (int x = 0; x < sizeX; ++x)
       {       
            for (int y = 0; y < sizeY; ++y)
            {
                if (seam[x] < 0 || seam[x] >= sizeY ||
                    Math.abs(prev-seam[x]) > 1)
                    throw new IllegalArgumentException ();
                prev = seam[x];
                if (seam[x] != y)
                    temp.set(x, (y > seam[x])? y-1 : y, mPicture.get(x, y));
            }
       }
       mPicture = temp;
   }
   
   public void removeVerticalSeam(int[] seam)
   {
       int sizeX = mPicture.width();
       int sizeY = mPicture.height();
       if (seam.length != sizeY)
            throw new IllegalArgumentException ();
       Picture temp;
       temp = new Picture(sizeX-1, sizeY);
       int prev = seam[0];
       for (int y = 0; y < sizeY; ++y)
       {       
            for (int x = 0; x < sizeX; ++x)
            {
                if (seam[y] < 0 || seam[y] >= sizeX ||
                    Math.abs(prev-seam[y]) > 1)
                    throw new IllegalArgumentException ();
                prev = seam[y];
                if (seam[y] != x)
                    temp.set((x > seam[y])? x-1 : x, y, mPicture.get(x, y));
            }
       }
       mPicture = temp;
   }
   
    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        SeamCarver seam = new SeamCarver(picture);
        int[] vertical = seam.findVerticalSeam();
        StdOut.println("Vertical seam:");
        for (int ver : vertical)
        {
            StdOut.println(ver);
        }
        int[] horizontal = seam.findHorizontalSeam();
        StdOut.println("Horizontal seam:");
        for (int hor : horizontal)
        {
            StdOut.println(hor);
        }
    }
}

