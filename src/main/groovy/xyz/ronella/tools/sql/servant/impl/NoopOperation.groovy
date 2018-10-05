package xyz.ronella.tools.sql.servant.impl

import org.apache.log4j.Logger
import xyz.ronella.tools.sql.servant.CliArgs
import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.IOperation
import xyz.ronella.tools.sql.servant.IStatus
import xyz.ronella.tools.sql.servant.conf.QueriesConfig
import xyz.ronella.tools.sql.servant.db.QueryModeWrapper

import java.util.concurrent.Future

class NoopOperation implements IOperation {

    public final static def LOG = Logger.getLogger(NoopOperation.class.name)

    @Override
    def perform(List<Future<IStatus>> futures, Config config, QueriesConfig qryConfig, CliArgs cliArgs) {
        LOG.info "---[${qryConfig.description}]${cliArgs.parallel || qryConfig.parallel ? '[PARALLEL]' : ''}---"
        LOG.info "Connection String: ${qryConfig.connectionString}"
        LOG.info "Mode: ${new QueryModeWrapper(qryConfig.mode).mode}"
        def queries = qryConfig.queries
        if (queries && queries.length > 0) {
            queries.each {query ->
                LOG.info("Will run: ${query}")
            }
        }
        if (qryConfig.next) {
            perform(futures, config, qryConfig.next, cliArgs)
        }
    }
}
