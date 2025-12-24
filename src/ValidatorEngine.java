import ast.html.DocumentNode;
import errors.ErrorCollector;
import errors.ErrorReporter;
import parser.Parser;
import validators.AssignmentValidator;
import validators.ConstantValidator;
import validators.DataInputValidator;
import validators.DataOutputValidator;
import validators.FunctionValidator;
import validators.HtmlElementValidator;
import validators.HtmlStructureValidator;
import validators.IdentifierValidator;
import validators.Validator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main orchestrator for the validation system.
 * Coordinates lexer, parser, validators, and error reporter.
 * 
 * @author eduardo
 */
public class ValidatorEngine {
    private final String inputFilePath;
    private final String outputFilePath;
    private final ErrorCollector errorCollector;
    private DocumentNode documentNode;
    private Parser parser;
    
    /**
     * Creates a new validator engine.
     * 
     * @param inputFilePath Path to the input HTML file
     * @param outputFilePath Path to the output .txt file
     */
    public ValidatorEngine(String inputFilePath, String outputFilePath) {
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
        this.errorCollector = new ErrorCollector();
    }
    
    /**
     * Runs the complete validation process.
     * 
     * @throws IOException If file I/O fails
     */
    public void validate() throws IOException {
        // Step 1: Read input file
        String inputContent = utils.FileUtils.readFile(inputFilePath);
        
        // Step 2: Parse and build AST
        parser = new Parser(inputContent);
        documentNode = parser.parse();
        
        // Step 3: Collect HTML element IDs
        HtmlElementValidator htmlElementValidator = new HtmlElementValidator();
        List<String> htmlElementIds = htmlElementValidator.collectElementIds(documentNode);
        
        // Step 4: Run all validators
        List<Validator> validators = new ArrayList<>();
        
        // Requirement #2: Identifier Validator
        validators.add(new IdentifierValidator());
        
        // Requirement #3: Constant Validator
        validators.add(new ConstantValidator());
        
        // Requirement #4: Assignment Validator
        validators.add(new AssignmentValidator());
        
        // Requirement #5: Function Validator
        validators.add(new FunctionValidator());
        
        // Requirement #6: Data Input Validator (needs HTML element IDs)
        validators.add(new DataInputValidator(htmlElementIds));
        
        // Requirement #7: Data Output Validator (needs HTML element IDs)
        validators.add(new DataOutputValidator(htmlElementIds));
        
        // Requirement #8: HTML Structure Validator
        validators.add(new HtmlStructureValidator());
        
        // Run all validators
        for (Validator validator : validators) {
            validator.validate(documentNode, errorCollector);
        }
        
        // Step 5: Generate error report (Requirement #1)
        ErrorReporter reporter = new ErrorReporter(inputFilePath, outputFilePath, errorCollector);
        reporter.generateReport();
    }
    
    /**
     * Gets the error collector.
     * 
     * @return Error collector
     */
    public ErrorCollector getErrorCollector() {
        return errorCollector;
    }
    
    /**
     * Gets the parsed document node.
     * 
     * @return Document node
     */
    public DocumentNode getDocumentNode() {
        return documentNode;
    }
}

