import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    QTNode root = new QTNode("root", MapServer.ROOT_ULLON, MapServer.ROOT_LRLON,
            MapServer.ROOT_ULLAT, MapServer.ROOT_LRLAT, 0);
    // Recommended: QuadTree instance variable. You'll need to make
    //              your own QuadTree since there is no built-in quadtree in Java.

    /**
     * imgRoot is the name of the directory containing the images.
     * You may not actually need this for your class.
     */
    public Rasterer(String imgRoot) {
        // YOUR CODE HERE
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     * The grid of images must obey the following properties, where image in the
     * grid is referred to as a "tile".
     * <ul>
     * <li>Has dimensions of at least w by h, where w and h are the user viewport width
     * and height.</li>
     * <li>The tiles collected must cover the most longitudinal distance per pixel
     * (LonDPP) possible, while still covering less than or equal to the amount of
     * longitudinal distance per pixel in the query box for the user viewport size. </li>
     * <li>Contains all tiles that intersect the query bounding box that fulfill the
     * above condition.</li>
     * <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     * </ul>
     * The grid of images must obey the following properties, where image in the
     * grid is referred to as a "tile".
     * <ul>
     * <li>The tiles collected must cover the most longitudinal distance per pixel
     * (LonDPP) possible, while still covering less than or equal to the amount of
     * longitudinal distance per pixel in the query box for the user viewport size. </li>
     * <li>Contains all tiles that intersect the query bounding box that fulfill the
     * above condition.</li>
     * <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     * </ul>
     * </p>
     *
     * @param //params// Map of the HTTP GET request's query parameters - the query box and
     *                   the user viewport width and height.
     * @return A map of results for the front end as specified:
     * "render_grid"   -> String[][], the files to display
     * "raster_ul_lon" -> Number, the bounding upper left longitude of the rastered image <br>
     * "raster_ul_lat" -> Number, the bounding upper left latitude of the rastered image <br>
     * "raster_lr_lon" -> Number, the bounding lower right longitude of the rastered image <br>
     * "raster_lr_lat" -> Number, the bounding lower right latitude of the rastered image <br>
     * "depth"         -> Number, the 1-indexed quadtree depth of the nodes of the rastered image.
     * Can also be interpreted as the length of the numbers in the image
     * string. <br>
     * "query_success" -> Boolean, whether the query was able to successfully complete. Don't
     * forget to set this to true! <br>
     * @see #//REQUIRED_RASTER_REQUEST_PARAMS//
     */

    public class Query {

        double upperLeftX;
        double lowerRightX;
        double upperLeftY;
        double lowerRightY;
        double distPerPixel;
        double width;
        double height;

        public Query(double upperLeftX, double lowerRightX,
                     double upperLeftY, double lowerRightY, double width, double height) {

            this.upperLeftX = upperLeftX;
            this.upperLeftY = upperLeftY;
            this.lowerRightX = lowerRightX;
            this.lowerRightY = lowerRightY;
            this.width = width;
            this.height = height;
            this.distPerPixel = (lowerRightX - upperLeftX) / width;
        }
    }

    public static void print2D(String[][] list) {
        for (int i = 0; i < list.length; i++) {
            for (int j = 0; j < list[0].length; j++) {
                System.out.print(list[i][j] + " ");
            }
            System.out.println();
        }
    }

    private String[][] finalListFinder(ArrayList<QTNode> unordered,
                                       TreeMap<Double, ArrayList<QTNode>> sorted) {

        int index = 0;
        int numRows = sorted.size();
        int numCols = unordered.size() / sorted.size();
        String[][] ordered = new String[numRows][numCols];

        for (Double key : sorted.keySet()) {
            ArrayList<QTNode> row = sorted.get(key);

            for (int i = 0; i < row.size(); i++) {
                ordered[index][i] = ("img/" + row.get(i).name + ".png");
            }
            index += 1;
        }

        return ordered;
    }


    public Map<String, Object> getMapRaster(Map<String, Double> params) {

        String[][] orderedTilesNames;           //Got some help with the idea of
        ArrayList<QTNode> unorderedTiles;       //implementation from Divi Kumar
        TreeMap<Double, ArrayList<QTNode>> sortedTiles;
        Map<String, Object> results = new HashMap<>();

        Query query = new Query(params.get("ullon"), params.get("lrlon"),
                params.get("ullat"), params.get("lrlat"), params.get("w"), params.get("h"));

        unorderedTiles = root.tilesFinder(query);    //finding all the needed tiles
        sortedTiles = root.sort(unorderedTiles);           //sorted version
        orderedTilesNames = finalListFinder(unorderedTiles, sortedTiles);   //setting the 2D String

        int numRows = sortedTiles.size();
        int numCols = unorderedTiles.size() / sortedTiles.size();

        QTNode first = unorderedTiles.get(0);
        QTNode last = unorderedTiles.get(unorderedTiles.size() - 1);

        results.put("render_grid", orderedTilesNames);
        results.put("raster_ul_lon", first.upperLeftX);
        results.put("raster_ul_lat", first.upperLeftY);
        results.put("raster_lr_lon", last.lowerRightX);
        results.put("raster_lr_lat", last.lowerRightY);
        results.put("depth", first.depth);
        results.put("query_success", true);

        return results;
    }

}
