package xyz.ronella.tools.sql.servant.parser.impl;

import xyz.ronella.tools.sql.servant.Config;
import xyz.ronella.tools.sql.servant.conf.QueriesConfig;

/**
 * The default parser implementation that does nothing to the query.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
public class DefaultQueryParser extends AbstractQueryParser {

    /**
     * The base constructor that must be called.
     *
     * @param config An instance of Config.
     * @param qryConfig An instance of QueriesConfig.
     */
    public DefaultQueryParser(final Config config, final QueriesConfig qryConfig) {
        super(config, qryConfig);
    }

    /**
     * The actual parsing implementation.
     *
     * @param query The query to be parsed.
     * @return The parsed query.
     */
    @Override
    public String queryParse(final String query) {
        return query;
    }
}
