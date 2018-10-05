package xyz.ronella.tools.sql.servant.async

import org.apache.commons.lang3.concurrent.BasicThreadFactory
import org.apache.log4j.Logger

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

class ParallelEngine {

    private final static ReentrantLock LOCK = new ReentrantLock()
    public final static def LOG = Logger.getLogger(ParallelEngine.class.name)

    private static ParallelEngine INSTANCE
    private ExecutorService executor
    private boolean isStarted
    private int maxThreads
    private int minThreads

    private ParallelEngine() {
        maxThreads = Runtime.getRuntime().availableProcessors()
        minThreads = maxThreads

        if (maxThreads>1) {
            minThreads = maxThreads - 1 //Only use all threads if needed.
        }
    }

    static ParallelEngine getInstance() {
        if (INSTANCE==null) {
            try {
                LOCK.lock()
                if (INSTANCE==null) {
                    INSTANCE = new ParallelEngine()
                }
            }
            finally {
                LOCK.unlock()
            }
        }
        INSTANCE
    }

    def start() {
        if (isStarted) {
            LOG.warn "Engine was already started"
        }
        else {
            LOG.info "Starting engine"
            executor = new ThreadPoolExecutor(minThreads, maxThreads,
                    500, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(maxThreads * 2), new BasicThreadFactory.Builder()
                    .daemon(true)
                    .namingPattern("sql-servant-%d").build())
            isStarted = true
        }
    }

    def stop() {
        if (isStarted) {
            LOG.info "Stopping engine"
            executor.awaitTermination(500, TimeUnit.MILLISECONDS)
            LOG.info "Engine completely stopped"
            isStarted = false
        }
        else {
            LOG.warn "Engine was already stopped"
        }
    }

    boolean isStarted() {
        return this.isStarted
    }

    public <TYPE_OBJECT> Future<TYPE_OBJECT> process(Callable<TYPE_OBJECT> task) {
        executor.submit task
    }
}