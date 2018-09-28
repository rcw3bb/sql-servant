package xyz.ronella.tools.sql.servant

import com.google.gson.Gson

import xyz.ronella.tools.sql.servant.conf.JsonConfig
import xyz.ronella.tools.sql.servant.conf.JsonConfigWrapper

class Config {

    private String filename
    private String confDir

    Config() {
        this(null)
    }

    Config(String filename) {
        this(null, filename)
    }

    Config(String confDir, String filename) {
        this.confDir = "${new File(confDir?:'./conf').absolutePath}${File.separator}"
        this.filename = "${this.confDir}${filename?:'default'}.json"
    }

    String getConfigFilename() {
        new File(this.filename).getAbsolutePath()
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

    JsonConfig getConfigAsJson() {
        def jsonStr = this.configAsString
        if (jsonStr) {
            new JsonConfigWrapper(new Gson().fromJson(jsonStr, JsonConfig.class))
        }
        else {
            null
        }
    }
}
