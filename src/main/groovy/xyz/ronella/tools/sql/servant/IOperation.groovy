package xyz.ronella.tools.sql.servant

import xyz.ronella.tools.sql.servant.conf.QueriesConfig

import java.util.concurrent.Future

interface IOperation {
    def perform(List<Future<IStatus>> futures, Config config, QueriesConfig qryConfig, CliArgs cliArgs)
}