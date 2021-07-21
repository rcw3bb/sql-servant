package xyz.ronella.tools.sql.servant.listener

import org.apache.log4j.Logger
import xyz.ronella.tools.sql.servant.conf.QueriesConfig

import java.text.SimpleDateFormat

/**
 * The main class that is responsible for invoking the listener.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
class ListenerInvoker {

    public final static def LOG = Logger.getLogger(ListenerInvoker.class.name)
    private final static String DEFAULT_FILTER_REPLACEMENT = '_'

    private QueriesConfig qryConfig

    /**
     * Creates an instance of ListenerInvoker.
     *
     * @param qryConfig The instance of QueriesConfig where listeners were attached.
     */
    ListenerInvoker(QueriesConfig qryConfig) {
        this.qryConfig = qryConfig
    }

    private static String getDateArg() {
        def dateFormatter = new SimpleDateFormat('dd-MM-yyyy')
        dateFormatter.format(new Date())
    }

    private String getFilter() {
        qryConfig.listeners.filter
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
                throw new ListenerException("Failed to execute: ${cmd}")
            }
        }
    }

    private String applyFilter(String data) {
        filter.inject(data, { String ___result, String ___item ->
            ___result.replaceAll(___item, DEFAULT_FILTER_REPLACEMENT)
        })
    }

    /**
     * The method responsible for invoking the start listener.
     *
     * @param cmd The configured name of the listener.
     * @param description The description of the query begin processed.
     * @param query The actual query being executed.
     * @param firstTime Indicates if the current query description is being processed the first time.
     */
    void invokeStartListener(String cmd, String description, String query, boolean firstTime) {
        if (command) {
            def cmdToRun = "${command} \"\"${cmd}\" \"${dateArg}\" \"${applyFilter(description)}\" \"${applyFilter(query)}\" \"${firstTime}\"\""
            LOG.debug("[${qryConfig.description}] ${cmdToRun}")
            runCommand(cmdToRun)
        }
    }

    /**
     * The method responsible for invoking the data listener.
     *
     * @param cmd The configured name of the listener.
     * @param description The description of the query begin processed.
     * @param query The actual query being executed.
     * @param params The data parameters returned after executing the query.
     */
    void invokeDataListener(String cmd, String description, String query, String params) {
        String args = params.split(',').inject(new StringBuilder(),
                {___output, param ->
                    ___output.append(___output.length()>0?' ':'')
                    ___output.append('\"').append(applyFilter(param)).append('\"')
                }).toString()

        if (command) {
            def cmdToRun = "${command} \"\"${cmd}\" \"${dateArg}\" \"${applyFilter(description)}\" \"${applyFilter(query)}\" ${args}\""
            LOG.debug("[${qryConfig.description}] ${cmdToRun}")
            runCommand(cmdToRun)
        }
    }

    /**
     * The method responsible for invoking the complete listener.
     *
     * @param cmd The configured name of the listener.
     * @param description The description of the query begin processed.
     * @param query The actual query being executed.
     * @param success Holds true if the execution of the query is a success.
     */
    void invokeCompleteListener(String cmd, String description, String query, String success) {
        if (command) {
            def cmdToRun = "${command} \"\"${cmd}\" \"${dateArg}\" \"${applyFilter(description)}\" \"${applyFilter(query)}\" \"${success}\"\""
            LOG.debug("[${qryConfig.description}] ${cmdToRun}")
            runCommand(cmdToRun)
        }
    }
}
