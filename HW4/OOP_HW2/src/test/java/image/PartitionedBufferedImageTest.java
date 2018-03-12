package image;

import org.junit.jupiter.api.Test;
import process.ImageProcessor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PartitionedBufferedImageTest {

    @Test
    void glueImage() throws Exception {
        BufferedImage source = ImageIO.read(new File("rabbit_divided.png"));
        PartitionedBufferedImage partitionedBufferedImage = new PartitionedBufferedImage(source);
        BufferedImage img = partitionedBufferedImage.glueImage();
        BufferedImage actual = ImageIO.read(new File("rabbit_divided.png"));
        assertImage(img, actual);

        partitionedBufferedImage = new PartitionedBufferedImage(source);
        partitionedBufferedImage.setPartitions(ImageProcessor.parallelProcess(partitionedBufferedImage.getPartitions(), Arrays.asList(
                (image) -> ImageProcessor.mirrorX(image)
                ))
        );
        img = partitionedBufferedImage.glueImage();
        actual = ImageIO.read(new File("src\\test\\actual\\png\\parallelMirrorX.png"));
        assertImage(img, actual);

        partitionedBufferedImage = new PartitionedBufferedImage(source);
        partitionedBufferedImage.setPartitions(ImageProcessor.parallelProcess(partitionedBufferedImage.getPartitions(), Arrays.asList(
                (image) -> ImageProcessor.mirrorY(image)
                ))
        );
        img = partitionedBufferedImage.glueImage();
        actual = ImageIO.read(new File("src\\test\\actual\\png\\parallelMirrorY.png"));
        assertImage(img, actual);

        partitionedBufferedImage = new PartitionedBufferedImage(source);
        partitionedBufferedImage.setPartitions(ImageProcessor.parallelProcess(partitionedBufferedImage.getPartitions(), Arrays.asList(
                (image) -> ImageProcessor.rotate(image, 90)
                ))
        );
        img = partitionedBufferedImage.glueImage();
        actual = ImageIO.read(new File("src\\test\\actual\\png\\parallelRotate90.png"));
        assertImage(img, actual);

        partitionedBufferedImage = new PartitionedBufferedImage(source);
        partitionedBufferedImage.setPartitions(ImageProcessor.parallelProcess(partitionedBufferedImage.getPartitions(), Arrays.asList(
                (image) -> ImageProcessor.rotate(image, 180)
                ))
        );
        img = partitionedBufferedImage.glueImage();
        actual = ImageIO.read(new File("src\\test\\actual\\png\\parallelRotate180.png"));
        assertImage(img, actual);
    }

    static void assertImage(BufferedImage img1, BufferedImage img2) {
        assertTrue(bufferedImagesEqual(img1, img2));
    }

    static boolean bufferedImagesEqual(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
            for (int x = 0; x < img1.getWidth(); x++) {
                for (int y = 0; y < img1.getHeight(); y++) {
                    if (img1.getRGB(x, y) != img2.getRGB(x, y))
                        return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

}