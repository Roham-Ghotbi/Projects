/**
 * Created by Roham on 4/19/2017.
 */
public class PathNode implements Comparable<PathNode> {
    GraphNode node;
    double distToNode;
    double priority;
    PathNode prevNode;

    PathNode( double distToNode,double priority, GraphNode node, PathNode prevNode) {
        this.node = node;
        this.distToNode = distToNode;
        this.priority = priority;
        this.prevNode = prevNode;
    }

    @Override
    public int compareTo(PathNode o) {
        return Double.compare(priority, o.priority);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PathNode pathNode = (PathNode) o;
        return pathNode.node.id == node.id;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = node != null ? node.hashCode() : 0;
        temp = Double.doubleToLongBits(distToNode);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(priority);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (prevNode != null ? prevNode.hashCode() : 0);
        return result;
    }
}
