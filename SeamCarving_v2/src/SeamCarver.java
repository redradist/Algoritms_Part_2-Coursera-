import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.AcyclicSP;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.Iterator;

public class SeamCarver {
    private Picture picture;

    /**
     * Create a seam carver object based on the given picture
     * @param picture
     */
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("picture should not be null !!");
        }
        this.picture = picture;
    }

    /**
     * Current picture
     * @return Picture
     */
    public Picture picture() {
        return picture;
    }

    /**
     * Width of current picture
     * @return Width
     */
    public int width() {
        return picture.width();
    }

    /**
     * Height of current picture
     * @return Height
     */
    public int height() {
        return picture.height();
    }

    /**
     * Energy of pixel at column x and row y
     * @param x Column
     * @param y Row
     * @return Energy of pixel
     */
    public double energy(int x, int y) {
        if (x < 0 || x > (width() - 1)) {
            throw new IllegalArgumentException("x is out of range !!");
        }
        if (y < 0 || y > (height() - 1)) {
            throw new IllegalArgumentException("y is out of range !!");
        }
        double energy = 1000;
        if (x > 0 && x < (width() - 1) &&
            y > 0 && y < (height() - 1)) {
            Color rightOfPixel = picture.get(x + 1, y);
            Color leftOfPixel = picture.get(x - 1, y);
            double deltaX = Math.pow(rightOfPixel.getRed() - leftOfPixel.getRed(), 2) +
                            Math.pow(rightOfPixel.getGreen() - leftOfPixel.getGreen(), 2) +
                            Math.pow(rightOfPixel.getBlue() - leftOfPixel.getBlue(), 2);

            Color belowOfPixel = picture.get(x, y + 1);
            Color aboveOfPixel = picture.get(x, y - 1);
            double deltaY = Math.pow(belowOfPixel.getRed() - aboveOfPixel.getRed(), 2) +
                            Math.pow(belowOfPixel.getGreen() - aboveOfPixel.getGreen(), 2) +
                            Math.pow(belowOfPixel.getBlue() - aboveOfPixel.getBlue(), 2);

            energy = Math.sqrt(deltaX + deltaY);
        }
        return energy;
    }

    /**
     * Sequence of indices for horizontal seam
     * @return
     */
    public int[] findHorizontalSeam() {
        int[] seam = null;
        EdgeWeightedDigraph graph = createHorizontalGraph();
        AcyclicSP sp = new AcyclicSP(graph, 0);
        if (sp.hasPathTo(picture.width() * picture.height() + 1)) {
            seam = new int[picture.width()];
            Iterator<DirectedEdge> iter = sp.pathTo(picture.width() * picture.height() + 1).iterator();
            for (int i = 0; iter.hasNext(); ++i) {
                DirectedEdge edge = iter.next();
                if (i != 0 && i != picture.width() + 1) {
                    seam[i-1] = (edge.from() - 1) % picture.height();
                }
            }
        }
        return seam;
    }

    /**
     * Sequence of indices for vertical seam
     * @return
     */
    public int[] findVerticalSeam() {
        int[] seam = null;
        EdgeWeightedDigraph graph = createVerticalGraph();
        AcyclicSP sp = new AcyclicSP(graph, 0);
        if (sp.hasPathTo(picture.width() * picture.height() + 1)) {
            seam = new int[picture.height()];
            Iterator<DirectedEdge> iter = sp.pathTo(picture.width() * picture.height() + 1).iterator();
            for (int i = 0; iter.hasNext(); ++i) {
                DirectedEdge edge = iter.next();
                if (i != 0 && i != picture.height() + 1) {
                    seam[i-1] = (edge.from() - 1) % picture.width();
                }
            }
        }
        return seam;
    }

    /**
     * Horizontal seam from current picture
     * @param seam
     */
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("seam to remove should not be null !!");
        }
        if (seam.length != picture.width()) {
            throw new IllegalArgumentException("Wrong seam.length !!");
        }
        if (height() <= 1) {
            throw new IllegalArgumentException("Impossible to delete seam for height = 1 !!");
        }
        Picture newPicture = new Picture(picture.width(), picture.height() - 1);
        Integer prevSeamValue = null;
        for (int x = 0; x < picture.width(); ++x) {
            if (prevSeamValue != null) {
                if (Math.abs(prevSeamValue - seam[x]) > 1) {
                    throw new IllegalArgumentException("Element in seam differentiate more than 1 !!");
                }
            }
            for (int y = 0; y < picture.height(); ++y) {
                if (y != seam[x]) {
                    newPicture.set(x, (y > seam[x]) ? y - 1 : y, picture.get(x, y));
                }
            }
            prevSeamValue = seam[x];
        }
        picture = newPicture;
    }

    /**
     * Remove vertical seam from current picture
     * @param seam
     */
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("seam to remove should not be null !!");
        }
        if (seam.length != picture.height()) {
            throw new IllegalArgumentException("Wrong seam.length !!");
        }
        if (width() <= 1) {
            throw new IllegalArgumentException("Impossible to delete seam for width = 1 !!");
        }
        Picture newPicture = new Picture(picture.width() - 1, picture.height());
        Integer prevSeamValue = null;
        for (int y = 0; y < picture.height(); ++y) {
            if (prevSeamValue != null) {
                if (Math.abs(prevSeamValue - seam[y]) > 1) {
                    throw new IllegalArgumentException("Element in seam differentiate more than 1 !!");
                }
            }
            for (int x = 0; x < picture.width(); ++x) {
                if (x != seam[y]) {
                    newPicture.set((x > seam[y]) ? x - 1 : x, y, picture.get(x, y));
                }
            }
            prevSeamValue = seam[y];
        }
        picture = newPicture;
    }

    private EdgeWeightedDigraph createVerticalGraph() {
        EdgeWeightedDigraph graph = new EdgeWeightedDigraph(picture.width() * picture.height() + 2);
        for (int x = 0; x < picture.width(); ++x) {
            graph.addEdge(new DirectedEdge(0, x + 1, 0));
            for (int y = 0; y < picture.height() - 1; ++y) {
                if (x != 0) {
                    graph.addEdge(new DirectedEdge(
                            picture.width() * y + (x + 1),
                            picture.width() * (y + 1) + x,
                            energy(x, y)));
                }
                graph.addEdge(new DirectedEdge(
                        picture.width() * y + (x + 1),
                        picture.width() * (y + 1) + (x + 1),
                        energy(x, y)));
                if (x != picture.width() - 1) {
                    graph.addEdge(new DirectedEdge(
                            picture.width() * y + (x + 1),
                            picture.width() * (y + 1) + (x + 2),
                            energy(x, y)));
                }
            }
            graph.addEdge(new DirectedEdge(
                    picture.width() * (picture.height() - 1) + x + 1,
                    picture.width() * picture.height() + 1,
                    energy(x, picture.height() - 1)));
        }
        return graph;
    }

    private EdgeWeightedDigraph createHorizontalGraph() {
        EdgeWeightedDigraph graph = new EdgeWeightedDigraph(picture.width() * picture.height() + 2);
        for (int y = 0; y < picture.height(); ++y) {
            graph.addEdge(new DirectedEdge(0, y + 1, 0));
            for (int x = 0; x < picture.width() - 1; ++x) {
                if (y != 0) {
                    graph.addEdge(new DirectedEdge(
                            picture.height() * x + (y + 1),
                            picture.height() * (x + 1) + y,
                            energy(x, y)));
                }
                graph.addEdge(new DirectedEdge(
                        picture.height() * x + (y + 1),
                        picture.height() * (x + 1) + (y + 1),
                        energy(x, y)));
                if (y != picture.height() - 1) {
                    graph.addEdge(new DirectedEdge(
                            picture.height() * x + (y + 1),
                            picture.height() * (x + 1) + (y + 2),
                            energy(x, y)));
                }
            }
            graph.addEdge(new DirectedEdge(
                    picture.height() * (picture.width() - 1) + y + 1,
                    picture.height() * picture.width() + 1,
                    energy(picture.width() - 1, y)));
        }
        return graph;
    }

    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        SeamCarver seam = new SeamCarver(picture);

        int[] verticals = seam.findVerticalSeam();
        StdOut.println("Vertical seam:");
        for (int ver : verticals) {
            StdOut.println(ver);
        }
        seam.removeVerticalSeam(verticals);

        int[] horizontals = seam.findHorizontalSeam();
        StdOut.println("Horizontal seam:");
        for (int hor : horizontals) {
            StdOut.println(hor);
        }
        seam.removeHorizontalSeam(horizontals);
    }
}
