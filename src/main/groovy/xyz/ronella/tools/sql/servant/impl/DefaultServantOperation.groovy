package xyz.ronella.tools.sql.servant.impl

import org.apache.log4j.Logger
import xyz.ronella.tools.sql.servant.CliArgs
import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.IOperation
import xyz.ronella.tools.sql.servant.conf.QueriesConfig
import xyz.ronella.tools.sql.servant.db.DBManager
import xyz.ronella.tools.sql.servant.db.QueryModeWrapper

class DefaultServantOperation implements IOperation {

    public final static def LOG = Logger.getLogger(DefaultServantOperation.class.name)

    @Override
    def perform(Config config, QueriesConfig qryConfig, CliArgs cliArgs) {
        LOG.info "---[${qryConfig.description}]${cliArgs.parallel || qryConfig.parallel ? '[PARALLEL]' : ''}---"
        LOG.info "Connection String: ${qryConfig.connectionString}"
        LOG.info "Mode: ${new QueryModeWrapper(qryConfig.mode).mode}"
        def queries = qryConfig.queries
        if (queries && queries.length > 0) {
            queries.each {query ->
                LOG.info("Executing: ${query}")
                try {
                    DBManager.getInstance(config.configAsJson.dbPoolConfig).runStatement(qryConfig, query)
                    LOG.info("Success running: ${query}")
                }
                catch(Exception e) {
                    LOG.info("Failed running: ${query}")
                    LOG.error(e.fillInStackTrace())
                }
            }
        }
        if (qryConfig.next) {
            perform(config, qryConfig.next, cliArgs)
        }
    }
}
