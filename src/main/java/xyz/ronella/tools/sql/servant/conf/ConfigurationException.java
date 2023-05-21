package xyz.ronella.tools.sql.servant.conf;

/**
 * Thrown if there is a configuration exception.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
public class ConfigurationException extends RuntimeException {

    /**
     * Creates an instance of ConfigurationException.
     *
     * @param message The actual exception to display.
     */
    ConfigurationException(final String message) {
        super(message);
    }
}