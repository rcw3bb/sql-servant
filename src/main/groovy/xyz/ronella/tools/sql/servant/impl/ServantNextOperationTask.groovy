package xyz.ronella.tools.sql.servant.impl

import org.apache.log4j.Logger
import xyz.ronella.tools.sql.servant.CliArgs
import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.IOperation
import xyz.ronella.tools.sql.servant.IStatus
import xyz.ronella.tools.sql.servant.QueryServant
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
        boolean isSuccessful = false

        try {
            boolean isEverythingSuccessful = true
            localFutures.each {
                isEverythingSuccessful = it.get().isSuccessful() && isEverythingSuccessful
            }
            if (isEverythingSuccessful) {
                operation.perform(localFutures, config, qryConfig.next, cliArgs)
                isSuccessful = true
            }
            else {
                LOG.warn("[${qryConfig.description}] Premature exit")
            }
        }
        finally {
            QueryServant.usageLevelDown()
        }

        return new DefaultStatus(isSuccessful)
    }
}
