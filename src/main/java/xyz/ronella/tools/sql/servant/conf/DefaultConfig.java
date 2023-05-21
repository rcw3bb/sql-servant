package xyz.ronella.tools.sql.servant.conf;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Holds the default configuration to use if some of the properties of an instance
 * of QueryConfig has no value.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
@NoArgsConstructor
@Data
public class DefaultConfig {

    /**
     * Holds the description of the group of queries.
     */
    private String description;

    /**
     * Holds the default jdbcDriver to use.
     */
    private String jdbcDriver;

    /**
     * Holds the default connection string to use.
     */
    private String connectionString;

    /**
     * Holds the default username to use.
     */
    private String username;

    /**
     * Holds the default password to use.
     */
    private String password;

    /**
     * Holds the default mode to use. This can be either "stmt" (i.e. default)
     * or "query".
     */
    private String mode;

    /**
     * Specifies to use the windowsAuthentication for sqlserver.
     */
    private Boolean windowsAuthentication;

    /**
     * Specifies to use parallel processing.
     */
    private Boolean parallel;

    /**
     * Holds the configured listeners
     *
     * @since 1.2.0
     */
    private ListenersConfig listeners;

    /**
     * Holds the external filename of the default configuration.
     *
     * @since 1.3.0
     */
    private String filename;
}
