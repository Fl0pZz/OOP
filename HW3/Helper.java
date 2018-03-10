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

    public StringBuilder makeStringBuilderToResult(int i, int j, String resultPath) {
        StringBuilder pathToASCIIImage = new StringBuilder(resultPath);
        pathToASCIIImage.append("_");
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
}
