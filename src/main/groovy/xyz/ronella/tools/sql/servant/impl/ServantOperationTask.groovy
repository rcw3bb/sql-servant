package xyz.ronella.tools.sql.servant.impl

import org.apache.log4j.Logger
import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.IStatus
import xyz.ronella.tools.sql.servant.TaskException
import xyz.ronella.tools.sql.servant.async.ProcessedHolder
import xyz.ronella.tools.sql.servant.conf.JsonConfig
import xyz.ronella.tools.sql.servant.conf.ListenersConfig
import xyz.ronella.tools.sql.servant.db.QueryMode
import xyz.ronella.tools.sql.servant.db.QueryModeWrapper
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

    public final static def LOG = Logger.getLogger(ServantOperationTask.class.name)

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

    private def header(String description) {
        LOG.info("[${description}] Executing: ${query}")

        if (LOG.isTraceEnabled()) {
            LOG.trace("Description: ${qryConfig.description}")
            LOG.trace("Connection String: ${qryConfig.connectionString}")
        }
    }

    private def buildQuery(String description, JsonConfig jsonConfig) {
        String parsedQuery = new QueryParserStrategy(config, qryConfig).parse(query)

        if ([QueryMode.SINGLE_QUERY_SCRIPT, QueryMode.SCRIPT].contains(new QueryModeWrapper(qryConfig.mode).mode)) {
            parsedQuery = ParamManager.applyParams(jsonConfig.params, parsedQuery)
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("[${description}] Parsed Query: ${parsedQuery}")
        }
        return parsedQuery
    }

    private def invokeComplete(ListenersConfig listeners, String ___description, String ___query, String ___success)
    {
        if (listeners.onComplete) {
            new ListenerInvoker(qryConfig).invokeCompleteListener(listeners.onComplete, ___description,
                    ___query, ___success)
        }
    }

    /**
     * The actual task to run by the engine which can also be called directly if not in
     * parallel mode.
     *
     * @return An implementation of IStatus.
     */
    @Override
    IStatus call() {
        def description = qryConfig.description
        def listeners = qryConfig.listeners

        header(description)

        def isProcessed = new ProcessedHolder().isProcessed(description)

        if (listeners.onStart) {
            new ListenerInvoker(qryConfig).invokeStartListener(listeners.onStart, description, query, !isProcessed)
        }

        boolean isSuccessful = false
        def startTime = new Date().time
        def jsonConfig = config.configAsJson
        try {
            String parsedQuery = buildQuery(description, jsonConfig)
            DBManager.getInstance(jsonConfig.dbPoolConfig).runStatement(qryConfig, parsedQuery)
            LOG.info("[${description}] Success running: ${query}")
            invokeComplete(listeners, description, query, 'success')
            isSuccessful = true
        }
        catch(Exception exp) {
            var message = "[${description}] Failed running: ${query}"
            LOG.error(exp)
            LOG.info(message)
            invokeComplete(listeners, description, query, 'failed')
            QueryServant.hasTaskException = true
            throw new TaskException(message)
        }
        finally {
            LOG.info("[${description}] Elapse: [${new Date().time - startTime}ms]")
            QueryServant.usageLevelDown()
        }

        return new DefaultStatus(isSuccessful)
    }
}
