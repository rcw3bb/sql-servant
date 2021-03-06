package xyz.ronella.tools.sql.servant

import org.apache.log4j.Logger
import xyz.ronella.tools.sql.servant.async.ParallelEngine
import xyz.ronella.tools.sql.servant.conf.JsonConfig
import xyz.ronella.tools.sql.servant.conf.ParamConfig
import xyz.ronella.tools.sql.servant.impl.UnexpectedParameterException
import xyz.ronella.tools.sql.servant.impl.UnresolvedParametersException

import java.util.concurrent.Future
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * The main class to accepts the configuration to process.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
class QueryServant {

    public final static def LOG = Logger.getLogger(QueryServant.class.name)
    private final static Lock LOCK = new ReentrantLock()

    private static int usageLevel

    private Config config

    /**
     * Creates an instance of QueryServant.
     *
     * @param config An instance of configuration to process.
     */
    QueryServant(Config config) {
        this.config = config
    }

    /**
     * Increments the depth of the QueriesConfig since it can be nested.
     * The usageLevel is being used for waiting all the threads to complete
     * before shutting down the ParallelEngine.
     */
    static void usageLevelUp() {
        try {
            LOCK.lock()
            usageLevel++
        }
        finally {
            LOCK.unlock()
        }
    }

    /**
     * Decrements the depth of the QueriesConfig since it can be nested.
     * The usageLevel is being used for waiting all the threads to complete
     * before shutting down the ParallelEngine.
     */
    static void usageLevelDown() {
        try {
            LOCK.lock()
            usageLevel--
        }
        finally {
            LOCK.unlock()
        }
    }

    private static def checkParams(CliArgs args, JsonConfig configJson) {
        args.params.each {___cliParam ->
            ParamConfig configParam = configJson.params.find {it.name == ___cliParam.key}
            if (!configParam) {
                throw new UnexpectedParameterException(___cliParam.key)
            }
            configParam.value = ___cliParam.value
        }

        def nullParams = configJson.params.findAll {!it.value}
        if (nullParams) {
            throw new UnresolvedParametersException(nullParams.inject(new StringBuilder(), { ___result, ___item ->
                ___result.append(___result.length()>0?', ':'').append(___item.name)
                if (___item.description) {
                    ___result.append('[').append(___item.description).append(']')
                }
                ___result
            }).toString())
        }
    }

    /**
     * The actual method the is doing the configuration processing.
     *
     * @param args An instance of CliArgs.
     */
    def perform(CliArgs args) {
        LOG.info "User: ${System.getProperty("user.name")?:'Unknown'}"
        if (args.environment) {
            LOG.info "Environment: ${args.environment}"
        }
        LOG.info "Configuration: ${config.configFilename}"

        def configJson = config.configAsJson

        try {
            if (configJson) {
                checkParams(args, configJson)

                if (args.params) {
                    def strParams = configJson.params.inject(new StringBuilder(), {___result, ___item ->
                        ___result.append(___result.length()>0?', ':'').append(___item.name).
                                append('=').append(___item.value)
                        ___result
                    })
                    LOG.info "Parameters: ${strParams}"
                }

                ParallelEngine.instance.with {
                    List<Future<IStatus>> futures = new ArrayList<>()
                    try {
                        configJson.queries.each { qryConfig ->
                            if (LOG.isTraceEnabled()) {
                                LOG.trace("Description: ${qryConfig.description}")
                                LOG.trace("Connection String: ${qryConfig.connectionString}")
                            }
                            new OperationStrategy(args).runOperation(futures, config, qryConfig)
                        }
                        Iterator<Future<IStatus>> iterator=futures.iterator()
                        while (iterator.hasNext()) {
                            iterator.next().get()
                        }
                    }
                    finally {
                        while(usageLevel!=0) {
                            Thread.sleep(500)
                        }
                        if (isStarted()) {
                            stop()
                        }
                    }
                }
            }
            else {
                LOG.info "Nothing to process."
            }
        }
        catch (UnexpectedParameterException upe) {
            LOG.error("Unexpected parameter: ${upe.message}")
        }
        catch (UnresolvedParametersException upe) {
            LOG.error("Missing parameter(s): ${upe.message}")
        }

        LOG.info 'Done'
    }

}
