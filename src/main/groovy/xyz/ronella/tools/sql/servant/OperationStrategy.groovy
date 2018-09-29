package xyz.ronella.tools.sql.servant

import xyz.ronella.tools.sql.servant.conf.QueriesConfig
import xyz.ronella.tools.sql.servant.impl.DefaultServantOperation
import xyz.ronella.tools.sql.servant.impl.NoopOperation

class OperationStrategy {

    private CliArgs cliArgs
    private IOperation operation

    OperationStrategy(CliArgs cliArgs) {
        this.cliArgs = cliArgs
        if (cliArgs.noop) {
            this.operation = new NoopOperation()
        }
        else {
            this.operation = new DefaultServantOperation()
        }
    }

    def runOperation(Config config, QueriesConfig queriesConfig) {
        this.operation.perform(config, queriesConfig, cliArgs)
    }

}
