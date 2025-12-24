package utils;

import java.util.HashSet;
import java.util.Set;

/**
 * Contains JavaScript reserved words that cannot be used as identifiers.
 * 
 * @author eduardo
 */
public class ReservedWords {
    private static final Set<String> RESERVED_WORDS = new HashSet<>();
    
    static {
        // JavaScript keywords
        RESERVED_WORDS.add("let");
        RESERVED_WORDS.add("var");
        RESERVED_WORDS.add("const");
        RESERVED_WORDS.add("function");
        RESERVED_WORDS.add("if");
        RESERVED_WORDS.add("else");
        RESERVED_WORDS.add("for");
        RESERVED_WORDS.add("while");
        RESERVED_WORDS.add("do");
        RESERVED_WORDS.add("switch");
        RESERVED_WORDS.add("case");
        RESERVED_WORDS.add("break");
        RESERVED_WORDS.add("continue");
        RESERVED_WORDS.add("return");
        RESERVED_WORDS.add("try");
        RESERVED_WORDS.add("catch");
        RESERVED_WORDS.add("finally");
        RESERVED_WORDS.add("throw");
        RESERVED_WORDS.add("new");
        RESERVED_WORDS.add("this");
        RESERVED_WORDS.add("typeof");
        RESERVED_WORDS.add("instanceof");
        RESERVED_WORDS.add("true");
        RESERVED_WORDS.add("false");
        RESERVED_WORDS.add("null");
        RESERVED_WORDS.add("undefined");
        RESERVED_WORDS.add("void");
        RESERVED_WORDS.add("delete");
        RESERVED_WORDS.add("in");
        RESERVED_WORDS.add("of");
        RESERVED_WORDS.add("class");
        RESERVED_WORDS.add("extends");
        RESERVED_WORDS.add("super");
        RESERVED_WORDS.add("static");
        RESERVED_WORDS.add("async");
        RESERVED_WORDS.add("await");
        RESERVED_WORDS.add("yield");
        RESERVED_WORDS.add("import");
        RESERVED_WORDS.add("export");
        RESERVED_WORDS.add("default");
        RESERVED_WORDS.add("from");
        RESERVED_WORDS.add("as");
        RESERVED_WORDS.add("with");
        RESERVED_WORDS.add("debugger");
    }
    
    /**
     * Checks if a word is a JavaScript reserved word.
     * 
     * @param word The word to check
     * @return true if reserved, false otherwise
     */
    public static boolean isReserved(String word) {
        return RESERVED_WORDS.contains(word);
    }
    
    /**
     * Gets all reserved words.
     * 
     * @return Set of reserved words
     */
    public static Set<String> getReservedWords() {
        return new HashSet<>(RESERVED_WORDS);
    }
}

