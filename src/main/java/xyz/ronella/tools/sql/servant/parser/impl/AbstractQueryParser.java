package xyz.ronella.tools.sql.servant.parser.impl;

import xyz.ronella.tools.sql.servant.Config;
import xyz.ronella.tools.sql.servant.parser.IQueryParser;
import xyz.ronella.tools.sql.servant.conf.QueriesConfig;

/**
 * The partial base implementation of the IQueryParser.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
public abstract class AbstractQueryParser implements IQueryParser {

    protected final Config config;
    protected final QueriesConfig qryConfig;

    /**
     * The base constructor that must be called.
     *
     * @param config An instance of Config.
     * @param qryConfig An instance of QueriesConfig.
     */
    public AbstractQueryParser(final Config config, final QueriesConfig qryConfig) {
        this.config = config;
        this.qryConfig = qryConfig;
    }

}
