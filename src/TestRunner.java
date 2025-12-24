import utils.FileUtils;
import java.io.File;
import java.io.IOException;

/**
 * Test runner for validating HTML files in the test directory.
 * Runs validation on all test files and reports results.
 * 
 * @author eduardo
 */
public class TestRunner {
    
    /**
     * Main method to run tests.
     * 
     * @param args Command line arguments (optional: test directory path)
     */
    public static void main(String[] args) {
        String testDir = args.length > 0 ? args[0] : "test";
        
        File testDirectory = new File(testDir);
        if (!testDirectory.exists() || !testDirectory.isDirectory()) {
            System.err.println("Test directory not found: " + testDir);
            System.exit(1);
        }
        
        File[] testFiles = testDirectory.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".html"));
        
        if (testFiles == null || testFiles.length == 0) {
            System.out.println("No HTML test files found in " + testDir);
            return;
        }
        
        System.out.println("Running validation tests on " + testFiles.length + " files...\n");
        
        int passed = 0;
        int failed = 0;
        
        for (File testFile : testFiles) {
            String inputPath = testFile.getAbsolutePath();
            String outputPath = FileUtils.getOutputFilePath(inputPath);
            
            System.out.println("Testing: " + testFile.getName());
            
            try {
                ValidatorEngine engine = new ValidatorEngine(inputPath, outputPath);
                engine.validate();
                
                int errorCount = engine.getErrorCollector().getErrorCount();
                System.out.println("  ✓ Completed - Errors found: " + errorCount);
                System.out.println("  Output: " + new File(outputPath).getName());
                
                if (errorCount > 0) {
                    System.out.println("  Errors:");
                    for (errors.Error error : engine.getErrorCollector().getErrorsSortedByLine()) {
                        System.out.println("    " + error.formatMessage());
                    }
                }
                
                passed++;
            } catch (StackOverflowError e) {
                System.err.println("  ✗ FAILED - StackOverflowError");
                e.printStackTrace();
                failed++;
            } catch (Exception e) {
                System.err.println("  ✗ FAILED - " + e.getMessage());
                e.printStackTrace();
                failed++;
            }
            
            System.out.println();
        }
        
        System.out.println("========================================");
        System.out.println("Test Summary:");
        System.out.println("  Passed: " + passed);
        System.out.println("  Failed: " + failed);
        System.out.println("  Total:  " + (passed + failed));
    }
}

