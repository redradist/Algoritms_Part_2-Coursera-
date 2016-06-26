
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.IndexMinPQ;
import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;
import java.awt.Color;

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
   private Picture mPicture;
   
   private class Item {
       public int          index;
       public Item         next;

        private Item() {
            this.next = null;
        }
   }
   
   private class Way {
       public double       distance;
       public Item         start;

        private Way() {
            this.start = null;
        }
   }
   
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
   
   // relax edge e and update pq if changed
    private DirectedEdge[] edgesYFrom(int vertex)
    {
        int sizeX = mPicture.width(); 
        int sizeY = mPicture.height();
        DirectedEdge[] edges = new DirectedEdge[0];
        if (vertex < sizeY * sizeX)
        {
            if (vertex >= sizeY * (sizeX-1))
            {
                edges = new DirectedEdge[1];
                int relativeYIndex = vertex%sizeY;
                int relativeXIndex = vertex/sizeY;
                double energy = energy(sizeX-relativeXIndex-1, relativeYIndex);
                edges[0] = new DirectedEdge(vertex, sizeX * sizeY, energy);
            }
            else 
            {
              int relativeYIndex = vertex%sizeY;
              int relativeXIndex = vertex/sizeY;
              double energy = energy(sizeX-relativeXIndex-1, relativeYIndex);
              int edgesNum = (relativeYIndex-1 >= 0 && relativeYIndex+1 < sizeY) ? 3 : 2;
              edges = new DirectedEdge[edgesNum];

              int i = 0;
              if (relativeYIndex-1 >= 0)
                  edges[i++] = new DirectedEdge(vertex, vertex+sizeY-1, energy);
              if (relativeYIndex+1 < sizeY)
                  edges[i++] = new DirectedEdge(vertex, vertex+sizeY+1, energy);
              edges[i] = new DirectedEdge(vertex, vertex+sizeY, energy);
            }
        }
        return edges;
    }
   
   public int[] findHorizontalSeam()
   {
        int sizeX = mPicture.width(); 
        int sizeY = mPicture.height();
        int vertexes = sizeX * sizeY + 1;
       
        double[] distTo = new double[vertexes];
        DirectedEdge[] edgeTo = new DirectedEdge[vertexes];
        for (int v = 0; v < vertexes; v++)
            distTo[v] = Double.POSITIVE_INFINITY;
        
        int[] horizontalSeam = new int[sizeX];
        double minDistance = Double.POSITIVE_INFINITY;
        // relax vertices in order of distance from s
        IndexMinPQ<Double> pq = new IndexMinPQ<Double>(vertexes);
        for (int y = sizeY-1; y >= 0; --y)
        {
            distTo[y] = 0.0;
            pq.insert(y, distTo[y]);
        }
        while (!pq.isEmpty()) 
        {
            int v = pq.delMin();
            for (DirectedEdge e : edgesYFrom(v))
            {
                if (e != null)
                {
                    int s = e.from();
                    int i = e.to();
                    if (distTo[i] > distTo[s] + e.weight()) {
                        distTo[i] = distTo[s] + e.weight();
                        edgeTo[i] = e;
                        if (pq.contains(i)) pq.decreaseKey(i, distTo[i]);
                        else                pq.insert(i, distTo[i]);
                    }
                }
            }
        }
        
        if (distTo[sizeX * sizeY] < minDistance)
        {
            minDistance = distTo[sizeX * sizeY];
            int i = 0;
            for (DirectedEdge e = edgeTo[sizeX * sizeY]; e != null; e = edgeTo[e.from()]) {
                if (e.from() != 0 && e.from() != sizeX * sizeY)
                    horizontalSeam[i++] = e.from()%sizeY;
            }
        }
        
        if (sizeX > 1)
        {
            horizontalSeam[0] = horizontalSeam[1];
            if (horizontalSeam[sizeX-2]+1 < sizeY)
                horizontalSeam[sizeX-1] = horizontalSeam[sizeX-2]+1;
        }
        return horizontalSeam;
   }
   
    // relax edge e and update pq if changed
    private DirectedEdge[] edgesXFrom(int vertex)
    {
        int sizeX = mPicture.width(); 
        int sizeY = mPicture.height();
        DirectedEdge[] edges = new DirectedEdge[0];
        if (vertex < sizeX * sizeY)
        {
            if (vertex >= sizeX * (sizeY-1))
            {
                edges = new DirectedEdge[1];
                int relativeXIndex = vertex%sizeX;
                int relativeYIndex = vertex/sizeX;
                double energy = energy(relativeXIndex, relativeYIndex);
                edges[0] = new DirectedEdge(vertex, sizeX * sizeY, energy);
            }
            else 
            {
              int relativeXIndex = vertex%sizeX;
              int relativeYIndex = vertex/sizeX;
              double energy = energy(relativeXIndex, relativeYIndex);
              int edgesNum = (relativeXIndex-1 >= 0 && relativeXIndex+1 < sizeX) ? 3 : 2;
              edges = new DirectedEdge[edgesNum];

              int i = 0;
              if (relativeXIndex-1 >= 0)
                  edges[i++] = new DirectedEdge(vertex, vertex+sizeX-1, energy);
              if (relativeXIndex+1 < sizeX)
                  edges[i++] = new DirectedEdge(vertex, vertex+sizeX+1, energy);
              edges[i] = new DirectedEdge(vertex, vertex+sizeX, energy);
            }
        }
        return edges;
    }
   
   public int[] findVerticalSeam()
   {
        int sizeX = mPicture.width(); 
        int sizeY = mPicture.height();
        int vertexes = sizeX * sizeY + 1;
       
        double[] distTo = new double[vertexes];
        DirectedEdge[] edgeTo = new DirectedEdge[vertexes];
        for (int v = 0; v < vertexes; v++)
            distTo[v] = Double.POSITIVE_INFINITY;
        
        int[] verticalSeam = new int[sizeY];
        double minDistance = Double.POSITIVE_INFINITY;
        // relax vertices in order of distance from s
        IndexMinPQ<Double> pq = new IndexMinPQ<Double>(vertexes);
        for (int x = sizeX-1; x >= 0; --x)
        {
            distTo[x] = 0.0;
            pq.insert(x, distTo[x]);
        }
        while (!pq.isEmpty()) 
        {
            int v = pq.delMin();
            for (DirectedEdge e : edgesXFrom(v))
            {
                if (e != null)
                {
                    int s = e.from();
                    int i = e.to();
                    if (distTo[i] > distTo[s] + e.weight()) {
                        distTo[i] = distTo[s] + e.weight();
                        edgeTo[i] = e;
                        if (pq.contains(i)) pq.decreaseKey(i, distTo[i]);
                        else                pq.insert(i, distTo[i]);
                    }
                }
            }
        }
        if (distTo[sizeX * sizeY] < minDistance)
        {
            minDistance = distTo[sizeX * sizeY];
            int i = sizeY - 1;
            for (DirectedEdge e = edgeTo[sizeX * sizeY]; e != null; e = edgeTo[e.from()]) {
                if (e.from() != sizeX * sizeY)
                    verticalSeam[i--] = e.from()%sizeX;
            }
        }
        
        if (sizeY > 1)
        {
            verticalSeam[sizeY-1] = verticalSeam[sizeY-2];
            if (verticalSeam[1]-1 >= 0)
                verticalSeam[0] = verticalSeam[1]-1;
        } 
        return verticalSeam;
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
