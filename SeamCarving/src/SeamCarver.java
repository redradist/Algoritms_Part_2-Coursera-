
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.DijkstraAllPairsSP;
import edu.princeton.cs.algs4.DijkstraSP;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.IndexMinPQ;
import edu.princeton.cs.algs4.Picture;
import java.util.Iterator;

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
   private Picture m_picture;
   private final double[][] m_energy;
   public SeamCarver(Picture picture)
   {
    m_picture = new Picture(picture);
    m_energy = new double[m_picture.width()][m_picture.height()];
    int size_x = m_picture.width(); 
    int size_y = m_picture.height();
    for (int x = 0; x < size_x; ++x)
    {
        for (int y = 0; y < size_y; ++y) 
        {
           m_energy[x][y] = energy(x, y);
        }
    }
   }
   public Picture picture()
   {
       return m_picture;
   }
   public int width()
   {
       return m_picture.width();
   }
   public int height()
   {
       return m_picture.height();
   }
   public double energy(int x, int y)
   {
       double energy_pixel = 0;
       //
       if (x-1 >= 0 && x+1 < m_picture.width())
       {
           energy_pixel += 
                Math.pow(Math.abs(m_energy[x+1][y]-m_energy[x-1][y]),2);
       }
       else if (x-1 >= 0)
       {
           energy_pixel += 
                Math.pow(Math.abs(m_energy[x][y]-m_energy[x-1][y]),2);
       }
       else 
       {
           energy_pixel += 
                Math.pow(Math.abs(m_energy[x+1][y]-m_energy[x][y]),2);
       }
       if (y-1 >= 0 && y+1 < m_picture.height())
       {
           energy_pixel += 
                Math.pow(Math.abs(m_energy[x][y+1]-m_energy[x][y-1]),2);
       }
       else if (y-1 >= 0)
       {
           energy_pixel += 
                Math.pow(Math.abs(m_energy[x][y]-m_energy[x][y-1]),2);
       }
       else 
       {
           energy_pixel += 
                Math.pow(Math.abs(m_energy[x][y+1]-m_energy[x][y]),2);
       }
       return Math.sqrt(energy_pixel);
   }
   public int[] findHorizontalSeam()
   {
       EdgeWeightedDigraph graph;
       graph = new EdgeWeightedDigraph(m_picture.width()*m_picture.height()+2);
       int size_x = m_picture.width(); 
       int size_y = m_picture.height();
       int max = size_x * size_y + 1;
       for (int i = 0; i < size_x; ++i)
       {
            int x = i+1;
            graph.addEdge(new DirectedEdge(0, x, 0));
            for (int k = 0; k < size_y; ++k)
            {
                if (x+size_x-1 < max && k-1 > 0)
                    graph.addEdge(new DirectedEdge(x, x+size_x-1, m_energy[x][k-1]));
                if (x+size_x < max)
                    graph.addEdge(new DirectedEdge(x, x+size_x, m_energy[x][k]));
                if (x+size_x+1 < max && k+1 < size_y)
                    graph.addEdge(new DirectedEdge(x, x+size_x+1, m_energy[x][k+1]));
            }
            graph.addEdge(
               new DirectedEdge((size_y-1)*size_x+i+1, size_x*size_y+1, 0));
       }
       
       DijkstraSP shortest_path;
       shortest_path = new DijkstraSP(graph, 0);
       int[] horizontalSeam = new int[size_y];
       Iterator<DirectedEdge> path = 
               shortest_path.pathTo(size_x*size_y+1).iterator(); 
       int i = 0;
       path.next();
       while(path.hasNext())
       {
           horizontalSeam[i] = path.next().from();
           i++;
       }
       horizontalSeam[size_y-1] = horizontalSeam[size_y-2];
       return horizontalSeam;
   }
   public int[] findVerticalSeam()
   {
       EdgeWeightedDigraph graph;
       graph = new EdgeWeightedDigraph(m_picture.width()*m_picture.height()+2);
       int size_x = m_picture.width(); 
       int size_y = m_picture.height();
       int max = size_x * size_y + 1;
       for (int i = 0; i < size_y; ++i)
       {
            int y = i+1;
            graph.addEdge(new DirectedEdge(0, y, 0));
            for (int k = 0; k < size_x; ++k)
            {
                if (y+size_y-1 < max && k-1 > 0)
                    graph.addEdge(new DirectedEdge(y, y+size_y-1, m_energy[k-1][y]));
                if (y+size_y < max)
                    graph.addEdge(new DirectedEdge(y, y+size_y, m_energy[k][y]));
                if (y+size_y+1 < max && k+1 < size_x)
                    graph.addEdge(new DirectedEdge(y, y+size_y+1, m_energy[k+1][y]));
            }
            graph.addEdge(
               new DirectedEdge((size_x-1)*size_y+i+1, size_x*size_y+1, 0));
       }
       
       DijkstraSP shortest_path;
       shortest_path = new DijkstraSP(graph, 0);
       int[] verticalSeam = new int[size_x];
       Iterator<DirectedEdge> path = 
               shortest_path.pathTo(size_x*size_y+1).iterator(); 
       int i = 0;
       path.next();
       while(path.hasNext())
       {
           verticalSeam[i] = path.next().from();
           i++;
       }
       verticalSeam[size_y-1] = verticalSeam[size_y-2];
       return verticalSeam;
   }
   public void removeHorizontalSeam(int[] seam)
   {
       m_picture.
   }
   public void removeVerticalSeam(int[] seam)
   {
       
   }
}
