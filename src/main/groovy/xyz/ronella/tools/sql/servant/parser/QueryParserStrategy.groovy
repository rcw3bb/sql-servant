package xyz.ronella.tools.sql.servant.parser

import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.conf.QueriesConfig
import xyz.ronella.tools.sql.servant.db.QueryMode
import xyz.ronella.tools.sql.servant.db.QueryModeWrapper
import xyz.ronella.tools.sql.servant.parser.impl.DefaultQueryParser
import xyz.ronella.tools.sql.servant.parser.impl.ScriptQueryParser

class QueryParserStrategy {
    private Config config
    private QueriesConfig qryConfig

    QueryParserStrategy(Config config, QueriesConfig qryConfig) {
        this.config = config
        this.qryConfig = qryConfig
    }

    String parse(String query) {
        switch(new QueryModeWrapper(qryConfig.mode).mode) {
            case QueryMode.STATEMENT:
            case QueryMode.QUERY:
                new DefaultQueryParser(config, qryConfig).queryParse(query)
                break
            case QueryMode.SCRIPT:
                new ScriptQueryParser(config, qryConfig).queryParse(query)
                break
        }
    }

}
