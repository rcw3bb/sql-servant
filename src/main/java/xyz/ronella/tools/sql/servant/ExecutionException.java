package xyz.ronella.tools.sql.servant;

/**
 * The exception thrown when at least one statement failed.
 *
 * @author Ron Webb
 * @since 2.1.0
 */
public class ExecutionException extends RuntimeException {

    /**
     * Creates an instance of the ExecutionException.
     */
    public ExecutionException() {
        super();
    }
}
