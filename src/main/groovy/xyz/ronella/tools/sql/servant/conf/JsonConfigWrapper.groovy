package xyz.ronella.tools.sql.servant.conf

/**
 * Makes the JSONConfig instance ready for use.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
class JsonConfigWrapper extends JsonConfig {
    private JsonConfig jsonConfig
    private DefaultConfig defaults
    private DBPoolConfig dbPoolConfig
    private QueriesConfig[] queries

    private static final String OS_NAME = System.getProperty("os.name").toLowerCase()
    private static final int DB_POOL_MIN_IDLE = 1
    private static final int DB_POOL_MAX_IDLE = 1
    private static final int DB_POOL_MAX_OPEN_PREPARED_STATEMENTS = 50
    private static final def DEFAULT_QUERY_DESCRIPTION = {___idx -> "Query ${___idx}"}
    private static final String DB_MSSQL_DRIVER = 'com.microsoft.sqlserver.jdbc.SQLServerDriver'

    /**
     * Creates an instance of JsonConfigWrapper.
     *
     * @param jsonConfig An instance of JsonConfig to prepare.
     */
    JsonConfigWrapper(JsonConfig jsonConfig) {
        this.jsonConfig = jsonConfig
    }

    /**
     * Get the prepared instance of DefaultConfig.
     *
     * @return An instance of DefaultConfig.
     */
    DefaultConfig getDefaults() {
        if (!this.defaults) {
            def defaults = jsonConfig.defaults
            this.defaults = new DefaultConfig(
                    mode: defaults.mode?:'stmt',
                    jdbcDriver: defaults.jdbcDriver,
                    connectionString: defaults.connectionString,
                    parallel: defaults.parallel?:false,
                    windowsAuthentication: defaults.windowsAuthentication?:false,
                    username: defaults.username?:'',
                    password: defaults.password?:''
            )
            processWindowsAuthentication(this.defaults)
        }
        this.defaults
    }

    private static void processWindowsAuthentication(DefaultConfig defaults) {
        if (defaults.windowsAuthentication) {

            if (!OS_NAME.contains('win')) {
                throw new ConfigurationException("windowsAuthentication cannot be used in ${OS_NAME}")
            }

            String connectStr = defaults.connectionString
            if (!connectStr.contains('integratedSecurity')) {
                defaults.connectionString="${connectStr};integratedSecurity=true"
            }
            if (!defaults.jdbcDriver) {
                defaults.jdbcDriver = DB_MSSQL_DRIVER
            }
        }
    }

    private QueriesConfig createNewQueryConfig(QueriesConfig ___qryConfig, String description, QueriesConfig defaults) {
        QueriesConfig newQueriesConfig = new QueriesConfig(
                jdbcDriver: ___qryConfig.jdbcDriver ?: defaults.jdbcDriver,
                connectionString: ___qryConfig.connectionString ?: defaults.connectionString,
                username: ___qryConfig.username ?: defaults.username,
                password: ___qryConfig.password ?: defaults.password,
                mode: ___qryConfig.mode==null ? defaults.mode : ___qryConfig.mode,
                parallel: ___qryConfig.parallel==null ? defaults.parallel : ___qryConfig.parallel,
                windowsAuthentication: ___qryConfig.windowsAuthentication==null ? defaults.windowsAuthentication :
                        ___qryConfig.windowsAuthentication,
                description: description,
                queries: ___qryConfig.queries ?: defaults.queries)

        processWindowsAuthentication(newQueriesConfig)

        def nextConfig=___qryConfig.next
        newQueriesConfig.next= nextConfig ? createNewQueryConfig(nextConfig
                , nextConfig.description ?: "${description} [NEXT]", newQueriesConfig) : null

        return newQueriesConfig
    }

    /**
     * Get the prepared instances of QueriesConfig.
     *
     * @return An arrays of QueriesConfig.
     */
    QueriesConfig[] getQueries() {
        if (!this.queries) {
            List<QueriesConfig> queries = new ArrayList<>()
            jsonConfig.queries.eachWithIndex { it, ___idx ->
                queries.add(createNewQueryConfig(it, it.description ?: DEFAULT_QUERY_DESCRIPTION(___idx+1),
                new QueriesConfigDefaultWrapper(this.getDefaults())))
            }
            this.queries = queries.toArray(new QueriesConfig[0])
        }
        this.queries
    }

    /**
     * Get the prepared instance of DBPoolConfig.
     *
     * @return An instance of DBPoolConfig.
     */
    DBPoolConfig getDbPoolConfig() {
        if (!this.dbPoolConfig) {
            def dbPoolConfig = jsonConfig.dbPoolConfig
            this.dbPoolConfig = new DBPoolConfig(
                    minIdle: dbPoolConfig ? (dbPoolConfig.minIdle?:DB_POOL_MIN_IDLE) : DB_POOL_MIN_IDLE,
                    maxIdle: dbPoolConfig ? (dbPoolConfig.maxIdle?:DB_POOL_MAX_IDLE) : DB_POOL_MAX_IDLE,
                    maxOpenPreparedStatements: dbPoolConfig
                            ? (dbPoolConfig.maxOpenPreparedStatements?:DB_POOL_MAX_OPEN_PREPARED_STATEMENTS)
                            : DB_POOL_MAX_OPEN_PREPARED_STATEMENTS
            )
        }
        this.dbPoolConfig
    }

}
