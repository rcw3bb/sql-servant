package xyz.ronella.tools.sql.servant.impl

import org.apache.log4j.Logger
import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.IStatus
import xyz.ronella.tools.sql.servant.listener.ListenerInvoker
import xyz.ronella.tools.sql.servant.parser.QueryParserStrategy
import xyz.ronella.tools.sql.servant.QueryServant
import xyz.ronella.tools.sql.servant.conf.QueriesConfig
import xyz.ronella.tools.sql.servant.db.DBManager

import java.util.concurrent.Callable

/**
 * An implementation of Callable that processes the first configured queries.
 * This is normally the prior tasks before the ServantNextOperationTask task.
 *
 * @author Ron Webb
 * @since 2010-10-07
 */
class ServantOperationTask implements Callable<IStatus> {

    public final static def LOG = Logger.getLogger(DefaultServantOperation.class.name)

    private Config config
    private QueriesConfig qryConfig
    private String query

    /**
     * Creates an instance of ServantOperationTask
     *
     * @param operation An implementation of IOperation.
     * @param globalFutures All the instances of the collected type Futures.
     * @param localFutures The instance of the collected type Futures of the collected
     *          parent task.
     * @param config An instance of Config.
     * @param qryConfig An instance of QueriesConfig
     * @param cliArgs An instance of CliArgs.
     */
    ServantOperationTask(Config config, QueriesConfig qryConfig, String query) {
        this.config = config
        this.qryConfig = qryConfig
        this.query = query
        QueryServant.usageLevelUp()
    }

    /**
     * The actual task to run by the engine which can also be called directly if not in
     * parallel mode.
     *
     * @return An implmentation of IStatus.
     */
    @Override
    IStatus call() {
        def description = qryConfig.description
        def listeners = qryConfig.listeners

        def invokeComplete = {String ___description, String ___query, String ___success ->
            if (listeners.onComplete) {
                new ListenerInvoker(qryConfig).invokeCompleteListener(listeners.onComplete, ___description,
                        ___query, ___success)
            }
        }

        LOG.info("[${description}] Executing: ${query}")

        if (listeners.onStart) {
            new ListenerInvoker(qryConfig).invokeStartListener(listeners.onStart, description, query)
        }

        boolean isSuccessful = false
        def startTime = new Date().time
        try {
            String parsedQuery = new QueryParserStrategy(config, qryConfig).parse(query)
            if (LOG.isDebugEnabled()) {
                LOG.debug("[${description}] Parsed Query: ${parsedQuery}")
            }
            DBManager.getInstance(config.configAsJson.dbPoolConfig).runStatement(qryConfig, parsedQuery)
            LOG.info("[${description}] Success running: ${query}")
            invokeComplete(description, query, 'success')
            isSuccessful = true
        }
        catch(Exception e) {
            LOG.error(e.fillInStackTrace())
            LOG.info("[${description}] Failed running: ${query}")
            invokeComplete(description, query, 'failed')
        }
        finally {
            LOG.info("[${description}] Elapse: [${new Date().time - startTime}ms]")
            QueryServant.usageLevelDown()
        }

        return new DefaultStatus(isSuccessful)
    }
}
