package xyz.ronella.tools.sql.servant.impl

/**
 * The exception thrown if the parameter supplied from the command line is unexpected.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
class UnexpectedParameterException extends ParameterException {

    /**
     * Creates an instance of UnexpectedParameterException.
     *
     * @param msg The message associated with the exception.
     */
    UnexpectedParameterException(String msg) {
        super(msg)
    }
}