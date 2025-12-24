package ast.html;

import ast.Node;

/**
 * Represents text content within HTML tags.
 * 
 * @author eduardo
 */
public class TextNode implements Node {
    private final String text;
    private final int lineNumber;
    private final int columnNumber;
    
    public TextNode(String text, int lineNumber, int columnNumber) {
        this.text = text;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    public String getText() {
        return text;
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

