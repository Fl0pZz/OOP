import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class Img2ASCII {


    // Параметры командной строки должны представляться в виде -n imageName -r rotationDegree -d decreasingSize
    // если присутствует параметр -m, то будет выполнено отзеркаливание вертикальное и горизонтальное.
    @Parameter(names={"--rotate", "-r"})
    private Integer degree = null;
    @Parameter(names={"--mirror", "-m"})
    private boolean mirroring;
    @Parameter(names={"--decrease", "-d"})
    private Integer decreaseTimes = null;
    @Parameter(names={"--name", "-n"})
    private String imgName = null;


    private static final String PATH_TO_RESULT_DIR = "D:\\OOP\\HW1\\src\\main\\java\\";
    private static final String PATH_TO_RESULT = "D:\\OOP\\HW1\\src\\main\\java\\result.txt";
    private static final String PATH_TO_HORIZONTAL_MIRROR = "D:\\OOP\\HW1\\src\\main\\java\\result_horizontal_mirror.txt";
    private static final String PATH_TO_VERTICAL_MIRROR = "D:\\OOP\\HW1\\src\\main\\java\\result_vertical_mirror.txt";
    private static final String IO_WRITER_EXCEPTION = "Sorry, writer exception";
    private static final String IO_READER_EXCEPTION = "Sorry, reader exception, probably incorrect file name";
    private static final String INCORRECT_ROTATION = "Sorry, incorrect value of degree rotation";

    private BufferedImage img = null;
    private PrintWriter printWriter;


    private Img2ASCII(String imgName) throws IOException {
        try {
            img = ImageIO.read(new File(imgName));
        } catch (IOException e) {
            throw new IOException(IO_READER_EXCEPTION);
        }
    }


    private void makePrintWriter(String pathToWrite) throws IOException {
        FileWriter fileWriter;

        try {
            fileWriter = new FileWriter(pathToWrite);
            printWriter = new PrintWriter(fileWriter, true);
        } catch (IOException ex) {
            throw new IOException(IO_WRITER_EXCEPTION);
        }
    }


    // Преобразует изображение в ascii формат
    private void convertToAscii() throws IOException {
        makePrintWriter(PATH_TO_RESULT);

        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                Color pixelColor = new Color(img.getRGB(j, i));
                double pixelValue =
                        pixelColor.getRed() * 0.3 + pixelColor.getGreen() * 0.11 + pixelColor.getBlue() * 0.59;
                String s = valueToChar(pixelValue);
                printToFile(String.valueOf(s));
            }
            printToFile("");
        }
        printWriter.close();
    }


    // Сжимает ascii изображение в times раз
    private void decreaseSize(Integer times, String resultPath) throws IOException {
        makePrintWriter(resultPath);

        for (int i = 0; i < img.getHeight() / times; i++) {
            for (int j = 0; j < img.getWidth() / times; j++) {
                BufferedImage subImage;
                try {
                    subImage = img.getSubimage(j * times, i * times, times, times);
                } catch (Exception ex) {
                    continue;
                }
                int red = 0;
                int green = 0;
                int blue = 0;
                for (int q = 0; q < subImage.getHeight(); q++) {
                    for (int p = 0; p < subImage.getWidth(); p++) {
                        Color pixelColor = new Color(subImage.getRGB(p, q));
                        red += pixelColor.getRed();
                        green += pixelColor.getGreen();
                        blue += pixelColor.getBlue();
                    }
                }
                red = red / times;
                green = green / times;
                blue = blue / times;
                double pixelValue = red * 0.3 + green * 0.11 + blue * 0.59;
                String s = valueToChar(pixelValue);
                printToFile(String.valueOf(s));
            }
            printToFile("");
        }
        printWriter.close();
    }


    // Горизонтальное отзеркаливание ascii изображения
    private void horizontalMirrorImage() throws IOException {
        makePrintWriter(PATH_TO_HORIZONTAL_MIRROR);
        try (BufferedReader br = new BufferedReader(new FileReader(PATH_TO_RESULT))) {

            String currentLine;

            while ((currentLine = br.readLine()) != null) {
                String reversedCurrentLine = new StringBuilder(currentLine).reverse().toString();
                printToFile(reversedCurrentLine);
                printToFile("");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        printWriter.close();
    }


    // Вертикальное отзеркаливание ascii изображения
    private void verticalMirrorImage() throws Exception {
        horizontalMirrorImage();
        rotateImage(180, PATH_TO_VERTICAL_MIRROR, PATH_TO_HORIZONTAL_MIRROR);
    }


    // Поворот ascii изображения на degree градусов
    private void rotateImage(Integer degree, String pathToResultFile, String pathToImg) throws Exception {
        makePrintWriter(pathToResultFile);

        try (BufferedReader br = new BufferedReader(new FileReader(pathToImg))) {

            String currentLine;

            if (degree == 180) {
                StringBuilder allLines = new StringBuilder();
                while ((currentLine = br.readLine()) != null) {
                    allLines.append(currentLine);
                    allLines.append("\n");
                }
                printToFile(allLines.reverse().toString());
                printToFile("");
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
                        for (int y = 0; y <height; y++) {
                            newAllSymbols[height - y - 1][x] = allSymbols[x][y];
                        }
                    }
                } else if (degree == 90) {
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y <height; y++) {
                            newAllSymbols[y][width - x - 1] = allSymbols[x][y];
                        }
                    }
                } else {
                    throw new Exception(INCORRECT_ROTATION);
                }
                for (int x = 0; x < newAllSymbols.length; x++) {
                    String currLine = String.valueOf(newAllSymbols[x]);
                    printToFile(currLine);
                    printToFile("");
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


    private void printToFile(String symbol) throws IOException {
        try {
            if (symbol.equals("")) {
                printWriter.println(symbol);
            } else {
                printWriter.print(symbol);
            }
        } catch (Exception ex) {
            throw new IOException(IO_WRITER_EXCEPTION);
        }
    }

    public static void main(String[] args) throws Exception {
		// Так как путь до файла передается в конструктор класса, а JCommander работает уже с объектом класса
		// параметр -n imageName, например -n picture1.jpg, должен стоять в самом начале аргументов командной строки
		// дальшейний порядок аргументов не имеет значения
        StringBuilder fileName = new StringBuilder(PATH_TO_RESULT_DIR);
        fileName.append(args[1]);
        Img2ASCII imgInASCII = new Img2ASCII(fileName.toString());
        imgInASCII.convertToAscii();
        JCommander.newBuilder()
                .addObject(imgInASCII)
                .build()
                .parse(args);
        if (imgInASCII.degree != null) {
            StringBuilder pathBuilder = new StringBuilder(PATH_TO_RESULT_DIR);
            pathBuilder.append("result_rotate_");
            pathBuilder.append(imgInASCII.degree);
            pathBuilder.append("_degrees.txt");
            imgInASCII.rotateImage(imgInASCII.degree, pathBuilder.toString(), PATH_TO_RESULT);
        }
        if (imgInASCII.mirroring) {
            imgInASCII.verticalMirrorImage();
        }
        if (imgInASCII.decreaseTimes != null) {
            StringBuilder pathBuilder = new StringBuilder(PATH_TO_RESULT_DIR);
            pathBuilder.append("result_decreased_");
            pathBuilder.append(imgInASCII.decreaseTimes);
            pathBuilder.append("_times.txt");
            imgInASCII.decreaseSize(imgInASCII.decreaseTimes, pathBuilder.toString());
        }
    }
}