package validators;

import ast.Node;
import ast.html.DocumentNode;
import ast.html.TagNode;
import ast.js.ConstantNode;
import ast.js.IdentifierNode;
import ast.js.ScriptNode;
import ast.js.VariableNode;
import errors.ErrorCollector;
import utils.ReservedWords;

/**
 * Validates JavaScript constant declarations (Requirement #3).
 * Checks that constants follow identifier rules, are initialized, and declared before var/let.
 * 
 * @author eduardo
 */
public class ConstantValidator implements Validator {
    
    @Override
    public void validate(DocumentNode document, ErrorCollector errorCollector) {
        // Traverse all script nodes
        traverseDocument(document, errorCollector);
    }
    
    /**
     * Traverses the document to find all script nodes.
     */
    private void traverseDocument(Node node, ErrorCollector errorCollector) {
        if (node instanceof ScriptNode) {
            ScriptNode scriptNode = (ScriptNode) node;
            validateScriptNode(scriptNode, errorCollector);
        } else if (node instanceof DocumentNode) {
            DocumentNode doc = (DocumentNode) node;
            for (TagNode tag : doc.getChildren()) {
                traverseTag(tag, errorCollector);
            }
        } else if (node instanceof TagNode) {
            traverseTag((TagNode) node, errorCollector);
        }
    }
    
    /**
     * Traverses HTML tags to find script nodes.
     */
    private void traverseTag(TagNode tag, ErrorCollector errorCollector) {
        for (Node child : tag.getChildren()) {
            if (child instanceof ScriptNode) {
                validateScriptNode((ScriptNode) child, errorCollector);
            } else if (child instanceof TagNode) {
                traverseTag((TagNode) child, errorCollector);
            }
        }
    }
    
    /**
     * Validates a script node for constant declarations.
     */
    private void validateScriptNode(ScriptNode scriptNode, ErrorCollector errorCollector) {
        boolean foundVarOrLet = false;
        
        for (int i = 0; i < scriptNode.getStatements().size(); i++) {
            Node statement = scriptNode.getStatements().get(i);
            
            if (statement instanceof ConstantNode) {
                ConstantNode constNode = (ConstantNode) statement;
                validateConstant(constNode, errorCollector, foundVarOrLet);
            } else if (statement instanceof VariableNode) {
                foundVarOrLet = true;
            }
        }
    }
    
    /**
     * Validates a constant declaration according to Requirement #3 rules.
     */
    private void validateConstant(ConstantNode constNode, ErrorCollector errorCollector, 
                                  boolean varOrLetFoundBefore) {
        IdentifierNode identifier = constNode.getIdentifier();
        String name = identifier.getName();
        int lineNumber = identifier.getLineNumber();
        
        // Check identifier rules (same as Requirement #2)
        if (!isValidIdentifier(name)) {
            errorCollector.addError(lineNumber, 
                    String.format("Constant name '%s' does not follow identifier rules", name), 
                    "CONSTANT");
        }
        
        // Check if reserved word
        if (ReservedWords.isReserved(name)) {
            errorCollector.addError(lineNumber, 
                    String.format("Constant name '%s' is a JavaScript reserved word", name), 
                    "CONSTANT");
        }
        
        // Check if value is assigned (required for const)
        if (constNode.getValue() == null) {
            errorCollector.addError(lineNumber, 
                    "Constant must be assigned a value at declaration", 
                    "CONSTANT");
        }
        
        // Check if const is declared after var/let in same scope
        if (varOrLetFoundBefore) {
            errorCollector.addError(lineNumber, 
                    "Constant cannot be declared after var or let in the same scope", 
                    "CONSTANT");
        }
    }
    
    /**
     * Checks if a name is a valid identifier.
     */
    private boolean isValidIdentifier(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        
        char firstChar = name.charAt(0);
        if (!Character.isLetter(firstChar) && firstChar != '_' && 
            !Character.isUnicodeIdentifierStart(firstChar)) {
            return false;
        }
        
        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_' && 
                !Character.isUnicodeIdentifierPart(c)) {
                return false;
            }
        }
        
        return !name.contains(" ") && !name.contains("-") && 
               !name.contains("+") && !name.contains("*") && !name.contains("/");
    }
}

