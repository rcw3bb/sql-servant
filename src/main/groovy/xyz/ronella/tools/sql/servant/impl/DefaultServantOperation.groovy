package xyz.ronella.tools.sql.servant.impl

import org.apache.log4j.Logger
import xyz.ronella.tools.sql.servant.CliArgs
import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.IOperation
import xyz.ronella.tools.sql.servant.async.ParallelEngine
import xyz.ronella.tools.sql.servant.conf.QueriesConfig
import xyz.ronella.tools.sql.servant.db.QueryModeWrapper

import java.util.concurrent.Future

class DefaultServantOperation implements IOperation {

    public final static def LOG = Logger.getLogger(DefaultServantOperation.class.name)

    @Override
    def perform(List<Future> futures, Config config, QueriesConfig qryConfig, CliArgs cliArgs) {
        LOG.info "---[${qryConfig.description}]${cliArgs.parallel || qryConfig.parallel ? '[PARALLEL]' : ''}---"
        LOG.info "[${qryConfig.description}] Connection String: ${qryConfig.connectionString}"
        LOG.info "[${qryConfig.description}] Mode: ${new QueryModeWrapper(qryConfig.mode).mode}"

        List<Future> localFutures = new ArrayList<>()

        def queries = qryConfig.queries
        if (queries && queries.length > 0) {
            queries.each {query ->

                def servantTask = new ServantOperationTask(config, qryConfig, query)

                if (qryConfig.parallel) {
                    ParallelEngine.instance.with {
                        if (!isStarted()) {
                            start()
                        }
                        def future = process(servantTask)
                        localFutures.add(future)
                        futures.add(future)
                    }
                }
                else {
                    servantTask.run()
                }
            }
        }

        if (qryConfig.next) {
            def nextTask = new ServantNextOperationTask(this, futures, localFutures, config, qryConfig, cliArgs)

/*
            if (qryConfig.parallel) {
                futures.add(ParallelEngine.instance.process(nextTask))
            }
            else {
*/
                nextTask.run()
//            }
        }
    }
}
