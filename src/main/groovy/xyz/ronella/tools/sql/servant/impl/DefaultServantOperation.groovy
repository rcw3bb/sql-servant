package xyz.ronella.tools.sql.servant.impl

import static xyz.ronella.tools.sql.servant.QueryServant.*

import org.apache.logging.log4j.LogManager

import xyz.ronella.tools.sql.servant.CliArgs
import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.IOperation
import xyz.ronella.tools.sql.servant.IStatus
import xyz.ronella.tools.sql.servant.TaskException
import xyz.ronella.tools.sql.servant.Validate
import xyz.ronella.tools.sql.servant.async.ParallelEngine
import xyz.ronella.tools.sql.servant.conf.QueriesConfig
import xyz.ronella.tools.sql.servant.db.QueryModeWrapper
import xyz.ronella.tools.sql.servant.listener.HasActiveListener

import java.util.concurrent.Future

/**
 * An implementation of IOperation that handles non-NOOP operation.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
class DefaultServantOperation implements IOperation {

    public final static def LOG = LogManager.getLogger(DefaultServantOperation.class.name)

    private def processSubsequentQuery(List<Future<IStatus>> futures, List<Future<IStatus>> localFutures, Config config, QueriesConfig qryConfig, CliArgs cliArgs) {
        if (qryConfig.next) {
            def nextTask = new ServantNextOperationTask(this, futures, localFutures, config, qryConfig, cliArgs)
            if (qryConfig.parallel) {
                futures.add(ParallelEngine.instance.process(nextTask))
            } else {
                try {
                    nextTask.call()
                }
                catch (TaskException te) {
                    if (!cliArgs.ignoreTaskException) {
                        LOG.error(te)
                        setHasTaskException(true)
                    }
                }
            }
        }
    }

    private def header(String description, QueriesConfig qryConfig) {
        LOG.info "---[${description}]${qryConfig.parallel ? '[PARALLEL]' : ''}---"
        LOG.info "[${description}] Connection String: ${qryConfig.connectionString}"
        LOG.info "[${description}] Mode: ${new QueryModeWrapper(qryConfig.mode).mode}"
    }

    private def invokeWithParallelEngine(List<Future<IStatus>> localFutures, ServantOperationTask servantTask) {
        ParallelEngine.instance.with {
            if (!isStarted()) {
                start()
            }
            def future = process(servantTask)
            localFutures.add(future)
        }
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
    def perform(List<Future<IStatus>> futures, Config config, QueriesConfig qryConfig, CliArgs cliArgs) {
        def description = qryConfig.description

        if (cliArgs.parallel && !(new Validate(new HasActiveListener()).check(qryConfig.listeners))) {
            qryConfig.parallel=true
        }

        header(description, qryConfig)

        List<Future<IStatus>> localFutures = new ArrayList<>()
        boolean continueNext = true

        def queries = qryConfig.queries
        if (queries && queries.length > 0) {
            for (query in queries) {
                if (hasException(cliArgs)) {
                    continueNext = false
                    break
                }
                def updatedQuery = ParamManager.applyParams(cliArgs.params, query)
                def servantTask = new ServantOperationTask(config, qryConfig, updatedQuery)

                if (qryConfig.parallel) {
                    invokeWithParallelEngine(localFutures, servantTask)
                } else {
                    try {
                        continueNext = servantTask.call().isSuccessful() && continueNext
                    }
                    catch (TaskException te) {
                        if (!cliArgs.ignoreTaskException) {
                            LOG.error(te)
                            setHasTaskException(true)
                            continueNext = false
                            break
                        }
                    }
                }
            }
        }

        if (continueNext) {
            processSubsequentQuery(futures, localFutures, config, qryConfig, cliArgs)
        } else {
            LOG.warn("[${description}] Premature exit")
        }
    }
}
