package ast.js;

import ast.Node;

/**
 * Represents a JavaScript variable declaration (let or var).
 * 
 * @author eduardo
 */
public class VariableNode implements Node {
    private final String keyword; // "let" or "var"
    private final IdentifierNode identifier;
    private final Node initialValue; // Can be null if uninitialized
    private final int lineNumber;
    private final int columnNumber;
    
    public VariableNode(String keyword, IdentifierNode identifier, Node initialValue,
                       int lineNumber, int columnNumber) {
        this.keyword = keyword;
        this.identifier = identifier;
        this.initialValue = initialValue;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public IdentifierNode getIdentifier() {
        return identifier;
    }
    
    public Node getInitialValue() {
        return initialValue;
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

