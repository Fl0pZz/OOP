import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class WriterToFileHelper {
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
}
