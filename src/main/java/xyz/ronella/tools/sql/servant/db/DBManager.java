package xyz.ronella.tools.sql.servant.db;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.ronella.tools.sql.servant.listener.ListenerInvoker;
import xyz.ronella.tools.sql.servant.conf.DBPoolConfig;
import xyz.ronella.tools.sql.servant.conf.QueriesConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The class that is responsible to the actual query processing.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
final public class DBManager {
    public final static Logger LOG = LogManager.getLogger(DBManager.class);

    private static final Map<String, DataSource> DATA_SOURCES = new HashMap<>();
    private static final Lock LOCK = new ReentrantLock();
    private static DBManager dbManager;
    private DBPoolConfig dbPoolConfig;

    private DBManager(DBPoolConfig dbPoolConfig) {
        this.dbPoolConfig = dbPoolConfig;
    }

    /**
     * Returns the one and only instance of DBManager.
     *
     * @param dbPoolConfig An instance of DBPoolConfig.
     * @return An instance of DBManager.
     */
    public static DBManager getInstance(DBPoolConfig dbPoolConfig) {
        if (dbManager==null) {
            try {
                LOCK.lock();
                if (dbManager==null) {
                    dbManager = new DBManager(dbPoolConfig);
                }
            }
            finally {
                LOCK.unlock();
            }
        }
        return dbManager;
    }

    private static void validate(QueriesConfig qryConfig) throws DBMissingValueException {
        if (qryConfig.getJdbcDriver()==null) {
            throw new DBMissingValueException("jdbcDriver");
        }

        if (qryConfig.getConnectionString()==null) {
            throw new DBMissingValueException("connectionString");
        }
    }

    /**
     * Retrieve a instance of DataSource from the pool.
     *
     * @param qryConfig An instance of QueriesConfig.
     * @return An instance of DataSource.
     */
    public DataSource getDataSource(final QueriesConfig qryConfig) throws DBMissingValueException {
        validate(qryConfig);

        final var connectionString = qryConfig.getConnectionString();

        var dataSource = DATA_SOURCES.get(connectionString);

        if (dataSource==null) {
            try {
                LOCK.lock();
                dataSource = DATA_SOURCES.get(connectionString);
                if (dataSource==null) {
                    LOG.trace(String.format("Registering %s to pool.", connectionString));
                    Class.forName(qryConfig.getJdbcDriver());

                    final var basicDataSource = new BasicDataSource();
                    basicDataSource.setUrl(connectionString);

                    if (qryConfig.getWindowsAuthentication()==null) {
                        basicDataSource.setUsername(qryConfig.getUsername());
                        basicDataSource.setPassword(qryConfig.getPassword());
                    }

                    basicDataSource.setMinIdle(dbPoolConfig.getMinIdle());
                    basicDataSource.setMaxIdle(dbPoolConfig.getMaxIdle());
                    basicDataSource.setMaxOpenPreparedStatements(dbPoolConfig.getMaxOpenPreparedStatements());
                    dataSource = basicDataSource;

                    DATA_SOURCES.put(connectionString, dataSource);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } finally {
                LOCK.unlock();
            }
        }
        else {
            LOG.trace(String.format("Retrieve %s from pool.", connectionString));
        }

        return dataSource;
    }

    private void runCmd(final QueriesConfig qryConfig, final String cmd, final String description, final String query, final String params) {
        new ListenerInvoker(qryConfig).invokeDataListener(cmd, description,
                query, params);
    }

    /**
     * The actual runner of the sql statement.
     *
     * @param qryConfig An instance of QueriesConfig.
     * @param statement The actual sql statement to execute.
     */
    public void runStatement(final QueriesConfig qryConfig, final String statement) throws DBMissingValueException {
        final var dataSource = getDataSource(qryConfig);
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(statement);

            switch (new QueryModeWrapper(qryConfig.getMode()).getMode()) {
                case QUERY:
                case SINGLE_QUERY_SCRIPT:
                    final ResultSet rs = stmt.executeQuery();
                    final var metaData = rs.getMetaData();
                    long rsCount = 0;
                    try {
                        var firstLoad = true;
                        final var listeners = qryConfig.getListeners();

                        while (rs.next()) {
                            rsCount++;
                            StringBuilder sbHdr = new StringBuilder();
                            StringBuilder sbRec = new StringBuilder();

                            for (int idx = 1; idx <= metaData.getColumnCount(); idx++) {
                                sbHdr.append(sbHdr.length()>0?",":"").append(metaData.getColumnName(idx));
                                sbRec.append(sbRec.length()>0?",":"").append(rs.getString(idx));
                            }

                            if (firstLoad) {
                                final var rawHdr = sbHdr.toString();
                                LOG.info(String.format("[%s] [HDR]: %s", qryConfig.getDescription(), rawHdr));

                                if (listeners.getOnHeader()!=null) {
                                    runCmd(qryConfig, listeners.getOnHeader(), qryConfig.getDescription(), statement, rawHdr);
                                }
                            }
                            final var rawData = sbRec.toString();
                            LOG.info(String.format("[%s] [REC]: %s", qryConfig.getDescription(), rawData));

                            if (listeners.getOnData()!=null) {
                                runCmd(qryConfig, listeners.getOnData(), qryConfig.getDescription(), statement, rawData);
                            }
                            firstLoad = false;
                        }
                    }
                    finally {
                        LOG.info(String.format("[%s] %s rows", qryConfig.getDescription(), rsCount));
                        try {rs.close();} catch (Exception e) {}
                    }
                    break;
                default:
                    stmt.execute();
                    try {LOG.info(String.format("[%s] %s rows affected", qryConfig.getDescription(), stmt.getUpdateCount()));}
                    catch(Exception e) {}
                    break;
            }
        } catch (SQLException | QueryModeException e) {
            throw new RuntimeException(e);
        } finally {
            try {stmt.close();} catch (Exception e) {}
            try {conn.close();} catch (Exception e) {}
        }
    }
}
