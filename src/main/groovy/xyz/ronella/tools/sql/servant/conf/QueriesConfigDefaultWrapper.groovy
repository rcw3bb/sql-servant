package xyz.ronella.tools.sql.servant.conf

class QueriesConfigDefaultWrapper extends QueriesConfig {

    private QueriesConfig queriesConfig

    QueriesConfigDefaultWrapper(DefaultConfig defaultConfig) {
        this.queriesConfig = new QueriesConfig(
                jdbcDriver: defaultConfig.jdbcDriver,
                connectionString: defaultConfig.connectionString,
                username: defaultConfig.username,
                password: defaultConfig.password,
                mode: defaultConfig.mode,
                parallel: defaultConfig.parallel,
                windowsAuthentication: defaultConfig.windowsAuthentication,
                queries: new String[0]
        )
    }

    String getJdbcDriver() {
        queriesConfig.jdbcDriver
    }

    String getConnectionString() {
        queriesConfig.connectionString
    }

    String getUsername() {
        queriesConfig.username
    }

    String getPassword() {
        queriesConfig.password
    }

    String getMode() {
        queriesConfig.mode
    }

    Boolean getParallel() {
        queriesConfig.parallel
    }

    String getDescription() {
        queriesConfig.description
    }

    String[] getQueries() {
        queriesConfig.queries
    }

    QueriesConfig getNext() {
        queriesConfig.next
    }

    Boolean getWindowsAuthentication() {
        queriesConfig.windowsAuthentication
    }
}
