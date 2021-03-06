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
        return defaultInstanceValue==null ? ((fileInstance!=null?fileInstanceValue(fileInstance):defaultValue)?:defaultValue) : defaultInstanceValue
    }

    /**
     * Get the prepared instance of DefaultConfig.
     *
     * @return An instance of DefaultConfig.
     */
    DefaultConfig getDefaults() {
        if (!this.defaults) {
            def defaults = jsonConfig.defaults?:new DefaultConfig()
            DefaultConfig defaultsExternal = getJsonInstance {defaults.filename}

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

            resolveListeners(this.defaults.listeners)

            processWindowsAuthentication(this.defaults)
        }
        this.defaults
    }

    private void resolveListeners(ListenersConfig listenersConfig) {
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

    private static final ListenersConfig processExternalListeners(final ListenersConfig listeners) {
        ListenersConfig externalListeners = getJsonInstance {
            listeners.filename
        }

        ListenersConfig newListener = listeners

        if (externalListeners) {
            newListener = new ListenersConfig(
                    command: resolveValue(listeners.command, externalListeners, listeners.command,
                            {___fileInstance->___fileInstance.command}),
                    onStart: resolveValue(listeners.onStart, externalListeners, listeners.command,
                            {___fileInstance->___fileInstance.onStart}),
                    onHeader: resolveValue(listeners.onHeader, externalListeners, listeners.command,
                            {___fileInstance->___fileInstance.onHeader}),
                    onData: resolveValue(listeners.onData, externalListeners, listeners.command,
                            {___fileInstance->___fileInstance.onData}),
                    onComplete: resolveValue(listeners.onComplete, externalListeners, listeners.command,
                            {___fileInstance->___fileInstance.onComplete}),
                    filter: resolveValue(listeners.filter, externalListeners, listeners.command,
                            {___fileInstance->___fileInstance.filter}),
            )
        }

        newListener
    }

    private static final ListenersConfig initListeners(ListenersConfig defaultListeners, ListenersConfig queryListeners) {

        ListenersConfig newDefaultListeners = processExternalListeners(defaultListeners)
        ListenersConfig newQueryListeners = processExternalListeners(queryListeners)

        newQueryListeners.with {
            command = command ?: newDefaultListeners.command
            onStart = onStart ?: newDefaultListeners.onStart
            onHeader = onHeader ?: newDefaultListeners.onHeader
            onData = onData ?: newDefaultListeners.onData
            onComplete = onComplete ?: newDefaultListeners.onComplete
            filter = filter ?: newDefaultListeners.filter
        }

        newQueryListeners
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

    private static <TYPE_OUTPUT, TYPE_INPUT extends TYPE_OUTPUT> TYPE_OUTPUT getJsonInstance(Class<TYPE_INPUT> clazz,
                                                                                             Closure<String> filename) {
        String actualFilename = filename()
        TYPE_OUTPUT jsonInstance = null

        if (actualFilename) {
            File defaultsFile = new File(actualFilename)
            String defaultsText = Invoker.invoke(new GetConfigText(defaultsFile.absolutePath))
            if (defaultsText) {
                jsonInstance = Invoker.invoke(new ToJson<TYPE_INPUT>(defaultsText, clazz))
            }
        }

        return jsonInstance
    }

    private static <TYPE_OUTPUT> TYPE_OUTPUT getJsonInstance(Closure<String> filename) {
        return getJsonInstance(TYPE_OUTPUT.class, filename)
    }

    private QueriesConfig createNewQueryConfig(QueriesConfig ___qryConfig, String description, QueriesConfig defaults) {

        DefaultConfig queriesExternal = getJsonInstance(QueriesConfig.class, {
            ___qryConfig.filename
        })

        String ___description = resolveValue(___qryConfig.description, queriesExternal, description,
                {___fileInstance -> ___fileInstance.description})

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
                description: ___description,
                queries: resolveValue(___qryConfig.queries, queriesExternal, defaults.queries,
                        {___fileInstance -> ___fileInstance.queries}),
                listeners: resolveValue(___qryConfig.listeners, queriesExternal, defaults.listeners,
                        {___fileInstance -> ___fileInstance.listeners}))

        processWindowsAuthentication(newQueriesConfig)

        newQueriesConfig.listeners = initListeners(defaults.listeners, newQueriesConfig.listeners)

        def newListeners = newQueriesConfig.listeners

        if (newQueriesConfig.parallel && new Validate(new HasActiveListener()).check(newListeners)) {
            LOG.info("[${___description}] Converting to non-parallel processing because of active listener.")
            newQueriesConfig.parallel = false
        }

        resolveListeners(newListeners)

        def nextConfig=resolveValue(___qryConfig.next, queriesExternal, null
                , {___fileInstance -> ___fileInstance.next})
        newQueriesConfig.next= nextConfig ? createNewQueryConfig(nextConfig
                , nextConfig.description ?: "${___description} [NEXT]", newQueriesConfig) : null

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

            DBPoolConfig dbPoolExternal = getJsonInstance {dbPoolConfig.filename}

            this.dbPoolConfig = new DBPoolConfig(
                    minIdle: resolveValue(dbPoolConfig.minIdle, dbPoolExternal, DB_POOL_MIN_IDLE,
                            {___fileInstance -> ___fileInstance.minIdle}),
                    maxIdle: resolveValue(dbPoolConfig.maxIdle, dbPoolExternal, DB_POOL_MAX_IDLE,
                            {___fileInstance -> ___fileInstance.maxIdle}),
                    maxOpenPreparedStatements: resolveValue(dbPoolConfig.maxOpenPreparedStatements, dbPoolExternal,
                            DB_POOL_MAX_OPEN_PREPARED_STATEMENTS,
                            {___fileInstance -> ___fileInstance.maxOpenPreparedStatements})
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
            def locParams = jsonConfig.params?:[] as ParamConfig[]

            def newParams = new ArrayList<ParamConfig>()
            locParams.each { ___paramConfig ->
                ParamConfig paramExternal = getJsonInstance {
                    ___paramConfig.filename
                }

                if (paramExternal) {
                   newParams.add(new ParamConfig(
                           name: resolveValue(___paramConfig.name, paramExternal, null,
                                   {___fileInstance -> ___fileInstance.name}),
                           description: resolveValue(___paramConfig. description, paramExternal, null,
                                   {___fileInstance -> ___fileInstance.description}),
                           value: resolveValue(___paramConfig.value, paramExternal, null,
                                   {___fileInstance -> ___fileInstance.value}))
                   )
                }
                else {
                    newParams.add(___paramConfig)
                }
            }
            this.params = newParams as ParamConfig[]
        }
        this.params
    }

}
