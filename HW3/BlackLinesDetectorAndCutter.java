import javafx.util.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class BlackLinesDetectorAndCutter {


    // ищем горизонтальные или вертикальные разделяющие линии
    public List<Integer> findLines(BufferedImage img, String option) {
        List<Integer> alllLines = new ArrayList<>();
        Integer width = null;
        Integer height = null;
        boolean isVertical = option.equals(Constants.VERTICAL);
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
            if (pixelColor.getRed() <= Constants.PIXEL_MAX_VALUE && pixelColor.getGreen() <= Constants.PIXEL_MAX_VALUE
                    && pixelColor.getBlue() <= Constants.PIXEL_MAX_VALUE) {
                for (int j = 1; j < width; j++) {
                    Color currentPixelColor = null;
                    if (isVertical) {
                        currentPixelColor = new Color(img.getRGB(i, j));
                    } else {
                        currentPixelColor = new Color(img.getRGB(j, i));
                    }
                    if (currentPixelColor.getRed() <= Constants.PIXEL_MAX_VALUE
                            && currentPixelColor.getGreen() <= Constants.PIXEL_MAX_VALUE
                            && currentPixelColor.getBlue() <= Constants.PIXEL_MAX_VALUE) {
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
        boolean isVertical = option.equals(Constants.VERTICAL);
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
}
