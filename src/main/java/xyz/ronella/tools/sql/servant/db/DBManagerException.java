package xyz.ronella.tools.sql.servant.db;

/**
 * The base exception that are all related to DBManager.
 *
 * @autho Ron Webb
 * @since 2018-10-07
 */
public class DBManagerException extends RuntimeException {

    /**
     * Creates an instance of DBManagerException.
     *
     * @param message The actual exception to display.
     */
    public DBManagerException(String message) {
        super(message);
    }
}
