import xyz.ronella.tools.sql.servant.CliArgs
import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.ConfigByEnv
import xyz.ronella.tools.sql.servant.QueryServant

/**
 * Processes the command line arguments to prepare the instance of CliArgs.
 *
 * @param cliArgs An instance of CliArgs to update.
 * @param args An actual arguments received from the command line.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
void processArgs(final CliArgs cliArgs, String ... args) {
    final Properties properties = new Properties()

    InputStream sqlsrvntIS
    try {
        sqlsrvntIS = this.getClass().getClassLoader().getResourceAsStream('sqlsrvnt.properties')
        properties.load(sqlsrvntIS)
    }
    finally {
        if (sqlsrvntIS) {
            sqlsrvntIS.close()
        }
    }

    def cli = new CliBuilder(usage:'sqlsrvnt -[hnpv] [-c <config-name>] [-e <environment>] [-P <parameter=value>]')
    cli.with {
        h longOpt : 'help', 'Show usage information'
        n longOpt : 'noop', 'Run without actually performing the gathering'
        p longOpt : 'parallel', 'Run the actual gathering in parallel'
        v longOpt : 'version', 'Shows the current version'
        c longOpt : 'config', args: 1, argName: 'config-name', 'Run a different configuration other than the default'
        e longOpt : 'env', args: 1, argName: 'environment', 'The environment associated with the configuration'
        P(args: 2, valueSeparator: '=', argName: 'parameter=value', 'Assigns value to parameters found in the configuration')
    }

    def options = cli.parse(args)
    if (!options) {
        return
    }

    def optionsLogic = [{options.n} : {cliArgs.noop = true},
                        {options.p} : {cliArgs.parallel = true},
                        {options.c} : {cliArgs.config = options.c},
                        {options.e} : {cliArgs.environment = options.e},
                        {options.P} : {
                            int idx = 0
                            cliArgs.params = options.Ps.inject([:], {___result, ___item ->
                                if (++idx % 2 == 0) {
                                    ___result.put(options.Ps.get(idx-2), ___item)
                                }
                                ___result
                            })
                        }
    ]

    if (options.h) {
        cli.usage()
    }
    else if (options.v) {
        String version = properties.getProperty('version', '')
        String year = properties.getProperty('year', '2018')
        println "SQL Servant ${version} [${year}]"
    }
    else {
        optionsLogic.each {___key , ___value ->
            if (___key()) {
                ___value()
            }
        }

        def config = new ConfigByEnv(new Config(cliArgs.config)).createConfigByEnv(cliArgs)
        new QueryServant(config).perform(cliArgs)
    }
}

CliArgs cliArgs = new CliArgs()
processArgs(cliArgs, args)
