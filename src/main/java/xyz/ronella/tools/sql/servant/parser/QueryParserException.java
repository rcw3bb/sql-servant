package xyz.ronella.tools.sql.servant.parser;

/**
 * The base exception related query parsing.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
public class QueryParserException extends Exception {

    /**
     * Creates an instance of QueryParserException.
     *
     * @param message The message associated with the exception.
     */
    public QueryParserException(final String message) {
        super(message);
    }
}
