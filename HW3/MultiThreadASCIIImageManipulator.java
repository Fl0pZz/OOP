import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import javafx.util.Pair;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class MultiThreadASCIIImageManipulator {

    // Параметры командной строки должны представляться в виде -n imageName -r rotationDegree
    // если присутствует параметр -vm или -hm, будет выполнено вертикальное или горизонтальное отзеркаливание
    @Parameter(names={"--rotate", "-r"})
    private Integer degree = null;
    @Parameter(names={"--vertical_mirror", "-vm"})
    private boolean verticalMirror;
    @Parameter(names={"--horizontal_mirror", "-hm"})
    private boolean horizontalMirror;
    @Parameter(names={"--name", "-n"})
    private String imgName = null;

    private BufferedImage img = null;
    private static Helper helper = new Helper();

    private static final Integer PIXEL_MAX_VALUE = 30;
    private static final String HORIZONTAL = "horizontal";
    private static final String VERTICAL = "vertical";

    private MultiThreadASCIIImageManipulator(String imgName) throws IOException {
        try {
            img = ImageIO.read(new File(imgName));
        } catch (IOException e) {
            throw new IOException(Constants.IO_READER_EXCEPTION);
        }
    }

    // ищем горизонтальные или вертикальные разделяющие линии
    private List<Integer> findLines(BufferedImage img, String option) {
        List<Integer> alllLines = new ArrayList<>();
        Integer width = null;
        Integer height = null;
        boolean isVertical = option.equals(VERTICAL);
        if (isVertical) {
            width = img.getHeight();
            height = img.getWidth();
        } else {
            height = img.getHeight();
            width = img.getWidth();
        }
        // идем по прямой x = 0 для horizontal и y = 0 для vertical
        for (int i = 0; i < height; i++) {
            boolean haveFoundLine = true;
            Color pixelColor = null;
            if (isVertical) {
                pixelColor = new Color(img.getRGB(i, 0));
            } else {
                pixelColor = new Color(img.getRGB(0, i));
            }
            if (pixelColor.getRed() <= PIXEL_MAX_VALUE && pixelColor.getGreen() <= PIXEL_MAX_VALUE
                    && pixelColor.getBlue() <= PIXEL_MAX_VALUE) {
                for (int j = 1; j < width; j++) {
                    Color currentPixelColor = null;
                    if (isVertical) {
                        currentPixelColor = new Color(img.getRGB(i, j));
                    } else {
                        currentPixelColor = new Color(img.getRGB(j, i));
                    }
                    if (currentPixelColor.getRed() <= PIXEL_MAX_VALUE
                            && currentPixelColor.getGreen() <= PIXEL_MAX_VALUE
                            && currentPixelColor.getBlue() <= PIXEL_MAX_VALUE) {
                        continue;
                    } else {
                        haveFoundLine = false;
                        break;
                    }
                }
            } else {
                haveFoundLine = false;
            }
            if (haveFoundLine) {
                alllLines.add(i);
            }
        }
        return alllLines;
    }

    // получить батчи по данным разделяющим линиям
    public List<BufferedImage> getPartsOfImage(BufferedImage img, List<Integer> allLines, String option) {
        Integer width = null;
        Integer height = null;
        boolean isVertical = option.equals(VERTICAL);
        if (isVertical) {
            width = img.getHeight();
            height = img.getWidth();
        } else {
            width = img.getWidth();
            height = img.getHeight();
        }
        List<BufferedImage> subImages = new ArrayList<>();
        List<Pair<Integer, Integer>> parts = new ArrayList<>();
        boolean isSubImageBeginning = true;
        Integer currentBegin = null;
        for (int i = 0; i < height; i++) {
            if (!allLines.contains(i) && isSubImageBeginning) {
                currentBegin = i;
                isSubImageBeginning = false;
            } else if (allLines.contains(i) && !isSubImageBeginning) {
                parts.add(new Pair<>(currentBegin, i - 1));
                isSubImageBeginning = true;
            }
        }
        if (!isSubImageBeginning) {
            parts.add(new Pair<>(currentBegin, height - 1));
        }
        for (int i = 0; i < parts.size(); i++) {
            Integer partBegin = parts.get(i).getKey();
            Integer partEnd = parts.get(i).getValue();
            BufferedImage tmpImage = null;
            if (isVertical) {
                tmpImage = img.getSubimage(partBegin, 0, partEnd - partBegin + 1, width);
            } else {
                tmpImage = img.getSubimage(0, partBegin, width, partEnd - partBegin + 1);
            }
            subImages.add(tmpImage);
        }
        return subImages;
    }

    public static void main(String[] args) throws IOException {
        StringBuilder fileName = new StringBuilder(Constants.PATH_TO_RESULT_DIR);
        fileName.append(args[1]);
        final MultiThreadASCIIImageManipulator bigImage = new MultiThreadASCIIImageManipulator(fileName.toString());
        // Так как путь до файла передается в конструктор класса, а JCommander работает уже с объектом класса
        // параметр -n imageName, например -n picture1.jpg, должен стоять в самом начале аргументов командной строки
        // далее задается флаг манипуляции с мини-батчами
        JCommander.newBuilder()
                .addObject(bigImage)
                .build()
                .parse(args);
        List<Integer> horizontalLines = bigImage.findLines(bigImage.img, HORIZONTAL);
        List<Integer> verticalLines = bigImage.findLines(bigImage.img, VERTICAL);
        List<BufferedImage> horizontalPartsOfImage = bigImage.getPartsOfImage(bigImage.img, horizontalLines, HORIZONTAL);
        for (int i = 0; i < horizontalPartsOfImage.size(); i++) {
            final List<BufferedImage> resultPartsOfHorizontalPart =
                    bigImage.getPartsOfImage(horizontalPartsOfImage.get(i), verticalLines, VERTICAL);
            ArrayList<Thread> arrThreads = new ArrayList<Thread>();
            for (int j = 0; j < resultPartsOfHorizontalPart.size(); j++) {

                final int iTMP = i;
                final int jTMP = j;
                // заводим треды для обработки
                Thread currentThread = new Thread("" + j){
                    public void run(){
                        StringBuilder pathToASCIIImage = helper.makeStringBuilderToResult(iTMP, jTMP);
                        pathToASCIIImage.append(".txt");
                        Img2ASCII tmpASCII = new Img2ASCII(resultPartsOfHorizontalPart.get(jTMP), pathToASCIIImage.toString());
                        try {
                            tmpASCII.convertToAscii();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        StringBuilder pathToResult = helper.makeStringBuilderToResult(iTMP, jTMP);
                        pathToResult.append("_result");
                        pathToResult.append(".txt");
                        if (bigImage.degree != null) {
                            try {
                                tmpASCII.rotateImage(bigImage.degree,
                                        pathToResult.toString(), pathToASCIIImage.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (bigImage.horizontalMirror) {
                            try {
                                tmpASCII.horizontalMirrorImage(pathToResult.toString(),
                                        pathToASCIIImage.toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (bigImage.verticalMirror) {
                            StringBuilder pathToTMP = helper.makeStringBuilderToResult(iTMP, jTMP);
                            try {
                                tmpASCII.verticalMirrorImage(pathToResult.toString(),
                                        pathToASCIIImage.toString(), pathToTMP.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                arrThreads.add(currentThread);
            }

            for (Thread t: arrThreads) {
                t.run();
            }

            // склеивание всех батчей в ascii-линии
            String subResultPath = Constants.PATH_TO_RESULT_DIR + i + "_result.txt";
            PrintWriter subResultASCII = helper.makePrintWriter(subResultPath);
            List<BufferedReader> subImagesReaders = new ArrayList<>();
            for (int j = 0; j < resultPartsOfHorizontalPart.size(); j++) {
                BufferedReader brCurrent = new BufferedReader(new FileReader(
                        Constants.PATH_TO_RESULT_DIR + i + "_" + j + "_result.txt"));
                subImagesReaders.add(brCurrent);
            }
            String resultString = subImagesReaders.get(0).readLine();
            while (resultString != null) {
                for (int j = 1; j < subImagesReaders.size(); j++) {
                    String tmpString = subImagesReaders.get(j).readLine();
                    resultString += tmpString;
                }
                subResultASCII.println(resultString);
                resultString = subImagesReaders.get(0).readLine();

            }
            subResultASCII.close();

            for (int j = 0; j < subImagesReaders.size(); j++) {
                subImagesReaders.get(j).close();
            }
        }

        // склеивание всех ascii-строк
        String resultPath = Constants.PATH_TO_RESULT_DIR + "result.txt";
        PrintWriter subResultASCII = helper.makePrintWriter(resultPath);
        for (int i = 0; i < horizontalPartsOfImage.size(); i++) {
            String pathToTMP = Constants.PATH_TO_RESULT_DIR + i + "_result.txt";
            BufferedReader brCurrent = new BufferedReader(new FileReader(pathToTMP));
            String currentLine = brCurrent.readLine();
            while (currentLine != null) {
                subResultASCII.println(currentLine);
                currentLine = brCurrent.readLine();
            }
            brCurrent.close();
        }
        subResultASCII.close();

        // удаление всех ненужных файлов
        File directory = new File(Constants.PATH_TO_RESULT_DIR);
        File[] files = directory.listFiles();
        List<String> allowedNames = new ArrayList<>();
        allowedNames.add("MultiThreadASCIIImageManipulator.java");
        allowedNames.add("Helper.java");
        allowedNames.add("Constants.java");
        allowedNames.add("Img2ASCII.java");
        allowedNames.add("result.txt");
        allowedNames.add(bigImage.imgName);
        for (File f: files) {
            if (!allowedNames.contains(f.getName())) {
                f.delete();
            }
        }
    }
}
