import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.TreeMap;

/**
 * Created by Roham on 4/17/2017.
 */


public class QTNode {
    final double pixel = MapServer.TILE_SIZE;
    String name;
    QTNode tile1;
    QTNode tile2;
    QTNode tile3;
    QTNode tile4;

    double upperLeftX;
    double lowerRightX;
    double upperLeftY;
    double lowerRightY;
    double distPerPixel;
    int depth;

    public QTNode(String name, double upperLeftX, double lowerRightX,
                  double upperLeftY, double lowerRightY, int depth) {

        this.name = name;
        this.upperLeftX = upperLeftX;
        this.upperLeftY = upperLeftY;
        this.lowerRightX = lowerRightX;
        this.lowerRightY = lowerRightY;
        this.distPerPixel = (lowerRightX - upperLeftX) / this.pixel;
        this.depth = depth;

        if (name.equals("root")) {
            this.tile1 = new QTNode("1", upperLeftX, (upperLeftX + lowerRightX) / 2,
                    upperLeftY, (upperLeftY + lowerRightY) / 2, 1);

            this.tile2 = new QTNode("2", (upperLeftX + lowerRightX) / 2, lowerRightX,
                    upperLeftY, (upperLeftY + lowerRightY) / 2, 1);

            this.tile3 = new QTNode("3", upperLeftX, (upperLeftX + lowerRightX) / 2,
                    (upperLeftY + lowerRightY) / 2, lowerRightY, 1);

            this.tile4 = new QTNode("4", (upperLeftX + lowerRightX) / 2, lowerRightX,
                    (upperLeftY + lowerRightY) / 2, lowerRightY, 1);

        } else if (depth < 7) {
            this.tile1 = new QTNode(name + 1, upperLeftX, (upperLeftX + lowerRightX) / 2,
                    upperLeftY, (upperLeftY + lowerRightY) / 2, depth + 1);

            this.tile2 = new QTNode(name + 2, (upperLeftX + lowerRightX) / 2, lowerRightX,
                    upperLeftY, (upperLeftY + lowerRightY) / 2, depth + 1);

            this.tile3 = new QTNode(name + 3, upperLeftX, (upperLeftX + lowerRightX) / 2,
                    (upperLeftY + lowerRightY) / 2, lowerRightY, depth + 1);

            this.tile4 = new QTNode(name + 4, (upperLeftX + lowerRightX) / 2, lowerRightX,
                    (upperLeftY + lowerRightY) / 2, lowerRightY, depth + 1);
        } else {
            this.tile1 = null;
            this.tile2 = null;
            this.tile3 = null;
            this.tile4 = null;
        }

    }

    public boolean intersects(Rasterer.Query query) {

        return !(query.lowerRightX < upperLeftX || query.upperLeftX > lowerRightX
                || query.lowerRightY > upperLeftY || query.upperLeftY < lowerRightY);
    }

    public ArrayList<QTNode> tilesFinder(Rasterer.Query query) {

        ArrayList<QTNode> tiles = new ArrayList<>();
        Deque<QTNode> finder = new ArrayDeque<>();
        finder.add(this);

        while (!finder.isEmpty()) {
            QTNode node = finder.remove();
            if (node.intersects(query)) {

                if (node.distPerPixel <= query.distPerPixel || node.depth > 6) {
                    tiles.add(node);
                } else {
                    if (node.tile1.intersects(query)) {
                        finder.add(node.tile1);
                    }
                    if (node.tile2.intersects(query)) {
                        finder.add(node.tile2);
                    }
                    if (node.tile3.intersects(query)) {
                        finder.add(node.tile3);
                    }
                    if (node.tile4.intersects(query)) {
                        finder.add(node.tile4);
                    }
                }
            }
        }

        return tiles;
    }

    @Override
    public String toString() {
        return name;
    }

    public void print(QTNode node) {
        if (node != null) {

            if (node.name.equals("root")) {
                System.out.print(node.name + " ");
            }

            if (node.tile1 != null) {
                System.out.print(node.tile1.name + " ");
                print(node.tile1);
                System.out.print(node.tile2.name + " ");
                print(node.tile2);
                System.out.print(node.tile3.name + " ");
                print(node.tile3);
                System.out.print(node.tile4.name + " ");
                print(node.tile4);


            }
        }
    }


    public TreeMap<Double, ArrayList<QTNode>> sort(ArrayList<QTNode> tiles) {

        TreeMap<Double, ArrayList<QTNode>> grouper = new TreeMap<>();
        for (QTNode node : tiles) {
            double key = node.upperLeftY * (-1);
            if (grouper.containsKey(key)) {
                grouper.get(key).add(node);
            } else {
                grouper.put(key, new ArrayList<>(Arrays.asList(node)));
            }
        }

        return grouper;
    }
}
