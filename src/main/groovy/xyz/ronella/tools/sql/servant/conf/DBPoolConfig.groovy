package xyz.ronella.tools.sql.servant.conf

/**
 * Holder of the configurations for database pool.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
class DBPoolConfig {

    /**
     * The minimum number of DataSource instances that are always ready to create a
     * connection.
     */
    Integer minIdle

    /**
     * The maximum number of DataSource instances that can be added from the minIdle.
     */
    Integer maxIdle

    /**
     * The maximum number of PreparedStatement instances before blocking.
     */
    Integer maxOpenPreparedStatements

    /**
     * Holds the external filename of the DB pool configuration.
     *
     * @since 1.3.0
     */
    String filename
}
