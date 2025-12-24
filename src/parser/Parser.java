package parser;

import ast.Node;
import ast.html.AttributeNode;
import ast.html.DocumentNode;
import ast.html.TagNode;
import ast.html.TextNode;
import ast.js.AssignmentNode;
import ast.js.CallNode;
import ast.js.ConstantNode;
import ast.js.ExpressionNode;
import ast.js.FunctionNode;
import ast.js.IdentifierNode;
import ast.js.ScriptNode;
import ast.js.VariableNode;
import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * State machine parser that builds an Abstract Syntax Tree (AST) from tokens.
 * Handles both HTML and JavaScript parsing, transitioning between modes as needed.
 * 
 * @author eduardo
 */
public class Parser {
    private final List<Token> tokens;
    private int currentTokenIndex;
    private ParserState currentState;
    private final DocumentNode documentNode;
    private final Stack<TagNode> tagStack; // For tracking nested HTML tags
    private ScriptNode currentScriptNode;
    
    // Context tracking for validators
    private final List<String> htmlElementIds; // Track HTML element IDs
    private final List<String> declaredVariables; // Track declared variables for scope checking
    
    /**
     * Creates a new parser for the given input string.
     * 
     * @param input The HTML/JavaScript code to parse
     */
    public Parser(String input) {
        Lexer lexer = new Lexer(input);
        this.tokens = lexer.tokenize();
        this.currentTokenIndex = 0;
        this.currentState = ParserState.HTML_MODE;
        this.documentNode = new DocumentNode(1, 1);
        this.tagStack = new Stack<>();
        this.htmlElementIds = new ArrayList<>();
        this.declaredVariables = new ArrayList<>();
    }
    
    /**
     * Parses the input and builds the AST.
     * 
     * @return The root DocumentNode of the AST
     */
    public DocumentNode parse() {
        while (currentTokenIndex < tokens.size()) {
            Token token = getCurrentToken();
            
            if (token.getType() == TokenType.EOF) {
                break;
            }
            
            switch (currentState) {
                case HTML_MODE:
                    parseHTMLMode(token);
                    break;
                case SCRIPT_MODE:
                    parseJavaScriptMode(token);
                    break;
                default:
                    advanceToken();
                    break;
            }
        }
        
        return documentNode;
    }
    
    /**
     * Parses tokens in HTML mode.
     */
    private void parseHTMLMode(Token token) {
        switch (token.getType()) {
            case HTML_DOCTYPE:
                parseDOCTYPE(token);
                break;
            case HTML_TAG_OPEN:
                parseHTMLTag(token);
                break;
            case HTML_TAG_CLOSE:
                parseHTMLClosingTag(token);
                break;
            case HTML_TEXT:
                parseHTMLText(token);
                break;
            case SCRIPT_OPEN:
                // Enter JavaScript mode
                currentState = ParserState.SCRIPT_MODE;
                currentScriptNode = new ScriptNode(token.getLineNumber(), token.getColumnNumber());
                // Add script node to current tag's children
                if (!tagStack.isEmpty()) {
                    tagStack.peek().addChild(currentScriptNode);
                }
                advanceToken();
                break;
            case COMMENT:
                // Skip comments
                advanceToken();
                break;
            default:
                advanceToken();
                break;
        }
    }
    
    /**
     * Parses tokens in JavaScript mode.
     */
    private void parseJavaScriptMode(Token token) {
        switch (token.getType()) {
            case SCRIPT_CLOSE:
                // Exit JavaScript mode
                currentState = ParserState.HTML_MODE;
                currentScriptNode = null;
                advanceToken();
                break;
            case JS_KEYWORD:
                String keyword = token.getValue();
                if (keyword.equals("function")) {
                    parseFunctionDeclaration();
                } else if (keyword.equals("let") || keyword.equals("var")) {
                    parseVariableDeclaration(keyword);
                } else if (keyword.equals("const")) {
                    parseConstantDeclaration();
                } else if (keyword.equals("new") || keyword.equals("if") || keyword.equals("else")) {
                    // Handle 'new', 'if', 'else' - parse as expression
                    parseExpression();
                } else {
                    // Other keywords - skip for now
                    advanceToken();
                }
                break;
            case JS_IDENTIFIER:
                // Could be an assignment or expression
                parseAssignmentOrExpression();
                break;
            case COMMENT:
                // Skip comments
                advanceToken();
                break;
            default:
                advanceToken();
                break;
        }
    }
    
