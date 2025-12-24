package validators;

import ast.Node;
import ast.html.DocumentNode;
import ast.js.IdentifierNode;
import ast.js.ScriptNode;
import ast.js.VariableNode;
import errors.ErrorCollector;
import utils.ReservedWords;

/**
 * Validates JavaScript identifiers (Requirement #2).
 * Checks that identifiers follow naming rules and are not reserved words.
 * 
 * @author eduardo
 */
public class IdentifierValidator implements Validator {
    
    @Override
    public void validate(DocumentNode document, ErrorCollector errorCollector) {
        // Traverse all script nodes
        traverseDocument(document, errorCollector);
    }
    
    /**
     * Traverses the document to find all script nodes and validate identifiers.
     */
    private void traverseDocument(Node node, ErrorCollector errorCollector) {
        if (node instanceof ScriptNode) {
            ScriptNode scriptNode = (ScriptNode) node;
            for (Node statement : scriptNode.getStatements()) {
                validateStatement(statement, errorCollector);
            }
        } else if (node instanceof DocumentNode) {
            DocumentNode doc = (DocumentNode) node;
            for (ast.html.TagNode tag : doc.getChildren()) {
                traverseTag(tag, errorCollector);
            }
        } else if (node instanceof ast.html.TagNode) {
            traverseTag((ast.html.TagNode) node, errorCollector);
        }
    }
    
    /**
     * Traverses HTML tags to find script nodes.
     */
    private void traverseTag(ast.html.TagNode tag, ErrorCollector errorCollector) {
        for (Node child : tag.getChildren()) {
            if (child instanceof ScriptNode) {
                ScriptNode scriptNode = (ScriptNode) child;
                for (Node statement : scriptNode.getStatements()) {
                    validateStatement(statement, errorCollector);
                }
            } else if (child instanceof ast.html.TagNode) {
                traverseTag((ast.html.TagNode) child, errorCollector);
            }
        }
    }
    
    /**
     * Validates identifiers in a statement.
     */
    private void validateStatement(Node statement, ErrorCollector errorCollector) {
        if (statement instanceof VariableNode) {
            VariableNode varNode = (VariableNode) statement;
            validateIdentifier(varNode.getIdentifier(), errorCollector);
        } else if (statement instanceof ast.js.FunctionNode) {
            ast.js.FunctionNode funcNode = (ast.js.FunctionNode) statement;
            // Validate function name
            IdentifierNode funcName = new IdentifierNode(funcNode.getFunctionName(), 
                    funcNode.getLineNumber(), funcNode.getColumnNumber());
            validateIdentifier(funcName, errorCollector);
            // Validate parameters
            for (IdentifierNode param : funcNode.getParameters()) {
                validateIdentifier(param, errorCollector);
            }
            // Validate body statements
            for (Node bodyStmt : funcNode.getBodyStatements()) {
                validateStatement(bodyStmt, errorCollector);
            }
        }
    }
    
    /**
     * Validates a single identifier according to Requirement #2 rules.
     */
    private void validateIdentifier(IdentifierNode identifier, ErrorCollector errorCollector) {
        String name = identifier.getName();
        int lineNumber = identifier.getLineNumber();
        
        if (name == null || name.isEmpty()) {
            errorCollector.addError(lineNumber, "Identifier cannot be empty", "IDENTIFIER");
            return;
        }
        
        // Check if starts with valid character
        char firstChar = name.charAt(0);
        if (!Character.isLetter(firstChar) && firstChar != '_' && 
            !Character.isUnicodeIdentifierStart(firstChar)) {
            errorCollector.addError(lineNumber, 
                    String.format("Identifier '%s' must start with a letter, underscore, or Unicode letter", name), 
                    "IDENTIFIER");
        }
        
        // Check remaining characters
        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_' && 
                !Character.isUnicodeIdentifierPart(c)) {
                errorCollector.addError(lineNumber, 
                        String.format("Identifier '%s' contains invalid character '%c'", name, c), 
                        "IDENTIFIER");
                break;
            }
        }
        
        // Check for spaces
        if (name.contains(" ")) {
            errorCollector.addError(lineNumber, 
                    String.format("Identifier '%s' cannot contain spaces", name), 
                    "IDENTIFIER");
        }
        
        // Check for special characters
        String specialChars = "-+*/";
        for (char c : specialChars.toCharArray()) {
            if (name.indexOf(c) >= 0) {
                errorCollector.addError(lineNumber, 
                        String.format("Identifier '%s' cannot contain special character '%c'", name, c), 
                        "IDENTIFIER");
                break;
            }
        }
        
        // Check if reserved word
        if (ReservedWords.isReserved(name)) {
            errorCollector.addError(lineNumber, 
                    String.format("Identifier '%s' is a JavaScript reserved word and cannot be used", name), 
                    "IDENTIFIER");
        }
    }
}

