package xyz.ronella.tools.sql.servant.parser.impl

import org.junit.Test
import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.conf.QueriesConfig

class ScriptQueryParserTest {

    final def testDefaultConfig = new Config('./src/test/resources/conf','sample-scripts-conf')

    @Test(expected = ScriptQueryParserException.class)
    void testNonExistingScript() {
        def qryConfig = new QueriesConfig(mode: 'scripts')
        new ScriptQueryParser(testDefaultConfig, qryConfig).queryParse('non-existing.sql')
    }

    @Test
    void testExistingScript() {
        def qryConfig = new QueriesConfig(mode: 'scripts')
        String query = new ScriptQueryParser(testDefaultConfig, qryConfig).queryParse('test-script.txt')
        assert '''select * from dummy''' == query
    }
}
