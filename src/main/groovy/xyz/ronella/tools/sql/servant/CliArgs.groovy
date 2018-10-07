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
}