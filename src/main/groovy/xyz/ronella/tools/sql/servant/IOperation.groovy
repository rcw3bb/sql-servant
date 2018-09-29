package xyz.ronella.tools.sql.servant

import xyz.ronella.tools.sql.servant.conf.QueriesConfig

interface IOperation {
    def perform(Config config, QueriesConfig qryConfig, CliArgs cliArgs)
}