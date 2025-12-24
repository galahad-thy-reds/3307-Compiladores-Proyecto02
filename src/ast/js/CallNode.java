package ast.js;

import ast.Node;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a JavaScript function or method call.
 * e.g., document.getElementById("id")
 * 
 * @author eduardo
 */
public class CallNode implements Node {
    private final Node callee; // What's being called
    private final List<Node> arguments;
    private final int lineNumber;
    private final int columnNumber;
    
    public CallNode(Node callee, int lineNumber, int columnNumber) {
        this.callee = callee;
        this.arguments = new ArrayList<>();
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    public Node getCallee() {
        return callee;
    }
    
    public void addArgument(Node argument) {
        arguments.add(argument);
    }
    
    public List<Node> getArguments() {
        return arguments;
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

