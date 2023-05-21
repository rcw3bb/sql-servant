package xyz.ronella.tools.sql.servant.conf;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The JSON instance representation of the target configuration.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
@NoArgsConstructor
@Data
public class JsonConfig {

    /**
     * Holds an instance of DefaultConfiguration based on configuration.
     */
    private DefaultConfig defaults;

    /**
     * Holds an instance of DBPoolConfig based on configuration.
     */
    private DBPoolConfig dbPoolConfig;

    /**
     * Holds all the instances of the QueriesConfiguration based on configuration.
     */
    private QueriesConfig[] queries;

    /**
     * Holds all the parameters configuration.
     * @since 1.2.0
     */
    private ParamConfig[] params;
}
