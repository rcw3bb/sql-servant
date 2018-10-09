package xyz.ronella.tools.sql.servant

/**
 * A decorator for config to consider the environment in finding the
 *
 * @author Ron Webb
 * @since 1.2.0
 */
class ConfigByEnv {

    private Config config

    /**
     * Creates an instance of ConfigByEnv
     *
     * @param config An instance of config.
     */
    ConfigByEnv(Config config) {
        this.config = config
    }

    /**
     * Creates an instance of config based on environment.
     *
     * @param cliArgs An instance of CliArgs that has the environment information.
     * @return An instance of Config based on environment.
     */
    Config createConfigByEnv(CliArgs cliArgs) {

        if (cliArgs.environment) {
            Config confByEnv = new Config(config.configDirectory,"${cliArgs.config}.${cliArgs.environment}")
            if (confByEnv.configAsJson) {
                return confByEnv
            }
        }

        return config
    }


}
