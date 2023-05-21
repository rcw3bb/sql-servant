package xyz.ronella.tools.sql.servant.parser.impl;

import lombok.SneakyThrows;
import xyz.ronella.tools.sql.servant.Config;
import xyz.ronella.tools.sql.servant.command.Invoker;
import xyz.ronella.tools.sql.servant.command.impl.GetConfigText;
import xyz.ronella.tools.sql.servant.conf.QueriesConfig;

import java.io.File;

/**
 * The parser implementation that deals with scripts.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
public class ScriptQueryParser extends AbstractQueryParser {

    /**
     * Creates an instance of the ScriptQueryParser.
     *
     * @param config An instance of Config.
     * @param qryConfig An instance of QueriesConfig.
     */
    public ScriptQueryParser(final Config config, final QueriesConfig qryConfig) {
        super(config, qryConfig);
    }

    /**
     * The actual parsing implementation.
     *
     * @param query The query to be parsed.
     * @return The parsed query.
     */
    @SneakyThrows
    @Override
    public String queryParse(final String query) {
        final var script = new File(String.format("%s/%s", config.getScriptDirectory(), query));
        if (script.exists()) {
            return Invoker.invoke(new GetConfigText(script.getAbsolutePath()));
        }
        else {
            throw new ScriptQueryParserException(String.format("%s doesn't exists", script.getAbsolutePath()));
        }
    }
}
