import process.ImageProcessor;
import process.ImageToAsciiBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File(args.length > 0 ? args[0] : "rabbit_divided.jpg"));
        String cmd;
        Scanner inputScanner = new Scanner(System.in);
        while ((cmd = inputScanner.nextLine()) != null) {
            String[] parameters = cmd.split(" ");
            try {
                switch (parameters[0]) {
                    case "rotate":
                        img = ImageProcessor.rotate(img, Integer.valueOf(parameters[1]));
                        System.out.println("OK");
                        break;
                    case "resize":
                        img = ImageProcessor.resize(img, Integer.valueOf(parameters[1]), Integer.valueOf(parameters[2]));
                        System.out.println("OK");
                        break;
                    case "mirrorX":
                        img = ImageProcessor.mirrorX(img);
                        System.out.println("OK");
                        break;
                    case "mirrorY":
                        img = ImageProcessor.mirrorY(img);
                        System.out.println("OK");
                        break;
                    case "toascii":
                        String asciiImage = ImageToAsciiBuilder.convertToAscii(img);
                        if (parameters.length == 2) {
                            OutputStream outputStream = new FileOutputStream(parameters[1]);
                            outputStream.write(asciiImage.getBytes());
                        } else {
                            System.out.println(asciiImage);
                        }
                        System.out.println("OK");
                        break;
                    case "save":
                        ImageIO.write(img, "png", new File(parameters[1]));
                        System.out.println("OK");
                        break;
                    default:
                        System.out.println("Unknown command");
                }
            } catch (Exception e) {
                System.out.println("Bad command");
            }
        }
    }
}
