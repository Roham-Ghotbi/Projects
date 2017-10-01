import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */

    HashMap<Long, GraphNode> nodes = new HashMap<>();

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputFile, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        HashMap<Long, GraphNode> nodesTemp = new HashMap<>();
        for (Long key :nodes.keySet()) {
            if (nodes.get(key).adj.size() != 0) {
                nodesTemp.put(nodes.get(key).id, nodes.get(key));
            }
        }

        nodes = nodesTemp;
    }

    /** Returns an iterable of all vertex IDs in the graph. */
    Iterable<Long> vertices() {
        ArrayList<Long> list = new ArrayList<>();

        for (Long key : nodes.keySet()) {
            list.add(nodes.get(key).id);
        }

        return list;
    }

    /** Returns ids of all vertices adjacent to v. */
    Iterable<Long> adjacent(long v) {

        return nodes.get(v).adj;
    }

    /** Returns the Euclidean distance between vertices v and w, where Euclidean distance
     *  is defined as sqrt( (lonV - lonV)^2 + (latV - latV)^2 ). */
    double distance(long v, long w) {
        GraphNode node1 = nodes.get(v);
        GraphNode node2 = nodes.get(w);
        return coorDistance(node1.longitude, node1.latitude, node2.longitude, node2.latitude);
    }

    /** Returns the vertex id closest to the given longitude and latitude. */
    long closest(double lon, double lat) {
        double minDist = Double.MAX_VALUE;
        GraphNode target = new GraphNode("null", 0, 0, 0);
        for (Long key : nodes.keySet()) {
            GraphNode node = nodes.get(key);
            double distance = coorDistance(lon, lat, node.longitude, node.latitude);
            if (minDist > distance) {
                minDist = distance;
                target = node;
            }
        }
        return target.id;
    }

    /** Longitude of vertex v. */
    double lon(long v) {
        return nodes.get(v).longitude;
    }

    /** Latitude of vertex v. */
    double lat(long v) {
        return nodes.get(v).latitude;
    }

    double coorDistance(double lon1, double lat1, double lon2, double lat2) {
        return Math.sqrt(Math.pow((lat1 - lat2), 2)
                + Math.pow((lon1 - lon2), 2));
    }

    void addNode(GraphNode node) {
        nodes.put(node.id, node);
    }

    void addEdge(GraphNode node1, GraphNode node2) {
        nodes.get(node1.id).adj.add(node2.id);
        nodes.get(node2.id).adj.add(node1.id);
    }
}
