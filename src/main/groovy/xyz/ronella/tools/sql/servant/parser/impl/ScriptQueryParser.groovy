package xyz.ronella.tools.sql.servant.parser.impl

import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.conf.QueriesConfig

class ScriptQueryParser extends AbstractQueryParser {

    ScriptQueryParser(Config config, QueriesConfig qryConfig) {
        super(config, qryConfig)
    }

    @Override
    String queryParse(String query) {
        def script = new File("${config.scriptDirectory}/${query}")
        if (script.exists()) {
            String parsedQuery = script.text
            return parsedQuery
        }
        else {
            throw new ScriptQueryParserException("${script.absolutePath} doesn't exists")
        }
    }
}
