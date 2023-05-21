package xyz.ronella.tools.sql.servant;

/**
 * A decorator for config to consider the environment in finding the
 *
 * @author Ron Webb
 * @since 1.2.0
 */
public class ConfigByEnv {

    private final Config config;

    /**
     * Creates an instance of ConfigByEnv
     *
     * @param config An instance of config.
     */
    public ConfigByEnv(final Config config) {
        this.config = config;
    }

    /**
     * Creates an instance of config based on environment.
     *
     * @param cliArgs An instance of CliArgs that has the environment information.
     * @return An instance of Config based on environment.
     */
    public Config createConfigByEnv(final CliArgs cliArgs) {
        final var env = cliArgs.getEnvironment();
        if (env!=null) {
            final var filename = String.format("%s.%s", cliArgs.getConfig(), env);
            final var confByEnv = new Config(config.getConfigDirectory(),filename, false);
            final var jsonConf = confByEnv.getConfigAsJson();
            if (jsonConf!=null) {
                return confByEnv;
            }
        }

        return config;
    }


}