    /**
     * Parses a DOCTYPE declaration.
     */
    private void parseDOCTYPE(Token token) {
        TagNode doctypeNode = new TagNode("!DOCTYPE", false, false, 
                                          token.getLineNumber(), token.getColumnNumber());
        documentNode.setDoctype(doctypeNode);
        advanceToken();
    }
    
    /**
     * Parses an HTML opening tag.
     * Handles multiple tags on the same line like <html><head>.
     * Since the lexer creates one token per tag (stops at first >), 
     * this method processes a single tag but checks for additional tags in the same token.
     */
    private void parseHTMLTag(Token token) {
        String tagContent = token.getValue();
        
        // Check if there are multiple tags in this token (e.g., <html><head><meta...>)
        // The lexer should create separate tokens, but handle edge cases
        if (tagContent.indexOf('<', 1) > 0) {
            // Multiple tags in one token - split them
            String remaining = tagContent;
            
            while (remaining.length() > 0 && remaining.contains("<")) {
                // Find the next complete tag
                int tagStart = remaining.indexOf('<');
                if (tagStart < 0) break;
                
                // Find matching closing >
                int tagEnd = findTagEnd(remaining, tagStart);
                if (tagEnd < 0) break;
                
                // Extract this tag
                String singleTag = remaining.substring(tagStart, tagEnd + 1);
                remaining = remaining.substring(tagEnd + 1);
                
                // Parse this single tag
                parseSingleHTMLTag(singleTag, token.getLineNumber());
            }
        } else {
            // Single tag - parse normally
            parseSingleHTMLTag(tagContent, token.getLineNumber());
        }
        
        advanceToken();
    }
    
