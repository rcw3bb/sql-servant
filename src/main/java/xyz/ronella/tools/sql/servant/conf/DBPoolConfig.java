package xyz.ronella.tools.sql.servant.conf;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Holder of the configurations for database pool.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
@NoArgsConstructor
@Data
public class DBPoolConfig {

    /**
     * The minimum number of DataSource instances that are always ready to create a
     * connection.
     */
    private Integer minIdle;

    /**
     * The maximum number of DataSource instances that can be added from the minIdle.
     */
    private Integer maxIdle;

    /**
     * The maximum number of PreparedStatement instances before blocking.
     */
    private Integer maxOpenPreparedStatements;

    /**
     * Holds the external filename of the DB pool configuration.
     *
     * @since 1.3.0
     */
    private String filename;

}
