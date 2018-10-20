package xyz.ronella.tools.sql.servant.impl

/**
 * The exception thrown if the expected parameters doesn't have values.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
class UnresolvedParametersException extends ParameterException {

    /**
     * Creates an instance of UnresolvedParametersException.
     * @param msg The associated message with the exception.
     */
    UnresolvedParametersException(String msg) {
        super(msg)
    }
}
