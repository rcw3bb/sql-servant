package xyz.ronella.tools.sql.servant

import org.junit.Test
import xyz.ronella.tools.sql.servant.db.QueryMode
import xyz.ronella.tools.sql.servant.db.QueryModeWrapper

class ConfigTest {

    private static final String OS_NAME = System.getProperty("os.name").toLowerCase()

    final def testDefaultConfig = new Config('./src/test/resources/conf','ss-default')
    final def testEmptyDefaultConfig = new Config('./src/test/resources/conf','empty')
    final def testReallyEmptyDefConfig = new Config('./src/test/resources/conf','really-empty')
    final def testFilenameConfig = new Config('./src/test/resources/conf','ss-filename')

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
    void testEmptyConfigDefaultListeners() {
        assert testEmptyDefaultConfig.configAsJson.defaults.listeners
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

    @Test
    void testListenersDirectory() {
        String expected = new File("${testDefaultConfig.configDirectory}/../listeners").absolutePath
        assert expected == testDefaultConfig.listenerDirectory
    }

    @Test
    void testListenersWindowsCommand() {
        if (OS_NAME.contains('win')) {
            assert 'cmd.exe /c' == testDefaultConfig.configAsJson.defaults.listeners.command
        }
    }

    @Test
    void testReallyDefaultEmptyMode() {
        assert QueryMode.STATEMENT == new QueryModeWrapper(testReallyEmptyDefConfig.configAsJson.defaults.mode).getMode()
    }

    @Test
    void testReallyEmptyDBMaxIdle() {
        assert 1 == testReallyEmptyDefConfig.configAsJson.dbPoolConfig.maxIdle
    }

    @Test
    void testReallyEmptyQueriesCount() {
        assert 0 == testReallyEmptyDefConfig.configAsJson.queries.size()
    }

    @Test
    void testReallyEmptyParamsCount() {
        assert 0 == testReallyEmptyDefConfig.configAsJson.params.size()
    }

    @Test
    void testFilename() {
        assert !testFilenameConfig.configAsJson.defaults.filename
    }

    @Test
    void testFilenameParallel() {
        assert testFilenameConfig.configAsJson.defaults.parallel
    }

    @Test
    void testFilenameConnectionString() {
        assert 'test connection string' == testFilenameConfig.configAsJson.defaults.connectionString
    }

    @Test
    void testFilenameQueries() {
        assert !testFilenameConfig.configAsJson.queries[0].filename
    }

    @Test
    void testFilenameDescription() {
        assert 'Overridden description'==testFilenameConfig.configAsJson.queries[0].description
    }

    @Test
    void testFilenameQueriesCount() {
        assert 3 == testFilenameConfig.configAsJson.queries[0].queries.size()
    }

    @Test
    void testListenerOnHeaderQueries() {
        File file = new File("${testFilenameConfig.configDirectory}/../listeners/sample-h2-datum.bat")
        String expected = file.absolutePath
        assert expected == testFilenameConfig.configAsJson.queries[0].listeners.onHeader
    }

    @Test
    void testListenerOnDataQueries() {
        File file = new File("${testFilenameConfig.configDirectory}/../listeners/sample-h2-data.bat")
        String expected = file.absolutePath
        assert expected == testFilenameConfig.configAsJson.queries[0].listeners.onData
    }

    @Test
    void testFilenameDBPoolMinIdle() {
        assert 5 == testFilenameConfig.configAsJson.dbPoolConfig.minIdle
    }

    @Test
    void testFilenameDBPoolMaxIdle() {
        assert 10 == testFilenameConfig.configAsJson.dbPoolConfig.maxIdle
    }

    @Test
    void testFilenameParamNameExternal() {
        def params = testFilenameConfig.configAsJson.params
        assert null!=params.find({'param'==it.name})
    }

    @Test
    void testFilenameParamNameOverridden() {
        def params = testFilenameConfig.configAsJson.params
        assert null!=params.find({it.name=='overridden'})
    }

    @Test
    void testFilenameParamNameOrdinary() {
        def params = testFilenameConfig.configAsJson.params
        assert null!=params.find({it.name=='ordinary'})
    }

}
