
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.DijkstraAllPairsSP;
import edu.princeton.cs.algs4.Picture;

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
       if (x-1 >= 0 && x+1 < m_picture.width())
       {
           energy_pixel += 
                Math.pow(Math.abs(m_energy[x+1][y]-m_energy[x-1][y]),2);
       }
       if (y-1 >= 0 && y+1 < m_picture.height())
       {
           energy_pixel += 
                Math.pow(Math.abs(m_energy[x][y+1]-m_energy[x][y-1]),2);
       }
       return Math.sqrt(energy_pixel);
   }
   public int[] findHorizontalSeam()
   {
       EdgeWeightedDigraph graph;
       DijkstraSP shortest_path;
       graph = new EdgeWeightedDigraph(m_picture.width()*m_picture.height());
       return new int[10];
   }
   public int[] findVerticalSeam()
   {
       EdgeWeightedDigraph;
       return new int[10];
   }
   public void removeHorizontalSeam(int[] seam)
   {
       []
       m_picture
   }
   public void removeVerticalSeam(int[] seam)
   {
       
   }
}
