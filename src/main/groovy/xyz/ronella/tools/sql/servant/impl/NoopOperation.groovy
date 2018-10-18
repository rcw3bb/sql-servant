package xyz.ronella.tools.sql.servant.impl

import org.apache.log4j.Logger
import xyz.ronella.tools.sql.servant.CliArgs
import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.IOperation
import xyz.ronella.tools.sql.servant.IStatus
import xyz.ronella.tools.sql.servant.conf.QueriesConfig
import xyz.ronella.tools.sql.servant.db.QueryModeWrapper

import java.util.concurrent.Future

/**
 * An implementation of IOperation that handles the NOOP option.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
class NoopOperation implements IOperation {

    public final static def LOG = Logger.getLogger(NoopOperation.class.name)

    /**
     * The actual implmentation that handles NOOP operation.
     *
     * @param futures An instance of collected instances of typed Futures.
     * @param config An instance of Config.
     * @param qryConfig An instance QueriesConfig
     * @param cliArgs An instance of CliArgs
     */
    @Override
    def perform(List<Future<IStatus>> futures, Config config, QueriesConfig qryConfig, CliArgs cliArgs) {
        LOG.info "---[${qryConfig.description}]${cliArgs.parallel || qryConfig.parallel ? '[PARALLEL]' : ''}---"
        LOG.info "Connection String: ${qryConfig.connectionString}"
        LOG.info "Mode: ${new QueryModeWrapper(qryConfig.mode).mode}"
        def queries = qryConfig.queries
        if (queries && queries.length > 0) {
            queries.each {query ->
                def newQuery = ParamManager.applyParams(cliArgs.params, query)
                LOG.info("Will run: ${newQuery}")
            }
        }
        if (qryConfig.next) {
            perform(futures, config, qryConfig.next, cliArgs)
        }
    }
}
