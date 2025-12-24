import utils.FileUtils;
import java.io.IOException;

/**
 * Main entry point for the HTML/JavaScript validator.
 * Reads an HTML file, validates it according to 8 requirements, and generates an error report.
 * 
 * @author eduardo
 */
public class Main {

    /**
     * Main method - entry point of the program.
     * 
     * @param args Command line arguments. Expects one argument: path to HTML file
     */
    public static void main(String[] args) {
        // Check command line arguments
        if (args.length < 1) {
            System.out.println("Usage: java Main <input.html>");
            System.out.println("Example: java Main test.html");
            System.exit(1);
        }
        
        String inputFilePath = args[0];
        
        // Validate input file exists and has .html extension
        if (!inputFilePath.toLowerCase().endsWith(".html")) {
            System.out.println("Error: Input file must have .html extension");
            System.exit(1);
        }
        
        // Generate output file path
        String outputFilePath = FileUtils.getOutputFilePath(inputFilePath);
        
        try {
            // Create and run validator engine
            ValidatorEngine engine = new ValidatorEngine(inputFilePath, outputFilePath);
            engine.validate();
            
            // Report results
            int errorCount = engine.getErrorCollector().getErrorCount();
            System.out.println("Validation complete!");
            System.out.println("Errors found: " + errorCount);
            System.out.println("Report generated: " + outputFilePath);
            
            if (errorCount > 0) {
                System.out.println("\nErrors:");
                for (errors.Error error : engine.getErrorCollector().getErrorsSortedByLine()) {
                    System.out.println("  " + error.formatMessage());
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error reading or writing file: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
