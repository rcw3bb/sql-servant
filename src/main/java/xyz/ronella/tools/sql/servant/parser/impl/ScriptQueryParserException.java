package xyz.ronella.tools.sql.servant.parser.impl;

import xyz.ronella.tools.sql.servant.parser.QueryParserException;

/**
 * The exception thrown if there are any query parsing exception.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
public class ScriptQueryParserException extends QueryParserException {

    /**
     * Creates an instance of ScriptQueryParserException.
     *
     * @param message The message associated with the exception.
     */
    public ScriptQueryParserException(final String message) {
        super(message);
    }

}
