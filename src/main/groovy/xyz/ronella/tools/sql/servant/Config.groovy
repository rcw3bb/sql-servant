package xyz.ronella.tools.sql.servant

import com.google.gson.Gson

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
     */
    Config(String confDir, String filename) {
        this.confDir = "${new File(confDir?:'./conf').absolutePath}${File.separator}"
        this.filename = "${this.confDir}${filename?:'ss-default'}.json"
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

    private String getConfigAsString() {
        File file = new File(this.filename)
        if (file.exists() && file.canRead()) {
            file.text
        }
        else {
            null
        }
    }

    /**
     * Creates a JsonCcnfig instance based on the configuration being processed.
     *
     * @return An instance of JsonConfig.
     */
    JsonConfig getConfigAsJson() {
        def jsonStr = this.configAsString
        if (jsonStr) {
            new JsonConfigWrapper(this, new Gson().fromJson(jsonStr, JsonConfig.class))
        }
        else {
            null
        }
    }
}
