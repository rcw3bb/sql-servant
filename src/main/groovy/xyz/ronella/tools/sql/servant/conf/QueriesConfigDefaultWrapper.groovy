package xyz.ronella.tools.sql.servant.conf

/**
 * Generates a default based on the instance of DefaultConfig or QueriesConfig.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
class QueriesConfigDefaultWrapper extends QueriesConfig {

    private QueriesConfig queriesConfig

    /**
     * Creates an instance of QueriesConfigDefaultWrapper
     *
     * @param defaultConfig An instance of either DefaultConfig or QueriesConfig.
     */
    QueriesConfigDefaultWrapper(DefaultConfig defaultConfig) {
        this.queriesConfig = new QueriesConfig(
                jdbcDriver: defaultConfig.jdbcDriver,
                connectionString: defaultConfig.connectionString,
                username: defaultConfig.username,
                password: defaultConfig.password,
                mode: defaultConfig.mode,
                parallel: defaultConfig.parallel,
                windowsAuthentication: defaultConfig.windowsAuthentication,
                listeners: defaultConfig.listeners,
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

    ListenersConfig getListeners() {
        queriesConfig.listeners
    }
}
