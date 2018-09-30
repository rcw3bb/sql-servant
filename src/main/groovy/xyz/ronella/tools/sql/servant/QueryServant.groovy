package xyz.ronella.tools.sql.servant

import org.apache.log4j.Logger
import xyz.ronella.tools.sql.servant.async.ParallelEngine

import java.util.concurrent.Future

class QueryServant {

    public final static def LOG = Logger.getLogger(QueryServant.class.name)

    private Config config

    QueryServant(Config config) {
        this.config = config
    }

    def perform(CliArgs args) {
        LOG.info "User: ${System.getProperty("user.name")?:'Unknown'}"
        LOG.info "Configuration: ${config.configFilename}"

        def configJson = config.configAsJson

        if (configJson) {
            ParallelEngine.instance.with {
                List<Future> futures = new ArrayList<>()
                try {
                    configJson.queries.each { qryConfig ->
                        new OperationStrategy(args).runOperation(futures, config, qryConfig)
                    }
                    futures.each {it.get()}
                }
                finally {
                    if (isStarted()) {
                        stop()
                    }
                }
            }
        }
        else {
            LOG.info "Nothing to process."
        }

        LOG.info 'Done'
    }

}
