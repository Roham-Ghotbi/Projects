import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /**
     * Return a LinkedList of <code>Long</code>s representing the shortest path from st to dest,
     * where the longs are node IDs.
     */


    public static LinkedList<Long> shortestPath(GraphDB g, double stlon, double stlat,
                                                double destlon, double destlat) {

        GraphNode destination = g.nodes.get(g.closest(destlon, destlat));
        GraphNode start = g.nodes.get(g.closest(stlon, stlat));
        PathNode finalNode = new PathNode(0, 0, destination, null);
        PathNode startP = new PathNode(0, 0, start, null);
        PriorityQueue<PathNode> fringe = new PriorityQueue<>(PathNode::compareTo);
        LinkedList<Long> solution = new LinkedList<>();
        fringe.add(startP);
        boolean reachedGoal = false;
        HashSet<Long> seen = new HashSet<>();
        while (!fringe.isEmpty() && !reachedGoal) {
            PathNode item = fringe.remove();
            seen.add(item.node.id);
            if (item.equals(finalNode)) {
                reachedGoal = true;
                while (item != null) {
                    solution.addFirst(item.node.id);
                    item = item.prevNode;
                }
            } else {
                for (long id : item.node.adj) {
                    if (!seen.contains(id)) {
                        GraphNode temp = g.nodes.get(id);
                        fringe.add(new PathNode(item.distToNode + g.distance(item.node.id, id),
                                item.distToNode
                                + g.distance(item.node.id, id)
                                        + g.distance(id, destination.id), temp, item));
                    }
                }
            }
        }
        return solution;
    }
}

