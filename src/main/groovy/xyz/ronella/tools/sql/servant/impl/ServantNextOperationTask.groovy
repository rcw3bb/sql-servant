package xyz.ronella.tools.sql.servant.impl

import static xyz.ronella.tools.sql.servant.QueryServant.*

import org.apache.log4j.Logger
import xyz.ronella.tools.sql.servant.CliArgs
import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.IOperation
import xyz.ronella.tools.sql.servant.IStatus
import xyz.ronella.tools.sql.servant.conf.QueriesConfig

import java.util.concurrent.Callable
import java.util.concurrent.Future

/**
 * An implementation of Callable that handles parallel processing for the succeeding
 * configured instances of QueriesConfig.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
class ServantNextOperationTask implements Callable<IStatus> {

    public final static def LOG = Logger.getLogger(ServantNextOperationTask.class.name)

    private IOperation operation
    private List<Future<IStatus>> globalFutures
    private List<Future<IStatus>> localFutures
    private Config config
    private QueriesConfig qryConfig
    private CliArgs cliArgs

    /**
     * Creates an instance of ServantNextOperationTask
     *
     * @param operation An implementation of IOperation.
     * @param globalFutures All the instances of the collected type Futures.
     * @param localFutures The instance of the collected type Futures of the collected
     *          parent task.
     * @param config An instance of Config.
     * @param qryConfig An instance of QueriesConfig
     * @param cliArgs An instance of CliArgs.
     */
    ServantNextOperationTask(IOperation operation, List<Future<IStatus>> globalFutures, List<Future<IStatus>> localFutures,
                             Config config, QueriesConfig qryConfig, CliArgs cliArgs) {
        this.operation = operation
        this.globalFutures = globalFutures
        this.localFutures = localFutures
        this.config = config
        this.qryConfig = qryConfig
        this.cliArgs = cliArgs
        usageLevelUp()
    }

    /**
     * The actual task to run by the engine which can also be called directly if not in
     * parallel mode.
     *
     * @return An implmentation of IStatus.
     */
    @Override
    IStatus call() {
        def isSuccessful = false

        try {
            def isEverythingSuccessful = true
            for (localFuture in localFutures) {
                try {
                    isEverythingSuccessful = localFuture.get().isSuccessful() && isEverythingSuccessful
                }
                catch(Exception exception) {
                    LOG.error(exception)
                    if (!cliArgs.ignoreTaskException) {
                        isEverythingSuccessful = false
                        hasTaskException=true
                    }
                }
            }
            if (isEverythingSuccessful) {

                def qryConfigNext = qryConfig.next

                if (LOG.isTraceEnabled()) {
                    LOG.trace("Description: ${qryConfigNext.description}")
                    LOG.trace("Connection String: ${qryConfigNext.connectionString}")
                }

                operation.perform(localFutures, config, qryConfigNext, cliArgs)
                isSuccessful = true
            }
            else {
                LOG.warn("[${qryConfig.description}] Premature exit")
            }
        }
        finally {
            usageLevelDown()
        }

        return new DefaultStatus(isSuccessful)
    }
}
