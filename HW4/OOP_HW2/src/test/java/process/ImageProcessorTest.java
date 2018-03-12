package process;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ImageProcessorTest {

    @Test
    void resize() throws Exception {
        BufferedImage img = ImageIO.read(new File("rabbit_divided.png"));
        BufferedImage actual = ImageIO.read(new File("src\\test\\actual\\png\\resize120110.png"));
        img = ImageProcessor.resize(img, 120, 110);
        assertImage(img, actual);
    }

    @Test
    void rotate() throws Exception {
        BufferedImage img = ImageIO.read(new File("rabbit_divided.png"));
        BufferedImage actual = ImageIO.read(new File("src\\test\\actual\\png\\rotate53.png"));
        img = ImageProcessor.rotate(img, 53);
        assertImage(img, actual);
    }

    @Test
    void mirrorX() throws Exception {
        BufferedImage img = ImageIO.read(new File("rabbit_divided.png"));
        BufferedImage actual = ImageIO.read(new File("src\\test\\actual\\png\\mirrorX.png"));
        img = ImageProcessor.mirrorX(img);
        assertImage(img, actual);
    }

    @Test
    void mirrorY() throws Exception{
        BufferedImage img = ImageIO.read(new File("rabbit_divided.png"));
        BufferedImage actual = ImageIO.read(new File("src\\test\\actual\\png\\mirrorY.png"));
        img = ImageProcessor.mirrorY(img);
        assertImage(img, actual);
    }

    @Test
    void parallelProcess() throws Exception {
        BufferedImage img1 = ImageIO.read(new File("rabbit_divided.png"));
        BufferedImage img2 = ImageIO.read(new File("rabbit_divided.png"));
        BufferedImage img3 = ImageIO.read(new File("rabbit_divided.png"));


        List<BufferedImage> result = ImageProcessor.parallelProcess(Arrays.asList(img1, img2, img3), Arrays.asList(
                (image) -> ImageProcessor.rotate(image, 53)
        ));

        BufferedImage actual = ImageIO.read(new File("src\\test\\actual\\png\\rotate53.png"));
        for (BufferedImage img : result) {
            assertImage(img, actual);
        }

        result = ImageProcessor.parallelProcess(Arrays.asList(img1, img2, img3), Arrays.asList(
                (image) -> ImageProcessor.resize(image, 120, 110)
        ));

        actual = ImageIO.read(new File("src\\test\\actual\\png\\resize120110.png"));
        for (BufferedImage img : result) {
            assertImage(img, actual);
        }

        result = ImageProcessor.parallelProcess(Arrays.asList(img1, img2, img3), Arrays.asList(
                (image) -> ImageProcessor.mirrorX(image)
        ));

        actual = ImageIO.read(new File("src\\test\\actual\\png\\mirrorX.png"));
        for (BufferedImage img : result) {
            assertImage(img, actual);
        }

        result = ImageProcessor.parallelProcess(Arrays.asList(img1, img2, img3), Arrays.asList(
                (image) -> ImageProcessor.mirrorY(image)
        ));

        actual = ImageIO.read(new File("src\\test\\actual\\png\\mirrorY.png"));
        for (BufferedImage img : result) {
            assertImage(img, actual);
        }

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