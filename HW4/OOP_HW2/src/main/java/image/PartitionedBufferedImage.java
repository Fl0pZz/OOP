package image;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 08.02.2018.
 */
public class PartitionedBufferedImage {
    private List<BufferedImage> partitions;
    private List<Integer> xOffset;
    private List<Integer> yOffset;
    private int width;
    private int height;
    private int type;

    public PartitionedBufferedImage(BufferedImage image) {
        partitions = new ArrayList<>();
        xOffset = new ArrayList<>();
        yOffset = new ArrayList<>();
        width = image.getWidth();
        height = image.getHeight();
        type = image.getType();

        List<Integer> xLines = new ArrayList<>();
        List<Integer> yLines = new ArrayList<>();
        findLines(xLines, yLines, image);
        xLines = concatLines(xLines);
        yLines = concatLines(yLines);
        for (int xIndex = 0; xIndex < xLines.size() - 1; ++xIndex) {
            for (int yIndex = 0; yIndex < yLines.size() - 1; ++yIndex) {
                addPartition(
                        image.getSubimage(
                                xLines.get(xIndex), yLines.get(yIndex),
                                xLines.get(xIndex + 1) - xLines.get(xIndex),
                                yLines.get(yIndex + 1) - yLines.get(yIndex)
                        ),
                        xLines.get(xIndex), yLines.get(yIndex));
            }
        }
    }

    public BufferedImage glueImage() {
        BufferedImage result = new BufferedImage(
                width, height, type
        );
        for (int i = 0; i < partitions.size(); ++i) {
            BufferedImage img = partitions.get(i);
            for (int x = 0; x < img.getWidth(); ++x){
                for (int y = 0; y < img.getHeight(); ++y){
                    result.setRGB(x + xOffset.get(i), y + yOffset.get(i), img.getRGB(x, y));
                }
            }
        }
        return result;
    }

    public void setPartitions(List<BufferedImage> partitions) {
        this.partitions = partitions;
    }

    public List<BufferedImage> getPartitions() {
        return partitions;
    }

    private void findLines(List<Integer> xLines, List<Integer> yLines, BufferedImage image) {
        xLines.add(0);
        yLines.add(0);

//        TODO: recognize black color; refactor
        for (int x = 0; x < width; ++x) {
            if (image.getRGB(x, 0) < -10000) {
                boolean flag = true;
                for (int y = 1; y < height; ++y) {
                    if (image.getRGB(x, y) > -10000) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    xLines.add(x);
                }
            }
        }

        for (int y = 0; y < height; ++y) {
            if (image.getRGB(0, y) < -10000) {
                boolean flag = false;
                for (int x = 1; x < width; ++x) {
                    if (image.getRGB(x, y) > -10000) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    yLines.add(y);
                }
            }
        }

        xLines.add(width);
        yLines.add(height);
    }

    private List<Integer> concatLines(List<Integer> lines) {
        List<Integer> result = new ArrayList<>();
        Integer start = lines.get(0);
        Integer previous = start;
        int i = 1;
        for (; i < lines.size(); ++i) {
            Integer line = lines.get(i);
            if (line != previous + 1) {
                result.add((previous + start) / 2);
                start = line;
            }
            previous = line;
        }
        result.add(lines.get(i-1));
        return result;
    }

    private void addPartition(BufferedImage partition, int x, int y) {
        xOffset.add(x);
        yOffset.add(y);
        partitions.add(partition);
    }

}
