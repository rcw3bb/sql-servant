package xyz.ronella.tools.sql.servant.async;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ParallelEngine is the one responsible to doing parallel tasks.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
final public class ParallelEngine {

    private final static ReentrantLock LOCK = new ReentrantLock();
    public final static Logger LOG = LogManager.getLogger(ParallelEngine.class);

    private static ParallelEngine INSTANCE;
    private ExecutorService executor;
    private boolean isStarted;
    private int maxThreads;
    private int minThreads;

    private ParallelEngine() {
        maxThreads = Runtime.getRuntime().availableProcessors();
        minThreads = maxThreads;

        if (maxThreads>1) {
            minThreads = maxThreads - 1; //Only use all threads if needed.
        }
    }

    /**
     * Returns the singleton instance of ParallelEngine.
     *
     * @return An instance of ParallelEngine.
     */
    public static ParallelEngine getInstance() {
        if (INSTANCE==null) {
            try {
                LOCK.lock();
                if (INSTANCE==null) {
                    INSTANCE = new ParallelEngine();
                }
            }
            finally {
                LOCK.unlock();
            }
        }
        return INSTANCE;
    }

    /**
     * Starts the ParallelEngine if not yet started.
     */
    public void start() {
        if (isStarted) {
            LOG.warn("Engine was already started");
        }
        else {
            LOG.info("Starting engine");
            executor = new ThreadPoolExecutor(minThreads, maxThreads,
                    500, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(maxThreads * 2), new BasicThreadFactory.Builder()
                    .daemon(true)
                    .namingPattern("sql-servant-%d").build());
            isStarted = true;
        }
    }

    /**
     * Stops the ParallelEngine if it was started.
     */
    public void stop() throws InterruptedException {
        if (isStarted) {
            LOG.info("Stopping engine");
            executor.awaitTermination(500, TimeUnit.MILLISECONDS);
            LOG.info("Engine completely stopped");
            isStarted = false;
        }
        else {
            LOG.warn("Engine was already stopped");
        }
    }

    /**
     * Checks if the ParallelEngine was started.
     *
     * @return true if started.
     */
    public boolean isStarted() {
        return this.isStarted;
    }

    /**
     * The one responsible for sending tasks to the ParallelEngine.
     *
     * @param task An instance of typed Callable.
     * @return An instance of typed Future.
     */
    public <TYPE_OBJECT> Future<TYPE_OBJECT> process(Callable<TYPE_OBJECT> task) {
        return executor.submit(task);
    }
}