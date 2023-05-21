package xyz.ronella.tools.sql.servant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.ronella.tools.sql.servant.async.ParallelEngine;
import xyz.ronella.tools.sql.servant.conf.JsonConfig;
import xyz.ronella.tools.sql.servant.impl.UnexpectedParameterException;
import xyz.ronella.tools.sql.servant.impl.UnresolvedParametersException;
import xyz.ronella.trivial.decorator.Mutable;
import xyz.ronella.trivial.decorator.StringBuilderAppender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * The main class to accepts the configuration to process.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
public class QueryServant {

    public final static Logger LOG = LogManager.getLogger(QueryServant.class);
    private final static Lock LOCK = new ReentrantLock();
    private static boolean hasExecutionExceptionThrown = false;
    private static boolean hasTaskExceptionThrown = false;

    private static int usageLevel;

    private Config config;

    public static boolean hasTaskException() {
        try {
            LOCK.lock();
            return hasTaskExceptionThrown;
        }
        finally {
            LOCK.unlock();
        }
    }

    public static void setTaskException(boolean taskExceptionThrown) {
        try {
            LOCK.lock();
            hasTaskExceptionThrown = taskExceptionThrown;
        }
        finally {
            LOCK.unlock();
        }
    }

    public static boolean hasExecutionException() {
        return hasExecutionExceptionThrown;
    }

    public static void setExecutionException(boolean executionExceptionThrown) {
        try {
            LOCK.lock();
            hasExecutionExceptionThrown = executionExceptionThrown;
        }
        finally {
            LOCK.unlock();
        }
    }

    /**
     * Creates an instance of QueryServant.
     *
     * @param config An instance of configuration to process.
     */
    public QueryServant(final Config config) {
        this.config = config;
    }

    /**
     * Increments the depth of the QueriesConfig since it can be nested.
     * The usageLevel is being used for waiting all the threads to complete
     * before shutting down the ParallelEngine.
     */
    public static void usageLevelUp() {
        try {
            LOCK.lock();
            usageLevel++;
        }
        finally {
            LOCK.unlock();
        }
    }

    /**
     * Decrements the depth of the QueriesConfig since it can be nested.
     * The usageLevel is being used for waiting all the threads to complete
     * before shutting down the ParallelEngine.
     */
    public static void usageLevelDown() {
        try {
            LOCK.lock();
            usageLevel--;
            LOG.debug("LockLevel : ${usageLevel}");
        }
        finally {
            LOCK.unlock();
        }
    }

    private static void checkParams(final CliArgs args, final JsonConfig configJson)
            throws UnexpectedParameterException, UnresolvedParametersException {

        final var hasUnexpectedParameter = new Mutable<>(false);
        final var keys = new Mutable<>("");

        args.getParams().forEach((___key, ___value) -> {
            final var configParam = Arrays.stream(configJson.getParams())
                    .filter(___param -> ___param.getName().equals(___key))
                    .findFirst();
            if (configParam.isEmpty()) {
                hasUnexpectedParameter.set(true);
                keys.set(___key);
                throw new RuntimeException(new UnexpectedParameterException(___key));
            }
            configParam.get().setValue(___value);
        });

        if (hasUnexpectedParameter.get()) {
            throw new UnexpectedParameterException(keys.get());
        }

        final var nullParams = Arrays.stream(configJson.getParams()).filter(___param -> ___param.getValue()==null).collect(Collectors.toList());
        if (!nullParams.isEmpty()) {

            final var sbResult = new StringBuilderAppender(___sb -> ___sb.append(___sb.length()>0 ? ", " : ""));
            nullParams.forEach(___item -> {
                sbResult.append(___item.getName());
                if (___item.getDescription()!=null) {
                    sbResult.append("[", ___item.getDescription(), "]");
                }
            });

            throw new UnresolvedParametersException(sbResult.toString());
        }
    }

    public static boolean hasException(final CliArgs args) throws ExecutionException, TaskException {
        if (!args.isNoop()) {
            final var hasTaskError = !args.isIgnoreTaskException() && hasTaskException();
            final var hasExecError = !args.isIgnoreExecutionException() && hasExecutionException();

            if (!args.isTestMode()) {
                return hasTaskError || hasExecError;
            } else if (hasExecError) {
                throw new ExecutionException();
            } else if (hasTaskError) {
                throw new TaskException();
            }
        }

        return false;
    }

