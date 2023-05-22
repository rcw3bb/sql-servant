package xyz.ronella.tools.sql.servant.conf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.ronella.tools.sql.servant.Config;
import xyz.ronella.tools.sql.servant.Validate;
import xyz.ronella.tools.sql.servant.command.Invoker;
import xyz.ronella.tools.sql.servant.command.impl.*;
import xyz.ronella.tools.sql.servant.listener.HasActiveListener;
import xyz.ronella.tools.sql.servant.listener.ListenerException;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Makes the JSONConfig instance ready for use.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
public class JsonConfigWrapper extends JsonConfig {
    final private Config config;
    final private JsonConfig jsonConfig;
    private DefaultConfig defaults;
    private DBPoolConfig dbPoolConfig;
    private QueriesConfig[] queries;
    private ParamConfig[] params;

    public final static Logger LOG = LogManager.getLogger(JsonConfigWrapper.class);
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    private static final int DB_POOL_MIN_IDLE = 1;
    private static final int DB_POOL_MAX_IDLE = 1;
    private static final int DB_POOL_MAX_OPEN_PREPARED_STATEMENTS = 50;
    private static final Function<Integer, String> DEFAULT_QUERY_DESCRIPTION = ___idx -> String.format("Query %s", ___idx);
    private static final String DB_MSSQL_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    /**
     * Creates an instance of JsonConfigWrapper.
     *
     * @param config An instance of the Config.
     * @param jsonConfig An instance of JsonConfig to prepare.
     */
    public JsonConfigWrapper(final Config config, final JsonConfig jsonConfig) {
        this.config = config;
        this.jsonConfig = jsonConfig;
    }

    private static <TYPE_EXTERNAL_SOURCE,TYPE_OUTPUT> TYPE_OUTPUT resolveValue(TYPE_OUTPUT defaultInstanceValue,
                                                                               TYPE_EXTERNAL_SOURCE fileInstance,
                                                                               TYPE_OUTPUT defaultValue,
                                                                               Function<TYPE_EXTERNAL_SOURCE, TYPE_OUTPUT> fileInstanceValue
    ) {
        /*
        return defaultInstanceValue==null ? ((fileInstance!=null ? fileInstanceValue(fileInstance) : defaultValue) ?: defaultValue) : defaultInstanceValue
        */
        final var byFileInstance = (fileInstance!=null ? fileInstanceValue.apply(fileInstance) : defaultValue);
        return defaultInstanceValue==null ? (byFileInstance==null ? defaultValue : byFileInstance) : defaultInstanceValue;
    }

    /**
     * Get the prepared instance of DefaultConfig.
     *
     * @return An instance of DefaultConfig.
     */
    public DefaultConfig getDefaults() {
        var defaults = this.defaults;
        if (defaults==null) {
            defaults = jsonConfig.getDefaults()!=null ? jsonConfig.getDefaults(): new DefaultConfig();

            final var filename = defaults.getFilename();
            final var defaultsExternal = getJsonInstance(QueriesConfig.class,
                    () -> Invoker.invoke(new GetFilenameByEnv(filename, config.getEnvironment())));

            final var EMPTY_LISTENERS = new ListenersConfig();
            final String DEFAULT_MODE = "stmt";

            this.defaults = new DefaultConfig();
            final var defaultConfig = this.defaults;

            defaultConfig.setMode(resolveValue(defaults.getMode(), defaultsExternal, DEFAULT_MODE, DefaultConfig::getMode));
            defaultConfig.setJdbcDriver(resolveValue(defaults.getJdbcDriver(), defaultsExternal, null, DefaultConfig::getJdbcDriver));
            defaultConfig.setConnectionString(resolveValue(defaults.getConnectionString(), defaultsExternal, null, DefaultConfig::getConnectionString));
            defaultConfig.setParallel(resolveValue(defaults.getParallel(), defaultsExternal, null, DefaultConfig::getParallel));
            defaultConfig.setWindowsAuthentication(resolveValue(defaults.getWindowsAuthentication(), defaultsExternal, false, DefaultConfig::getWindowsAuthentication));
            defaultConfig.setUsername(resolveValue(defaults.getUsername(), defaultsExternal, "", DefaultConfig::getUsername));
            defaultConfig.setPassword(resolveValue(defaults.getPassword(), defaultsExternal, "", DefaultConfig::getPassword));
            defaultConfig.setListeners(resolveValue(defaults.getListeners(), defaultsExternal, EMPTY_LISTENERS, DefaultConfig::getListeners));

            if (isWindows()) {
                this.defaults.getListeners().setCommand("cmd.exe /c");
            }

            resolveListeners(this.defaults.getListeners());

            processWindowsAuthentication(this.defaults);
        }
        return this.defaults;
    }

