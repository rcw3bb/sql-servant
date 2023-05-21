package xyz.ronella.tools.sql.servant;

/**
 * The exception thrown when the processing of statement failed.
 *
 * @author Ron Webb
 * @since 2.1.0
 */
public class TaskException extends RuntimeException {

    /**
     * Creates an instance of TaskException
     */
    public TaskException() {
        super();
    }

    /**
     * Creates an instance of TaskException
     *
     * @param message The message for exception.
     */
    public TaskException(String message) {
        super(message);
    }

    /**
     * Creates an instance of TaskException
     *
     * @param throwable A instance of Throwable
     */
    public TaskException(Throwable throwable) {
        super(throwable);
    }
}