    /**
     * Finds the end of a tag (matching >), handling attributes with quoted values.
     */
    private int findTagEnd(String content, int start) {
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        
        for (int i = start + 1; i < content.length(); i++) {
            char c = content.charAt(i);
            
            if (c == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
            } else if (c == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
            } else if (c == '>' && !inSingleQuote && !inDoubleQuote) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * Parses a single HTML tag.
     */
    private void parseSingleHTMLTag(String tagContent, int lineNumber) {
        String tagName = extractTagName(tagContent);
        
        // Debug: Check if we're parsing html tag
        if (tagName.equalsIgnoreCase("html") && tagStack.isEmpty()) {
            // This is the html tag at the root level
        }
        
        TagNode tagNode = new TagNode(tagName, false, false, lineNumber, 1);
        
        // Parse attributes
        parseTagAttributes(tagContent, tagNode);
        
        // Check if self-closing
        if (tagContent.endsWith("/>") || tagContent.endsWith(" /")) {
            tagNode = new TagNode(tagName, false, true, lineNumber, 1);
            parseTagAttributes(tagContent, tagNode);
        }
        
        // Track HTML element IDs
        for (AttributeNode attr : tagNode.getAttributes()) {
            if (attr.getName().equals("id")) {
                String idValue = attr.getValue();
                if (idValue != null && !idValue.isEmpty()) {
                    // Remove quotes if present
                    idValue = idValue.replaceAll("^[\"']|[\"']$", "");
                    htmlElementIds.add(idValue);
                }
            }
        }
        
        // Add to document or parent tag
        // Always check for html tag in document children (fallback if not set correctly)
        if (tagStack.isEmpty()) {
            if (tagName.equalsIgnoreCase("html")) {
                // Set as html tag if not already set
                if (documentNode.getHtmlTag() == null) {
                    documentNode.setHtmlTag(tagNode);
                }
            }
            documentNode.addChild(tagNode);
        } else {
            tagStack.peek().addChild(tagNode);
        }
        
        // Also ensure html tag is set if we find it in children later
        if (tagName.equalsIgnoreCase("html") && documentNode.getHtmlTag() == null) {
            documentNode.setHtmlTag(tagNode);
        }
        
        // Push to stack if not self-closing
        if (!tagNode.isSelfClosing()) {
            tagStack.push(tagNode);
        }
    }
    
    /**
     * Parses an HTML closing tag.
     */
    private void parseHTMLClosingTag(Token token) {
        String tagContent = token.getValue();
        String tagName = extractTagName(tagContent);
        
        // Pop from stack until we find matching tag
        while (!tagStack.isEmpty()) {
            TagNode top = tagStack.pop();
            if (top.getTagName().equalsIgnoreCase(tagName)) {
                break;
            }
        }
        
        advanceToken();
    }
    
    /**
     * Parses HTML text content.
     */
    private void parseHTMLText(Token token) {
        String text = token.getValue().trim();
        if (!text.isEmpty() && !tagStack.isEmpty()) {
            TextNode textNode = new TextNode(text, token.getLineNumber(), token.getColumnNumber());
            tagStack.peek().addChild(textNode);
        }
        advanceToken();
    }
    
    /**
     * Extracts tag name from tag content.
     */
    private String extractTagName(String tagContent) {
        // Remove < and >
        String cleaned = tagContent.replaceAll("^<|>$", "").trim();
        
        // Remove closing tag marker
        cleaned = cleaned.replaceAll("^/", "");
        
        // Extract first word (tag name)
        String[] parts = cleaned.split("\\s+");
        if (parts.length > 0) {
            return parts[0].toLowerCase();
        }
        return cleaned;
    }
    
    /**
     * Parses attributes from a tag string.
     */
    private void parseTagAttributes(String tagContent, TagNode tagNode) {
        // Simple attribute parsing - extract name="value" pairs
        // This is a simplified version; a full parser would handle more cases
        String cleaned = tagContent.replaceAll("^<|>$", "").trim();
        String[] parts = cleaned.split("\\s+");
        
        if (parts.length > 1) {
            for (int i = 1; i < parts.length; i++) {
                String part = parts[i];
                if (part.contains("=")) {
                    String[] attrParts = part.split("=", 2);
                    if (attrParts.length == 2) {
                        String name = attrParts[0].trim();
                        String value = attrParts[1].trim();
                        // Remove quotes from value
                        value = value.replaceAll("^[\"']|[\"']$", "");
                        AttributeNode attr = new AttributeNode(name, value, 
                                                               tagNode.getLineNumber(), 
                                                               tagNode.getColumnNumber());
                        tagNode.addAttribute(attr);
                    }
                } else if (!part.equals("/")) {
                    // Boolean attribute (no value)
                    AttributeNode attr = new AttributeNode(part, "", 
                                                         tagNode.getLineNumber(), 
                                                         tagNode.getColumnNumber());
                    tagNode.addAttribute(attr);
                }
            }
        }
    }
    
    /**
     * Parses a JavaScript function declaration.
     */
    private void parseFunctionDeclaration() {
        Token functionToken = getCurrentToken();
        advanceToken(); // Skip 'function'
        
        // Get function name
        Token nameToken = getCurrentToken();
        if (nameToken.getType() == TokenType.JS_IDENTIFIER) {
            String functionName = nameToken.getValue();
            FunctionNode functionNode = new FunctionNode(functionName,
                                                         functionToken.getLineNumber(),
                                                         functionToken.getColumnNumber());
            
            advanceToken(); // Skip function name
            
            // Parse parameters
            if (getCurrentToken().getType() == TokenType.LEFT_PAREN) {
                advanceToken(); // Skip (
                parseFunctionParameters(functionNode);
            }
            
            // Parse function body
            if (getCurrentToken().getType() == TokenType.LEFT_BRACE) {
                advanceToken(); // Skip {
                parseFunctionBody(functionNode);
            }
            
            if (currentScriptNode != null) {
                currentScriptNode.addStatement(functionNode);
            }
        } else {
            advanceToken();
        }
    }
    
    /**
     * Parses function parameters.
     */
    private void parseFunctionParameters(FunctionNode functionNode) {
        while (getCurrentToken().getType() != TokenType.RIGHT_PAREN) {
            Token paramToken = getCurrentToken();
            if (paramToken.getType() == TokenType.JS_IDENTIFIER) {
                IdentifierNode param = new IdentifierNode(paramToken.getValue(),
                                                         paramToken.getLineNumber(),
                                                         paramToken.getColumnNumber());
                functionNode.addParameter(param);
                advanceToken();
                
                if (getCurrentToken().getType() == TokenType.JS_PUNCTUATION && 
                    getCurrentToken().getValue().equals(",")) {
                    advanceToken(); // Skip comma
                }
            } else {
                advanceToken();
            }
        }
        advanceToken(); // Skip )
    }
    
    /**
     * Parses function body statements.
     */
    private void parseFunctionBody(FunctionNode functionNode) {
        int braceDepth = 1;
        
        while (braceDepth > 0 && currentTokenIndex < tokens.size()) {
            Token token = getCurrentToken();
            
            if (token.getType() == TokenType.LEFT_BRACE) {
                braceDepth++;
            } else if (token.getType() == TokenType.RIGHT_BRACE) {
                braceDepth--;
                if (braceDepth == 0) {
                    advanceToken(); // Skip closing }
                    break;
                }
            } else if (token.getType() == TokenType.JS_KEYWORD) {
                String keyword = token.getValue();
                if (keyword.equals("let") || keyword.equals("var")) {
                    parseVariableDeclaration(keyword);
                    if (currentScriptNode != null) {
                        functionNode.addBodyStatement(
                            currentScriptNode.getStatements().get(
                                currentScriptNode.getStatements().size() - 1));
                    }
                } else if (keyword.equals("const")) {
                    parseConstantDeclaration();
                    if (currentScriptNode != null) {
                        functionNode.addBodyStatement(
                            currentScriptNode.getStatements().get(
                                currentScriptNode.getStatements().size() - 1));
                    }
                } else {
                    advanceToken();
                }
            } else if (token.getType() == TokenType.JS_IDENTIFIER) {
                parseAssignmentOrExpression();
                if (currentScriptNode != null && !currentScriptNode.getStatements().isEmpty()) {
                    functionNode.addBodyStatement(
                        currentScriptNode.getStatements().get(
                            currentScriptNode.getStatements().size() - 1));
                }
            } else {
                advanceToken();
            }
        }
    }
    
    /**
     * Parses a variable declaration (let or var).
     */
    private void parseVariableDeclaration(String keyword) {
        Token keywordToken = getCurrentToken();
        advanceToken(); // Skip 'let' or 'var'
        
        Token identifierToken = getCurrentToken();
        if (identifierToken.getType() == TokenType.JS_IDENTIFIER) {
            IdentifierNode identifier = new IdentifierNode(identifierToken.getValue(),
                                                           identifierToken.getLineNumber(),
                                                           identifierToken.getColumnNumber());
            declaredVariables.add(identifier.getName());
            
            advanceToken(); // Skip identifier
            
            Node initialValue = null;
            if (getCurrentToken().getType() == TokenType.JS_OPERATOR && 
                getCurrentToken().getValue().equals("=")) {
                advanceToken(); // Skip =
                initialValue = parseExpression();
            }
            
            VariableNode variableNode = new VariableNode(keyword, identifier, initialValue,
                                                       keywordToken.getLineNumber(),
                                                       keywordToken.getColumnNumber());
            
            if (currentScriptNode != null) {
                currentScriptNode.addStatement(variableNode);
            }
            
            // Skip semicolon if present
            if (getCurrentToken().getType() == TokenType.JS_PUNCTUATION && 
                getCurrentToken().getValue().equals(";")) {
                advanceToken();
            }
        } else {
            advanceToken();
        }
    }
    
    /**
     * Parses a constant declaration (const).
     */
    private void parseConstantDeclaration() {
        Token constToken = getCurrentToken();
        advanceToken(); // Skip 'const'
        
        Token identifierToken = getCurrentToken();
        if (identifierToken.getType() == TokenType.JS_IDENTIFIER) {
            IdentifierNode identifier = new IdentifierNode(identifierToken.getValue(),
                                                         identifierToken.getLineNumber(),
                                                         identifierToken.getColumnNumber());
            declaredVariables.add(identifier.getName());
            
            advanceToken(); // Skip identifier
            
            Node value = null;
            if (getCurrentToken().getType() == TokenType.JS_OPERATOR && 
                getCurrentToken().getValue().equals("=")) {
                advanceToken(); // Skip =
                value = parseExpression();
            }
            
            ConstantNode constantNode = new ConstantNode(identifier, value,
                                                        constToken.getLineNumber(),
                                                        constToken.getColumnNumber());
            
            if (currentScriptNode != null) {
                currentScriptNode.addStatement(constantNode);
            }
            
            // Skip semicolon if present
            if (getCurrentToken().getType() == TokenType.JS_PUNCTUATION && 
                getCurrentToken().getValue().equals(";")) {
                advanceToken();
            }
        } else {
            advanceToken();
        }
    }
    
    /**
     * Parses an assignment or expression statement.
     */
    private void parseAssignmentOrExpression() {
        // Check if it's an assignment
        int lookahead = 1;
        while (currentTokenIndex + lookahead < tokens.size()) {
            Token lookaheadToken = tokens.get(currentTokenIndex + lookahead);
            if (lookaheadToken.getType() == TokenType.JS_OPERATOR) {
                String op = lookaheadToken.getValue();
                if (op.equals("=") || op.equals("+=") || op.equals("-=") || 
                    op.equals("*=") || op.equals("/=")) {
                    // It's an assignment
                    parseAssignment();
                    return;
                }
            }
            if (lookaheadToken.getType() == TokenType.JS_PUNCTUATION && 
                lookaheadToken.getValue().equals(";")) {
                break;
            }
            lookahead++;
        }
        
        // Otherwise, it's just an expression
        Node expr = parseExpression();
        if (currentScriptNode != null && expr != null) {
            currentScriptNode.addStatement(expr);
        }
        
        // Skip semicolon if present
        if (getCurrentToken().getType() == TokenType.JS_PUNCTUATION && 
            getCurrentToken().getValue().equals(";")) {
            advanceToken();
        }
    }
    
    /**
     * Parses an assignment statement.
     * Only creates AssignmentNode for actual assignment operators (=, +=, -=, *=, /=, %=).
     */
    private void parseAssignment() {
        Token identifierToken = getCurrentToken();
        if (identifierToken.getType() == TokenType.JS_IDENTIFIER) {
            IdentifierNode lhs = new IdentifierNode(identifierToken.getValue(),
                                                   identifierToken.getLineNumber(),
                                                   identifierToken.getColumnNumber());
            advanceToken(); // Skip identifier
            
            Token operatorToken = getCurrentToken();
            if (operatorToken.getType() != TokenType.JS_OPERATOR) {
                // Not an operator - this is not an assignment, parse as expression instead
                parseExpression();
                return;
            }
            
            String operator = operatorToken.getValue();
            
            // Only proceed if it's actually an assignment operator
            if (!isAssignmentOperator(operator)) {
                // Not an assignment operator - parse as expression instead
                parseExpression();
                return;
            }
            
            advanceToken(); // Skip operator
            
            Node rhs = parseExpression();
            
            AssignmentNode assignment = new AssignmentNode(lhs, operator, rhs,
                                                          identifierToken.getLineNumber(),
                                                          identifierToken.getColumnNumber());
            
            if (currentScriptNode != null) {
                currentScriptNode.addStatement(assignment);
            }
        } else {
            advanceToken();
        }
    }
    
    /**
     * Checks if an operator is an assignment operator.
     */
    private boolean isAssignmentOperator(String operator) {
        return operator.equals("=") || operator.equals("+=") || 
               operator.equals("-=") || operator.equals("*=") || 
               operator.equals("/=") || operator.equals("%=");
    }
    
    /**
     * Parses a JavaScript expression.
     * Handles method calls, new expressions, literals, and operators.
     */
    private Node parseExpression() {
        if (currentTokenIndex >= tokens.size()) {
            return null;
        }
        
        Token token = getCurrentToken();
        
        // Handle 'new' keyword (e.g., new Date())
        if (token.getType() == TokenType.JS_KEYWORD && token.getValue().equals("new")) {
            advanceToken(); // Skip 'new'
            return parseMethodCall(); // Parse the constructor call
        }
        
        // Handle method calls like document.getElementById("id")
        if (token.getType() == TokenType.JS_IDENTIFIER) {
            // Check if it's a method call or property access
            int lookahead = 1;
            while (currentTokenIndex + lookahead < tokens.size()) {
                Token lookaheadToken = tokens.get(currentTokenIndex + lookahead);
                if (lookaheadToken.getType() == TokenType.JS_PUNCTUATION && 
                    lookaheadToken.getValue().equals(".")) {
                    // It's a method call chain or property access
                    return parseMethodCall();
                }
                if (lookaheadToken.getType() == TokenType.LEFT_PAREN) {
                    // Function call
                    return parseMethodCall();
                }
                if (lookaheadToken.getType() == TokenType.JS_PUNCTUATION && 
                    (lookaheadToken.getValue().equals(";") || lookaheadToken.getValue().equals(",") ||
                     lookaheadToken.getValue().equals(")"))) {
                    // Simple identifier
                    IdentifierNode id = new IdentifierNode(token.getValue(),
                                                         token.getLineNumber(),
                                                         token.getColumnNumber());
                    advanceToken();
                    return id;
                }
                // Check for operators that indicate this is not a method call
                if (lookaheadToken.getType() == TokenType.JS_OPERATOR) {
                    break;
                }
                lookahead++;
            }
        }
        
        // Handle literals
        if (token.getType() == TokenType.JS_LITERAL_STRING || 
            token.getType() == TokenType.JS_LITERAL_NUMBER ||
            token.getType() == TokenType.JS_LITERAL_BOOLEAN ||
            token.getType() == TokenType.JS_LITERAL_NULL) {
            IdentifierNode literal = new IdentifierNode(token.getValue(),
                                                       token.getLineNumber(),
                                                       token.getColumnNumber());
            advanceToken();
            return literal;
        }
        
        // Default: create expression node (for binary operations, comparisons, etc.)
        ExpressionNode expr = new ExpressionNode(token.getLineNumber(), token.getColumnNumber());
        int maxIterations = 1000; // Prevent infinite loops
        int iterations = 0;
        
        while (currentTokenIndex < tokens.size() && iterations < maxIterations) {
            iterations++;
            token = getCurrentToken();
            
            if (token.getType() == TokenType.EOF) {
                break;
            }
            
            if (token.getType() == TokenType.JS_PUNCTUATION && 
                (token.getValue().equals(";") || token.getValue().equals(",") || 
                 token.getValue().equals(")"))) {
                break;
            }
            
            // Handle operators (including comparison operators)
            if (token.getType() == TokenType.JS_OPERATOR) {
                String op = token.getValue();
                // Only add as operator if it's not an assignment operator (those are handled separately)
                if (!op.equals("=") && !op.equals("+=") && !op.equals("-=") && 
                    !op.equals("*=") && !op.equals("/=") && !op.equals("%=")) {
                    expr.addOperator(op);
                    advanceToken();
                } else {
                    break; // Assignment operators are handled by parseAssignment
                }
            } else if (token.getType() == TokenType.JS_IDENTIFIER) {
                // Check if it's a method call
                int lookahead = 1;
                boolean isMethodCall = false;
                while (currentTokenIndex + lookahead < tokens.size()) {
                    Token lookaheadToken = tokens.get(currentTokenIndex + lookahead);
                    if (lookaheadToken.getType() == TokenType.JS_PUNCTUATION && 
                        lookaheadToken.getValue().equals(".")) {
                        isMethodCall = true;
                        break;
                    }
                    if (lookaheadToken.getType() == TokenType.LEFT_PAREN) {
                        isMethodCall = true;
                        break;
                    }
                    if (lookaheadToken.getType() == TokenType.JS_OPERATOR ||
                        lookaheadToken.getType() == TokenType.JS_PUNCTUATION) {
                        break;
                    }
                    lookahead++;
                }
                if (isMethodCall) {
                    Node callNode = parseMethodCall();
                    if (callNode != null) {
                        expr.addOperand(callNode);
                    }
                } else {
                    IdentifierNode id = new IdentifierNode(token.getValue(),
                                                          token.getLineNumber(),
                                                          token.getColumnNumber());
                    expr.addOperand(id);
                    advanceToken();
                }
            } else if (token.getType() == TokenType.JS_LITERAL_STRING ||
                      token.getType() == TokenType.JS_LITERAL_NUMBER ||
                      token.getType() == TokenType.JS_LITERAL_BOOLEAN ||
                      token.getType() == TokenType.JS_LITERAL_NULL) {
                IdentifierNode literal = new IdentifierNode(token.getValue(),
                                                          token.getLineNumber(),
                                                          token.getColumnNumber());
                expr.addOperand(literal);
                advanceToken();
            } else {
                // Unknown token - advance to prevent infinite loop
                advanceToken();
                break;
            }
        }
        
        // Return a single operand if it's not really an expression, otherwise return the expression
        if (expr.getOperands().size() == 1 && expr.getOperators().isEmpty()) {
            return expr.getOperands().get(0);
        }
        
        // Return null if no operands were added (invalid expression)
        if (expr.getOperands().isEmpty()) {
            return null;
        }
        
        return expr;
    }
    
    /**
     * Parses a method call like document.getElementById("id") or property access like .value.
     * Handles chained calls like document.getElementById("id").value.
     */
    private Node parseMethodCall() {
        Token firstToken = getCurrentToken();
        Node callee = null;
        
        // Build callee (could be document.getElementById or just document)
        StringBuilder calleeName = new StringBuilder();
        boolean hasParentheses = false;
        
        while (currentTokenIndex < tokens.size()) {
            Token token = getCurrentToken();
            if (token.getType() == TokenType.JS_IDENTIFIER) {
                if (calleeName.length() > 0) {
                    calleeName.append(".");
                }
                calleeName.append(token.getValue());
                advanceToken();
            } else if (token.getType() == TokenType.JS_PUNCTUATION && 
                      token.getValue().equals(".")) {
                calleeName.append(".");
                advanceToken();
            } else if (token.getType() == TokenType.LEFT_PAREN) {
                hasParentheses = true;
                break;
            } else {
                break;
            }
        }
        
        if (calleeName.length() > 0) {
            callee = new IdentifierNode(calleeName.toString(), 
                                      firstToken.getLineNumber(),
                                      firstToken.getColumnNumber());
        }
        
        // Parse arguments if there's a function call
        CallNode callNode = new CallNode(callee, firstToken.getLineNumber(), firstToken.getColumnNumber());
        
        if (hasParentheses && getCurrentToken().getType() == TokenType.LEFT_PAREN) {
            advanceToken(); // Skip (
            
            while (currentTokenIndex < tokens.size() && getCurrentToken().getType() != TokenType.RIGHT_PAREN) {
                Node arg = parseExpression();
                if (arg != null) {
                    callNode.addArgument(arg);
                }
                
                if (getCurrentToken().getType() == TokenType.JS_PUNCTUATION && 
                    getCurrentToken().getValue().equals(",")) {
                    advanceToken(); // Skip comma
                } else if (getCurrentToken().getType() == TokenType.RIGHT_PAREN) {
                    break;
                } else {
                    advanceToken();
                }
            }
            
            if (getCurrentToken().getType() == TokenType.RIGHT_PAREN) {
                advanceToken(); // Skip )
            }
        }
        
        // Handle property access after method call (e.g., .value)
        if (currentTokenIndex < tokens.size()) {
            Token nextToken = getCurrentToken();
            if (nextToken.getType() == TokenType.JS_PUNCTUATION && 
                nextToken.getValue().equals(".")) {
                advanceToken(); // Skip .
                // Recursively parse the property access as another method call
                Node propertyAccess = parseMethodCall();
                // Chain the calls: create a new CallNode that represents the property access
                // For now, we'll create an IdentifierNode that represents the full chain
                String fullChain = calleeName.toString();
                if (propertyAccess instanceof CallNode) {
                    CallNode propCall = (CallNode) propertyAccess;
                    if (propCall.getCallee() instanceof IdentifierNode) {
                        fullChain += "." + ((IdentifierNode) propCall.getCallee()).getName();
                    }
                } else if (propertyAccess instanceof IdentifierNode) {
                    fullChain += "." + ((IdentifierNode) propertyAccess).getName();
                }
                return new IdentifierNode(fullChain, firstToken.getLineNumber(), firstToken.getColumnNumber());
            }
        }
        
        return callNode;
    }
    
    /**
     * Gets the current token.
     */
    private Token getCurrentToken() {
        if (currentTokenIndex < tokens.size()) {
            return tokens.get(currentTokenIndex);
        }
        return new Token(TokenType.EOF, "", 0, 0);
    }
    
    /**
     * Advances to the next token.
     */
    private void advanceToken() {
        if (currentTokenIndex < tokens.size()) {
            currentTokenIndex++;
        }
    }
    
    /**
     * Gets the list of HTML element IDs found during parsing.
     * 
     * @return List of element IDs
     */
    public List<String> getHtmlElementIds() {
        return htmlElementIds;
    }
    
    /**
     * Gets the list of declared variables.
     * 
     * @return List of variable names
     */
    public List<String> getDeclaredVariables() {
        return declaredVariables;
    }
}

