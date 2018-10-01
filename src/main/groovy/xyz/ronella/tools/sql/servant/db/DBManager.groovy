package xyz.ronella.tools.sql.servant.db

import org.apache.commons.dbcp2.BasicDataSource
import org.apache.log4j.Logger
import xyz.ronella.tools.sql.servant.conf.DBPoolConfig
import xyz.ronella.tools.sql.servant.conf.QueriesConfig

import javax.sql.DataSource
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class DBManager {
    public final static def LOG = Logger.getLogger(DBManager.class.name)

    private static final Map<String, DataSource> DATA_SOURCES = new HashMap<>()
    private static final Lock LOCK = new ReentrantLock()
    private static DBManager dbManager
    private DBPoolConfig dbPoolConfig

    private DBManager(DBPoolConfig dbPoolConfig) {
        this.dbPoolConfig = dbPoolConfig
    }

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

    DataSource getDataSource(QueriesConfig qryConfig) {
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
                    dataSource.setUsername(qryConfig.username)
                    dataSource.setPassword(qryConfig.password)
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

    def runStatement(QueriesConfig qryConfig, String statement) {
        def dataSource = getDataSource(qryConfig)
        Connection conn
        PreparedStatement stmt
        try {
            conn = dataSource.getConnection()
            stmt = conn.prepareStatement(statement)

            switch (new QueryModeWrapper(qryConfig.mode).mode) {
                case QueryMode.STATEMENT:
                    stmt.execute()
                    break
                case QueryMode.QUERY:
                default:
                    ResultSet rs = stmt.executeQuery()
                    def metaData = rs.metaData
                    try {
                        def firstLoad = true
                        while (rs.next()) {
                            StringBuilder sbHdr = new StringBuilder()
                            StringBuilder sbRec = new StringBuilder()
                            metaData.with {
                                for (int idx = 1; idx <= columnCount; idx++) {
                                    sbHdr.append(sbHdr.length()>0?',':'').append(getColumnName(idx))
                                    sbRec.append(sbRec.length()>0?',':'').append(rs.getString(idx))
                                }
                            }
                            if (firstLoad) {
                                LOG.info("[${qryConfig.description}] [HDR]: ${sbHdr.toString()}")
                            }
                            LOG.info("[${qryConfig.description}] [REC]: ${sbRec.toString()}")
                            firstLoad = false
                        }
                    }
                    finally {
                        try {rs.close()} catch (Exception e) {LOG.warn(e.message)}
                    }
            }
        }
        finally {
            try {stmt.close()} catch (Exception e) {LOG.warn(e.message)}
            try {conn.close()} catch (Exception e) {LOG.warn(e.message)}
        }
    }
}
