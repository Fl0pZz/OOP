package process;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class ImageProcessor {
    public interface Operation {
        BufferedImage execute(BufferedImage image);
    }

    public static BufferedImage resize(BufferedImage image, int targetWidth, int targetHeight) {

        BufferedImage resultImage = new BufferedImage(targetWidth, targetHeight, image.getType());
        Graphics2D g2d = resultImage.createGraphics();
        g2d.drawImage(image, 0, 0, resultImage.getWidth(), resultImage.getHeight(), null);
        g2d.dispose();

        return resultImage;
    }

    public static BufferedImage rotate(BufferedImage image, int degrees) {

        BufferedImage resultImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g2d = resultImage.createGraphics();
        AffineTransform trans = new AffineTransform();
        trans.rotate( Math.toRadians(degrees), resultImage.getHeight() / 2, resultImage.getWidth() / 2);
        g2d.drawImage(image, trans, null);
        g2d.dispose();

        return resultImage;
    }

    public static BufferedImage mirrorX(BufferedImage image) {
        return mirror(image, -1, 1);
    }

    public static BufferedImage mirrorY(BufferedImage image) {
        return mirror(image, 1, -1);
    }

    private static BufferedImage mirror(BufferedImage image, int sx, int sy) {

        BufferedImage resultImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g2d = resultImage.createGraphics();
        g2d.setTransform(AffineTransform.getScaleInstance(sx, sy));
        g2d.drawImage(image, sx < 0 ? -image.getWidth() : 0, sy < 0 ? -image.getHeight() : 0, null);
        g2d.dispose();

        return resultImage;
    }

    public static List<BufferedImage> parallelProcess(List<BufferedImage> images, List<Operation> operations) throws Exception {
        Stack<Thread> workers = new Stack<>();
        List<BufferedImage> resultImages = new ArrayList<>(images);
        for (int i = 0; i < images.size(); ++i) {
            int index = i;
            workers.add(
                    new Thread(() -> {
                        BufferedImage img = images.get(index);
                        for (Operation operation : operations) {
                            img = operation.execute(img);
                        }
                        resultImages.set(index, img);
                    })
            );
            workers.peek().start();
        }
        for (Thread thread : workers) {
            thread.join();
        }
        return resultImages;
    }

}
