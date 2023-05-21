package xyz.ronella.tools.sql.servant.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.ronella.tools.sql.servant.conf.QueriesConfig;
import xyz.ronella.trivial.handy.CommandRunner;
import xyz.ronella.trivial.handy.MissingCommandException;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

/**
 * The main class that is responsible for invoking the listener.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
public final class ListenerInvoker {

    public final static Logger LOG = LogManager.getLogger(ListenerInvoker.class);
    private final static String DEFAULT_FILTER_REPLACEMENT = "_";

    private final QueriesConfig qryConfig;

    /**
     * Creates an instance of ListenerInvoker.
     *
     * @param qryConfig The instance of QueriesConfig where listeners were attached.
     */
    public ListenerInvoker(final QueriesConfig qryConfig) {
        this.qryConfig = qryConfig;
    }

    private static String getDateArg() {
        final var dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormatter.format(new Date());
    }

    private String getFilter() {
        return qryConfig.getListeners().getFilter();
    }

    private String getCommand() {
        return qryConfig.getListeners().getCommand();
    }

    private static void runCommand(final String cmd) throws ListenerException {
        if (cmd!=null) {
            final var output = new StringBuilder();
            final var error = new StringBuilder();

            try {
                CommandRunner.runCommand(___pb -> ___pb.redirectErrorStream(true), (___output, ___error) ->
                        {
                            try(final var outputReader = new Scanner(___output);
                                final var errorReader = new Scanner(___error)) {

                                while (outputReader.hasNextLine()) {
                                    output.append(outputReader.nextLine()).append("\n");
                                }

                                while (errorReader.hasNextLine()) {
                                    error.append(errorReader.nextLine()).append("\n");
                                }
                            }
                        },
                        cmd);
            } catch (MissingCommandException mce) {
                LOG.error(mce.getMessage());
                throw new RuntimeException(mce);
            }

            LOG.debug(output);
            if (error.length() > 0) {
                throw new ListenerException(String.format("Failed to execute: %s", cmd));
            }
        }
    }

    private String applyFilter(final String data) {
        return data.replaceAll(getFilter(), DEFAULT_FILTER_REPLACEMENT);
    }

    /**
     * The method responsible for invoking the start listener.
     *
     * @param cmd The configured name of the listener.
     * @param description The description of the query to be processed.
     * @param query The actual query being executed.
     * @param firstTime Indicates if the current query description is being processed the first time.
     */
    public void invokeStartListener(final String cmd, final String description, final String query, boolean firstTime) throws ListenerException {
        final var command = getCommand();
        if (command!=null) {
            final var cmdToRun = String.format("%s \"\"%s\" \"%s\" \"%s\" \"%s\" \"%s\"\"",
                    command, cmd, getDateArg(), applyFilter(description), applyFilter(query), firstTime);
            LOG.debug(String.format("[%s] %s", qryConfig.getDescription(), cmdToRun));
            runCommand(cmdToRun);
        }
    }

    /**
     * The method responsible for invoking the data listener.
     *
     * @param cmd The configured name of the listener.
     * @param description The description of the query to be processed.
     * @param query The actual query being executed.
     * @param params The data parameters returned after executing the query.
     */
    public void invokeDataListener(final String cmd, final String description, final String query, final String params) throws ListenerException {
        final var args = Arrays.stream(params.split(",")).map(StringBuilder::new).reduce(new StringBuilder(),
                (___output, param) -> {
                        ___output.append(___output.length()>0?" ":"");
                        ___output.append("\"").append(applyFilter(param.toString())).append("\"");
                        return ___output;
                }).toString();
        final var command = getCommand();
        if (command!=null) {
            final var cmdToRun = String.format("%s \"\"%s\" \"%s\" \"%s\" \"%s\" %s\"",
                    command, cmd, getDateArg(), applyFilter(description), applyFilter(query), args);
            LOG.debug(String.format("[%s] %s", qryConfig.getDescription(), cmdToRun));
            runCommand(cmdToRun);
        }
    }

    /**
     * The method responsible for invoking the complete listener.
     *
     * @param cmd The configured name of the listener.
     * @param description The description of the query to be processed.
     * @param query The actual query being executed.
     * @param success Holds true if the execution of the query is a success.
     */
    public void invokeCompleteListener(final String cmd, final String description, final String query, final String success) throws ListenerException {
        final var command = getCommand();
        if (command!=null) {
            final var cmdToRun = String.format("%s \"\"%s\" \"%s\" \"%s\" \"%s\" \"%s\"\"",
                    command, cmd, getDateArg(), applyFilter(description), applyFilter(query), success);
            LOG.debug(String.format("[%s] %s", qryConfig.getDescription(), cmdToRun));
            runCommand(cmdToRun);
        }
    }
}
