package ast.js;

import ast.Node;

/**
 * Represents a JavaScript identifier (variable name, function name, etc.).
 * 
 * @author eduardo
 */
public class IdentifierNode implements Node {
    private final String name;
    private final int lineNumber;
    private final int columnNumber;
    
    public IdentifierNode(String name, int lineNumber, int columnNumber) {
        this.name = name;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    public String getName() {
        return name;
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

