package photomosaics;

import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class UserInterface {

    private Scanner scanner;

    public UserInterface() {
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("===Photomosaics===");
        System.out.println("");
        while (true) {
            System.out.println("1. Make photomosaic");
            System.out.println("2. Maybe pixelate?");
            System.out.println("3. Crop (and load) dataset");
            System.out.println("0. Exit");
            System.out.print("> ");
            String choice = scanner.nextLine();
            if ("0".equals(choice)) {
                break;
            }
            if ("1".equals(choice)) {
                makePhotomosaic();
                continue;
            }
            if ("2".equals(choice)) {
                pixelate();
                continue;
            }
            if ("3".equals(choice)) {
                cropSources();
                continue;
            }
        }
    }

    private void printImages(File[] dir) {
        System.out.println("===");

        for (File file : dir) {
            System.out.println(file.getName());
        }

        System.out.println("===");
    }

    private void pixelate() {
        printImages(DataHandler.inputs());

        File imgFile = userSelectingFile();
        var img = DataHandler.readImg(imgFile);
        int groupDim = getPixelSize();

        System.out.println("Pixelating...");
        var pixelated = ImageHandler.pixelate(img, groupDim);
        try {
            String format = DataHandler.getDotlessExtension(imgFile);
            String noFormatName = DataHandler.getNameMinusExtension(imgFile);
            DataHandler.outputImg(pixelated, String.format("%s_pixelate.%s",
                    noFormatName, format), format);
            System.out.println("Success!"
                    + " See the output in the \"output\" folder.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void cropSources() {
        System.out.println("Processing...");
        try {
            ImageHandler.cropDataset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Success!");
    }

    private File userSelectingFile() {
        while (true) {
            System.out.print("Choose image: ");

            File imgFile = new File(DataHandler.PATH_TO_INPUT
                    + scanner.nextLine());

            if (!imgFile.exists()) {
                System.out.println("Couldn't find image. (Remember about file extension)");
                continue;
            }
            return imgFile;
        }
    }

    private int getPixelSize() {
        System.out.println("Give size of a pixel. (Leave empty for automatic estimate)");
        System.out.print("> ");
        String input = scanner.nextLine();
        int groupDim = -1;
        if (!input.isEmpty()) {
            groupDim = Integer.valueOf(input);
        }
        return groupDim;
    }

    private void makePhotomosaic() {
        printImages(DataHandler.inputs());

        File imgFile = userSelectingFile();
        var img = DataHandler.readImg(imgFile);
        int groupDim = getPixelSize();

        System.out.println("Creating photomosaic...");
        long start = System.currentTimeMillis();
        var mosaic = groupDim == -1 ? ImageHandler.photomosaic(img)
                : ImageHandler.photomosaic(img, groupDim);
        try {
            String format = DataHandler.getDotlessExtension(imgFile);
            String noFormatName = DataHandler.getNameMinusExtension(imgFile);
            DataHandler.outputImg(mosaic, String.format("%s_mosaic.%s",
                    noFormatName, format), format);
            System.out.println("Success! (" + (System.currentTimeMillis() - start)
                    + " ms) See the output in the \"output\" folder.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
