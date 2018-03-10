import com.beust.jcommander.JCommander;

public class Main {
    public static void main(String[] args) throws Exception {
        ImgToASCII imgInASCII = new ImgToASCII();
        JCommander.newBuilder()
                .addObject(imgInASCII)
                .build()
                .parse(args);

        imgInASCII.getImg(imgInASCII.imgPath);

        StringBuilder pathToASCIIResult = new StringBuilder(imgInASCII.resultPath);
        StringBuilder pathToHorizontalMirror = pathToASCIIResult.append("_horizontal_mirror");
        imgInASCII.pathToHorizontalMirror = pathToHorizontalMirror.toString();
        pathToASCIIResult.setLength(pathToASCIIResult.length() - "_horizontal_mirror".length());
        StringBuilder pathToVerticalMirror = pathToASCIIResult.append("_vertical_mirror");
        imgInASCII.pathToVerticalMirror = pathToVerticalMirror.toString();

        imgInASCII.convertToAscii();

        if (imgInASCII.degree != null) {
            StringBuilder pathBuilder = new StringBuilder(imgInASCII.resultPath);
            pathBuilder.append("_rotate_");
            pathBuilder.append(imgInASCII.degree);
            pathBuilder.append("_degrees");
            imgInASCII.rotateImage(imgInASCII.degree, pathBuilder.toString(), imgInASCII.resultPath);
        }

        if (imgInASCII.mirroring) {
            imgInASCII.verticalMirrorImage();
        }

        if (imgInASCII.decreaseTimes != null) {
            StringBuilder pathBuilder = new StringBuilder(imgInASCII.resultPath);
            pathBuilder.append("_decreased_");
            pathBuilder.append(imgInASCII.decreaseTimes);
            pathBuilder.append("_times");
            imgInASCII.decreaseSize(imgInASCII.decreaseTimes, pathBuilder.toString());
        }
    }
}