    private void resolveListeners(final ListenersConfig listenersConfig) {
        final var listenerDirectory = config.getListenerDirectory();
        final var listeners = List.of("onStart", "onHeader", "onData", "onComplete");

        listeners.forEach((___field) -> {
            try {
                final var field = ListenersConfig.class.getDeclaredField(___field);
                field.setAccessible(true);

                final var listener = (String) field.get(listenersConfig);
                if (listener!=null) {
                    if (!(new File(listener).exists())) {
                        final var actualListenerFile = new File(String.format("%s%s%s", listenerDirectory, File.separator, listener));
                        if (actualListenerFile.exists()) {
                            field.set(listenersConfig, actualListenerFile.getAbsolutePath());
                        } else {
                            throw new ListenerException(String.format("%s doesn't exists", actualListenerFile));
                        }
                    }

                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static ListenersConfig processExternalListeners(final String environment, final ListenersConfig listeners) {
        final var externalListeners = getJsonInstance(ListenersConfig.class,
                ()-> Invoker.invoke(new GetFilenameByEnv(listeners.getFilename(), environment)));

        if (externalListeners!=null) {

            final var newListener = new ListenersConfig();

            newListener.setCommand(resolveValue(listeners.getCommand(), externalListeners, listeners.getCommand(), ListenersConfig::getCommand));
            newListener.setOnStart(resolveValue(listeners.getOnStart(), externalListeners, listeners.getCommand(), ListenersConfig::getOnStart));
            newListener.setOnHeader(resolveValue(listeners.getOnHeader(), externalListeners, listeners.getCommand(), ListenersConfig::getOnHeader));
            newListener.setOnData(resolveValue(listeners.getOnData(), externalListeners, listeners.getCommand(), ListenersConfig::getOnData));
            newListener.setOnComplete(resolveValue(listeners.getOnComplete(), externalListeners, listeners.getCommand(), ListenersConfig::getOnComplete));
            newListener.setFilter(resolveValue(listeners.getFilter(), externalListeners, listeners.getCommand(), ListenersConfig::getFilter));

            return newListener;
        }

        return null;
    }

    private static ListenersConfig initListeners(final String environment, final ListenersConfig defaultListeners, final ListenersConfig queryListeners) {

        final var newDefaultListeners = processExternalListeners(environment, defaultListeners);
        final var newQueryListeners = processExternalListeners(environment, queryListeners);

        newQueryListeners.setCommand(Optional.ofNullable(newQueryListeners.getCommand()).orElse(newDefaultListeners.getCommand()));
        newQueryListeners.setOnStart(Optional.ofNullable(newQueryListeners.getOnStart()).orElse(newDefaultListeners.getOnStart()));
        newQueryListeners.setOnHeader(Optional.ofNullable(newQueryListeners.getOnHeader()).orElse(newDefaultListeners.getOnHeader()));
        newQueryListeners.setOnData(Optional.ofNullable(newQueryListeners.getOnData()).orElse(newDefaultListeners.getOnData()));
        newQueryListeners.setOnComplete(Optional.ofNullable(newQueryListeners.getOnComplete()).orElse(newDefaultListeners.getOnComplete()));
        newQueryListeners.setFilter(Optional.ofNullable(newQueryListeners.getFilter()).orElse(newDefaultListeners.getFilter()));

        return newQueryListeners;
    }

    private static boolean isWindows() {
        return OS_NAME.contains("win");
    }

    private static void processWindowsAuthentication(final DefaultConfig defaults) throws ConfigurationException {
        if (defaults.getWindowsAuthentication()) {

            if (!isWindows()) {
                throw new ConfigurationException(String.format("windowsAuthentication cannot be used in %s", OS_NAME));
            }

            final var connectStr = defaults.getConnectionString();
            if (!connectStr.contains("integratedSecurity")) {
                defaults.setConnectionString(String.format("%s;integratedSecurity=true", connectStr));
            }
            if (defaults.getJdbcDriver()==null) {
                defaults.setJdbcDriver(DB_MSSQL_DRIVER);
            }
        }
    }

    private static <TYPE_OUTPUT, TYPE_INPUT extends TYPE_OUTPUT> TYPE_OUTPUT getJsonInstance(final Class<TYPE_INPUT> clazz,
                                                                                             final Supplier<String> filename) {
        final var actualFilename = filename.get();
        TYPE_OUTPUT jsonInstance = null;

        if (actualFilename!=null) {
            final var defaultsFile = new File(actualFilename);
            final var defaultsText = Invoker.invoke(new GetConfigText(defaultsFile.getAbsolutePath()));
            if (defaultsText!=null) {
                jsonInstance = Invoker.invoke(new ToJson<>(defaultsText, clazz));
            }
        }

        return jsonInstance;
    }

    private QueriesConfig createNewQueryConfig(final QueriesConfig ___qryConfig, final String description, final QueriesConfig defaults) {

        final var queriesExternal = getJsonInstance(QueriesConfig.class,
                ()-> Invoker.invoke(new GetFilenameByEnv(___qryConfig.getFilename(), config.getEnvironment()))
        );

        final var ___description = resolveValue(___qryConfig.getDescription(), queriesExternal, description,
                DefaultConfig::getDescription);

        final var newQueriesConfig = new QueriesConfig();

        newQueriesConfig.setJdbcDriver(resolveValue(___qryConfig.getJdbcDriver(), queriesExternal, defaults.getJdbcDriver(), DefaultConfig::getJdbcDriver));
        newQueriesConfig.setConnectionString(resolveValue(___qryConfig.getConnectionString(), queriesExternal,defaults.getConnectionString(), DefaultConfig::getConnectionString));
        newQueriesConfig.setUsername(resolveValue(___qryConfig.getUsername(), queriesExternal,defaults.getUsername(), DefaultConfig::getUsername));
        newQueriesConfig.setPassword(resolveValue(___qryConfig.getPassword(), queriesExternal, defaults.getPassword(), DefaultConfig::getPassword));
        newQueriesConfig.setMode(resolveValue(___qryConfig.getMode(), queriesExternal, defaults.getMode(), DefaultConfig::getMode));
        newQueriesConfig.setParallel(resolveValue(___qryConfig.getParallel(), queriesExternal, defaults.getParallel(), DefaultConfig::getParallel));
        newQueriesConfig.setWindowsAuthentication(resolveValue(___qryConfig.getWindowsAuthentication(), queriesExternal, defaults.getWindowsAuthentication(), DefaultConfig::getWindowsAuthentication));
        newQueriesConfig.setDescription(___description);
        newQueriesConfig.setQueries(resolveValue(___qryConfig.getQueries(), queriesExternal, defaults.getQueries(), QueriesConfig::getQueries));
        newQueriesConfig.setListeners(resolveValue(___qryConfig.getListeners(), queriesExternal, defaults.getListeners(), DefaultConfig::getListeners));

        processWindowsAuthentication(newQueriesConfig);

        newQueriesConfig.setListeners(initListeners(config.getEnvironment(), defaults.getListeners(), newQueriesConfig.getListeners()));

        final var newListeners = newQueriesConfig.getListeners();

        if (newQueriesConfig.getParallel() && new Validate(new HasActiveListener()).check(newListeners)) {
            LOG.info(String.format("[%s] Converting to non-parallel processing because of active listener.", ___description));
            newQueriesConfig.setParallel(false);
        }

        resolveListeners(newListeners);

        final var nextConfig=resolveValue(___qryConfig.getNext(), queriesExternal, null
                , QueriesConfig::getNext);
        newQueriesConfig.setNext(nextConfig!=null ? createNewQueryConfig(nextConfig
                , nextConfig.getDescription()!=null ? nextConfig.getDescription() : String.format("%s [NEXT]", ___description), newQueriesConfig) : null);

        return newQueriesConfig;
    }

    /**
     * Get the prepared instances of QueriesConfig.
     *
     * @return An arrays of QueriesConfig.
     */
    public QueriesConfig[] getQueries() {
        if (this.queries==null) {
            final var queries = new ArrayList<QueriesConfig>();

            for (int ___idx = 0; ___idx < jsonConfig.getQueries().length; ___idx++) {
                final var it = jsonConfig.getQueries()[___idx];
                queries.add(createNewQueryConfig(it, it.getDescription()!=null ? it.getDescription() : DEFAULT_QUERY_DESCRIPTION.apply(___idx+1),
                        new QueriesConfigDefaultWrapper(this.getDefaults())));
            }

            this.queries = queries.toArray(new QueriesConfig[0]);
        }
        return this.queries;
    }

    /**
     * Get the prepared instance of DBPoolConfig.
     *
     * @return An instance of DBPoolConfig.
     */
    public DBPoolConfig getDbPoolConfig() {
        if (this.dbPoolConfig==null) {
            final var dbPoolConfig = jsonConfig.getDbPoolConfig()!=null? jsonConfig.getDbPoolConfig() : new DBPoolConfig();

            DBPoolConfig dbPoolExternal = getJsonInstance(DBPoolConfig.class, ()->
                Invoker.invoke(new GetFilenameByEnv(dbPoolConfig.getFilename(), config.getEnvironment())));

            this.dbPoolConfig = new DBPoolConfig();

            this.dbPoolConfig.setMinIdle(resolveValue(dbPoolConfig.getMinIdle(), dbPoolExternal, DB_POOL_MIN_IDLE, DBPoolConfig::getMinIdle));
            this.dbPoolConfig.setMaxIdle(resolveValue(dbPoolConfig.getMaxIdle(), dbPoolExternal, DB_POOL_MAX_IDLE, DBPoolConfig::getMaxIdle));
            this.dbPoolConfig.setMaxOpenPreparedStatements(resolveValue(dbPoolConfig.getMaxOpenPreparedStatements(), dbPoolExternal, DB_POOL_MAX_OPEN_PREPARED_STATEMENTS, DBPoolConfig::getMaxOpenPreparedStatements));
        }
        return this.dbPoolConfig;
    }

    /**
     * Get the parameter's configuration.
     *
     * @return An instance of ParamsConfig
     * @since 1.2.0
     */
    public ParamConfig[] getParams() {
        if (this.params==null) {
            final var locParams = Arrays.stream(Optional.ofNullable(jsonConfig.getParams()).orElse(new ParamConfig[]{})).collect(Collectors.toList());

            final var newParams = new ArrayList<ParamConfig>();
            locParams.forEach(___paramConfig -> {
                final var paramExternal = getJsonInstance(ParamConfig.class,
                        ()-> Invoker.invoke(new GetFilenameByEnv(___paramConfig.getFilename(), config.getEnvironment())));

                if (paramExternal!=null) {
                    final var newParam = new ParamConfig();
                    newParam.setName(resolveValue(___paramConfig.getName(), paramExternal, null, ParamConfig::getName));
                    newParam.setDescription(resolveValue(___paramConfig.getDescription(), paramExternal, null, ParamConfig::getDescription));
                    newParam.setValue(resolveValue(___paramConfig.getValue(), paramExternal, null, ParamConfig::getValue));

                    newParams.add(newParam);
                }
                else {
                    newParams.add(___paramConfig);
                }
            });
            this.params = newParams.toArray(new ParamConfig[] {});
        }
        return this.params;
    }

}
