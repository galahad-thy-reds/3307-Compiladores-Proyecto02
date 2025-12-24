package lexer;

/**
 * Enumeration of all token types that can be produced by the lexer.
 * Includes tokens for both HTML and JavaScript parsing.
 * 
 * @author eduardo
 */
public enum TokenType {
    // HTML Tokens
    HTML_TAG_OPEN,           // <tag
    HTML_TAG_CLOSE,          // </tag> or />
    HTML_TAG_NAME,           // tag name
    HTML_ATTRIBUTE_NAME,     // attribute name
    HTML_ATTRIBUTE_VALUE,    // attribute value (in quotes)
    HTML_TEXT,               // text content between tags
    HTML_DOCTYPE,            // <!DOCTYPE html>
    
    // JavaScript Keywords
    JS_KEYWORD,              // let, var, const, function, if, else, etc.
    JS_IDENTIFIER,           // variable/function names
    JS_LITERAL_STRING,       // "string" or 'string'
    JS_LITERAL_NUMBER,       // 42, 3.14, etc.
    JS_LITERAL_BOOLEAN,      // true, false
    JS_LITERAL_NULL,         // null
    
    // JavaScript Operators
    JS_OPERATOR,            // +, -, *, /, =, ==, ===, etc.
    JS_PUNCTUATION,         // ;, ,, ., :, ?, etc.
    
    // Brackets and Braces
    LEFT_PAREN,              // (
    RIGHT_PAREN,             // )
    LEFT_BRACE,              // {
    RIGHT_BRACE,             // }
    LEFT_BRACKET,            // [
    RIGHT_BRACKET,           // ]
    
    // Special
    WHITESPACE,              // spaces, tabs
    NEWLINE,                 // line breaks
    COMMENT,                 // // or /* */
    EOF,                     // End of file
    
    // Script tags
    SCRIPT_OPEN,             // <script>
    SCRIPT_CLOSE             // </script>
}

