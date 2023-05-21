package xyz.ronella.tools.sql.servant.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.ronella.tools.sql.servant.Config;
import xyz.ronella.tools.sql.servant.IStatus;
import xyz.ronella.tools.sql.servant.TaskException;
import xyz.ronella.tools.sql.servant.async.ProcessedHolder;
import xyz.ronella.tools.sql.servant.conf.JsonConfig;
import xyz.ronella.tools.sql.servant.conf.ListenersConfig;
import xyz.ronella.tools.sql.servant.db.QueryMode;
import xyz.ronella.tools.sql.servant.db.QueryModeException;
import xyz.ronella.tools.sql.servant.db.QueryModeWrapper;
import xyz.ronella.tools.sql.servant.listener.ListenerException;
import xyz.ronella.tools.sql.servant.listener.ListenerInvoker;
import xyz.ronella.tools.sql.servant.parser.QueryParserStrategy;
import xyz.ronella.tools.sql.servant.QueryServant;
import xyz.ronella.tools.sql.servant.conf.QueriesConfig;
import xyz.ronella.tools.sql.servant.db.DBManager;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * An implementation of Callable that processes the first configured queries.
 * This is normally the prior tasks before the ServantNextOperationTask task.
 *
 * @author Ron Webb
 * @since 2010-10-07
 */
public class ServantOperationTask implements Callable<IStatus> {

    public final static Logger LOG = LogManager.getLogger(ServantOperationTask.class);

    private final Config config;
    private final QueriesConfig qryConfig;
    private final String query;

    /**
     * Creates an instance of ServantOperationTask
     *
     * @param config An instance of Config.
     * @param qryConfig An instance of QueriesConfig
     * @param query The actual query.
     */
    public ServantOperationTask(final Config config, final QueriesConfig qryConfig, final String query) {
        this.config = config;
        this.qryConfig = qryConfig;
        this.query = query;
        QueryServant.usageLevelUp();
    }

    private void header(final String description) {
        LOG.info(String.format("[%s] Executing: %s", description, query));

        if (LOG.isTraceEnabled()) {
            LOG.trace(String.format("Description: %s", qryConfig.getDescription()));
            LOG.trace(String.format("Connection String: %s", qryConfig.getConnectionString()));
        }
    }

    private String buildQuery(final String description, final JsonConfig jsonConfig) throws QueryModeException {
        var parsedQuery = new QueryParserStrategy(config, qryConfig).parse(query);

        if (Arrays.asList(QueryMode.SINGLE_QUERY_SCRIPT, QueryMode.SCRIPT).contains(new QueryModeWrapper(qryConfig.getMode()).getMode())) {
            parsedQuery = ParamManager.applyParams(jsonConfig.getParams(), parsedQuery);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("[%s] Parsed Query: %s", description, parsedQuery));
        }
        return parsedQuery;
    }

    private void invokeComplete(final ListenersConfig listeners, final String ___description, final String ___query, final String ___success) throws ListenerException {
        final var onComplete = listeners.getOnComplete();
        if (onComplete!=null) {
            new ListenerInvoker(qryConfig).invokeCompleteListener(onComplete, ___description,
                    ___query, ___success);
        }
    }

    /**
     * The actual task to run by the engine which can also be called directly if not in
     * parallel mode.
     *
     * @return An implementation of IStatus.
     */
    @Override
    public IStatus call() {
        final var description = qryConfig.getDescription();
        final var listeners = qryConfig.getListeners();

        header(description);

        final var isProcessed = new ProcessedHolder().isProcessed(description);

        final var onStart = listeners.getOnStart();
        if (onStart!=null) {
            new ListenerInvoker(qryConfig).invokeStartListener(onStart, description, query, !isProcessed);
        }

        boolean isSuccessful;
        final var startTime = new Date().getTime();
        final var jsonConfig = config.getConfigAsJson();
        try {
            String parsedQuery = buildQuery(description, jsonConfig);
            DBManager.getInstance(jsonConfig.getDbPoolConfig()).runStatement(qryConfig, parsedQuery);
            LOG.info(String.format("[%s] Success running: %s", description, query));
            invokeComplete(listeners, description, query, "success");
            isSuccessful = true;
        }
        catch(Exception exp) {
            var message = String.format("[%s] Failed running: %s", description, query);
            LOG.error(exp);
            LOG.info(message);
            invokeComplete(listeners, description, query, "failed");
            QueryServant.setTaskException(true);
            throw new TaskException(message);
        }
        finally {
            LOG.info(String.format("[%s] Elapse: [%sms]", description, new Date().getTime() - startTime));
            QueryServant.usageLevelDown();
        }

        return new DefaultStatus(isSuccessful);
    }
}
