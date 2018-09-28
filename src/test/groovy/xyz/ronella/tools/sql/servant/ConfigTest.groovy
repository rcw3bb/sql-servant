package xyz.ronella.tools.sql.servant

import org.junit.Test

class ConfigTest {

    final def testDefaultConfig = new Config('./src/test/resources/conf','default')
    final def testEmptyDefaultConfig = new Config('./src/test/resources/conf','empty')

    @Test
    void testDefaultConfig() {
        assert "${new File('.').absolutePath}${File.separator}conf${File.separator}default.json"==(new Config().configFilename)
    }

    @Test
    void testDefaultConfigQueriesCount() {
        assert 2==testDefaultConfig.configAsJson.queries.length
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



}
