import java.util.ArrayList;
import java.util.List;

public class GraphNode {
    String name;
    long id;
    double latitude;
    double longitude;
    List<Long> adj = new ArrayList<>();


    GraphNode(String name, long id, double latitude, double longitude) {
        this.name = name;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

