import xyz.ronella.tools.sql.servant.CliArgs
import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.QueryServant

void processArgs(final CliArgs cliArgs, String ... args) {

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
        println 'SQL Servant v1.0.0 by Ron [2018]'
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