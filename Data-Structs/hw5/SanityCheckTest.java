import edu.princeton.cs.algs4.Picture;
import java.awt.Color;
import java.util.Arrays;
import static org.junit.Assert.*;
import org.junit.Test;

public class SanityCheckTest {
    @Test
    public void sanityEnergyTest() {
        /* Creates the sample array specified in the spec and checks for matching energies */
        Picture p = new Picture(3, 4);
        int[][][] exampleArray = {{{255, 101, 51}, {255, 101, 153}, {255, 101, 255}},
                                  {{255, 153, 51}, {255, 153, 153}, {255, 153, 255}},
                                  {{255, 203, 51}, {255, 204, 153}, {255, 205, 255}},
                                  {{255, 255, 51}, {255, 255, 153}, {255, 255, 255}}};
        double[][] exampleEnergy = {{20808.0, 52020.0, 20808.0},
                                    {20808.0, 52225.0, 21220.0},
                                    {20809.0, 52024.0, 20809.0},
                                    {20808.0, 52225.0, 21220.0}};
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                int[] colorVals = exampleArray[j][i];
                p.set(i, j, new Color(colorVals[0], colorVals[1], colorVals[2]));
            }
        }

        SeamCarver sc = new SeamCarver(p);
        double[][] energy = new double[3][4];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                energy[i][j] = sc.energy(i, j);
                assertEquals(energy[i][j], exampleEnergy[j][i], 1e-3);
            }
        }
    }

    @Test
    public void sanityVerticalSeamTest() {
        Picture p = new Picture("images/sample.png");
        SeamCarver sc = new SeamCarver(p);

        int[] seam = sc.findVerticalSeam();

    }

    @Test
    public void sanityHorizontalSeamTest() {
        Picture p = new Picture("images/sample.png");
        SeamCarver sc = new SeamCarver(p);


        /*for (int i = sc.eCumulative.length - 1; i >= 0; i--) {
            for (double elem : sc.eCumulative[i]) {
                System.out.print(elem + "   ");
            }
            System.out.println();
        }

        System.out.println();

        for (int i = sc.eGrid.length - 1; i >= 0; i--) {
            for (double elem : sc.eGrid[i]) {
                System.out.print(elem + "   ");
            }
            System.out.println();
        }

        sc.transpose();

        System.out.println("\n\n\n");

        for (int i = sc.eCumulative.length - 1; i >= 0; i--) {
            for (double elem : sc.eCumulative[i]) {
                System.out.print(elem + "   ");
            }
            System.out.println();
        }

        System.out.println();

        for (int i = sc.eGrid.length - 1; i >= 0; i--) {
            for (double elem : sc.eGrid[i]) {
                System.out.print(elem + "   ");
            }
            System.out.println();
        }*/

        int[] seam = sc.findHorizontalSeam();
        int[] expected = {2, 2, 1, 2, 1, 2};
        assertArrayEquals(expected, seam);
    }

    public static void main(String[] args) {
        jh61b.junit.TestRunner.runTests(SanityCheckTest.class);
    }
}
