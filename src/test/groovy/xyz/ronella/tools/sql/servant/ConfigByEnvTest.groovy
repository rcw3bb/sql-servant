package xyz.ronella.tools.sql.servant

import org.junit.Test

class ConfigByEnvTest {

    @Test
    void testWithTestEnv() {
        def cliArgs = new CliArgs(environment: 'test', config: 'sample-h2')
        def config = new Config('./src/test/resources',cliArgs.config)
        def filename = "${config.configDirectory}${File.separator}${cliArgs.config}.${cliArgs.environment}.json"
        def expected = new File(filename)
        assert expected.absolutePath==new ConfigByEnv(config).createConfigByEnv(cliArgs).configFilename
    }

    @Test
    void testWithNonExistingEnv() {
        def cliArgs = new CliArgs(environment: 'non-existing', config: 'sample-h2')
        def config = new Config('./src/test/resources',cliArgs.config)
        def filename = "${config.configDirectory}${File.separator}${cliArgs.config}.json"
        def expected = new File(filename)
        assert expected.absolutePath==new ConfigByEnv(config).createConfigByEnv(cliArgs).configFilename
    }

}
