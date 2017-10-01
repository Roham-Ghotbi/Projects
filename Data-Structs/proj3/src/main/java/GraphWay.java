import java.util.ArrayList;

/**
 * Created by Roham on 4/19/2017.
 */
public class GraphWay {
    String name;
    Long id;
    boolean valid;
    String maxSpeed;
    Long lastNode;
    ArrayList<Long> pathNodes;

    GraphWay(String name, Long id) {
        this.name = name;
        this.id = id;
        valid = false;
        pathNodes = new ArrayList<>();
    }
}
