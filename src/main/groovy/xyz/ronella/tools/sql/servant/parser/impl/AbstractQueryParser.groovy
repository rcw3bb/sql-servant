package xyz.ronella.tools.sql.servant.parser.impl

import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.parser.IQueryParser
import xyz.ronella.tools.sql.servant.conf.QueriesConfig

abstract class AbstractQueryParser implements IQueryParser {

    protected Config config
    protected QueriesConfig qryConfig

    AbstractQueryParser(Config config, QueriesConfig qryConfig) {
        this.config = config
        this.qryConfig = qryConfig
    }

}
