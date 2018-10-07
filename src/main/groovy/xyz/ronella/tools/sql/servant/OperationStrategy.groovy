package xyz.ronella.tools.sql.servant

import xyz.ronella.tools.sql.servant.conf.QueriesConfig
import xyz.ronella.tools.sql.servant.impl.DefaultServantOperation
import xyz.ronella.tools.sql.servant.impl.NoopOperation

import java.util.concurrent.Future

/**
 * The class responsible of which IOperation implementation to use.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
class OperationStrategy {

    private CliArgs cliArgs
    private IOperation operation

    /**
     * Creates an instance of OperationStrategy.
     *
     * @param cliArgs An instance of CliArgs.
     */
    OperationStrategy(CliArgs cliArgs) {
        this.cliArgs = cliArgs
        if (cliArgs.noop) {
            this.operation = new NoopOperation()
        }
        else {
            this.operation = new DefaultServantOperation()
        }
    }

    /**
     * Run the appropriate instance of IOperation.
     *
     * @param futures A collected instances of Futures.
     * @param config An instance of Config.
     * @param queriesConfig An instance of QueriesConfig.
     */
    def runOperation(List<Future> futures, Config config, QueriesConfig queriesConfig) {
        this.operation.perform(futures, config, queriesConfig, cliArgs)
    }

}
