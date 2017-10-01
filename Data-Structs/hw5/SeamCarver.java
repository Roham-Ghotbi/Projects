
import edu.princeton.cs.algs4.Picture;

/**
 * Created by Roham on 4/23/2017.
 */
public class SeamCarver {
    private Picture picture;
    private double[][] eCumulative;
    private double[][] eGrid;

    public SeamCarver(Picture picture) {
        this.picture = new Picture(picture);
        energyCalculator();

    }

    private void energyCalculator() {
        this.eCumulative = new double[height()][width()];
        this.eGrid = new double[height()][width()];

        for (int i = 0; i < height(); i++) {
            int index = height() - 1 - i;
            for (int j = 0; j < width(); j++) {
                double value = energyCalc(j, i);
                eGrid[index][j] = value;

                if (i == 0) {
                    eCumulative[index][j] = value;
                } else {
                    if (width() == 1) {
                        eCumulative[index][j] = value + eCumulative[index + 1][j];
                    } else {

                        if (j == 0) {
                            eCumulative[index][j] = value
                                + Math.min(eCumulative[index + 1][j],
                                    eCumulative[index + 1][j + 1]);
                        } else if (j == width() - 1) {
                            eCumulative[index][j] = value
                                + Math.min(eCumulative[index + 1][j - 1],
                                    eCumulative[index + 1][j]);
                        } else {
                            eCumulative[index][j] = value
                                    + Math.min(eCumulative[index + 1][j - 1],
                               Math.min(eCumulative[index + 1][j], eCumulative[index + 1][j + 1]));
                        }
                    }
                }
            }
        }
    }

    // current picture
    public Picture picture() {
        return new Picture(picture);
    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    // energy of pixel at column x and row y
    private double gradientCalc(int x1, int y1, int x2, int y2) {

        double b = picture.get(x1, y1).getBlue() - picture.get(x2, y2).getBlue();
        double g = picture.get(x1, y1).getGreen() - picture.get(x2, y2).getGreen();
        double r = picture.get(x1, y1).getRed() - picture.get(x2, y2).getRed();

        if (b < 0) {
            b = -b;
        }
        if (g < 0) {
            g = -g;
        }
        if (r < 0) {
            r = -r;
        }

        return r * r + g * g + b * b;
    }

    // energy of pixel at column x and row y
    private  double energyCalc(int x, int y) {
        double xEnergy;
        double yEnergy;

        if (eCumulative[0].length == 1) {
            xEnergy = gradientCalc(0, y, 0, y);
        } else {

            if (x == 0) {
                xEnergy = gradientCalc(eCumulative[0].length - 1, y, x + 1, y);
            } else if (x == eCumulative[0].length - 1) {
                xEnergy = gradientCalc(x - 1, y, 0, y);
            } else {
                xEnergy = gradientCalc(x - 1, y, x + 1, y);
            }
        }

        if (eCumulative.length == 1) {
            yEnergy = gradientCalc(x, 0, x, 0);
        } else {

            if (y == 0) {
                yEnergy = gradientCalc(x, eCumulative.length - 1, x, y + 1);

            } else if (y == eCumulative.length - 1) {
                yEnergy = gradientCalc(x, y - 1, x, 0);
            } else {
                yEnergy = gradientCalc(x, y - 1, x, y + 1);
            }
        }

        return xEnergy + yEnergy;
    }

    public double energy(int x, int y) {
        return eGrid[height() - 1 - y][x];
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {

        transposePic();
        energyCalculator();

        int[] result = findVerticalSeam();

        transposePic();
        energyCalculator();

        return result;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {

        int length = eCumulative.length - 1;
        int[] result = new int[eCumulative.length];
        int k = 0;
        int minX = Integer.MAX_VALUE;
        double rowMin = Double.MAX_VALUE;

        if (width() > 1) {
            for (int i = 0; i < eCumulative[0].length; i++) {
                if (eCumulative[0][i] < rowMin) {
                    rowMin = eCumulative[0][i];
                    minX = i;
                }
            }
            result[length] = minX;

            while (k < eCumulative.length - 1) {
                if (minX == 0) {
                    if (Double.compare(eCumulative[k][minX],
                            eCumulative[k + 1][minX + 1] + eGrid[k][minX]) == 0) {
                        minX = minX + 1;
                    }
                } else if (minX == width() - 1) {
                    if (Double.compare(eCumulative[k][minX],
                            eCumulative[k + 1][minX - 1] + eGrid[k][minX]) == 0) {
                        minX = minX - 1;
                    }
                } else {
                    if (Double.compare(eCumulative[k][minX],
                            eCumulative[k + 1][minX + 1] + eGrid[k][minX]) == 0) {
                        minX = minX + 1;
                    } else if (Double.compare(eCumulative[k][minX],
                            eCumulative[k + 1][minX - 1] + eGrid[k][minX]) == 0) {
                        minX = minX - 1;
                    }
                }
                result[eCumulative.length - 2 - k] = minX;
                k += 1;
            }
        }

        return result;
    }

    private void transposePic() {
        Picture newPicture = new Picture(height(), width());

        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                newPicture.set(j, i, picture.get(i, j));
            }
        }

        picture = newPicture;
    }

    // remove horizontal seam from picture
    public void removeHorizontalSeam(int[] seam) {

        if (seam.length != picture.width()) {
            throw new java.lang.IllegalArgumentException();
        } else {
            picture = SeamRemover.removeHorizontalSeam(picture, seam);
            energyCalculator();
        }
    }

    // remove vertical seam from picture
    public void removeVerticalSeam(int[] seam) {

        if (seam.length != picture.height()) {
            throw new java.lang.IllegalArgumentException();
        } else {
            picture = SeamRemover.removeVerticalSeam(picture, seam);
            energyCalculator();
        }
    }
}