    private void waitAllTasks(final Iterator<Future<IStatus>> iterator, final CliArgs args) throws TaskException {
        while (iterator.hasNext()) {
            try {
                iterator.next().get();
            }
            catch(Exception exception) {
                LOG.error(exception);
                if (!args.isIgnoreTaskException()) {
                    setTaskException(true);
                    if (args.isTestMode()) {
                        throw new TaskException(exception);
                    }
                }
            }
        }
    }

    private void logParams(final CliArgs args, final JsonConfig configJson) {
        if (!args.getParams().isEmpty()) {
            final var strParams = new StringBuilderAppender(___sb -> ___sb.append(___sb.length()>0 ? ", ": ""));

            Arrays.stream(configJson.getParams()).forEach(___item ->
                    strParams.append(___item.getName(), "=", ___item.getValue()));

            LOG.info(String.format("Parameters: %s", strParams));
        }
    }

    private void processQueries(final CliArgs args, final JsonConfig configJson, final List<Future<IStatus>> futures) throws TaskException, ExecutionException {
        for (final var qryConfig : configJson.getQueries()) {
            if (hasException(args)) {
                break;
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace("Description: ${qryConfig.description}");
                LOG.trace("Connection String: ${qryConfig.connectionString}");
            }
            new OperationStrategy(args).runOperation(futures, config, qryConfig);
        }
    }

    private void invokeWithParallelEngine(final CliArgs args, final JsonConfig configJson) throws ExecutionException, TaskException {
        final var parallel = ParallelEngine.getInstance();
        final List<Future<IStatus>> futures = new ArrayList<>();
        try {
            processQueries(args, configJson, futures);
            Iterator<Future<IStatus>> iterator=futures.iterator();
            waitAllTasks(iterator, args);

            if (!args.isIgnoreExecutionException() && args.isIgnoreTaskException() && hasTaskException()) {
                throw new ExecutionException();
            }
        }
        finally {
            while (usageLevel != 0) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    throw new RuntimeException(ie);
                }
            }
            if (parallel.isStarted()) {
                try {
                    parallel.stop();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void intro(final CliArgs args) {
        LOG.info(String.format("User: %s", System.getProperty("user.name")!=null?System.getProperty("user.name"):"Unknown"));
        final var env = args.getEnvironment();
        if (env!=null) {
            LOG.info(String.format("Environment: %s", args.getEnvironment()));
        }
        LOG.info(String.format("Configuration: %s", config.getConfigFilename()));
    }

    static private void exitLogic(CliArgs args) {
        if (!args.isTestMode()) {
            if (!args.isIgnoreExecutionException() && hasExecutionException()) {
                System.exit(ExitCode.EXECUTION_EXCEPTION);
            }
            if (!args.isIgnoreTaskException() && hasTaskException()) {
                System.exit(ExitCode.TASK_EXCEPTION);
            }
        }
    }

    /**
     * The actual method the is doing the configuration processing.
     *
     * @param args An instance of CliArgs.
     */
    public void perform(final CliArgs args) throws ExecutionException, TaskException {
        intro(args);
        final var configJson = config.getConfigAsJson();

        try {
            if (configJson!=null) {
                checkParams(args, configJson);
                logParams(args, configJson);
                invokeWithParallelEngine(args, configJson);
            }
            else {
                LOG.info("Nothing to process.");
            }
        }
        catch (UnexpectedParameterException upe) {
            LOG.error(String.format("Unexpected parameter: %s", upe.getMessage()));
        }
        catch (UnresolvedParametersException upe) {
            LOG.error(String.format("Missing parameter(s): %s", upe.getMessage()));
        }
        catch(ExecutionException ee) {
            LOG.error(ee);
            setExecutionException(true);
            if (args.isTestMode()) {
                throw ee;
            }
        }
        finally {
            LOG.info("Done");
        }
        exitLogic(args);
    }
}
