package xyz.ronella.tools.sql.servant

/**
 * Holds the command line arguments.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
class CliArgs {

    /**
     * Overrides the all configured parallel properties.
     */
    boolean parallel

    /**
     * Specifies to do NOOP operation.
     */
    boolean noop

    /**
     * Holds the configuration file to use.
     */
    String config

    /**
     * Holds the environment associated to the configuration.
     *
     * @since 1.2.0
     */
    String environment

    /**
     * Holds the parameters to be used with configuration.
     *
     * @since 1.2.0
     */
    Map<String, String> params
}