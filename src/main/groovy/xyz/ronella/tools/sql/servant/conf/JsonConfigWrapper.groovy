package xyz.ronella.tools.sql.servant.conf

import org.apache.log4j.Logger
import xyz.ronella.tools.sql.servant.Config
import xyz.ronella.tools.sql.servant.Validate
import xyz.ronella.tools.sql.servant.command.Invoker
import xyz.ronella.tools.sql.servant.command.impl.GetConfigText
import xyz.ronella.tools.sql.servant.command.impl.ToJson
import xyz.ronella.tools.sql.servant.listener.HasActiveListener
import xyz.ronella.tools.sql.servant.listener.ListenerException

/**
 * Makes the JSONConfig instance ready for use.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
class JsonConfigWrapper extends JsonConfig {
    private Config config
    private JsonConfig jsonConfig
    private DefaultConfig defaults
    private DBPoolConfig dbPoolConfig
    private QueriesConfig[] queries
    private ParamConfig[] params

    public final static def LOG = Logger.getLogger(JsonConfigWrapper.class.name)

    private static final String OS_NAME = System.getProperty("os.name").toLowerCase()
    private static final int DB_POOL_MIN_IDLE = 1
    private static final int DB_POOL_MAX_IDLE = 1
    private static final int DB_POOL_MAX_OPEN_PREPARED_STATEMENTS = 50
    private static final def DEFAULT_QUERY_DESCRIPTION = {___idx -> "Query ${___idx}"}
    private static final String DB_MSSQL_DRIVER = 'com.microsoft.sqlserver.jdbc.SQLServerDriver'
    private static final String DEFAULT_LISTENER_FILTER = '\"%'

    /**
     * Creates an instance of JsonConfigWrapper.
     *
     * @param config An instance of the Config.
     * @param jsonConfig An instance of JsonConfig to prepare.
     */
    JsonConfigWrapper(Config config, JsonConfig jsonConfig) {
        this.config = config
        this.jsonConfig = jsonConfig
    }

    private static <TYPE_EXTERNAL_SOURCE,TYPE_OUTPUT> TYPE_OUTPUT resolveValue(TYPE_OUTPUT defaultInstanceValue,
                                                                               TYPE_EXTERNAL_SOURCE fileInstance,
                                                                               TYPE_OUTPUT defaultValue,
                                                                               Closure<TYPE_OUTPUT> fileInstanceValue
    ) {
        return defaultInstanceValue?:(fileInstance!=null?fileInstanceValue(fileInstance):defaultValue)?:defaultValue
    }

    /**
     * Get the prepared instance of DefaultConfig.
     *
     * @return An instance of DefaultConfig.
     */
    DefaultConfig getDefaults() {
        if (!this.defaults) {
            def defaults = jsonConfig.defaults?:new DefaultConfig()
            String defaultsFilename = defaults.filename ? defaults.filename : null
            DefaultConfig defaultsExternal = null

            if (defaultsFilename) {
                File defaultsFile = new File(defaultsFilename)
                String defaultsText = Invoker.invoke(new GetConfigText(defaultsFile.absolutePath))
                if (defaultsText) {
                    defaultsExternal = Invoker.invoke(new ToJson<DefaultConfig>(defaultsText, DefaultConfig.class))
                }
            }

            final def EMPTY_LISTENERS = new ListenersConfig()
            final String DEFAULT_MODE = 'stmt'

            this.defaults = new DefaultConfig(
                    mode: resolveValue(defaults.mode, defaultsExternal, DEFAULT_MODE,
                            {___fileInstance -> ___fileInstance.mode}),
                    jdbcDriver: resolveValue(defaults.jdbcDriver, defaultsExternal, null,
                            {___fileInstance -> ___fileInstance.jdbcDriver}),
                    connectionString: resolveValue(defaults.connectionString, defaultsExternal, null,
                            {___fileInstance -> ___fileInstance.connectionString}),
                    parallel: resolveValue(defaults.parallel, defaultsExternal, false,
                            {___fileInstance -> ___fileInstance.parallel}),
                    windowsAuthentication: resolveValue(defaults.windowsAuthentication,defaultsExternal,false,
                            {___fileInstance -> ___fileInstance.windowsAuthentication}),
                    username: resolveValue(defaults.username, defaultsExternal, '',
                            {___fileInstance -> ___fileInstance.username}),
                    password: resolveValue(defaults.password, defaultsExternal, '',
                            {___fileInstance -> ___fileInstance.password}),
                    listeners: resolveValue(defaults.listeners, defaultsExternal, EMPTY_LISTENERS,
                            {___fileInstance -> ___fileInstance.listeners})
            )

            if (isWindows()) {
                this.defaults.listeners.command = 'cmd.exe /c'
            }

            initListeners(this.defaults.listeners)

            processWindowsAuthentication(this.defaults)
        }
        this.defaults
    }

    private void initListeners(ListenersConfig listenersConfig) {
        def listenerDirectory = config.listenerDirectory

        listenersConfig.with {
            filter = filter?:DEFAULT_LISTENER_FILTER
            def listeners = ['onStart', 'onHeader', 'onData', 'onComplete']
            listeners.each {
                String listener = listenersConfig[it]
                if (listener) {
                    if (!(new File(listener).exists())) {
                        def actualListenerFile = new File("${listenerDirectory}${File.separator}${listener}")
                        if (actualListenerFile.exists()) {
                            listenersConfig[it] = actualListenerFile.absolutePath
                        } else {
                            throw new ListenerException("${actualListenerFile} doesn't exists")
                        }
                    }
                }
            }
        }
    }

    private static boolean isWindows() {
        OS_NAME.contains('win')
    }

    private static void processWindowsAuthentication(DefaultConfig defaults) {
        if (defaults.windowsAuthentication) {

            if (!isWindows()) {
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
        String queriesFilename = ___qryConfig.filename ? ___qryConfig.filename : null
        DefaultConfig queriesExternal = null

        if (queriesFilename) {
            File defaultsFile = new File(queriesFilename)
            String defaultsText = Invoker.invoke(new GetConfigText(defaultsFile.absolutePath))
            if (defaultsText) {
                queriesExternal = Invoker.invoke(new ToJson<QueriesConfig>(defaultsText, QueriesConfig.class))
            }
        }

        QueriesConfig newQueriesConfig = new QueriesConfig(
                jdbcDriver: resolveValue(___qryConfig.jdbcDriver, queriesExternal, defaults.jdbcDriver,
                        {___fileInstance -> ___fileInstance.jdbcDriver}),
                connectionString: resolveValue(___qryConfig.connectionString, queriesExternal,defaults.connectionString,
                        {___fileInstance -> ___fileInstance.connectionString}),
                username: resolveValue(___qryConfig.username, queriesExternal, defaults.username,
                        {___fileInstance -> queriesExternal.username}),
                password: resolveValue(___qryConfig.password, queriesExternal, defaults.password,
                        {___fileInstance -> ___fileInstance.password}),
                mode: resolveValue(___qryConfig.mode, queriesExternal, defaults.mode,
                        {___fileInstance -> ___fileInstance.mode}),
                parallel: resolveValue(___qryConfig.parallel, queriesExternal, defaults.parallel,
                        {___fileInstance -> ___fileInstance.parallel}),
                windowsAuthentication: resolveValue(___qryConfig.windowsAuthentication, queriesExternal,
                        defaults.windowsAuthentication,
                        {___fileInstance -> ___fileInstance.windowsAuthentication}),
                description: resolveValue(description, queriesExternal, description,
                        {___fileInstance -> ___fileInstance.description}),
                queries: resolveValue(___qryConfig.queries, queriesExternal, defaults.queries,
                        {___fileInstance -> ___fileInstance.queries}),
                listeners: resolveValue(___qryConfig.listeners, queriesExternal, defaults.listeners,
                        {___fileInstance -> ___fileInstance.listeners}))

        processWindowsAuthentication(newQueriesConfig)

        def defListeners = defaults.listeners
        def newListeners = newQueriesConfig.listeners

        newListeners.with {
            command = command ?: defListeners.command
            onStart = onStart ?: defListeners.onStart
            onHeader = onHeader ?: defListeners.onHeader
            onData = onData ?: defListeners.onData
            onComplete = onComplete ?: defListeners.onComplete
            filter = filter ?: defListeners.filter
        }

        if (newQueriesConfig.parallel && new Validate(new HasActiveListener()).check(newListeners)) {
            LOG.info("[${description}] Converting to non-parallel processing because of active listener.")
            newQueriesConfig.parallel = false
        }

        initListeners(newListeners)

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
            def dbPoolConfig = jsonConfig.dbPoolConfig?:new DBPoolConfig()
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

    /**
     * Get the parameters configuration.
     *
     * @return An instance of ParamsConfig
     * @since 1.2.0
     */
    ParamConfig[] getParams() {
        if (!this.params) {
            def params = jsonConfig.params?:[] as ParamConfig[]
            this.params = params
        }
        this.params
    }

}
