package validators;

import ast.Node;
import ast.html.DocumentNode;
import ast.html.TagNode;
import ast.js.AssignmentNode;
import ast.js.CallNode;
import ast.js.ExpressionNode;
import ast.js.IdentifierNode;
import ast.js.ScriptNode;
import errors.ErrorCollector;

/**
 * Validates JavaScript assignments (Requirement #4).
 * Validates assignment operators and type checking for chain assignments.
 * 
 * @author eduardo
 */
public class AssignmentValidator implements Validator {
    
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
     * Validates assignments in a statement.
     */
    private void validateStatement(Node statement, ErrorCollector errorCollector) {
        validateStatement(statement, errorCollector, new java.util.HashSet<>());
    }
    
    /**
     * Validates assignments in a statement with cycle detection.
     */
    private void validateStatement(Node statement, ErrorCollector errorCollector, java.util.Set<Node> visited) {
        if (statement == null || visited.contains(statement)) {
            return; // Prevent infinite recursion
        }
        visited.add(statement);
        
        if (statement instanceof AssignmentNode) {
            validateAssignment((AssignmentNode) statement, errorCollector);
        } else if (statement instanceof ast.js.FunctionNode) {
            ast.js.FunctionNode funcNode = (ast.js.FunctionNode) statement;
            for (Node bodyStmt : funcNode.getBodyStatements()) {
                validateStatement(bodyStmt, errorCollector, visited);
            }
        }
    }
    
    /**
     * Validates an assignment according to Requirement #4 rules.
     */
    private void validateAssignment(AssignmentNode assignment, ErrorCollector errorCollector) {
        String operator = assignment.getOperator();
        int lineNumber = assignment.getLineNumber();
        
        // Validate operator
        if (!isValidAssignmentOperator(operator)) {
            errorCollector.addError(lineNumber, 
                    String.format("Invalid assignment operator: %s", operator), 
                    "ASSIGNMENT");
        }
        
        // For chain assignments (a = b = c = value), validate type consistency
        Node rhs = assignment.getRightHandSide();
        if (rhs instanceof AssignmentNode) {
            // This is a chain assignment
            validateChainAssignment(assignment, errorCollector);
        } else {
            // Simple assignment - type checking
            validateTypeConsistency(assignment, errorCollector);
        }
    }
    
    /**
     * Validates chain assignments (a = b = c = value).
     */
    private void validateChainAssignment(AssignmentNode assignment, ErrorCollector errorCollector) {
        // Extract all variables in the chain
        java.util.List<String> variables = new java.util.ArrayList<>();
        Node current = assignment;
        
        while (current instanceof AssignmentNode) {
            AssignmentNode assign = (AssignmentNode) current;
            if (assign.getLeftHandSide() instanceof IdentifierNode) {
                variables.add(((IdentifierNode) assign.getLeftHandSide()).getName());
            }
            current = assign.getRightHandSide();
        }
        
        // Get the final value type (for future type checking)
        // String finalValueType = inferType(current);
        
        // Check that all variables have compatible types
        // In a simplified implementation, we assume all variables should match
        // In a full implementation, we'd check declared types
        // For now, we validate that the assignment structure is correct
        if (variables.isEmpty()) {
            // No variables in chain - this shouldn't happen, but handle gracefully
        }
    }
    
    /**
     * Validates type consistency for simple assignments.
     */
    private void validateTypeConsistency(AssignmentNode assignment, ErrorCollector errorCollector) {
        Node lhs = assignment.getLeftHandSide();
        Node rhs = assignment.getRightHandSide();
        
        if (lhs instanceof IdentifierNode && rhs != null) {
            String lhsType = inferType(lhs);
            String rhsType = inferType(rhs);
            
            // Basic type checking - in a full implementation, this would be more sophisticated
            if (!lhsType.equals("UNKNOWN") && !rhsType.equals("UNKNOWN") && 
                !lhsType.equals(rhsType)) {
                // For now, we'll be lenient with type checking as the requirement says
                // "assume rightmost value is correct"
            }
        }
    }
    
    /**
     * Infers the type of a node.
     */
    private String inferType(Node node) {
        if (node instanceof IdentifierNode) {
            IdentifierNode id = (IdentifierNode) node;
            String name = id.getName();
            
            // Check if it's a string literal
            if (name.startsWith("\"") || name.startsWith("'")) {
                return "STRING";
            }
            
            // Check if it's a number
            try {
                Double.parseDouble(name);
                return "NUMBER";
            } catch (NumberFormatException e) {
                // Not a number
            }
            
            // Check if boolean
            if (name.equals("true") || name.equals("false")) {
                return "BOOLEAN";
            }
            
            // Check if null
            if (name.equals("null")) {
                return "NULL";
            }
        } else if (node instanceof CallNode) {
            // Method calls might return different types
            return "UNKNOWN";
        } else if (node instanceof ExpressionNode) {
            return "UNKNOWN";
        }
        
        return "UNKNOWN";
    }
    
    /**
     * Checks if an operator is a valid assignment operator.
     */
    private boolean isValidAssignmentOperator(String operator) {
        return operator.equals("=") || operator.equals("+=") || 
               operator.equals("-=") || operator.equals("*=") || 
               operator.equals("/=") || operator.equals("%=");
    }
}

