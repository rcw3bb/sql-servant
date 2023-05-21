package xyz.ronella.tools.sql.servant;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Holds the command line arguments.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
@NoArgsConstructor
@Data
public class CliArgs {

    /**
     * Overrides the all configured parallel properties.
     */
    private boolean parallel;

    /**
     * Specifies to do NOOP operation.
     */
    private boolean noop;

    /**
     * Holds the configuration file to use.
     */
    private String config;

    /**
     * Holds the environment associated to the configuration.
     *
     * @since 1.2.0
     */
    private String environment;

    /**
     * Holds the parameters to be used with configuration.
     *
     * @since 1.2.0
     */
    private Map<String, String> params;

    /**
     * Holds the configuration directory to search for configuration.
     *
     * @since 1.4.0
     */
    private String confDir;

    /**
     * Specifies to ignore ExecutionException
     *
     * @since 2.1.0
     */
    private boolean ignoreExecutionException;

    /**
     * Specifies to ignore TaskException
     *
     * @since 2.1.0
     */
    private boolean ignoreTaskException;

    /**
     * Private argument that is only used for testing.
     *
     * @since 2.1.0
     */
    private boolean isTestMode;

}