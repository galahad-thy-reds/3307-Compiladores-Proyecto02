package validators;

import ast.html.AttributeNode;
import ast.html.DocumentNode;
import ast.html.TagNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tracks HTML element IDs for use by DataInputValidator and DataOutputValidator.
 * This is a helper validator that builds a registry of HTML element IDs.
 * 
 * @author eduardo
 */
public class HtmlElementValidator {
    private final Set<String> htmlElementIds;
    
    /**
     * Creates a new HTML element validator.
     */
    public HtmlElementValidator() {
        this.htmlElementIds = new HashSet<>();
    }
    
    /**
     * Traverses the document and collects all HTML element IDs.
     * 
     * @param document The root document node
     * @return List of all HTML element IDs found
     */
    public List<String> collectElementIds(DocumentNode document) {
        htmlElementIds.clear();
        traverseDocument(document);
        return new ArrayList<>(htmlElementIds);
    }
    
    /**
     * Traverses the document to find all HTML element IDs.
     */
    private void traverseDocument(ast.Node node) {
        if (node instanceof DocumentNode) {
            DocumentNode doc = (DocumentNode) node;
            for (TagNode tag : doc.getChildren()) {
                traverseTag(tag);
            }
        } else if (node instanceof TagNode) {
            traverseTag((TagNode) node);
        }
    }
    
    /**
     * Traverses a tag and its children to find ID attributes.
     */
    private void traverseTag(TagNode tag) {
        // Check for id attribute
        for (AttributeNode attr : tag.getAttributes()) {
            if (attr.getName().equalsIgnoreCase("id")) {
                String idValue = attr.getValue();
                if (idValue != null && !idValue.isEmpty()) {
                    // Remove quotes if present
                    idValue = idValue.replaceAll("^[\"']|[\"']$", "");
                    if (!idValue.isEmpty()) {
                        htmlElementIds.add(idValue);
                    }
                }
            }
        }
        
        // Traverse children
        for (ast.Node child : tag.getChildren()) {
            if (child instanceof TagNode) {
                traverseTag((TagNode) child);
            }
        }
    }
    
    /**
     * Gets the collected HTML element IDs.
     * 
     * @return Set of element IDs
     */
    public Set<String> getHtmlElementIds() {
        return new HashSet<>(htmlElementIds);
    }
}

