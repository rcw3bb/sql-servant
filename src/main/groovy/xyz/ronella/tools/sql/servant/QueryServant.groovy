package xyz.ronella.tools.sql.servant

import org.apache.log4j.Logger
import xyz.ronella.tools.sql.servant.async.ParallelEngine

import java.util.concurrent.Future
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class QueryServant {

    public final static def LOG = Logger.getLogger(QueryServant.class.name)
    private final static Lock LOCK = new ReentrantLock()

    private static int usageLevel

    private Config config

    QueryServant(Config config) {
        this.config = config
    }

    static void usageLevelUp() {
        try {
            LOCK.lock()
            usageLevel++
        }
        finally {
            LOCK.unlock()
        }
    }

    static void usageLevelDown() {
        try {
            LOCK.lock()
            usageLevel--
        }
        finally {
            LOCK.unlock()
        }
    }

    def perform(CliArgs args) {
        LOG.info "User: ${System.getProperty("user.name")?:'Unknown'}"
        LOG.info "Configuration: ${config.configFilename}"

        def configJson = config.configAsJson

        if (configJson) {
            ParallelEngine.instance.with {
                List<Future<IStatus>> futures = new ArrayList<>()
                try {
                    configJson.queries.each { qryConfig ->
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

        LOG.info 'Done'
    }

}
