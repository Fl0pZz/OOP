import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Helper {

    public PrintWriter makePrintWriter(String pathToWrite) throws IOException {
        FileWriter fileWriter;
        PrintWriter printWriter;
        try {
            fileWriter = new FileWriter(pathToWrite);
            printWriter = new PrintWriter(fileWriter, true);
        } catch (IOException ex) {
            throw new IOException(Constants.IO_WRITER_EXCEPTION);
        }
        return printWriter;
    }

    public StringBuilder makeStringBuilderToResult(int i, int j) {
        StringBuilder pathToASCIIImage = new StringBuilder();
        pathToASCIIImage.append(Constants.PATH_TO_RESULT_DIR);
        pathToASCIIImage.append(i);
        pathToASCIIImage.append("_");
        pathToASCIIImage.append(j);
        return pathToASCIIImage;
    }

    public void printToFile(String symbol, PrintWriter printWriter) throws IOException {
        try {
            if (symbol.equals("")) {
                printWriter.println(symbol);
            } else {
                printWriter.print(symbol);
            }
        } catch (Exception ex) {
            throw new IOException(Constants.IO_WRITER_EXCEPTION);
        }
    }

    public String valueToChar(double value) {
        String str;

        if (value >= 230.0) {
            str = " ";
        } else if (value >= 200.0) {
            str = ".";
        } else if (value >= 180.0) {
            str = "*";
        } else if (value >= 160.0) {
            str = ":";
        } else if (value >= 130.0) {
            str = "o";
        } else if (value >= 100.0) {
            str = "&";
        } else if (value >= 70.0) {
            str = "8";
        } else if (value >= 50.0) {
            str = "#";
        } else {
            str = "@";
        }
        return str;
    }
}
