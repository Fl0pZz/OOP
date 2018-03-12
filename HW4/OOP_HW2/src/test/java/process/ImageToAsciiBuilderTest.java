package process;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;

import static org.junit.jupiter.api.Assertions.*;

class ImageToAsciiBuilderTest {
    @Test
    void convertToAscii() throws Exception {
        BufferedImage img = ImageIO.read(new File("rabbit_divided.png"));
        String asciiImage = ImageToAsciiBuilder.convertToAscii(img);
        File fileActual = new File("src\\test\\actual\\ascii\\asciiImage.txt");
        FileReader readerActual = new FileReader(fileActual);
        char[] data = new char[(int)fileActual.length()];
        readerActual.read(data);
        assertArrayEquals(data, asciiImage.toCharArray());
    }

}