package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private int openBlocks;
    private int length;
    private int top;
    private int bottom;
    private boolean[] openBlocksIndices;
    private WeightedQuickUnionUF connection;
    private WeightedQuickUnionUF connection2;

    private int xyTo1D(int row, int col) {
        return row * length + col;
    }

    // create N-by-N grid, with all sites initially blocked
    public Percolation(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException();
        } else {
            this.openBlocks = 0;
            this.openBlocksIndices = new boolean[N * N];
            this.length = N;
            this.connection = new WeightedQuickUnionUF(N * N + 2);
            this.connection2 = new WeightedQuickUnionUF(N * N + 2);
            this.top = N * N;
            this.bottom = N * N + 1;
        }
    }

    // open the site (row, col) if it is not open already
    public void open(int row, int col) {
        int blockIndex = xyTo1D(row, col);

        if (blockIndex < 0 || blockIndex >= this.length * this.length) {
            throw new IndexOutOfBoundsException();
        } else if (!isOpen(row, col)) {
            openBlocksIndices[blockIndex] = true;
            openBlocks += 1;

            if (blockIndex < this.length) {
                this.connection.union(this.top, blockIndex);
                this.connection2.union(this.top, blockIndex);
            }

            // checking connections as a disjoint set
            if (row - 1 >= 0) {
                int up = xyTo1D(row - 1, col);
                if (isOpen(row - 1, col) && !this.connection.connected(blockIndex, up)) {
                    this.connection.union(blockIndex, up);
                    this.connection2.union(blockIndex, up);
                }
            }
            if (row + 1 < this.length) {
                int down = xyTo1D(row + 1, col);
                if (isOpen(row + 1, col) && !this.connection.connected(blockIndex, down)) {
                    this.connection.union(blockIndex, down);
                    this.connection2.union(blockIndex, down);
                }
            }
            if (col - 1 >= 0) {
                int left = xyTo1D(row, col - 1);
                if (isOpen(row, col - 1) && !this.connection.connected(blockIndex, left)) {
                    this.connection.union(blockIndex, left);
                    this.connection2.union(blockIndex, left);
                }
            }
            if (col + 1 < this.length) {
                int right = xyTo1D(row, col + 1);
                if (isOpen(row, col + 1) && !this.connection.connected(blockIndex, right)) {
                    this.connection.union(blockIndex, right);
                    this.connection2.union(blockIndex, right);
                }
            }

            if ((this.length - 1) * this.length - 1
                    < blockIndex && blockIndex < this.length * this.length) {
                if (this.connection.connected(this.top, blockIndex)) {
                    this.connection.union(this.bottom, blockIndex);
                }
                this.connection2.union(this.bottom, blockIndex);
            }
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        int blockIndex = xyTo1D(row, col);

        if (blockIndex < 0 || blockIndex >= this.length * this.length) {
            throw new IndexOutOfBoundsException();
        } else {
            return openBlocksIndices[blockIndex];
        }
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        int blockIndex = xyTo1D(row, col);

        if (blockIndex < 0 || blockIndex >= this.length * this.length) {
            throw new IndexOutOfBoundsException();
        } else if (!openBlocksIndices[blockIndex]) {
            return false;
        }

        if (this.connection.connected(this.top, blockIndex)) {
            return true;
        }
        return false;
    }

    // number of open sites
    public int numberOfOpenSites() {
        return this.openBlocks;
    }

    // does the system percolate?
    public boolean percolates() {
        return this.connection2.connected(this.top, this.bottom);
    }

    // unit testing
    public static void main(String[] args) {
        Percolation grid = new Percolation(10);

        System.out.println("");
        grid.open(9, 4);
        grid.open(8, 4);
        grid.open(7, 4);
        grid.open(6, 4);
        grid.open(5, 4);
        grid.open(4, 4);
        grid.open(3, 4);
        grid.open(2, 4);
        grid.open(1, 4);
        grid.open(0, 4);
        System.out.println(grid.isFull(4, 5));
        System.out.println(grid.percolates());
        System.out.println(grid.isOpen(0, 0));
        /*for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                grid.open(i, j);
            }
        }
        System.out.println(grid.percolates());*/

    }
}                       
