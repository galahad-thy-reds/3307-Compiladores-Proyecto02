package lexer;

/**
 * Represents a token produced by the lexer.
 * Each token has a type, value, and line number for error reporting.
 * 
 * @author eduardo
 */
public class Token {
    private final TokenType type;
    private final String value;
    private final int lineNumber;
    private final int columnNumber;
    
    /**
     * Creates a new token.
     * 
     * @param type The type of the token
     * @param value The actual text value of the token
     * @param lineNumber The line number where this token appears (1-indexed)
     * @param columnNumber The column number where this token starts (1-indexed)
     */
    public Token(TokenType type, String value, int lineNumber, int columnNumber) {
        this.type = type;
        this.value = value;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    public TokenType getType() {
        return type;
    }
    
    public String getValue() {
        return value;
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    public int getColumnNumber() {
        return columnNumber;
    }
    
    @Override
    public String toString() {
        return String.format("Token(%s, '%s', line %d, col %d)", 
                type, value, lineNumber, columnNumber);
    }
}

