package process;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageToAsciiBuilder {

    private static final String ASCII_PALETTE = "MWNXK0Okxdolc:;,'...   ";
    private static int PALETTE_SIZE = ASCII_PALETTE.length();

    public static String convertToAscii(BufferedImage img) throws IOException {
        StringBuilder asciiImageBuilder = new StringBuilder();
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                Color pixColor = new Color(img.getRGB(j, i));
                double pixValue = colorToValue(pixColor);
                asciiImageBuilder.append(getPaletteChar(pixValue));
            }
            asciiImageBuilder.append('\n');
        }
        return asciiImageBuilder.toString().trim();
    }

    private static char getPaletteChar(double g) {
        return ASCII_PALETTE.charAt(
                (int)((g / 255) * (PALETTE_SIZE - 1))
        );
    }

    private static double colorToValue(Color pixColor) {
        return pixColor.getRed() * 0.30 + pixColor.getBlue() * 0.59 + pixColor.getGreen() * 0.11;
    }
}