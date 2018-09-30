package xyz.ronella.tools.sql.servant.conf

class JsonConfigWrapper extends JsonConfig {
    private JsonConfig jsonConfig
    private DefaultConfig defaults
    private DBPoolConfig dbPoolConfig
    private QueriesConfig[] queries

    private static final int DB_POOL_MIN_IDLE = 1
    private static final int DB_POOL_MAX_IDLE = 1
    private static final int DB_POOL_MAX_OPEN_PREPARED_STATEMENTS = 50
    private static final def DEFAULT_QUERY_DESCRIPTION = {___idx -> "Query ${___idx}"}

    JsonConfigWrapper(JsonConfig jsonConfig) {
        this.jsonConfig = jsonConfig
    }

    DefaultConfig getDefaults() {
        if (!this.defaults) {
            def defaults = jsonConfig.defaults
            this.defaults = new DefaultConfig(
                    mode: defaults.mode?:'stmt',
                    jdbcDriver: defaults.jdbcDriver,
                    connectionString: defaults.connectionString,
                    parallel: defaults.parallel?:false,
                    username: defaults.username?:'',
                    password: defaults.password?:''
            )
        }
        this.defaults
    }

    private QueriesConfig createNewQueryConfig(QueriesConfig ___qryConfig, String description, QueriesConfig defaults) {
        QueriesConfig newQueriesConfig = new QueriesConfig(
                jdbcDriver: ___qryConfig.jdbcDriver ?: defaults.jdbcDriver,
                connectionString: ___qryConfig.connectionString ?: defaults.connectionString,
                username: ___qryConfig.username ?: defaults.username,
                password: ___qryConfig.password ?: defaults.password,
                mode: ___qryConfig.mode ?: defaults.mode,
                parallel: ___qryConfig.parallel ?: defaults.parallel,
                description: description,
                queries: ___qryConfig.queries ?: defaults.queries)

        newQueriesConfig.next= ___qryConfig.next ? createNewQueryConfig(___qryConfig.next
                , "${description} [NEXT]", newQueriesConfig) : null

        return newQueriesConfig
    }

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
