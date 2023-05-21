package xyz.ronella.tools.sql.servant.conf;

/**
 * Generates a default based on the instance of DefaultConfig or QueriesConfig.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
public class QueriesConfigDefaultWrapper extends QueriesConfig {

    final private QueriesConfig queriesConfig;

    /**
     * Creates an instance of QueriesConfigDefaultWrapper
     *
     * @param defaultConfig An instance of either DefaultConfig or QueriesConfig.
     */
    QueriesConfigDefaultWrapper(DefaultConfig defaultConfig) {
        queriesConfig = new QueriesConfig();
        queriesConfig.setJdbcDriver(defaultConfig.getJdbcDriver());
        queriesConfig.setConnectionString(defaultConfig.getConnectionString());
        queriesConfig.setUsername(defaultConfig.getUsername());
        queriesConfig.setPassword(defaultConfig.getPassword());
        queriesConfig.setMode(defaultConfig.getMode());
        queriesConfig.setParallel(defaultConfig.getParallel());
        queriesConfig.setWindowsAuthentication(defaultConfig.getWindowsAuthentication());
        queriesConfig.setListeners(defaultConfig.getListeners());
        queriesConfig.setQueries(new String[0]);
    }

    @Override
    public String getJdbcDriver() {
        return queriesConfig.getJdbcDriver();
    }

    @Override
    public String getConnectionString() {
        return queriesConfig.getConnectionString();
    }

    @Override
    public String getUsername() {
        return queriesConfig.getUsername();
    }

    @Override
    public String getPassword() {
        return queriesConfig.getPassword();
    }

    @Override
    public String getMode() {
        return queriesConfig.getMode();
    }

    @Override
    public Boolean getParallel() {
        return queriesConfig.getParallel();
    }

    @Override
    public String getDescription() {
        return queriesConfig.getDescription();
    }

    @Override
    public String[] getQueries() {
        return queriesConfig.getQueries();
    }

    @Override
    public QueriesConfig getNext() {
        return queriesConfig.getNext();
    }

    @Override
    public Boolean getWindowsAuthentication() {
        return queriesConfig.getWindowsAuthentication();
    }

    @Override
    public ListenersConfig getListeners() {
        return queriesConfig.getListeners();
    }
}
