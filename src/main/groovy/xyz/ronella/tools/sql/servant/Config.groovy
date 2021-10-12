package xyz.ronella.tools.sql.servant

import xyz.ronella.tools.sql.servant.command.Invoker
import xyz.ronella.tools.sql.servant.command.impl.GetConfigText
import xyz.ronella.tools.sql.servant.command.impl.ToJson
import xyz.ronella.tools.sql.servant.conf.JsonConfig
import xyz.ronella.tools.sql.servant.conf.JsonConfigWrapper

/**
 * The class responsible for processing the configuration file.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
class Config {

    private String filename
    private String confDir
    private JsonConfigWrapper jsonWrapper
    private String environment

    private static final String ENV_VAR_CONF_DIR = 'SQL_SERVANT_CONF_DIR'

    /**
     * Creates an instance of Config based on the default 'ss-default.json'
     */
    Config() {
        this(null)
    }

    /**
     * Creates an instance of Config based on the filename provided.
     *
     * @param filename The filename of the configuration file excluding the extension
     *        (i.e. json).
     */
    Config(String filename) {
        this(null, filename)
    }

    /**
     * Creates an instance of Config based on the directory and filename provided.
     *
     * @param confDir The directory to contains the configuration file.
     * @param filename The filename of the configuration file excluding the extension
     *        (i.e. json).
     * @param environment The environment to run.
     *
     * @since 2.1.0
     */
    Config(String confDir, String filename, String environment, boolean suffix = true) {
        String externalConf = System.getenv(ENV_VAR_CONF_DIR)

        if (!confDir && externalConf) {
            confDir = externalConf
        }

        if (suffix && confDir) {
            confDir = "${confDir}${File.separator}conf"
        }

        this.confDir = "${new File(confDir?:'./conf').absolutePath}${File.separator}"
        this.filename = "${this.confDir}${filename?:'ss-default'}.json"
        this.environment = environment
    }

    /**
     * Returns the environment to use.
     *
     * @return The environment to use.
     *
     * @since 2.1.0
     */
    String getEnvironment() {
        return environment
    }

    /**
     * Creates an instance of Config based on the directory and filename provided.
     *
     * @param confDir The directory to contains the configuration file.
     * @param filename The filename of the configuration file excluding the extension
     *        (i.e. json).
     */
    Config(String confDir, String filename, boolean suffix = true) {
        this(confDir, filename, null, suffix)
    }

    /**
     * Returns the configuration filename that is being used.
     *
     * @return The filename of the configuration being used.
     */
    String getConfigFilename() {
        new File(this.filename).getAbsolutePath()
    }

    /**
     * Returns the configuration directory that is being used.
     *
     * @return The directory of the configuration being used.
     * @since 1.2.0
     */
    String getConfigDirectory() {
        new File(this.confDir).getAbsolutePath()
    }

    /**
     * Returns the query scripts directory.
     *
     * @return The directory of the query scripts.
     * @since 1.2.0
     */
    String getScriptDirectory() {
        new File("${this.confDir}/../scripts").getAbsolutePath()
    }

    /**
     * Returns the listener implementations directory.
     *
     * @return The directory of the listener implementations.
     * @since 1.2.0
     */
    String getListenerDirectory() {
        new File("${this.confDir}/../listeners").getAbsolutePath()
    }

    /**
     * Creates a JsonCcnfig instance based on the configuration being processed.
     *
     * @return An instance of JsonConfig.
     */
    JsonConfig getConfigAsJson() {
        if (!jsonWrapper) {
            def jsonStr = Invoker.invoke(new GetConfigText(filename))
            if (jsonStr) {
                jsonWrapper = new JsonConfigWrapper(this, Invoker.invoke(new ToJson<JsonConfig>(jsonStr, JsonConfig.class)))
            }
        }
        jsonWrapper
    }
}
