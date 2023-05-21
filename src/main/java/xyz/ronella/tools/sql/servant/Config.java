package xyz.ronella.tools.sql.servant;

import xyz.ronella.tools.sql.servant.command.Invoker;
import xyz.ronella.tools.sql.servant.command.impl.GetConfigText;
import xyz.ronella.tools.sql.servant.command.impl.ToJson;
import xyz.ronella.tools.sql.servant.conf.JsonConfig;
import xyz.ronella.tools.sql.servant.conf.JsonConfigWrapper;

import java.io.File;

/**
 * The class responsible for processing the configuration file.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
public class Config {

    private final String filename;
    private final String confDir;
    private JsonConfigWrapper jsonWrapper;
    private final String environment;

    private static final String ENV_VAR_CONF_DIR = "SQL_SERVANT_CONF_DIR";

    /**
     * Creates an instance of Config based on the default 'ss-default.json'
     */
    public Config() {
        this(null);
    }

    /**
     * Creates an instance of Config based on the filename provided.
     *
     * @param filename The filename of the configuration file excluding the extension
     *        (i.e. json).
     */
    public Config(final String filename) {
        this(null, filename);
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
    public Config(final String confDir, final String filename, final String environment, final boolean suffix) {
        final var externalConf = System.getenv(ENV_VAR_CONF_DIR);
        String calcConfDir = confDir;

        if (confDir==null && externalConf!=null) {
            calcConfDir = externalConf;
        }

        if (suffix && confDir!=null) {
            calcConfDir = String.format("%s%sconf", confDir, File.separator);
        }

        this.confDir = String.format("%s%s", new File(calcConfDir!=null ? calcConfDir : "./conf").getAbsolutePath(), File.separator);
        this.filename = String.format("%s%s.json", this.confDir, filename!=null ? filename : "ss-default");
        this.environment = environment;
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
    public Config(final String confDir, final String filename, final String environment) {
        this(confDir,filename, environment, true);
    }

    /**
     * Returns the environment to use.
     *
     * @return The environment to use.
     *
     * @since 2.1.0
     */
    public final String getEnvironment() {
        return environment;
    }

    /**
     * Creates an instance of Config based on the directory and filename provided.
     *
     * @param confDir The directory to contains the configuration file.
     * @param filename The filename of the configuration file excluding the extension
     *        (i.e. json).
     */
    public Config(final String confDir, final String filename, final boolean suffix) {
        this(confDir, filename, null, suffix);
    }

    /**
     * Creates an instance of Config based on the directory and filename provided.
     *
     * @param confDir The directory to contains the configuration file.
     * @param filename The filename of the configuration file excluding the extension
     *        (i.e. json).
     */
    public Config(final String confDir, final String filename) {
        this(confDir, filename, null, true);
    }

    /**
     * Returns the configuration filename that is being used.
     *
     * @return The filename of the configuration being used.
     */
    public String getConfigFilename() {
        return new File(this.filename).getAbsolutePath();
    }

    /**
     * Returns the configuration directory that is being used.
     *
     * @return The directory of the configuration being used.
     * @since 1.2.0
     */
    public String getConfigDirectory() {
        return new File(this.confDir).getAbsolutePath();
    }

    /**
     * Returns the query scripts directory.
     *
     * @return The directory of the query scripts.
     * @since 1.2.0
     */
    public String getScriptDirectory() {
        return new File(String.format("%s/../scripts", this.confDir)).getAbsolutePath();
    }

    /**
     * Returns the listener implementations directory.
     *
     * @return The directory of the listener implementations.
     * @since 1.2.0
     */
    public String getListenerDirectory() {
        return new File(String.format("%s/../listeners", this.confDir)).getAbsolutePath();
    }

    /**
     * Creates a JsonCcnfig instance based on the configuration being processed.
     *
     * @return An instance of JsonConfig.
     */
    public JsonConfig getConfigAsJson() {
        if (jsonWrapper==null) {
            final var jsonStr = Invoker.invoke(new GetConfigText(filename));
            if (jsonStr!=null) {
                jsonWrapper = new JsonConfigWrapper(this, Invoker.invoke(new ToJson<JsonConfig>(jsonStr, JsonConfig.class)));
            }
        }
        return jsonWrapper;
    }
}
