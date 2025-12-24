package lexer;

import java.util.ArrayList;
import java.util.List;

/**
 * Lexer for tokenizing HTML and JavaScript code.
 * Handles both HTML tags and embedded JavaScript within script tags.
 * 
 * @author eduardo
 */
public class Lexer {
    private final String input;
    private int position;
    private int lineNumber;
    private int columnNumber;
    private boolean inScriptTag;
    private boolean inString;
    private char stringDelimiter;
    
    // JavaScript reserved words
    private static final String[] JS_KEYWORDS = {
        "let", "var", "const", "function", "if", "else", "for", "while",
        "do", "switch", "case", "break", "continue", "return", "try",
        "catch", "finally", "throw", "new", "this", "typeof", "instanceof",
        "true", "false", "null", "undefined", "void", "delete", "in", "of",
        "class", "extends", "super", "static", "async", "await", "yield",
        "import", "export", "default", "from", "as", "with", "debugger"
    };
    
    /**
     * Creates a new lexer for the given input string.
     * 
     * @param input The HTML/JavaScript code to tokenize
     */
    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.lineNumber = 1;
        this.columnNumber = 1;
        this.inScriptTag = false;
        this.inString = false;
    }
    
    /**
     * Tokenizes the entire input and returns a list of tokens.
     * 
     * @return List of tokens
     */
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token token;
        
        while ((token = nextToken()) != null && token.getType() != TokenType.EOF) {
            tokens.add(token);
        }
        
        // Add EOF token
        tokens.add(new Token(TokenType.EOF, "", lineNumber, columnNumber));
        return tokens;
    }
    
    /**
     * Gets the next token from the input.
     * 
     * @return The next token, or null if end of input
     */
    private Token nextToken() {
        if (position >= input.length()) {
            return new Token(TokenType.EOF, "", lineNumber, columnNumber);
        }
        
        skipWhitespace();
        
        if (position >= input.length()) {
            return new Token(TokenType.EOF, "", lineNumber, columnNumber);
        }
        
        char current = input.charAt(position);
        
        // Check if we're entering or leaving a script tag
        if (!inScriptTag && current == '<') {
            String peek = peekAhead(9);
            if (peek.toLowerCase().startsWith("<script")) {
                inScriptTag = true;
                return parseScriptTag(true);
            }
        }
        
        if (inScriptTag && current == '<' && peekAhead(2).startsWith("</")) {
            String peek = peekAhead(10);
            if (peek.toLowerCase().startsWith("</script>")) {
                inScriptTag = false;
                return parseScriptTag(false);
            }
        }
        
        if (inScriptTag) {
            return parseJavaScript();
        } else {
            return parseHTML();
        }
    }
    
    /**
     * Parses HTML tokens (tags, attributes, text).
     */
    private Token parseHTML() {
        char current = input.charAt(position);
        int startPos = position;
        int startLine = lineNumber;
        int startCol = columnNumber;
        
        if (current == '<') {
            // Check for DOCTYPE
            String peek = peekAhead(15);
            if (peek.toUpperCase().startsWith("<!DOCTYPE")) {
                return parseDOCTYPE();
            }
            
            // Check for closing tag
            if (peekAhead(2).startsWith("</")) {
                return parseClosingTag();
            }
            
            // Opening tag
            return parseOpeningTag();
        } else {
            // HTML text content
            return parseHTMLText();
        }
    }
    
    /**
     * Parses JavaScript tokens.
     */
    private Token parseJavaScript() {
        char current = input.charAt(position);
        int startPos = position;
        int startLine = lineNumber;
        int startCol = columnNumber;
        
        // String literals
        if (current == '"' || current == '\'') {
            return parseString();
        }
        
        // Numbers
        if (Character.isDigit(current)) {
            return parseNumber();
        }
        
        // Identifiers and keywords
        if (Character.isLetter(current) || current == '_' || Character.isUnicodeIdentifierStart(current)) {
            return parseIdentifierOrKeyword();
        }
        
        // Operators and punctuation
        if (isOperator(current)) {
            return parseOperator();
        }
        
        // Brackets and braces
        switch (current) {
            case '(':
                advance();
                return new Token(TokenType.LEFT_PAREN, "(", startLine, startCol);
            case ')':
                advance();
                return new Token(TokenType.RIGHT_PAREN, ")", startLine, startCol);
            case '{':
                advance();
                return new Token(TokenType.LEFT_BRACE, "{", startLine, startCol);
            case '}':
                advance();
                return new Token(TokenType.RIGHT_BRACE, "}", startLine, startCol);
            case '[':
                advance();
                return new Token(TokenType.LEFT_BRACKET, "[", startLine, startCol);
            case ']':
                advance();
                return new Token(TokenType.RIGHT_BRACKET, "]", startLine, startCol);
            case ';':
                advance();
                return new Token(TokenType.JS_PUNCTUATION, ";", startLine, startCol);
            case ',':
                advance();
                return new Token(TokenType.JS_PUNCTUATION, ",", startLine, startCol);
            case '.':
                advance();
                return new Token(TokenType.JS_PUNCTUATION, ".", startLine, startCol);
            case ':':
                advance();
                return new Token(TokenType.JS_PUNCTUATION, ":", startLine, startCol);
            case '?':
                advance();
                return new Token(TokenType.JS_PUNCTUATION, "?", startLine, startCol);
        }
        
        // Comments
        if (current == '/' && position + 1 < input.length()) {
            char next = input.charAt(position + 1);
            if (next == '/') {
                return parseLineComment();
            } else if (next == '*') {
                return parseBlockComment();
            }
        }
        
        // Unknown character - advance and continue
        advance();
        return new Token(TokenType.JS_PUNCTUATION, String.valueOf(current), startLine, startCol);
    }
    
    private Token parseDOCTYPE() {
        int startLine = lineNumber;
        int startCol = columnNumber;
        StringBuilder sb = new StringBuilder();
        
        while (position < input.length() && peekAhead(1).charAt(0) != '>') {
            sb.append(input.charAt(position));
            advance();
        }
        if (position < input.length()) {
            sb.append(input.charAt(position));
            advance();
        }
        
        String doctype = sb.toString();
        return new Token(TokenType.HTML_DOCTYPE, doctype, startLine, startCol);
    }
    
    private Token parseOpeningTag() {
        int startLine = lineNumber;
        int startCol = columnNumber;
        StringBuilder sb = new StringBuilder();
        
        // Read until >, but handle multiple tags on the same line
        // For tags like <html><head>, we need to read only until the first >
        boolean inQuotes = false;
        char quoteChar = 0;
        
        while (position < input.length()) {
            char c = input.charAt(position);
            sb.append(c);
            
            // Track quotes to avoid splitting on > inside attribute values
            if ((c == '"' || c == '\'') && quoteChar == 0) {
                quoteChar = c;
                inQuotes = true;
            } else if (c == quoteChar && inQuotes) {
                quoteChar = 0;
                inQuotes = false;
            }
            
            advance();
            
            // Stop at > only if we're not inside quotes
            if (c == '>' && !inQuotes) {
                break;
            }
        }
        
        return new Token(TokenType.HTML_TAG_OPEN, sb.toString(), startLine, startCol);
    }
    
    private Token parseClosingTag() {
        int startLine = lineNumber;
        int startCol = columnNumber;
        StringBuilder sb = new StringBuilder();
        
        // Read </...>
        while (position < input.length()) {
            char c = input.charAt(position);
            sb.append(c);
            advance();
            if (c == '>') {
                break;
            }
        }
        
        return new Token(TokenType.HTML_TAG_CLOSE, sb.toString(), startLine, startCol);
    }
    
    private Token parseHTMLText() {
        int startLine = lineNumber;
        int startCol = columnNumber;
        StringBuilder sb = new StringBuilder();
        
        while (position < input.length()) {
            char c = input.charAt(position);
            if (c == '<') {
                break;
            }
            sb.append(c);
            advance();
        }
        
        String text = sb.toString().trim();
        if (text.isEmpty()) {
            return nextToken(); // Skip empty text
        }
        
        return new Token(TokenType.HTML_TEXT, text, startLine, startCol);
    }
    
    private Token parseScriptTag(boolean isOpen) {
        int startLine = lineNumber;
        int startCol = columnNumber;
        StringBuilder sb = new StringBuilder();
        
        if (isOpen) {
            // Read <script...>
            while (position < input.length()) {
                char c = input.charAt(position);
                sb.append(c);
                advance();
                if (c == '>') {
                    break;
                }
            }
            return new Token(TokenType.SCRIPT_OPEN, sb.toString(), startLine, startCol);
        } else {
            // Read </script>
            while (position < input.length()) {
                char c = input.charAt(position);
                sb.append(c);
                advance();
                if (c == '>') {
                    break;
                }
            }
            return new Token(TokenType.SCRIPT_CLOSE, sb.toString(), startLine, startCol);
        }
    }
    
    private Token parseString() {
        int startLine = lineNumber;
        int startCol = columnNumber;
        char delimiter = input.charAt(position);
        StringBuilder sb = new StringBuilder();
        sb.append(delimiter);
        advance();
        
        boolean escaped = false;
        while (position < input.length()) {
            char c = input.charAt(position);
            sb.append(c);
            
            if (escaped) {
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == delimiter) {
                advance();
                break;
            }
            
            advance();
        }
        
        return new Token(TokenType.JS_LITERAL_STRING, sb.toString(), startLine, startCol);
    }
    
    private Token parseNumber() {
        int startLine = lineNumber;
        int startCol = columnNumber;
        StringBuilder sb = new StringBuilder();
        
        while (position < input.length()) {
            char c = input.charAt(position);
            if (Character.isDigit(c) || c == '.' || c == 'e' || c == 'E' || c == '+' || c == '-') {
                sb.append(c);
                advance();
            } else {
                break;
            }
        }
        
        return new Token(TokenType.JS_LITERAL_NUMBER, sb.toString(), startLine, startCol);
    }
    
    private Token parseIdentifierOrKeyword() {
        int startLine = lineNumber;
        int startCol = columnNumber;
        StringBuilder sb = new StringBuilder();
        
        while (position < input.length()) {
            char c = input.charAt(position);
            if (Character.isLetterOrDigit(c) || c == '_' || Character.isUnicodeIdentifierPart(c)) {
                sb.append(c);
                advance();
            } else {
                break;
            }
        }
        
        String value = sb.toString();
        
        // Check if it's a keyword
        for (String keyword : JS_KEYWORDS) {
            if (value.equals(keyword)) {
                return new Token(TokenType.JS_KEYWORD, value, startLine, startCol);
            }
        }
        
        // Check for boolean/null literals
        if (value.equals("true") || value.equals("false")) {
            return new Token(TokenType.JS_LITERAL_BOOLEAN, value, startLine, startCol);
        }
        if (value.equals("null")) {
            return new Token(TokenType.JS_LITERAL_NULL, value, startLine, startCol);
        }
        
        return new Token(TokenType.JS_IDENTIFIER, value, startLine, startCol);
    }
    
    private Token parseOperator() {
        int startLine = lineNumber;
        int startCol = columnNumber;
        char current = input.charAt(position);
        
        // Multi-character operators
        if (position + 1 < input.length()) {
            String twoChar = input.substring(position, position + 2);
            if (twoChar.equals("==") || twoChar.equals("!=") || twoChar.equals("<=") ||
                twoChar.equals(">=") || twoChar.equals("++") || twoChar.equals("--") ||
                twoChar.equals("+=") || twoChar.equals("-=") || twoChar.equals("*=") ||
                twoChar.equals("/=") || twoChar.equals("===") || twoChar.equals("!==") ||
                twoChar.equals("&&") || twoChar.equals("||")) {
                advance(2);
                return new Token(TokenType.JS_OPERATOR, twoChar, startLine, startCol);
            }
        }
        
        // Single character operators
        advance();
        return new Token(TokenType.JS_OPERATOR, String.valueOf(current), startLine, startCol);
    }
    
    private Token parseLineComment() {
        int startLine = lineNumber;
        int startCol = columnNumber;
        StringBuilder sb = new StringBuilder();
        
        while (position < input.length()) {
            char c = input.charAt(position);
            sb.append(c);
            advance();
            if (c == '\n') {
                break;
            }
        }
        
        return new Token(TokenType.COMMENT, sb.toString(), startLine, startCol);
    }
    
    private Token parseBlockComment() {
        int startLine = lineNumber;
        int startCol = columnNumber;
        StringBuilder sb = new StringBuilder();
        
        sb.append(input.charAt(position)); // /
        advance();
        sb.append(input.charAt(position)); // *
        advance();
        
        while (position < input.length()) {
            char c = input.charAt(position);
            sb.append(c);
            advance();
            if (c == '*' && position < input.length() && input.charAt(position) == '/') {
                sb.append(input.charAt(position));
                advance();
                break;
            }
        }
        
        return new Token(TokenType.COMMENT, sb.toString(), startLine, startCol);
    }
    
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '=' ||
               c == '!' || c == '<' || c == '>' || c == '&' || c == '|' ||
               c == '%' || c == '^';
    }
    
    private void skipWhitespace() {
        while (position < input.length()) {
            char c = input.charAt(position);
            if (c == ' ' || c == '\t') {
                advance();
            } else if (c == '\n' || c == '\r') {
                if (c == '\r' && position + 1 < input.length() && input.charAt(position + 1) == '\n') {
                    advance(2);
                } else {
                    advance();
                }
                lineNumber++;
                columnNumber = 1;
            } else {
                break;
            }
        }
    }
    
    private void advance() {
        advance(1);
    }
    
    private void advance(int count) {
        for (int i = 0; i < count; i++) {
            if (position < input.length()) {
                position++;
                columnNumber++;
            }
        }
    }
    
    private String peekAhead(int length) {
        int end = Math.min(position + length, input.length());
        if (end <= position) {
            return "";
        }
        return input.substring(position, end);
    }
}

