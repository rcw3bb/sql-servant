package xyz.ronella.tools.sql.servant.impl;

import static xyz.ronella.tools.sql.servant.QueryServant.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.ronella.tools.sql.servant.CliArgs;
import xyz.ronella.tools.sql.servant.Config;
import xyz.ronella.tools.sql.servant.IOperation;
import xyz.ronella.tools.sql.servant.IStatus;
import xyz.ronella.tools.sql.servant.conf.QueriesConfig;
import xyz.ronella.tools.sql.servant.db.QueryModeException;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * An implementation of Callable that handles parallel processing for the succeeding
 * configured instances of QueriesConfig.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
public class ServantNextOperationTask implements Callable<IStatus> {

    public final static Logger LOG = LogManager.getLogger(ServantNextOperationTask.class);

    final private IOperation operation;
    final private List<Future<IStatus>> globalFutures;
    final private List<Future<IStatus>> localFutures;
    final private Config config;
    final private QueriesConfig qryConfig;
    final private CliArgs cliArgs;

    /**
     * Creates an instance of ServantNextOperationTask
     *
     * @param operation An implementation of IOperation.
     * @param globalFutures All the instances of the collected type Futures.
     * @param localFutures The instance of the collected type Futures of the collected
     *          parent task.
     * @param config An instance of Config.
     * @param qryConfig An instance of QueriesConfig
     * @param cliArgs An instance of CliArgs.
     */
    public ServantNextOperationTask(final IOperation operation, final List<Future<IStatus>> globalFutures, final List<Future<IStatus>> localFutures,
                             final Config config, final QueriesConfig qryConfig, final CliArgs cliArgs) {
        this.operation = operation;
        this.globalFutures = globalFutures;
        this.localFutures = localFutures;
        this.config = config;
        this.qryConfig = qryConfig;
        this.cliArgs = cliArgs;
        usageLevelUp();
    }

    private void processSubsequentQuery() throws QueryModeException {
        final var qryConfigNext = qryConfig.getNext();

        if (LOG.isTraceEnabled()) {
            LOG.trace(String.format("Description: %s", qryConfigNext.getDescription()));
            LOG.trace(String.format("Connection String: %s", qryConfigNext.getConnectionString()));
        }

        operation.perform(localFutures, config, qryConfigNext, cliArgs);
    }

    /**
     * The actual task to run by the engine which can also be called directly if not in
     * parallel mode.
     *
     * @return An implmentation of IStatus.
     */
    @Override
    public IStatus call() {
        var isSuccessful = false;

        try {
            var isEverythingSuccessful = true;
            for (final var localFuture : localFutures) {
                try {
                    isEverythingSuccessful = localFuture.get().isSuccessful() && isEverythingSuccessful;
                }
                catch(Exception exception) {
                    LOG.error(exception);
                    if (!cliArgs.isIgnoreTaskException()) {
                        isEverythingSuccessful = false;
                        setTaskException(true);
                    }
                }
            }
            if (isEverythingSuccessful) {
                processSubsequentQuery();
                isSuccessful = true;
            }
            else {
                LOG.warn(String.format("[%s] Premature exit", qryConfig.getDescription()));
            }
        } catch (QueryModeException e) {
            throw new RuntimeException(e);
        } finally {
            usageLevelDown();
        }

        return new DefaultStatus(isSuccessful);
    }
}
