package xyz.ronella.tools.sql.servant.impl

import org.apache.log4j.Logger
import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.QueryServant
import xyz.ronella.tools.sql.servant.conf.QueriesConfig
import xyz.ronella.tools.sql.servant.db.DBManager

class ServantOperationTask implements Runnable {

    public final static def LOG = Logger.getLogger(DefaultServantOperation.class.name)

    private Config config
    private QueriesConfig qryConfig
    private String query

    ServantOperationTask(Config config, QueriesConfig qryConfig, String query) {
        this.config = config
        this.qryConfig = qryConfig
        this.query = query
        QueryServant.usageLevelUp()
    }

    @Override
    void run() {
        LOG.info("[${qryConfig.description}] Executing: ${query}")
        def startTime = new Date().time
        try {
            DBManager.getInstance(config.configAsJson.dbPoolConfig).runStatement(qryConfig, query)
            LOG.info("[${qryConfig.description}] Success running: ${query}")
        }
        catch(Exception e) {
            LOG.info("[${qryConfig.description}] Failed running: ${query}")
            LOG.error(e.fillInStackTrace())
        }
        finally {
            LOG.info("[${qryConfig.description}] Elapse: [${new Date().time - startTime}ms]")
            QueryServant.usageLevelDown()
        }
    }
}
