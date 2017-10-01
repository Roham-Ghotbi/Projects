package hw2;

import edu.princeton.cs.introcs.StdRandom;
import edu.princeton.cs.introcs.StdStats;


public class PercolationStats {

    private int quantity;
    private int numBlocks;
    private double[] results;

    private double threshold(int N) {
        Percolation grid = new Percolation(N);
        double k = 0;

        while (!grid.percolates()) {
            int randomRow = StdRandom.uniform(0, N);
            int randomCol = StdRandom.uniform(0, N);
            if (!grid.isOpen(randomRow, randomCol)) {
                grid.open(randomRow, randomCol);
                k++;
            }
        }
        return k / (double) numBlocks;
    }

    // perform T independent experiments on an N-by-N grid
    public PercolationStats(int N, int T) {
        if (N <= 0 || T <= 0) {
            throw new IllegalArgumentException();
        }
        this.quantity = T;
        this.numBlocks = N * N;
        this.results = new double[T];

        for (int i = 0; i < T; i++) {
            results[i] = threshold(N);
        }

    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(this.results);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(this.results);
    }

    // low  endpoint of 95% confidence interval
    public double confidenceLow() {
        double low = this.mean() - (1.96 * this.stddev() / (Math.sqrt(this.quantity)));
        return low;
    }

    // high endpoint of 95% confidence interval
    public double confidenceHigh() {
        double high = this.mean() + (1.96 * this.stddev() / (Math.sqrt(this.quantity)));
        return high;
    }

    public static void main(String[] args) {

        PercolationStats calcer = new PercolationStats(20, 30);

    }
}                       
