package xyz.ronella.tools.sql.servant.parser.impl

import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.conf.QueriesConfig

/**
 * The parser implementation that deals with scripts.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
class ScriptQueryParser extends AbstractQueryParser {

    /**
     * Creates an instance of the ScriptQueryParser.
     *
     * @param config An instance of Config.
     * @param qryConfig An instance of QueriesConfig.
     */
    ScriptQueryParser(Config config, QueriesConfig qryConfig) {
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
