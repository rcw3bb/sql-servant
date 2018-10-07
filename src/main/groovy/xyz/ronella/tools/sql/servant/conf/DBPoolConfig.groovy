package xyz.ronella.tools.sql.servant.conf

/**
 * Holder of the configurations for database pool.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
class DBPoolConfig {

    /**
     * The maximum number of DataSource instances that are always ready to create a
     * connection.
     */
    Integer minIdle

    /**
     * The maximum number of DataSource instances that can be added from the minIdle.
     */
    Integer maxIdle

    /**
     * The maximum number of PreparedStatement instances that will be fed to one of the
     * DataSource instance.
     */
    Integer maxOpenPreparedStatements
}
