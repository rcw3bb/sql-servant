package xyz.ronella.tools.sql.servant;

import xyz.ronella.tools.sql.servant.conf.QueriesConfig;
import xyz.ronella.tools.sql.servant.impl.DefaultServantOperation;
import xyz.ronella.tools.sql.servant.impl.NoopOperation;

import java.util.List;
import java.util.concurrent.Future;

/**
 * The class responsible for which IOperation implementation to use.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
public class OperationStrategy {

    private final CliArgs cliArgs;
    private final IOperation operation;

    /**
     * Creates an instance of OperationStrategy.
     *
     * @param cliArgs An instance of CliArgs.
     */
    public OperationStrategy(final CliArgs cliArgs) {
        this.cliArgs = cliArgs;
        if (cliArgs.isNoop()) {
            this.operation = new NoopOperation();
        }
        else {
            this.operation = new DefaultServantOperation();
        }
    }

    /**
     * Run the appropriate instance of IOperation.
     *
     * @param futures A collected instances of Futures.
     * @param config An instance of Config.
     * @param queriesConfig An instance of QueriesConfig.
     */
    public void runOperation(final List<Future<IStatus>> futures, final Config config, final QueriesConfig queriesConfig) {
        this.operation.perform(futures, config, queriesConfig, cliArgs);
    }

}
