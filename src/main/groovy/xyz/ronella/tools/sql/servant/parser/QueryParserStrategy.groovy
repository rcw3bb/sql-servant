package xyz.ronella.tools.sql.servant.parser

import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.conf.QueriesConfig
import xyz.ronella.tools.sql.servant.db.QueryMode
import xyz.ronella.tools.sql.servant.db.QueryModeWrapper
import xyz.ronella.tools.sql.servant.parser.impl.DefaultQueryParser
import xyz.ronella.tools.sql.servant.parser.impl.ScriptQueryParser

/**
 * The one responsible parsing the query with the appropriate implementation.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
class QueryParserStrategy {
    private Config config
    private QueriesConfig qryConfig

    /**
     * Creates an instance of the QueryParserStrategy.
     *
     * @param config An instance of Config.
     * @param qryConfig An instance of QueriesConfig.
     */
    QueryParserStrategy(Config config, QueriesConfig qryConfig) {
        this.config = config
        this.qryConfig = qryConfig
    }

    /**
     * Does the actual query parsing.
     *
     * @param query The query to be parsed.
     * @return The parsed query.
     */
    String parse(String query) {
        switch(new QueryModeWrapper(qryConfig.mode).mode) {
            case QueryMode.STATEMENT:
            case QueryMode.QUERY:
                new DefaultQueryParser(config, qryConfig).queryParse(query)
                break
            case QueryMode.SCRIPT:
            case QueryMode.SINGLE_QUERY_SCRIPT:
                new ScriptQueryParser(config, qryConfig).queryParse(query)
                break
        }
    }

}
