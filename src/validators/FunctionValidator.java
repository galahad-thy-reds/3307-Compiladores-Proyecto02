package validators;

import ast.Node;
import ast.html.DocumentNode;
import ast.html.TagNode;
import ast.js.FunctionNode;
import ast.js.IdentifierNode;
import ast.js.ScriptNode;
import errors.ErrorCollector;
import utils.ReservedWords;

/**
 * Validates JavaScript function definitions (Requirement #5).
 * Checks function syntax, parameters, and body requirements.
 * 
 * @author eduardo
 */
public class FunctionValidator implements Validator {
    
    @Override
    public void validate(DocumentNode document, ErrorCollector errorCollector) {
        traverseDocument(document, errorCollector);
    }
    
    /**
     * Traverses the document to find all script nodes.
     */
    private void traverseDocument(Node node, ErrorCollector errorCollector) {
        if (node instanceof ScriptNode) {
            ScriptNode scriptNode = (ScriptNode) node;
            for (Node statement : scriptNode.getStatements()) {
                if (statement instanceof FunctionNode) {
                    validateFunction((FunctionNode) statement, errorCollector);
                }
            }
        } else if (node instanceof DocumentNode) {
            DocumentNode doc = (DocumentNode) node;
            for (TagNode tag : doc.getChildren()) {
                traverseTag(tag, errorCollector);
            }
        } else if (node instanceof TagNode) {
            traverseTag((TagNode) node, errorCollector);
        }
    }
    
    /**
     * Traverses HTML tags to find script nodes.
     */
    private void traverseTag(TagNode tag, ErrorCollector errorCollector) {
        for (Node child : tag.getChildren()) {
            if (child instanceof ScriptNode) {
                ScriptNode scriptNode = (ScriptNode) child;
                for (Node statement : scriptNode.getStatements()) {
                    if (statement instanceof FunctionNode) {
                        validateFunction((FunctionNode) statement, errorCollector);
                    }
                }
            } else if (child instanceof TagNode) {
                traverseTag((TagNode) child, errorCollector);
            }
        }
    }
    
    /**
     * Validates a function declaration according to Requirement #5 rules.
     */
    private void validateFunction(FunctionNode function, ErrorCollector errorCollector) {
        int lineNumber = function.getLineNumber();
        String functionName = function.getFunctionName();
        
        // Validate function name follows identifier rules
        if (functionName == null || functionName.isEmpty()) {
            errorCollector.addError(lineNumber, "Function name cannot be empty", "FUNCTION");
            return;
        }
        
        // Check identifier rules
        if (!isValidIdentifier(functionName)) {
            errorCollector.addError(lineNumber, 
                    String.format("Function name '%s' does not follow identifier rules", functionName), 
                    "FUNCTION");
        }
        
        // Check if reserved word
        if (ReservedWords.isReserved(functionName)) {
            errorCollector.addError(lineNumber, 
                    String.format("Function name '%s' is a JavaScript reserved word", functionName), 
                    "FUNCTION");
        }
        
        // Validate parameters
        for (IdentifierNode param : function.getParameters()) {
            String paramName = param.getName();
            if (!isValidIdentifier(paramName)) {
                errorCollector.addError(param.getLineNumber(), 
                        String.format("Function parameter '%s' does not follow identifier rules", paramName), 
                        "FUNCTION");
            }
            if (ReservedWords.isReserved(paramName)) {
                errorCollector.addError(param.getLineNumber(), 
                        String.format("Function parameter '%s' is a JavaScript reserved word", paramName), 
                        "FUNCTION");
            }
        }
        
        // Validate function body has at least one statement
        if (function.getBodyStatements().isEmpty()) {
            errorCollector.addError(lineNumber, 
                    "Function body must contain at least one statement", 
                    "FUNCTION");
        }
    }
    
    /**
     * Checks if a name is a valid identifier.
     */
    private boolean isValidIdentifier(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        
        char firstChar = name.charAt(0);
        if (!Character.isLetter(firstChar) && firstChar != '_' && 
            !Character.isUnicodeIdentifierStart(firstChar)) {
            return false;
        }
        
        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_' && 
                !Character.isUnicodeIdentifierPart(c)) {
                return false;
            }
        }
        
        return !name.contains(" ") && !name.contains("-") && 
               !name.contains("+") && !name.contains("*") && !name.contains("/");
    }
}

