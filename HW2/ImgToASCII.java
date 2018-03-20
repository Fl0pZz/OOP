import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import com.beust.jcommander.Parameter;

public class ImgToASCII {


    // Параметры командной строки должны представляться в виде -i imagePath -res resultPath -r rotationDegree -d decreasingSize
    // если присутствует параметр -m, то будет выполнено отзеркаливание вертикальное и горизонтальное.
    @Parameter(names={"--rotate", "-r"})
    public Integer degree = null;

    @Parameter(names={"--mirror", "-m"})
    public boolean mirroring;

    @Parameter(names={"--decrease", "-d"})
    public Integer decreaseTimes = null;

    @Parameter(names={"--result", "-res"})
    public String resultPath = null;

    @Parameter(names={"--img", "-i"})
    public String imgPath = null;
    /*
     * Поля класса должны быть объявлены в порядке: public, protected, private
     */
    private BufferedImage img = null;
    public String pathToHorizontalMirror = null;
    public String pathToVerticalMirror = null;
    /*
     * Возможно не стоило на каждый интсанс класса создавать еще один класс. Альтернативные вариант:
     *     создать конструктор, один из аргументов которого - инстанс WriterToFileHelper
     */
    private WriterToFileHelper writerToFileHelper = new WriterToFileHelper();


    public void getImg(String path) throws IOException {
        try {
            img = ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new IOException(Constants.IO_READER_EXCEPTION);
        }
    }

    // Преобразует изображение в ascii формат
    public void convertToAscii() throws IOException {
        PrintWriter printWriter = writerToFileHelper.makePrintWriter(resultPath);
        
        /*
         * Проход по всему изображению. Кажется это довольно полезаная функция, поэтому я бы сделал тут функицию,
         * аргумент которого - функция преобразования каждого пикселя, а возвращаемое значение - новая картинка
         */
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                Color pixelColor = new Color(img.getRGB(j, i));
                double pixelValue =
                        pixelColor.getRed() * 0.3 + pixelColor.getGreen() * 0.11 + pixelColor.getBlue() * 0.59;
                String s = valueToChar(pixelValue);
                writerToFileHelper.printToFile(String.valueOf(s), printWriter);
            }
            writerToFileHelper.printToFile("", printWriter);
        }
        printWriter.close();
    }


    // Сжимает ascii изображение в times раз
    public void decreaseSize(Integer times, String resultPath) throws IOException {
        PrintWriter printWriter = writerToFileHelper.makePrintWriter(resultPath);

        for (int i = 0; i < img.getHeight() / times; i++) {
            for (int j = 0; j < img.getWidth() / times; j++) {
                /*
                 * Зачем этот тут? Почему получив ошибку, никак ее не обрабатываем?
                 */
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
                writerToFileHelper.printToFile(String.valueOf(s), printWriter);
            }
            writerToFileHelper.printToFile("", printWriter);
        }
        printWriter.close();
    }


    // Горизонтальное отзеркаливание ascii изображения
    private void horizontalMirrorImage() throws IOException {
        PrintWriter printWriter = writerToFileHelper.makePrintWriter(pathToHorizontalMirror);
        try (BufferedReader br = new BufferedReader(new FileReader(resultPath))) {

            String currentLine;

            while ((currentLine = br.readLine()) != null) {
                String reversedCurrentLine = new StringBuilder(currentLine).reverse().toString();
                writerToFileHelper.printToFile(reversedCurrentLine, printWriter);
                writerToFileHelper.printToFile("", printWriter);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        printWriter.close();
    }


    // Вертикальное отзеркаливание ascii изображения
    public void verticalMirrorImage() throws Exception {
        horizontalMirrorImage();
        rotateImage(180, pathToVerticalMirror, pathToHorizontalMirror);
    }


    // Поворот ascii изображения на degree градусов
    public void rotateImage(Integer degree, String pathToResultFile, String pathToImg) throws Exception {
        PrintWriter printWriter = writerToFileHelper.makePrintWriter(pathToResultFile);

        try (BufferedReader br = new BufferedReader(new FileReader(pathToImg))) {

            String currentLine;

            if (degree == 180) {
                StringBuilder allLines = new StringBuilder();
                while ((currentLine = br.readLine()) != null) {
                    allLines.append(currentLine);
                    allLines.append("\n");
                }
                writerToFileHelper.printToFile(allLines.reverse().toString(), printWriter);
                writerToFileHelper.printToFile("", printWriter);
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
                // Кажется, чтобы разгрузить эту функцию, функции поворота можно было бы вынести отдельно, вызывая их когда нужно
                if (degree == 270) {
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            newAllSymbols[width - x - 1][y] = allSymbols[y][x];
                        }
                    }
                } else if (degree == 90) {
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y <height; y++) {
                            newAllSymbols[x][height - y - 1] = allSymbols[y][x];
                        }
                    }
                } else {
                    throw new Exception(Constants.INCORRECT_ROTATION);
                }
                for (int x = 0; x < newAllSymbols.length; x++) {
                    String currLine = String.valueOf(newAllSymbols[x]);
                    writerToFileHelper.printToFile(currLine, printWriter);
                    writerToFileHelper.printToFile("", printWriter);
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
