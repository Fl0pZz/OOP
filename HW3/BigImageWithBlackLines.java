
import com.beust.jcommander.Parameter;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class BigImageWithBlackLines {

    // Параметры командной строки должны представляться в виде -n imageName -r rotationDegree
    // если присутствует параметр -vm или -hm, будет выполнено вертикальное или горизонтальное отзеркаливание
    @Parameter(names={"--rotate", "-r"})
    public Integer degree = null;

    @Parameter(names={"--vertical_mirror", "-vm"})
    public boolean verticalMirror;

    @Parameter(names={"--horizontal_mirror", "-hm"})
    public boolean horizontalMirror;

    @Parameter(names={"--img", "-i"})
    public String imgPath = null;

    @Parameter(names={"--result", "-res"})
    public String resultPath = null;

    public BufferedImage img = null;
    public BlackLinesDetectorAndCutter BLDAC = new BlackLinesDetectorAndCutter();
    public Helper helper = new Helper();

    public void getImg(String path) throws IOException {
        try {
            img = ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new IOException(Constants.IO_READER_EXCEPTION);
        }
    }
}
