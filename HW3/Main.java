import com.beust.jcommander.JCommander;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static void collectASCIILines(BigImageWithBlackLines image, int linesCount) throws IOException {
        PrintWriter subResultASCII = image.helper.makePrintWriter(image.resultPath);
        for (int i = 0; i < linesCount; i++) {
            StringBuilder pathToTMP = new StringBuilder(image.resultPath);
            pathToTMP.append("_");
            pathToTMP.append(i);
            BufferedReader brCurrent = new BufferedReader(new FileReader(pathToTMP.toString()));
            String currentLine = brCurrent.readLine();
            while (currentLine != null) {
                subResultASCII.println(currentLine);
                currentLine = brCurrent.readLine();
            }
            brCurrent.close();
        }
        subResultASCII.close();
    }

    private static void collectBatchesIntoASCIILines(BigImageWithBlackLines image, int iteration,
                                                     List<BufferedImage> batches, List<String> tmpFiles) throws IOException {
        StringBuilder subResultPath = new StringBuilder(image.resultPath);
        subResultPath.append("_");
        subResultPath.append(iteration);
        tmpFiles.add(subResultPath.toString());
        PrintWriter subResultASCII = image.helper.makePrintWriter(subResultPath.toString());
        List<BufferedReader> subImagesReaders = new ArrayList<>();
        for (int j = 0; j < batches.size(); j++) {
            StringBuilder locationForFileReader =
                    image.helper.makeStringBuilderToResult(iteration, j, image.resultPath);
            locationForFileReader.append("_");
            BufferedReader brCurrent = new BufferedReader(new FileReader(locationForFileReader.toString()));
            subImagesReaders.add(brCurrent);
        }
        String resultString = subImagesReaders.get(0).readLine();
        while (resultString != null) {
            StringBuilder resultStringBuilder = new StringBuilder(resultString);
            for (int j = 1; j < subImagesReaders.size(); j++) {
                String tmpString = subImagesReaders.get(j).readLine();
                resultStringBuilder.append(tmpString);
            }
            subResultASCII.println(resultStringBuilder.toString());
            resultString = subImagesReaders.get(0).readLine();
        }
        subResultASCII.close();

        for (BufferedReader subImagesReader : subImagesReaders) {
            subImagesReader.close();
        }
    }



    public static void main(String[] args) throws IOException {
        final List<String> tmpFileNames = new ArrayList<>();
        final BigImageWithBlackLines bigImage = new BigImageWithBlackLines();

        JCommander.newBuilder()
                .addObject(bigImage)
                .build()
                .parse(args);

        bigImage.getImg(bigImage.imgPath);

        List<Integer> horizontalLines = bigImage.BLDAC.findLines(bigImage.img, Constants.HORIZONTAL);
        List<Integer> verticalLines = bigImage.BLDAC.findLines(bigImage.img, Constants.VERTICAL);

        List<BufferedImage> horizontalPartsOfImage =
                bigImage.BLDAC.getPartsOfImage(bigImage.img, horizontalLines, Constants.HORIZONTAL);
        for (int i = 0; i < horizontalPartsOfImage.size(); i++) {
            final List<BufferedImage> resultPartsOfHorizontalPart =
                    bigImage.BLDAC.getPartsOfImage(horizontalPartsOfImage.get(i), verticalLines, Constants.VERTICAL);
            ArrayList<Thread> arrThreads = new ArrayList<>();
            for (int j = 0; j < resultPartsOfHorizontalPart.size(); j++) {

                final int iTMP = i;
                final int jTMP = j;
                // заводим треды для обработки
                Thread currentThread = new Thread("" + j) {
                    public void run() {
                        StringBuilder pathToASCIIImage =
                                bigImage.helper.makeStringBuilderToResult(iTMP, jTMP, bigImage.resultPath);
                        tmpFileNames.add(pathToASCIIImage.toString());
                        Img2ASCII tmpASCII =
                                new Img2ASCII(resultPartsOfHorizontalPart.get(jTMP), pathToASCIIImage.toString());
                        try {
                            tmpASCII.convertToAscii();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        StringBuilder pathToResult =
                                bigImage.helper.makeStringBuilderToResult(iTMP, jTMP, bigImage.resultPath);
                        pathToResult.append("_");
                        tmpFileNames.add(pathToResult.toString());
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
                            StringBuilder pathToTMP =
                                    bigImage.helper.makeStringBuilderToResult(iTMP, jTMP, bigImage.resultPath);
                            pathToTMP.append("__");

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
            collectBatchesIntoASCIILines(bigImage, i, resultPartsOfHorizontalPart, tmpFileNames);
        }

        // склеивание всех ascii-строк

        collectASCIILines(bigImage, horizontalPartsOfImage.size());

//      удаление всех ненужных файлов
        for (String tmpFileName : tmpFileNames) {
            Files.delete(Paths.get(tmpFileName));
        }
    }
}
