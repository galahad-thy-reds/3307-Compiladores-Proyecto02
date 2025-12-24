package ast.html;

import ast.Node;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the root HTML document node.
 * Contains all top-level HTML elements.
 * 
 * @author eduardo
 */
public class DocumentNode implements Node {
    private final List<TagNode> children;
    private final int lineNumber;
    private final int columnNumber;
    private TagNode doctype;
    private TagNode htmlTag;
    
    public DocumentNode(int lineNumber, int columnNumber) {
        this.children = new ArrayList<>();
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    public void addChild(TagNode child) {
        children.add(child);
    }
    
    public List<TagNode> getChildren() {
        return children;
    }
    
    public TagNode getDoctype() {
        return doctype;
    }
    
    public void setDoctype(TagNode doctype) {
        this.doctype = doctype;
    }
    
    public TagNode getHtmlTag() {
        return htmlTag;
    }
    
    public void setHtmlTag(TagNode htmlTag) {
        this.htmlTag = htmlTag;
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

