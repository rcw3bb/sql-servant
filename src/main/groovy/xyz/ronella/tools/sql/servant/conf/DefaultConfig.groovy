package xyz.ronella.tools.sql.servant.conf

/**
 * Holds the default configuration to use if some of the properties of an instance
 * of QueryConfig has no value.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
class DefaultConfig {

    /**
     * Holds the default jdbcDriver to use.
     */
    String jdbcDriver

    /**
     * Holds the default connection string to use.
     */
    String connectionString

    /**
     * Holds the default username to use.
     */
    String username

    /**
     * Holds the default password to use.
     */
    String password

    /**
     * Holds the default mode to use. This can be either "stmt" (i.e. default)
     * or "query".
     */
    String mode

    /**
     * Specifies to use the windowsAuthentication for sqlserver.
     */
    Boolean windowsAuthentication

    /**
     * Specifies to use parallel processing.
     */
    Boolean parallel

    /**
     * Holds the configured listeners
     *
     * @since 1.2.0
     */
    ListenersConfig listeners

}
