package errors;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generates the numbered error report file (Requirement #1).
 * Creates a .txt copy of the HTML file with numbered lines and error annotations.
 * 
 * @author eduardo
 */
public class ErrorReporter {
    private final String inputFilePath;
    private final String outputFilePath;
    private final ErrorCollector errorCollector;
    
    /**
     * Creates a new error reporter.
     * 
     * @param inputFilePath Path to the input HTML file
     * @param outputFilePath Path to the output .txt file
     * @param errorCollector The error collector containing all errors
     */
    public ErrorReporter(String inputFilePath, String outputFilePath, ErrorCollector errorCollector) {
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
        this.errorCollector = errorCollector;
    }
    
    /**
     * Generates the numbered error report file.
     * 
     * @throws IOException If file I/O fails
     */
    public void generateReport() throws IOException {
        // Read original file
        List<String> lines = readFileLines();
        
        // Group errors by line number
        Map<Integer, List<Error>> errorsByLine = errorCollector.getErrors().stream()
                .collect(Collectors.groupingBy(Error::getLineNumber));
        
        // Write numbered file with errors
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            for (int i = 0; i < lines.size(); i++) {
                int lineNumber = i + 1;
                String line = lines.get(i);
                
                // Write numbered line (4-digit format: 0001, 0002, etc.)
                String numberedLine = String.format("%04d %s", lineNumber, line);
                writer.write(numberedLine);
                writer.newLine();
                
                // Write errors for this line (below the line)
                if (errorsByLine.containsKey(lineNumber)) {
                    for (Error error : errorsByLine.get(lineNumber)) {
                        String errorLine = String.format("     Error %d: %s at line %d", 
                                error.getErrorNumber(), error.getDescription(), error.getLineNumber());
                        writer.write(errorLine);
                        writer.newLine();
                    }
                }
            }
        }
    }
    
    /**
     * Reads all lines from the input file.
     * 
     * @return List of file lines
     * @throws IOException If file reading fails
     */
    private List<String> readFileLines() throws IOException {
        List<String> lines = new java.util.ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }
    
    /**
     * Gets the output file path.
     * 
     * @return Output file path
     */
    public String getOutputFilePath() {
        return outputFilePath;
    }
}

