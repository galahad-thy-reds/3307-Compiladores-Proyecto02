package ast;

/**
 * Base interface for all AST nodes.
 * Every node in the Abstract Syntax Tree implements this interface.
 * 
 * @author eduardo
 */
public interface Node {
    /**
     * Gets the line number where this node appears in the source code.
     * 
     * @return Line number (1-indexed)
     */
    int getLineNumber();
    
    /**
     * Gets the column number where this node starts.
     * 
     * @return Column number (1-indexed)
     */
    int getColumnNumber();
}

