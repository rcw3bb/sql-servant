package xyz.ronella.tools.sql.servant.parser;

import xyz.ronella.tools.sql.servant.Config;
import xyz.ronella.tools.sql.servant.conf.QueriesConfig;
import xyz.ronella.tools.sql.servant.db.QueryModeException;
import xyz.ronella.tools.sql.servant.db.QueryModeWrapper;
import xyz.ronella.tools.sql.servant.parser.impl.DefaultQueryParser;
import xyz.ronella.tools.sql.servant.parser.impl.ScriptQueryParser;

/**
 * The one responsible parsing the query with the appropriate implementation.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
public class QueryParserStrategy {
    private final Config config;
    private final QueriesConfig qryConfig;

    /**
     * Creates an instance of the QueryParserStrategy.
     *
     * @param config An instance of Config.
     * @param qryConfig An instance of QueriesConfig.
     */
    public QueryParserStrategy(final Config config, final QueriesConfig qryConfig) {
        this.config = config;
        this.qryConfig = qryConfig;
    }

    /**
     * Does the actual query parsing.
     *
     * @param query The query to be parsed.
     * @return The parsed query.
     */
    public String parse(final String query) throws QueryModeException {
        String parser = null;
        switch (new QueryModeWrapper(qryConfig.getMode()).getMode()) {
            case STATEMENT:
            case QUERY:
                parser =  new DefaultQueryParser(config, qryConfig).queryParse(query);
                break;
            case SCRIPT:
            case SINGLE_QUERY_SCRIPT:
                parser = new ScriptQueryParser(config, qryConfig).queryParse(query);
                break;
        }
        return parser;
    }

}
