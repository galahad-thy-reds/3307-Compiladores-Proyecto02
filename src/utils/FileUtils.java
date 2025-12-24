package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Utility class for file operations.
 * 
 * @author eduardo
 */
public class FileUtils {
    
    /**
     * Reads the entire contents of a file as a string.
     * 
     * @param filePath Path to the file
     * @return File contents as a string
     * @throws IOException If file reading fails
     */
    public static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
    
    /**
     * Gets the output file path by replacing the extension with .txt.
     * 
     * @param inputFilePath Input file path
     * @return Output file path with .txt extension
     */
    public static String getOutputFilePath(String inputFilePath) {
        if (inputFilePath.endsWith(".html")) {
            return inputFilePath.substring(0, inputFilePath.length() - 5) + ".txt";
        }
        return inputFilePath + ".txt";
    }
}

