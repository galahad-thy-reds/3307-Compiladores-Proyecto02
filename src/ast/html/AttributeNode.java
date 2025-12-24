package ast.html;

import ast.Node;

/**
 * Represents an HTML attribute.
 * 
 * @author eduardo
 */
public class AttributeNode implements Node {
    private final String name;
    private final String value;
    private final int lineNumber;
    private final int columnNumber;
    
    public AttributeNode(String name, String value, int lineNumber, int columnNumber) {
        this.name = name;
        this.value = value;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    public String getName() {
        return name;
    }
    
    public String getValue() {
        return value;
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

