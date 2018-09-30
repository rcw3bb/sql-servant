package xyz.ronella.tools.sql.servant

import org.apache.log4j.Logger

class QueryServant {

    public final static def LOG = Logger.getLogger(QueryServant.class.name)

    private Config config

    QueryServant(Config config) {
        this.config = config
    }

    def perform(CliArgs args) {
        LOG.info "User: ${System.getProperty("user.name")?:'Unknown'}"
        LOG.info "Configuration: ${config.configFilename}"

        config.configAsJson.queries.each { qryConfig ->
            new OperationStrategy(args).runOperation(config, qryConfig)
        }
    }

}
