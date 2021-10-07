package xyz.ronella.tools.sql.servant.impl

import org.apache.log4j.Logger

import xyz.ronella.tools.sql.servant.CliArgs
import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.ExecutionException
import xyz.ronella.tools.sql.servant.IOperation
import xyz.ronella.tools.sql.servant.IStatus
import xyz.ronella.tools.sql.servant.QueryServant
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

    public final static def LOG = Logger.getLogger(DefaultServantOperation.class.name)

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

        LOG.info "---[${description}]${qryConfig.parallel ? '[PARALLEL]' : ''}---"
        LOG.info "[${description}] Connection String: ${qryConfig.connectionString}"
        LOG.info "[${description}] Mode: ${new QueryModeWrapper(qryConfig.mode).mode}"

        List<Future<IStatus>> localFutures = new ArrayList<>()
        boolean continueNext = true

        def queries = qryConfig.queries
        if (queries && queries.length > 0) {
            queries.each { query ->

                def updatedQuery = ParamManager.applyParams(cliArgs.params, query)

                def servantTask = new ServantOperationTask(config, qryConfig, updatedQuery)

                if (qryConfig.parallel) {
                    ParallelEngine.instance.with {
                        if (!isStarted()) {
                            start()
                        }
                        def future = process(servantTask)
                        localFutures.add(future)
                        futures.add(future)
                    }
                } else {
                    continueNext = servantTask.call().isSuccessful() && continueNext
                }
            }
        }

        if (continueNext) {
            if (qryConfig.next) {
                def nextTask = new ServantNextOperationTask(this, futures, localFutures, config, qryConfig, cliArgs)
                if (qryConfig.parallel) {
                    futures.add(ParallelEngine.instance.process(nextTask))
                } else {
                    nextTask.call()
                }
            }
        } else {
            LOG.warn("[${description}] Premature exit")
        }

        if (!cliArgs.ignoreExecutionException && QueryServant.hasError) {
            throw new ExecutionException()
        }
    }
}
