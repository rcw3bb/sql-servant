package xyz.ronella.tools.sql.servant.impl;

/**
 * The base exception that are all related to parameters.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
public class ParameterException extends Exception {

    /**
     * Creates an instance of the ParameterException
     *
     * @param msg The message associated to the exception.
     */
    public ParameterException(final String msg) {
        super(msg);
    }
}