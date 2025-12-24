package ast.js;

import ast.Node;

/**
 * Represents a JavaScript assignment operation.
 * Can be simple (=) or compound (+=, -=, etc.).
 * 
 * @author eduardo
 */
public class AssignmentNode implements Node {
    private final IdentifierNode leftHandSide;
    private final String operator; // =, +=, -=, *=, /=
    private final Node rightHandSide;
    private final int lineNumber;
    private final int columnNumber;
    
    public AssignmentNode(IdentifierNode leftHandSide, String operator, Node rightHandSide,
                         int lineNumber, int columnNumber) {
        this.leftHandSide = leftHandSide;
        this.operator = operator;
        this.rightHandSide = rightHandSide;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    public IdentifierNode getLeftHandSide() {
        return leftHandSide;
    }
    
    public String getOperator() {
        return operator;
    }
    
    public Node getRightHandSide() {
        return rightHandSide;
    }
    
    @Override
    public int getLineNumber() {
        return lineNumber;
    }
    
    @Override
    public int getColumnNumber() {
        return columnNumber;
    }
}

