import xyz.ronella.tools.sql.servant.CliArgs
import xyz.ronella.tools.sql.servant.Config
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

    def cli = new CliBuilder(usage:'sqlsrvnt -[hnpcv] [config-name]')
    cli.with {
        h longOpt : 'help', 'Show usage information'
        n longOpt : 'noop', 'Run without actually performing the gathering'
        p longOpt : 'parallel', 'Run the actual gathering in parallel'
        c longOpt : 'config', args: 1, argName: 'config-name', 'Run a different configuration other than the default'
        v longOpt : 'version', 'Shows the current version'
    }

    def options = cli.parse(args)
    if (!options) {
        return
    }

    def optionsLogic = [{options.n} : {cliArgs.noop = true},
                        {options.p} : {cliArgs.parallel = true},
                        {options.c} : {cliArgs.config = options.c}
    ]

    if (options.h) {
        cli.usage()
    }
    else if (options.v) {
        String version = properties.getProperty('version', '')
        String year = properties.getProperty('year', '2018')
        println "SQL Servant ${version} by Ron [${year}]"
    }
    else {
        optionsLogic.each {___key , ___value ->
            if (___key()) {
                ___value()
            }
        }
        new QueryServant(new Config(cliArgs.config)).perform(cliArgs)
    }
}

CliArgs cliArgs = new CliArgs()
processArgs(cliArgs, args)