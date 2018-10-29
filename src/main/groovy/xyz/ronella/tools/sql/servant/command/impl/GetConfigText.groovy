package xyz.ronella.tools.sql.servant.command.impl

import org.apache.log4j.Logger
import xyz.ronella.tools.sql.servant.command.ICommandO

class GetConfigText implements ICommandO<String> {

    public final static def LOG = Logger.getLogger(GetConfigText.class.name)

    private String filename

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
