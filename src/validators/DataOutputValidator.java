package validators;

import ast.Node;
import ast.html.DocumentNode;
import ast.html.TagNode;
import ast.js.AssignmentNode;
import ast.js.IdentifierNode;
import ast.js.ScriptNode;
import errors.ErrorCollector;
import java.util.List;

/**
 * Validates JavaScript data output operations (Requirement #7).
 * Validates that innerHTML assignments reference existing HTML elements.
 * 
 * @author eduardo
 */
public class DataOutputValidator implements Validator {
    private final List<String> htmlElementIds;
    
    /**
     * Creates a new data output validator.
     * 
     * @param htmlElementIds List of HTML element IDs found in the document
     */
    public DataOutputValidator(List<String> htmlElementIds) {
        this.htmlElementIds = htmlElementIds;
    }
    
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
                validateStatement(statement, errorCollector);
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
                    validateStatement(statement, errorCollector);
                }
            } else if (child instanceof TagNode) {
                traverseTag((TagNode) child, errorCollector);
            }
        }
    }
    
    /**
     * Validates statements for innerHTML assignments.
     */
    private void validateStatement(Node statement, ErrorCollector errorCollector) {
        validateStatement(statement, errorCollector, new java.util.HashSet<>());
    }
    
    /**
     * Validates statements for innerHTML assignments with cycle detection.
     */
    private void validateStatement(Node statement, ErrorCollector errorCollector, java.util.Set<Node> visited) {
        if (statement == null || visited.contains(statement)) {
            return; // Prevent infinite recursion
        }
        visited.add(statement);
        
        if (statement instanceof AssignmentNode) {
            AssignmentNode assign = (AssignmentNode) statement;
            validateAssignment(assign, errorCollector);
        } else if (statement instanceof ast.js.FunctionNode) {
            ast.js.FunctionNode funcNode = (ast.js.FunctionNode) statement;
            for (Node bodyStmt : funcNode.getBodyStatements()) {
                validateStatement(bodyStmt, errorCollector, visited);
            }
        }
    }
    
    /**
     * Validates an assignment for innerHTML pattern.
     */
    private void validateAssignment(AssignmentNode assignment, ErrorCollector errorCollector) {
        Node lhs = assignment.getLeftHandSide();
        
        if (lhs instanceof IdentifierNode) {
            IdentifierNode lhsId = (IdentifierNode) lhs;
            String lhsName = lhsId.getName();
            
            // Check if it's document.getElementById("id").innerHTML
            if (lhsName.contains("innerHTML") || lhsName.endsWith(".innerHTML")) {
                // Extract the ID from the getElementById call
                String elementId = extractIdFromInnerHTML(lhsName);
                
                if (elementId != null) {
                    // Remove quotes
                    elementId = elementId.replaceAll("^[\"']|[\"']$", "");
                    
                    // Check if ID exists in HTML
                    if (!htmlElementIds.contains(elementId)) {
                        errorCollector.addError(assignment.getLineNumber(), 
                                String.format("innerHTML assignment references non-existent element ID: '%s'", elementId), 
                                "DATA_OUTPUT");
                    }
                }
            }
        }
    }
    
    /**
     * Extracts the element ID from an innerHTML assignment pattern.
     * Pattern: document.getElementById("id").innerHTML
     */
    private String extractIdFromInnerHTML(String expression) {
        // Look for getElementById("...") pattern
        int getElementByIdIndex = expression.indexOf("getElementById");
        if (getElementByIdIndex >= 0) {
            int startQuote = expression.indexOf("\"", getElementByIdIndex);
            if (startQuote < 0) {
                startQuote = expression.indexOf("'", getElementByIdIndex);
            }
            if (startQuote >= 0) {
                char quoteChar = expression.charAt(startQuote);
                int endQuote = expression.indexOf(quoteChar, startQuote + 1);
                if (endQuote > startQuote) {
                    return expression.substring(startQuote, endQuote + 1);
                }
            }
        }
        return null;
    }
}

