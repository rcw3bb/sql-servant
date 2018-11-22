package xyz.ronella.tools.sql.servant.db

import org.apache.commons.dbcp2.BasicDataSource
import org.apache.log4j.Logger
import xyz.ronella.tools.sql.servant.listener.ListenerInvoker
import xyz.ronella.tools.sql.servant.conf.DBPoolConfig
import xyz.ronella.tools.sql.servant.conf.QueriesConfig

import javax.sql.DataSource
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * The class that is responsible to the actual query processing.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
class DBManager {
    public final static def LOG = Logger.getLogger(DBManager.class.name)

    private static final Map<String, DataSource> DATA_SOURCES = new HashMap<>()
    private static final Lock LOCK = new ReentrantLock()
    private static DBManager dbManager
    private DBPoolConfig dbPoolConfig

    private DBManager(DBPoolConfig dbPoolConfig) {
        this.dbPoolConfig = dbPoolConfig
    }

    /**
     * Returns the one and only instance of DBManager.
     *
     * @param dbPoolConfig An instance of DBPoolConfig.
     * @return An instance of DBManager.
     */
    static DBManager getInstance(DBPoolConfig dbPoolConfig) {
        if (!dbManager) {
            try {
                LOCK.lock()
                if (!dbManager) {
                    dbManager = new DBManager(dbPoolConfig)
                }
            }
            finally {
                LOCK.unlock()
            }
        }
        return dbManager
    }

    private static void validate(QueriesConfig qryConfig) {
        if (!qryConfig.jdbcDriver) {
            throw new DBMissingValueException("jdbcDriver")
        }

        if (!qryConfig.connectionString) {
            throw new DBMissingValueException("connectionString")
        }
    }

    /**
     * Retrieve a instance of DataSource from the pool.
     *
     * @param qryConfig An instance of QueriesConfig.
     * @return An instance of DataSource.
     */
    DataSource getDataSource(QueriesConfig qryConfig) {
        validate(qryConfig)

        def connectionString = qryConfig.connectionString

        def dataSource = DATA_SOURCES.get(connectionString)

        if (!dataSource) {
            try {
                LOCK.lock()
                dataSource = DATA_SOURCES.get(connectionString)
                if (!dataSource) {
                    Class.forName(qryConfig.jdbcDriver)

                    dataSource = new BasicDataSource()

                    dataSource.setUrl(connectionString)

                    if (!qryConfig.windowsAuthentication) {
                        dataSource.setUsername(qryConfig.username)
                        dataSource.setPassword(qryConfig.password)
                    }

                    dataSource.setMinIdle(dbPoolConfig.minIdle)
                    dataSource.setMaxIdle(dbPoolConfig.maxIdle)
                    dataSource.setMaxOpenPreparedStatements(dbPoolConfig.maxOpenPreparedStatements)

                    DATA_SOURCES.put(connectionString, dataSource)
                }
            }
            finally {
                LOCK.unlock()
            }
        }

        return dataSource
    }

    /**
     * The actual runner of the sql statement.
     *
     * @param qryConfig An instance of QueriesConfig.
     * @param statement The actual sql statement to execute.
     */
    def runStatement(QueriesConfig qryConfig, String statement) {
        def dataSource = getDataSource(qryConfig)
        Connection conn
        PreparedStatement stmt
        try {
            conn = dataSource.getConnection()
            stmt = conn.prepareStatement(statement)

            switch (new QueryModeWrapper(qryConfig.mode).mode) {
                case QueryMode.QUERY:
                case QueryMode.SINGLE_QUERY_SCRIPT:
                    ResultSet rs = stmt.executeQuery()
                    def metaData = rs.metaData
                    long rsCount = 0
                    try {
                        def firstLoad = true
                        def listeners = qryConfig.listeners
                        def runCmd = {String ___cmd, String ___description, String ___query, String ___params ->
                            new ListenerInvoker(qryConfig).invokeDataListener(___cmd, ___description,
                                    ___query, ___params)
                        }

                        while (rs.next()) {
                            rsCount++
                            StringBuilder sbHdr = new StringBuilder()
                            StringBuilder sbRec = new StringBuilder()
                            metaData.with {
                                for (int idx = 1; idx <= columnCount; idx++) {
                                    sbHdr.append(sbHdr.length()>0?',':'').append(getColumnName(idx))
                                    sbRec.append(sbRec.length()>0?',':'').append(rs.getString(idx))
                                }
                            }
                            if (firstLoad) {
                                def rawHdr = sbHdr.toString()
                                LOG.info("[${qryConfig.description}] [HDR]: ${rawHdr}")

                                if (listeners.onHeader) {
                                    runCmd(listeners.onHeader, qryConfig.description, statement, rawHdr)
                                }
                            }
                            def rawData = sbRec.toString()
                            LOG.info("[${qryConfig.description}] [REC]: ${rawData}")

                            if (listeners.onData) {
                                runCmd(listeners.onData, qryConfig.description, statement, rawData)
                            }
                            firstLoad = false
                        }
                    }
                    finally {
                        LOG.info("[${qryConfig.description}] ${rsCount} rows")
                        try {rs.close()} catch (Exception e) {}
                    }
                    break
                default:
                    stmt.execute()
                    try {LOG.info("[${qryConfig.description}] ${stmt.getUpdateCount()} rows affected")}
                    catch(Exception e) {}
                    break
            }
        }
        finally {
            try {stmt.close()} catch (Exception e) {}
            try {conn.close()} catch (Exception e) {}
        }
    }
}
