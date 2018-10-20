package xyz.ronella.tools.sql.servant.parser.impl

import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.parser.IQueryParser
import xyz.ronella.tools.sql.servant.conf.QueriesConfig

/**
 * The partial base implementation of the IQueryParser.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
abstract class AbstractQueryParser implements IQueryParser {

    protected Config config
    protected QueriesConfig qryConfig

    /**
     * The base constructor that must be called.
     *
     * @param config An instance of Config.
     * @param qryConfig An instance of QueriesConfig.
     */
    AbstractQueryParser(Config config, QueriesConfig qryConfig) {
        this.config = config
        this.qryConfig = qryConfig
    }

}
