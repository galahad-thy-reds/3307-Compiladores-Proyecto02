package ast.html;

import ast.Node;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an HTML tag node.
 * Can be an opening tag, closing tag, or self-closing tag.
 * 
 * @author eduardo
 */
public class TagNode implements Node {
    private final String tagName;
    private final List<AttributeNode> attributes;
    private final List<Node> children;
    private final boolean isSelfClosing;
    private final boolean isClosingTag;
    private final int lineNumber;
    private final int columnNumber;
    
    public TagNode(String tagName, int lineNumber, int columnNumber) {
        this.tagName = tagName;
        this.attributes = new ArrayList<>();
        this.children = new ArrayList<>();
        this.isSelfClosing = false;
        this.isClosingTag = false;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    public TagNode(String tagName, boolean isClosingTag, boolean isSelfClosing, 
                   int lineNumber, int columnNumber) {
        this.tagName = tagName;
        this.attributes = new ArrayList<>();
        this.children = new ArrayList<>();
        this.isClosingTag = isClosingTag;
        this.isSelfClosing = isSelfClosing;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    public String getTagName() {
        return tagName;
    }
    
    public void addAttribute(AttributeNode attribute) {
        attributes.add(attribute);
    }
    
    public List<AttributeNode> getAttributes() {
        return attributes;
    }
    
    public void addChild(Node child) {
        children.add(child);
    }
    
    public List<Node> getChildren() {
        return children;
    }
    
    public boolean isSelfClosing() {
        return isSelfClosing;
    }
    
    public boolean isClosingTag() {
        return isClosingTag;
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

