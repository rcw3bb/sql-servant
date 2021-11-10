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
    private static def hasExecutionExceptionThrown = false
    private static def hasTaskExceptionThrown = false

    private static int usageLevel

    private Config config

    static boolean getHasTaskException() {
        try {
            LOCK.lock()
            return hasTaskExceptionThrown
        }
        finally {
            LOCK.unlock()
        }
    }

    static void setHasTaskException(boolean taskExceptionThrown) {
        try {
            LOCK.lock()
            hasTaskExceptionThrown = taskExceptionThrown
        }
        finally {
            LOCK.unlock()
        }
    }

    static boolean getHasExecutionException() {
        return hasExecutionExceptionThrown
    }

    static void setHasExecutionException(boolean executionExceptionThrown) {
        try {
            LOCK.lock()
            hasExecutionExceptionThrown = executionExceptionThrown
        }
        finally {
            LOCK.unlock()
        }
    }

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
            LOG.debug("LockLevel : ${usageLevel}")
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

    static def hasException(CliArgs args) {
        if (!args.noop) {
            def hasTaskError = !args.ignoreTaskException && hasTaskException
            def hasExecError = !args.ignoreExecutionException && hasExecutionException

            if (!args.isTestMode) {
                return hasTaskError || hasExecError
            } else if (hasExecError) {
                throw new ExecutionException()
            } else if (hasTaskError) {
                throw new TaskException()
            }
        }

        return false
    }

    private def waitAllTasks(Iterator<Future<IStatus>> iterator, CliArgs args) {
        while (iterator.hasNext()) {
            try {
                iterator.next().get()
            }
            catch(Exception exception) {
                LOG.error(exception)
                if (!args.ignoreTaskException) {
                    hasTaskException=true
                    if (args.isTestMode) {
                        throw new TaskException(exception)
                    }
                }
            }
        }
    }

    private def logParams(CliArgs args, JsonConfig configJson) {
        if (args.params) {
            def strParams = configJson.params.inject(new StringBuilder(), {___result, ___item ->
                ___result.append(___result.length()>0?', ':'').append(___item.name).
                        append('=').append(___item.value)
                ___result
            })
            LOG.info "Parameters: ${strParams}"
        }
    }

    private def processQueries(CliArgs args, JsonConfig configJson, List<Future<IStatus>> futures) {
        for (qryConfig in configJson.queries) {
            if (hasException(args)) {
                break
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace("Description: ${qryConfig.description}")
                LOG.trace("Connection String: ${qryConfig.connectionString}")
            }
            new OperationStrategy(args).runOperation(futures, config, qryConfig)
        }
    }

    private def invokeWithParallelEngine(CliArgs args, JsonConfig configJson) {
        ParallelEngine.instance.with {
            List<Future<IStatus>> futures = new ArrayList<>()
            try {
                processQueries(args, configJson, futures)
                Iterator<Future<IStatus>> iterator=futures.iterator()
                waitAllTasks(iterator, args)

                if (!args.ignoreExecutionException && args.ignoreTaskException && hasTaskException) {
                    throw new ExecutionException()
                }
            }
            finally {
                while (usageLevel != 0) {
                    Thread.sleep(500)
                }
                if (isStarted()) {
                    stop()
                }
            }
        }
    }

    private def intro(CliArgs args) {
        LOG.info "User: ${System.getProperty("user.name")?:'Unknown'}"
        if (args.environment) {
            LOG.info "Environment: ${args.environment}"
        }
        LOG.info "Configuration: ${config.configFilename}"
    }

    static private def exitLogic(CliArgs args) {
        if (!args.isTestMode) {
            if (!args.ignoreExecutionException && hasExecutionException) {
                System.exit(ExitCode.EXECUTION_EXCEPTION)
            }
            if (!args.ignoreTaskException && hasTaskException) {
                System.exit(ExitCode.TASK_EXCEPTION)
            }
        }
    }

    /**
     * The actual method the is doing the configuration processing.
     *
     * @param args An instance of CliArgs.
     */
    def perform(CliArgs args) {
        intro(args)
        def configJson = config.configAsJson

        try {
            if (configJson) {
                checkParams(args, configJson)
                logParams(args, configJson)
                invokeWithParallelEngine(args, configJson)
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
        catch(ExecutionException ee) {
            LOG.error(ee)
            hasExecutionException = true
            if (args.isTestMode) {
                throw ee
            }
        }
        finally {
            LOG.info 'Done'
        }
        exitLogic(args)
    }
}
