package xyz.ronella.tools.sql.servant.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.ronella.tools.sql.servant.CliArgs;
import xyz.ronella.tools.sql.servant.Config;
import xyz.ronella.tools.sql.servant.IOperation;
import xyz.ronella.tools.sql.servant.IStatus;
import xyz.ronella.tools.sql.servant.Validate;
import xyz.ronella.tools.sql.servant.conf.QueriesConfig;
import xyz.ronella.tools.sql.servant.db.QueryModeException;
import xyz.ronella.tools.sql.servant.db.QueryModeWrapper;
import xyz.ronella.tools.sql.servant.listener.HasActiveListener;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

/**
 * An implementation of IOperation that handles the NOOP option.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
public class NoopOperation implements IOperation {

    public final static Logger LOG = LogManager.getLogger(NoopOperation.class);

    /**
     * The actual implmentation that handles NOOP operation.
     *
     * @param futures An instance of collected instances of typed Futures.
     * @param config An instance of Config.
     * @param qryConfig An instance QueriesConfig
     * @param cliArgs An instance of CliArgs
     */
    @Override
    public void perform(final List<Future<IStatus>> futures, final Config config, final QueriesConfig qryConfig, final CliArgs cliArgs) throws QueryModeException {
        if (cliArgs.isParallel() && !(new Validate(new HasActiveListener()).check(qryConfig.getListeners()))) {
            qryConfig.setParallel(true);
        }

        LOG.info(String.format("---[%s]%s---", qryConfig.getDescription(), qryConfig.getParallel()!=null ? "[PARALLEL]" : ""));
        LOG.info(String.format("Connection String: %s", qryConfig.getConnectionString()));
        LOG.info(String.format("Mode: %s", new QueryModeWrapper(qryConfig.getMode()).getMode()));
        final var queries = qryConfig.getQueries();
        if (queries!=null && queries.length > 0) {
            Arrays.stream(queries).forEach(query -> {
                final var newQuery = ParamManager.applyParams(cliArgs.getParams(), query);
                LOG.info(String.format("Will run: %s", newQuery));
            });
        }
        final var next = qryConfig.getNext();
        if (next!=null) {
            perform(futures, config, next, cliArgs);
        }
    }
}
