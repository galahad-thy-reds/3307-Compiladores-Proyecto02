package errors;

/**
 * Represents an error found during validation.
 * Contains line number, error number, and description.
 * 
 * @author eduardo
 */
public class Error {
    private final int lineNumber;
    private final int errorNumber;
    private final String description;
    private final String errorType;
    
    /**
     * Creates a new error.
     * 
     * @param lineNumber The line number where the error occurs (1-indexed)
     * @param errorNumber The unique error number for this error type
     * @param description Human-readable error description
     * @param errorType The type/category of error (e.g., "IDENTIFIER", "CONSTANT", "HTML_STRUCTURE")
     */
    public Error(int lineNumber, int errorNumber, String description, String errorType) {
        this.lineNumber = lineNumber;
        this.errorNumber = errorNumber;
        this.description = description;
        this.errorType = errorType;
    }
    
    /**
     * Creates a new error with default error type.
     * 
     * @param lineNumber The line number where the error occurs
     * @param errorNumber The unique error number
     * @param description Human-readable error description
     */
    public Error(int lineNumber, int errorNumber, String description) {
        this(lineNumber, errorNumber, description, "GENERAL");
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    public int getErrorNumber() {
        return errorNumber;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getErrorType() {
        return errorType;
    }
    
    /**
     * Formats the error message for display.
     * 
     * @return Formatted error string
     */
    public String formatMessage() {
        return String.format("Error %d: %s at line %d", errorNumber, description, lineNumber);
    }
    
    @Override
    public String toString() {
        return formatMessage();
    }
}

