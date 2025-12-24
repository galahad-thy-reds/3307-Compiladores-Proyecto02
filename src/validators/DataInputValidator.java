package validators;

import ast.Node;
import ast.html.DocumentNode;
import ast.html.TagNode;
import ast.js.CallNode;
import ast.js.IdentifierNode;
import ast.js.ScriptNode;
import errors.ErrorCollector;
import java.util.List;

/**
 * Validates JavaScript data input operations (Requirement #6).
 * Validates that getElementById calls reference existing HTML elements.
 * 
 * @author eduardo
 */
public class DataInputValidator implements Validator {
    private final List<String> htmlElementIds;
    
    /**
     * Creates a new data input validator.
     * 
     * @param htmlElementIds List of HTML element IDs found in the document
     */
    public DataInputValidator(List<String> htmlElementIds) {
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
     * Validates statements for getElementById calls.
     */
    private void validateStatement(Node statement, ErrorCollector errorCollector) {
        if (statement instanceof ast.js.AssignmentNode) {
            ast.js.AssignmentNode assign = (ast.js.AssignmentNode) statement;
            validateNode(assign.getRightHandSide(), errorCollector);
        } else if (statement instanceof ast.js.ConstantNode) {
            ast.js.ConstantNode constNode = (ast.js.ConstantNode) statement;
            validateNode(constNode.getValue(), errorCollector);
        } else if (statement instanceof ast.js.VariableNode) {
            ast.js.VariableNode varNode = (ast.js.VariableNode) statement;
            if (varNode.getInitialValue() != null) {
                validateNode(varNode.getInitialValue(), errorCollector);
            }
        } else if (statement instanceof ast.js.FunctionNode) {
            ast.js.FunctionNode funcNode = (ast.js.FunctionNode) statement;
            for (Node bodyStmt : funcNode.getBodyStatements()) {
                validateStatement(bodyStmt, errorCollector);
            }
        }
    }
    
    /**
     * Validates a node for getElementById calls.
     */
    private void validateNode(Node node, ErrorCollector errorCollector) {
        validateNode(node, errorCollector, new java.util.HashSet<>());
    }
    
    /**
     * Validates a node for getElementById calls with cycle detection.
     */
    private void validateNode(Node node, ErrorCollector errorCollector, java.util.Set<Node> visited) {
        if (node == null || visited.contains(node)) {
            return; // Prevent infinite recursion
        }
        visited.add(node);
        
        if (node instanceof CallNode) {
            CallNode call = (CallNode) node;
            validateCall(call, errorCollector);
        } else if (node instanceof ast.js.ExpressionNode) {
            ast.js.ExpressionNode expr = (ast.js.ExpressionNode) node;
            for (Node operand : expr.getOperands()) {
                validateNode(operand, errorCollector, visited);
            }
        }
    }
    
    /**
     * Validates a method call for getElementById pattern.
     */
    private void validateCall(CallNode call, ErrorCollector errorCollector) {
        Node callee = call.getCallee();
        
        if (callee instanceof IdentifierNode) {
            IdentifierNode calleeId = (IdentifierNode) callee;
            String calleeName = calleeId.getName();
            
            // Check if it's document.getElementById
            if (calleeName.contains("getElementById") || calleeName.endsWith(".getElementById")) {
                // Extract the ID from arguments
                if (!call.getArguments().isEmpty()) {
                    Node arg = call.getArguments().get(0);
                    String elementId = extractIdFromArgument(arg);
                    
                    if (elementId != null) {
                        // Remove quotes
                        elementId = elementId.replaceAll("^[\"']|[\"']$", "");
                        
                        // Check if ID exists in HTML
                        if (!htmlElementIds.contains(elementId)) {
                            errorCollector.addError(call.getLineNumber(), 
                                    String.format("getElementById references non-existent element ID: '%s'", elementId), 
                                    "DATA_INPUT");
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Extracts the ID string from an argument node.
     */
    private String extractIdFromArgument(Node arg) {
        if (arg instanceof IdentifierNode) {
            IdentifierNode id = (IdentifierNode) arg;
            String value = id.getName();
            // Check if it's a string literal
            if ((value.startsWith("\"") && value.endsWith("\"")) || 
                (value.startsWith("'") && value.endsWith("'"))) {
                return value;
            }
        }
        return null;
    }
}

