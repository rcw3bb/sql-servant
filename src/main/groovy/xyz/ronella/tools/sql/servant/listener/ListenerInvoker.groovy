package xyz.ronella.tools.sql.servant.listener

import org.apache.log4j.Logger
import xyz.ronella.tools.sql.servant.conf.QueriesConfig

class ListenerInvoker {

    public final static def LOG = Logger.getLogger(ListenerInvoker.class.name)

    private QueriesConfig qryConfig

    ListenerInvoker(QueriesConfig qryConfig) {
        this.qryConfig = qryConfig
    }

    private static String getDateArg() {
        new Date().format('dd-MM-yyyy')
    }

    private String getCommand() {
        qryConfig.listeners.command
    }

    private static void runCommand(String cmd) {
        if (cmd) {
            def output = new StringBuilder()
            def error = new StringBuilder()

            def proc = cmd.execute()
            proc.waitForProcessOutput(output, error)

            LOG.debug(output)
            if (error.length() > 0) {
                LOG.error(error)
            }
        }
    }

    void invokeStartListener(String cmd, String description, String query) {
        if (command) {
            def cmdToRun = "${command} \"\"${cmd}\" \"${dateArg}\" \"${description}\" \"${query}\"\""
            LOG.debug("[${qryConfig.description}] ${cmdToRun}")
            runCommand(cmdToRun)
        }
    }

    void invokeDataListener(String cmd, String description, String query, String params) {
        String args = params.split(',').inject(new StringBuilder(),
                {___output, param ->
                    ___output.append(___output.length()>0?' ':'')
                    ___output.append('\"').append(param).append('\"')
                }).toString()

        if (command) {
            def cmdToRun = "${command} \"\"${cmd}\" \"${dateArg}\" \"${description}\" \"${query}\" ${args}\""
            LOG.debug("[${qryConfig.description}] ${cmdToRun}")
            runCommand(cmdToRun)
        }
    }

    void invokeCompleteListener(String cmd, String description, String query, String success) {
        if (command) {
            def cmdToRun = "${command} \"\"${cmd}\" \"${dateArg}\" \"${description}\" \"${query}\" \"${success}\"\""
            LOG.debug("[${qryConfig.description}] ${cmdToRun}")
            runCommand(cmdToRun)
        }
    }
}
