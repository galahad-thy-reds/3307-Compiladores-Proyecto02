package ast.js;

import ast.Node;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a JavaScript script block within HTML.
 * Contains all JavaScript statements within the script tag.
 * 
 * @author eduardo
 */
public class ScriptNode implements Node {
    private final List<Node> statements;
    private final int lineNumber;
    private final int columnNumber;
    
    public ScriptNode(int lineNumber, int columnNumber) {
        this.statements = new ArrayList<>();
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    public void addStatement(Node statement) {
        statements.add(statement);
    }
    
    public List<Node> getStatements() {
        return statements;
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

