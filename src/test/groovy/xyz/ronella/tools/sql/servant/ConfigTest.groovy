package xyz.ronella.tools.sql.servant

import org.junit.Test

class ConfigTest {

    final def testDefaultConfig = new Config('./src/test/resources/conf','ss-default')
    final def testEmptyDefaultConfig = new Config('./src/test/resources/conf','empty')

    @Test
    void testDefaultConfig() {
        assert "${new File('.').absolutePath}${File.separator}conf${File.separator}ss-default.json"==(new Config().configFilename)
    }

    @Test
    void testDefaultConfigQueriesCount() {
        assert 3==testDefaultConfig.configAsJson.queries.length
    }

    @Test
    void testDefaultConfigQueriesQueriesCount() {
        assert 2==testDefaultConfig.configAsJson.queries[1].queries.length
    }

    @Test
    void testDefaultConfigQueryDescription() {
        assert 'Query 1'==testDefaultConfig.configAsJson.queries[0].description
    }

    @Test
    void testEmptyConfigDefaultsParallel() {
        assert !testEmptyDefaultConfig.configAsJson.defaults.parallel
    }

    @Test
    void testEmptyConfigDefaultsQueries() {
        assert 0==testEmptyDefaultConfig.configAsJson.queries.length
    }

    @Test
    void testEmptyConfigDefaultsDBPoolMaxPreparedStatement() {
        assert 50==testEmptyDefaultConfig.configAsJson.dbPoolConfig.maxOpenPreparedStatements
    }

    @Test
    void testDefaultConfigNextQuery() {
        1==testDefaultConfig.configAsJson.queries[2].next.queries.length
    }

    @Test
    void testDefaultConfigNextQueryDescription() {
        assert 'Query 3 [NEXT]'==testDefaultConfig.configAsJson.queries[2].next.description
    }

    @Test
    void testDefaultConfigNextNextQueryDescription() {
        assert 'Query 3 [NEXT] [NEXT]'==testDefaultConfig.configAsJson.queries[2].next.next.description
    }

    @Test
    void testScriptDirectory() {
        String expected = new File("${testDefaultConfig.configDirectory}/../scripts").absolutePath
        assert expected == testDefaultConfig.scriptDirectory
    }

}
