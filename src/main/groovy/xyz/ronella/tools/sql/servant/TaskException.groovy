package xyz.ronella.tools.sql.servant

/**
 * The exception thrown when the processing of statement failed.
 *
 * @author Ron Webb
 * @since 2.1.0
 */
class TaskException extends Exception {

    /**
     * Creates an instance of TaskException
     *
     * @param message The message for exception.
     */
    TaskException(String message) {
        super(message)
    }

    /**
     * Creates an instance of TaskException
     *
     * @param throwable A instance of Throwable
     */
    TaskException(Throwable throwable) {
        super(throwable)
    }
}
