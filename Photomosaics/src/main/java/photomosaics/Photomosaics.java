package photomosaics;

import org.imgscalr.Scalr;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Rectangle;

/*
TODO:
    - fix the not-quite-working imgs not divisible in <45; 55> range
 */
public class Photomosaics {

    public static String PATH_TO_INPUT = String.format("%s%s%s%s", "pictures",
            File.separator, "input", File.separator);
    public static String PATH_TO_OUTPUT = String.format("%s%s%s%s", "pictures",
            File.separator, "output", File.separator);
    public static String PATH_TO_DATASET = String.format("%s%s%s%s", "pictures",
            File.separator, "dataset", File.separator);

    public static void main(String[] args) throws IOException {
        BufferedImage picSource = ImageIO.read(new File(PATH_TO_INPUT + "nature.jpg"));
        int groupDim = determinePixelGroupSideLength(picSource);
        System.out.println(groupDim);
        //int groupDim = 25;
        double[][][] avgs = getPixelGroupsColor(picSource, groupDim);
        BufferedImage pixelated = pixelate(picSource, avgs, groupDim);
        ImageIO.write(pixelated, "jpg", new File(PATH_TO_OUTPUT + "pixelated.jpg"));
    }

    private static int determinePixelGroupSideLength(BufferedImage img) {
        int picHeight = img.getHeight();
        int picWidth = img.getWidth();

        int minTotalR = 100;
        int equivalentLength = 44;
        for (int length = 45; length <= 55; length++) {
            int heightR = picHeight % length;
            int widthR = picWidth % length;
            int totalR = heightR + widthR;

            if (minTotalR > totalR) {
                minTotalR = totalR;
                equivalentLength = length;
            }
        }
        return equivalentLength;
    }

    public static double[][][] getPixelGroupsColor(BufferedImage img, int groupDim) {
        int rows = img.getHeight() / groupDim;
        int columns = img.getWidth() / groupDim;
        double[][][] groups = new double[rows][columns][3];

        int row = 0;
        while (row < rows) {
            for (int column = 0; column < columns; column++) {

                double avgRed = 0;
                double avgGreen = 0;
                double avgBlue = 0;

                for (int x = column * groupDim; x < column * groupDim + groupDim; x++) {

                    for (int y = row * groupDim; y < row * groupDim + groupDim; y++) {

                        int pixel = img.getRGB(x, y);
                        Color c = new Color(pixel);
                        int red = c.getRed();
                        int green = c.getGreen();
                        int blue = c.getBlue();
                        avgRed += red;
                        avgGreen += green;
                        avgBlue += blue;

                    }
                }

                avgRed = avgRed / (double) (groupDim * groupDim);
                avgGreen = avgGreen / (double) (groupDim * groupDim);
                avgBlue = avgBlue / (double) (groupDim * groupDim);
                groups[row][column][0] = avgRed;
                groups[row][column][1] = avgGreen;
                groups[row][column][2] = avgBlue;
            }
            row++;
        }
        return groups;
    }

    public static BufferedImage pixelate(BufferedImage img, double[][][] pxGroup, int groupDim) {
        BufferedImage copy = copyImage(img);
        Graphics2D graph = copy.createGraphics();
        for (int row = 0; row < pxGroup.length; row++) {

            for (int column = 0; column < pxGroup[0].length; column++) {
                int red = (int) pxGroup[row][column][0];
                int green = (int) pxGroup[row][column][1];
                int blue = (int) pxGroup[row][column][2];
                graph.setColor(new Color(red, green, blue));
                graph.fill(new Rectangle(column * groupDim, row * groupDim, groupDim, groupDim));

            }
        }
        graph.dispose();
        return copy;
    }

    public static BufferedImage copyImage(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    public static void cropAllDataset() throws IOException {
        File dir = new File(PATH_TO_DATASET);
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String name = pathname.getName().toLowerCase();
                return pathname.isFile();
            }
        });
        for (File file : files) {
            BufferedImage img = ImageIO.read(file);
            var cropped = cropToSquare(img);
            ImageIO.write(cropped, "jpg", new File(PATH_TO_DATASET + File.separator
                    + "cropped" + File.separator + file.getName()));
        }
    }

    public static BufferedImage cropToSquare(BufferedImage img) {
        int height = img.getHeight();
        int width = img.getWidth();
        int smaller = height > width ? width : height;
        var newImg = Scalr.resize(img, Scalr.Mode.FIT_EXACT, smaller, smaller);
        return newImg;
    }
}
