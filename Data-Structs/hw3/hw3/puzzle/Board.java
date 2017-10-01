package hw3.puzzle;

import edu.princeton.cs.algs4.Queue;

public class Board implements WorldState {

    private int sizeN;
    private int[][] board;
    private int[][] goal;

    /**
     * Constructs a board from an N-by-N array of tiles where tiles[i][j] = tile at row i, column j
     */
    public Board(int[][] tiles) {

        this.sizeN = tiles.length;
        int counter = 1;
        this.board = new int[this.sizeN][this.sizeN];
        this.goal = new int[this.sizeN][this.sizeN];

        for (int i = 0; i < this.sizeN; i++) {
            for (int j = 0; j < this.sizeN; j++) {
                this.board[i][j] = tiles[i][j];
                this.goal[i][j] = counter;
                counter += 1;
            }
        }

        goal[sizeN - 1][sizeN - 1] = 0;
    }

    /**
     * Returns value of tile at row i, column j (or 0 if blank)
     */
    public int tileAt(int i, int j) {
        if (i < 0 || i >= this.sizeN || j < 0 || j >= this.sizeN) {
            throw new IndexOutOfBoundsException();
        } else {
            return this.board[i][j];
        }
    }

    /**
     * Returns the board size N
     */
    public int size() {
        return this.sizeN;
    }

    /**
     * Returns the neighbors of the current board
     */
    @Override
    public Iterable<WorldState> neighbors() {
        Queue<WorldState> neighbors = new Queue<>();
        int hug = size();
        int bug = -1;
        int zug = -1;
        for (int rug = 0; rug < hug; rug++) {
            for (int tug = 0; tug < hug; tug++) {
                if (tileAt(rug, tug) == 0) {
                    bug = rug;
                    zug = tug;
                }
            }
        }
        int[][] ili1li1 = new int[hug][hug];
        for (int pug = 0; pug < hug; pug++) {
            for (int yug = 0; yug < hug; yug++) {
                ili1li1[pug][yug] = tileAt(pug, yug);
            }
        }
        for (int l11il = 0; l11il < hug; l11il++) {
            for (int lil1il1 = 0; lil1il1 < hug; lil1il1++) {
                if (Math.abs(-bug + l11il) + Math.abs(lil1il1 - zug) - 1 == 0) {
                    ili1li1[bug][zug] = ili1li1[l11il][lil1il1];
                    ili1li1[l11il][lil1il1] = 0;
                    Board neighbor = new Board(ili1li1);
                    neighbors.enqueue(neighbor);
                    ili1li1[l11il][lil1il1] = ili1li1[bug][zug];
                    ili1li1[bug][zug] = 0;
                }
            }
        }
        return neighbors;
    }    //provided code by professor in specs

    /**
     * Hamming estimate described below
     */
    public int hamming() {
        int hamming = 0;
        for (int i = 0; i < this.sizeN; i++) {
            for (int j = 0; j < this.sizeN; j++) {
                if (this.board[i][j] != this.goal[i][j] && this.board[i][j] != 0) {
                    hamming += 1;
                }
            }
        }
        return hamming;
    }

    /**
     * Manhattan estimate described below
     */
    public int manhattan() {
        int totDist = 0;

        for (int i = 0; i < this.sizeN; i++) {
            for (int j = 0; j < this.sizeN; j++) {

                int value = this.board[i][j];
                if (value != 0 && value != this.goal[i][j]) {
                    int row = (value - 1) / this.sizeN;
                    int column = (value - 1) % this.sizeN;
                    totDist += Math.abs(i - row) + Math.abs(j - column);
                }
            }
        }
        return totDist;
    }

    /**
     * Estimated distance to goal. This method should
     * simply return the results of manhattan() when submitted to
     * Gradescope.
     */

    @Override
    public int estimatedDistanceToGoal() {
        return this.manhattan();
    }

    /**
     * Returns true if is this board the goal board
     */

    @Override
    public boolean isGoal() {
        return this.hamming() == 0;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Returns true if this board's tile values are the same
     * position as y's
     */
    public boolean equals(Object y) {
        if (y == null || !y.getClass().equals(Board.class) || ((Board) y).sizeN != this.sizeN) {
            return false;
        } else {
            for (int i = 0; i < this.sizeN; i++) {
                for (int j = 0; j < this.sizeN; j++) {
                    if (this.board[i][j] != ((Board) y).board[i][j]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Returns the string representation of the board. This
     * method is provided in the skeleton
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        int N = size();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tileAt(i, j)));
            }
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }

}
