package xyz.ronella.tools.sql.servant.conf

/**
 * Thrown if there is a configuration exception.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
class ConfigurationException extends Exception {

    /**
     * Creates an instance of ConfigurationException.
     *
     * @param message The actual exception to display.
     */
    ConfigurationException(String message) {
        super(message)
    }
}
