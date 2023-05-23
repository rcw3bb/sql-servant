package xyz.ronella.tools.sql.servant.impl;

import static xyz.ronella.tools.sql.servant.QueryServant.*;

import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import xyz.ronella.tools.sql.servant.*;
import xyz.ronella.tools.sql.servant.async.ParallelEngine;
import xyz.ronella.tools.sql.servant.conf.QueriesConfig;
import xyz.ronella.tools.sql.servant.db.QueryModeException;
import xyz.ronella.tools.sql.servant.db.QueryModeWrapper;
import xyz.ronella.tools.sql.servant.listener.HasActiveListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * An implementation of IOperation that handles non-NOOP operation.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
public class DefaultServantOperation implements IOperation {

    public final static Logger LOG = LogManager.getLogger(DefaultServantOperation.class);

    public void processSubsequentQuery(final List<Future<IStatus>> futures, final List<Future<IStatus>> localFutures, final Config config, final QueriesConfig qryConfig, final CliArgs cliArgs) {
        if (qryConfig.getNext()!=null) {
            final var nextTask = new ServantNextOperationTask(this, futures, localFutures, config, qryConfig, cliArgs);
            final var parallel = qryConfig.getParallel();
            if (parallel!=null && parallel) {
                futures.add(ParallelEngine.getInstance().process(nextTask));
            } else {
                try {
                    nextTask.call();
                }
                catch (TaskException te) {
                    if (!cliArgs.isIgnoreTaskException()) {
                        LOG.error(te);
                        setTaskException(true);
                    }
                }
            }
        }
    }

    private void header(final String description, final QueriesConfig qryConfig) throws QueryModeException {
        LOG.info(String.format("---[%s]%s---", description, qryConfig.getParallel()!=null ? "[PARALLEL]" : ""));
        LOG.info(String.format("[%s] Connection String: %s", description, qryConfig.getConnectionString()));
        LOG.info(String.format("[%s] Mode: %s}", description, new QueryModeWrapper(qryConfig.getMode()).getMode()));
    }

    private void invokeWithParallelEngine(final List<Future<IStatus>> localFutures, final ServantOperationTask servantTask) {
        final var parallel = ParallelEngine.getInstance();
        if (!parallel.isStarted()) {
            parallel.start();
        }
        final var future = parallel.process(servantTask);
        localFutures.add(future);
    }

    /**
     * The default logic to actually execute the configured queries.
     *
     * @param futures An instance of collected instances of typed Futures.
     * @param config An instance of Config.
     * @param qryConfig An instance QueriesConfig
     * @param cliArgs An instance of CliArgs
     */
    @Override
    public void perform(final List<Future<IStatus>> futures, final Config config, final QueriesConfig qryConfig, final CliArgs cliArgs) throws QueryModeException {
        final var description = qryConfig.getDescription();

        if (cliArgs.isParallel() && !(new Validate(new HasActiveListener()).check(qryConfig.getListeners()))) {
            qryConfig.setParallel(true);
        }

        header(description, qryConfig);

        final List<Future<IStatus>> localFutures = new ArrayList<>();
        boolean continueNext = true;

        final var queries = qryConfig.getQueries();
        if (queries != null) {
            for (final var query : queries) {
                try {
                    if (hasException(cliArgs)) {
                        continueNext = false;
                        break;
                    }
                    final var updatedQuery = ParamManager.applyParams(cliArgs.getParams(), query);
                    final var servantTask = new ServantOperationTask(config, qryConfig, updatedQuery);
                    final var parallel = qryConfig.getParallel();
                    if (parallel != null && parallel) {
                        invokeWithParallelEngine(localFutures, servantTask);
                    } else {
                        continueNext = servantTask.call().isSuccessful() && continueNext;
                    }
                } catch (TaskException te) {
                    if (!cliArgs.isIgnoreTaskException()) {
                        LOG.error(te);
                        setTaskException(true);
                        continueNext = false;
                        break;
                    }
                }
            }
        }

        if (continueNext) {
            processSubsequentQuery(futures, localFutures, config, qryConfig, cliArgs);
        } else {
            LOG.warn(String.format("[%s] Premature exit", description));
        }
    }
}
