package hw3.puzzle;

import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import java.util.ArrayList;

public class Solver {


    private class SNode implements Comparable {

        private int numMoves;
        private int distance;
        private int priority;
        private WorldState node;
        private SNode prevNode;


        private SNode(WorldState node, int numMoves, SNode prevNode) {
            this.node = node;
            this.prevNode = prevNode;
            this.numMoves = numMoves;

            this.distance = node.estimatedDistanceToGoal();
            this.priority = this.distance + this.numMoves;
        }

        @Override
        public int compareTo(Object o) {
            SNode oddaNode = (SNode) o;

            if (this.priority < oddaNode.priority) {
                return -1;
            } else if (this.priority > oddaNode.priority) {
                return 1;
            } else {
                return 0;
            }
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            WorldState node2 = (WorldState) obj;
            return this.node.equals(node2);
        }

        @Override
        public String toString() {
            return this.node.toString();
        }
    }

    private MinPQ<SNode> sNodes = new MinPQ<>();
    private int moves = 0;
    private Stack<WorldState> solution = new Stack<>();

    /**Constructor which solves the puzzle, computing everything
     *  necessary for moves() and solution() to
        not have to solve the problem again. Solves the
     puzzle using the A* algorithm. Assumes a solution exists.*/
    public Solver(WorldState initial) {
        SNode init = new SNode(initial, 0, null);
        this.sNodes.insert(init);
        boolean goal = false;

        while (!goal) {

            SNode item = this.sNodes.delMin();
            if (item.node.isGoal()) {          //checking to see if the item is the goal or not
                goal = true;
                while (item != null) {
                    this.solution.push(item.node);
                    item = item.prevNode;
                    this.moves += 1;
                }
                this.moves -= 1;
            } else {
                SNode parent = item.prevNode;
                for (WorldState neighbor : item.node.neighbors()) {
                    if (parent == null || !neighbor.equals(parent.node)) {
                        this.sNodes.insert(new SNode(neighbor, item.numMoves + 1, item));
                    }
                }
            }
        }
    }

    /*Returns the minimum number of moves to solve the*/
     /* puzzle starting at the initial WorldState.*/
    public int moves() {
        return this.moves;
    }

    /**Returns a sequence of WorldStates from the initial WorldState to the solution.*/
    public Iterable<WorldState> solution() {
        ArrayList<WorldState> result = new ArrayList<>(this.solution.size());
        for (WorldState item : this.solution) {
            result.add(item);
        }
        return result;
    }

}
