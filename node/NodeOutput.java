package node;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class NodeOutput {
    public static String parserString = "";
    public static void writeToParser () throws IOException {
        String parserFilePath = "parser.txt";
        FileWriter write = new FileWriter(parserFilePath);
        //System.out.print(str + "\n");
        try (BufferedWriter parserWriter = Files.newBufferedWriter(Paths.get(parserFilePath), java.nio.charset.StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.APPEND)) {
            parserWriter.write(parserString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
