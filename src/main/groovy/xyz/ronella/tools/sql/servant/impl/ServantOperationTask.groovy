package xyz.ronella.tools.sql.servant.impl

import org.apache.log4j.Logger
import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.IStatus
import xyz.ronella.tools.sql.servant.QueryServant
import xyz.ronella.tools.sql.servant.conf.QueriesConfig
import xyz.ronella.tools.sql.servant.db.DBManager

import java.util.concurrent.Callable

class ServantOperationTask implements Callable<IStatus> {

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
    IStatus call() {
        LOG.info("[${qryConfig.description}] Executing: ${query}")
        boolean isSuccessful = false
        def startTime = new Date().time
        try {
            DBManager.getInstance(config.configAsJson.dbPoolConfig).runStatement(qryConfig, query)
            LOG.info("[${qryConfig.description}] Success running: ${query}")
            isSuccessful = true
        }
        catch(Exception e) {
            LOG.error(e.fillInStackTrace())
            LOG.info("[${qryConfig.description}] Failed running: ${query}")
        }
        finally {
            LOG.info("[${qryConfig.description}] Elapse: [${new Date().time - startTime}ms]")
            QueryServant.usageLevelDown()
        }

        return new DefaultStatus(isSuccessful)
    }
}
