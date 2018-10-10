package xyz.ronella.tools.sql.servant.parser.impl

import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.conf.QueriesConfig

class DefaultQueryParser extends AbstractQueryParser {

    DefaultQueryParser(Config config, QueriesConfig qryConfig) {
        super(config, qryConfig)
    }

    @Override
    String queryParse(String query) {
        query
    }
}
