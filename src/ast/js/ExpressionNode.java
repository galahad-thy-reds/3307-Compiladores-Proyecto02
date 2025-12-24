package ast.js;

import ast.Node;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a JavaScript expression.
 * Can contain operators, operands, and nested expressions.
 * 
 * @author eduardo
 */
public class ExpressionNode implements Node {
    private final List<Node> operands;
    private final List<String> operators;
    private final int lineNumber;
    private final int columnNumber;
    
    public ExpressionNode(int lineNumber, int columnNumber) {
        this.operands = new ArrayList<>();
        this.operators = new ArrayList<>();
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    public void addOperand(Node operand) {
        operands.add(operand);
    }
    
    public void addOperator(String operator) {
        operators.add(operator);
    }
    
    public List<Node> getOperands() {
        return operands;
    }
    
    public List<String> getOperators() {
        return operators;
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

