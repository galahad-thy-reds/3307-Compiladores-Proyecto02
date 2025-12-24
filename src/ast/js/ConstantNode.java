package ast.js;

import ast.Node;

/**
 * Represents a JavaScript constant declaration (const).
 * 
 * @author eduardo
 */
public class ConstantNode implements Node {
    private final IdentifierNode identifier;
    private final Node value; // Required for const
    private final int lineNumber;
    private final int columnNumber;
    
    public ConstantNode(IdentifierNode identifier, Node value,
                        int lineNumber, int columnNumber) {
        this.identifier = identifier;
        this.value = value;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    public IdentifierNode getIdentifier() {
        return identifier;
    }
    
    public Node getValue() {
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

