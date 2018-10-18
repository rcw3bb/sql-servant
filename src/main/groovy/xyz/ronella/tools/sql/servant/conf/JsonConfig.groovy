package xyz.ronella.tools.sql.servant.conf

/**
 * The JSON instance representation of the target configuration.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
class JsonConfig {

    /**
     * Holds an instance of DefaultConfiguration based on configuration.
     */
    DefaultConfig defaults

    /**
     * Holds an instance of DBPoolConfig based on configuration.
     */
    DBPoolConfig dbPoolConfig

    /**
     * Holds all the instances of the QueriesConfiguration based on configuration.
     */
    QueriesConfig[] queries

    /**
     * Holds all the parameters configuration.
     */
    ParamConfig[] params
}
