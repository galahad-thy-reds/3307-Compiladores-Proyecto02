package validators;

import ast.html.DocumentNode;
import errors.ErrorCollector;

/**
 * Base interface for all validators.
 * Each validator traverses the AST and reports errors to the ErrorCollector.
 * 
 * @author eduardo
 */
public interface Validator {
    /**
     * Validates the document and collects errors.
     * 
     * @param document The root document node of the AST
     * @param errorCollector The error collector to add errors to
     */
    void validate(DocumentNode document, ErrorCollector errorCollector);
}

