package parser;

/**
 * Enumeration of parser states for the state machine.
 * The parser transitions between these states as it processes HTML and JavaScript.
 * 
 * @author eduardo
 */
public enum ParserState {
    /**
     * Parsing HTML content (default state).
     */
    HTML_MODE,
    
    /**
     * Parsing JavaScript content within a script tag.
     */
    SCRIPT_MODE,
    
    /**
     * Parsing a string literal (handles escape sequences).
     */
    STRING_MODE,
    
    /**
     * Parsing an HTML comment.
     */
    HTML_COMMENT_MODE,
    
    /**
     * Parsing a JavaScript line comment (//).
     */
    JS_LINE_COMMENT_MODE,
    
    /**
     * Parsing a JavaScript block comment.
     */
    JS_BLOCK_COMMENT_MODE,
    
    /**
     * Parsing an HTML tag (reading tag name and attributes).
     */
    HTML_TAG_MODE,
    
    /**
     * Parsing an HTML attribute value.
     */
    HTML_ATTRIBUTE_VALUE_MODE,
    
    /**
     * End of file reached.
     */
    EOF
}

