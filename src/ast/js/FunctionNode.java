package ast.js;

import ast.Node;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a JavaScript function declaration.
 * 
 * @author eduardo
 */
public class FunctionNode implements Node {
    private final String functionName;
    private final List<IdentifierNode> parameters;
    private final List<Node> bodyStatements;
    private final int lineNumber;
    private final int columnNumber;
    
    public FunctionNode(String functionName, int lineNumber, int columnNumber) {
        this.functionName = functionName;
        this.parameters = new ArrayList<>();
        this.bodyStatements = new ArrayList<>();
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    public String getFunctionName() {
        return functionName;
    }
    
    public void addParameter(IdentifierNode parameter) {
        parameters.add(parameter);
    }
    
    public List<IdentifierNode> getParameters() {
        return parameters;
    }
    
    public void addBodyStatement(Node statement) {
        bodyStatements.add(statement);
    }
    
    public List<Node> getBodyStatements() {
        return bodyStatements;
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

