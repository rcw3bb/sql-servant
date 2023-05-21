package xyz.ronella.tools.sql.servant;

import xyz.ronella.tools.sql.servant.conf.QueriesConfig;
import xyz.ronella.tools.sql.servant.db.QueryModeException;

import java.util.List;
import java.util.concurrent.Future;

/**
 * The blueprint of implementing an Operation.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
public interface IOperation {

    /**
     * Must the actual implementation of the desired operation.
     *
     * @param futures An instance of collected instances of typed Futures.
     * @param config An instance of Config.
     * @param qryConfig An instance QueriesConfig
     * @param cliArgs An instance of CliArgs
     */
    void perform(List<Future<IStatus>> futures, Config config, QueriesConfig qryConfig, CliArgs cliArgs) throws QueryModeException;
}
