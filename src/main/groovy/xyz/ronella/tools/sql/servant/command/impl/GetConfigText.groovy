package xyz.ronella.tools.sql.servant.command.impl

import xyz.ronella.tools.sql.servant.command.ICommandO
import org.apache.logging.log4j.LogManager

/**
 * A command for reading the raw configuration file into text file.
 *
 * @author Ron Webb
 * @since 1.3.0
 */
class GetConfigText implements ICommandO<String> {

    public final static def LOG = LogManager.getLogger(GetConfigText.class.name)

    private String filename

    /**
     * Creates an instance of the GetConfigText command.
     *
     * @param filename The filename of the configuration file.
     */
    GetConfigText(String filename) {
        this.filename = filename
    }

    @Override
    String get() {
        File file = new File(this.filename)

        if (LOG.debugEnabled) {
            LOG.debug("Filename: ${file.absolutePath}")
        }

        if (file.exists() && file.canRead()) {
            file.text
        }
        else {
            null
        }
    }
}
