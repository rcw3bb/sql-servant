package xyz.ronella.tools.sql.servant.parser.impl

import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.conf.QueriesConfig

/**
 * The default parser implementation that doesn't nothing to the query.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
class DefaultQueryParser extends AbstractQueryParser {

    /**
     * The base constructor that must be called.
     *
     * @param config An instance of Config.
     * @param qryConfig An instance of QueriesConfig.
     */
    DefaultQueryParser(Config config, QueriesConfig qryConfig) {
        super(config, qryConfig)
    }

    /**
     * The actual parsing implementation.
     *
     * @param query The query to be parsed.
     * @return The parsed query.
     */
    @Override
    String queryParse(String query) {
        query
    }
}
