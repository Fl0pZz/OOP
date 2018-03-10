import java.io.*;
import java.awt.image.BufferedImage;
import java.awt.Color;

public class Img2ASCII {

    private BufferedImage img;
    private String pathToResult;
    private Helper helper = new Helper();


    Img2ASCII(BufferedImage img, String pathToManipulatedASCIIImage) {
        this.img = img;
        this.pathToResult = pathToManipulatedASCIIImage;
    }


    // Преобразует изображение в ascii формат
    public void convertToAscii() throws IOException {
        PrintWriter printWriter = helper.makePrintWriter(pathToResult);
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                Color pixelColor = new Color(img.getRGB(j, i));
                double pixelValue =
                        pixelColor.getRed() * 0.3 + pixelColor.getGreen() * 0.11 + pixelColor.getBlue() * 0.59;
                String s = valueToChar(pixelValue);
                helper.printToFile(s, printWriter);
            }
            helper.printToFile("", printWriter);
        }
        printWriter.close();
    }


    // Горизонтальное отзеркаливание ascii изображения
    public void horizontalMirrorImage(String pathToResultFile, String pathToASCIIImg) throws IOException {
        PrintWriter printWriter = helper.makePrintWriter(pathToResultFile);
        try (BufferedReader br = new BufferedReader(new FileReader(pathToASCIIImg))) {

            String currentLine;

            while ((currentLine = br.readLine()) != null) {
                String reversedCurrentLine = new StringBuilder(currentLine).reverse().toString();
                helper.printToFile(reversedCurrentLine, printWriter);
                helper.printToFile("", printWriter);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        printWriter.close();
    }


    // Вертикальное отзеркаливание ascii изображения
    public void verticalMirrorImage(String pathToResultFile, String pathToASCIIImg, String pathToTMP) throws Exception {
        horizontalMirrorImage(pathToTMP, pathToASCIIImg);
        rotateImage(180, pathToResultFile, pathToTMP);
        File file = new File(pathToTMP);
        file.delete();
    }


    // Поворот ascii изображения на degree градусов
    public void rotateImage(Integer degree, String pathToResultFile, String pathToImg) throws Exception {
        PrintWriter printWriter = helper.makePrintWriter(pathToResultFile);

        try (BufferedReader br = new BufferedReader(new FileReader(pathToImg))) {

            String currentLine;

            if (degree == 180) {
                StringBuilder allLines = new StringBuilder();
                while ((currentLine = br.readLine()) != null) {
                    allLines.append(currentLine);
                    allLines.append("\n");
                }
                helper.printToFile(allLines.reverse().toString(), printWriter);
                helper.printToFile("", printWriter);
            } else {
                int width = img.getWidth();
                int height = img.getHeight();
                char[][] allSymbols = new char [height][width];
                int counter = 0;
                while ((currentLine = br.readLine()) != null) {
                    allSymbols[counter] = currentLine.toCharArray();
                    counter++;
                }
                char[][] newAllSymbols = new char[width][height];
                if (degree == 270) {
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            newAllSymbols[width - x - 1][y] = allSymbols[y][x];
                        }
                    }
                } else if (degree == 90) {
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            newAllSymbols[x][height - y - 1] = allSymbols[y][x];
                        }
                    }
                } else {
                    throw new Exception(Constants.INCORRECT_ROTATION);
                }
                for (char[] newAllSymbol : newAllSymbols) {
                    String currLine = String.valueOf(newAllSymbol);
                    helper.printToFile(currLine, printWriter);
                    helper.printToFile("", printWriter);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        printWriter.close();
    }

    private String valueToChar(double value) {
        String str;

        if (value >= 230.0) {
            str = " ";
        } else if (value >= 200.0) {
            str = ".";
        } else if (value >= 180.0) {
            str = "*";
        } else if (value >= 160.0) {
            str = ":";
        } else if (value >= 130.0) {
            str = "o";
        } else if (value >= 100.0) {
            str = "&";
        } else if (value >= 70.0) {
            str = "8";
        } else if (value >= 50.0) {
            str = "#";
        } else {
            str = "@";
        }
        return str;
    }
}