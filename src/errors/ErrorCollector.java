package errors;

import java.util.ArrayList;
import java.util.List;

/**
 * Centralized error collection system.
 * All validators add errors here instead of printing directly.
 * 
 * @author eduardo
 */
public class ErrorCollector {
    private final List<Error> errors;
    private int nextErrorNumber;
    
    /**
     * Creates a new error collector.
     */
    public ErrorCollector() {
        this.errors = new ArrayList<>();
        this.nextErrorNumber = 1;
    }
    
    /**
     * Adds an error to the collection.
     * 
     * @param error The error to add
     */
    public void addError(Error error) {
        errors.add(error);
    }
    
    /**
     * Adds an error with automatic error number assignment.
     * 
     * @param lineNumber Line number where error occurs
     * @param description Error description
     * @param errorType Type of error
     * @return The created error
     */
    public Error addError(int lineNumber, String description, String errorType) {
        Error error = new Error(lineNumber, nextErrorNumber++, description, errorType);
        errors.add(error);
        return error;
    }
    
    /**
     * Adds an error with default error type.
     * 
     * @param lineNumber Line number where error occurs
     * @param description Error description
     * @return The created error
     */
    public Error addError(int lineNumber, String description) {
        return addError(lineNumber, description, "GENERAL");
    }
    
    /**
     * Gets all collected errors.
     * 
     * @return List of all errors
     */
    public List<Error> getErrors() {
        return new ArrayList<>(errors);
    }
    
    /**
     * Gets errors sorted by line number.
     * 
     * @return List of errors sorted by line number
     */
    public List<Error> getErrorsSortedByLine() {
        List<Error> sorted = new ArrayList<>(errors);
        sorted.sort((e1, e2) -> Integer.compare(e1.getLineNumber(), e2.getLineNumber()));
        return sorted;
    }
    
    /**
     * Gets the number of errors collected.
     * 
     * @return Error count
     */
    public int getErrorCount() {
        return errors.size();
    }
    
    /**
     * Checks if any errors have been collected.
     * 
     * @return true if errors exist, false otherwise
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * Clears all collected errors.
     */
    public void clear() {
        errors.clear();
        nextErrorNumber = 1;
    }
}

