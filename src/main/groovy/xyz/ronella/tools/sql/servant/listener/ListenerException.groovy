package xyz.ronella.tools.sql.servant.listener

/**
 * The base exception that are all related to listeners.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
class ListenerException extends Exception {

    /**
     * Creates an instance of ListenerException.
     *
     * @param message The message associated with the exception.
     */
    ListenerException(String message) {
        super(message)
    }
}
