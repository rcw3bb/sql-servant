package xyz.ronella.tools.sql.servant.db;

/**
 * The exception to thrown if the queries mode is invalid.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
public class QueryModeException extends DBManagerException {

    /**
     * Creates an instance of the DBMissingValueException.
     *
     * @param message Normally holds the actual field that has no value.
     */
    public QueryModeException(String message) {
        super(message);
    }
}