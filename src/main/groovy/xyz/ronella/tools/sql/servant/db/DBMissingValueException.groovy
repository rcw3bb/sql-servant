package xyz.ronella.tools.sql.servant.db

/**
 * The exception thrown if a particular expected field was not set for some reason.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
class DBMissingValueException extends DBManagerException {

    /**
     * Creates an instance of the DBMissingValueException.
     *
     * @param message Normally holds the actual field that has no value.
     */
    DBMissingValueException(String message) {
        super(message)
    }
}
