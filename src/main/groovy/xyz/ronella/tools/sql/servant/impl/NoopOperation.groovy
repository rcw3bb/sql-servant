package xyz.ronella.tools.sql.servant.impl

import org.apache.log4j.Logger
import xyz.ronella.tools.sql.servant.CliArgs
import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.IOperation
import xyz.ronella.tools.sql.servant.conf.QueriesConfig

class NoopOperation implements IOperation {

    public final static def LOG = Logger.getLogger(NoopOperation.class.name)

    @Override
    def perform(Config config, QueriesConfig qryConfig, CliArgs cliArgs) {
        def queries = qryConfig.queries
        if (queries && queries.length > 0) {
            queries.each {query ->
                LOG.info("Will run: ${query}")
            }
        }
    }
}
