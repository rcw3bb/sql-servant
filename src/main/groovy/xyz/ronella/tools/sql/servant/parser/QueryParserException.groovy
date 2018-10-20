package xyz.ronella.tools.sql.servant.parser

/**
 * The base exception of all related to query parsing.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
class QueryParserException extends Exception {

    /**
     * Creates an instance of QueryParserException.
     *
     * @param message The message associated with the exception.
     */
    QueryParserException(String message) {
        super(message)
    }
}
