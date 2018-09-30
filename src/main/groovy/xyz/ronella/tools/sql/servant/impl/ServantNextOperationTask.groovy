package xyz.ronella.tools.sql.servant.impl

import xyz.ronella.tools.sql.servant.CliArgs
import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.IOperation
import xyz.ronella.tools.sql.servant.QueryServant
import xyz.ronella.tools.sql.servant.conf.QueriesConfig

import java.util.concurrent.Future

class ServantNextOperationTask implements Runnable {

    private IOperation operation
    private List<Future> globalFutures
    private List<Future> localFutures
    private Config config
    private QueriesConfig qryConfig
    private CliArgs cliArgs

    ServantNextOperationTask(IOperation operation, List<Future> globalFutures, List<Future> localFutures,
                             Config config, QueriesConfig qryConfig, CliArgs cliArgs) {
        this.operation = operation
        this.globalFutures = globalFutures
        this.localFutures = localFutures
        this.config = config
        this.qryConfig = qryConfig
        this.cliArgs = cliArgs
        QueryServant.usageLevelUp()
    }

    @Override
    void run() {
        try {
            localFutures.each { it.get() }
            operation.perform(localFutures, config, qryConfig.next, cliArgs)
        }
        finally {
            QueryServant.usageLevelDown()
        }
    }
}
