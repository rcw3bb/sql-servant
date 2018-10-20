package xyz.ronella.tools.sql.servant.async

import groovy.transform.Synchronized

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * The class that tracks what has been processed.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
class ProcessedHolder {

    private final static List<String> PROCESSED = []
    private final static Lock LOCK = new ReentrantLock()

    /**
     * Checks or adds if the particular query is being processed.
     *
     * @param name Must holds the description of the query.
     * @return Returns true of the query is being processed.
     */
    @Synchronized
    boolean isProcessed(String name) {
        if (!PROCESSED.contains(name)) {
            try {
                LOCK.lock()
                if (!PROCESSED.contains(name)) {
                    PROCESSED.add(name)
                    return false
                }
            }
            finally {
                LOCK.unlock()
            }
        }
        return true
    }

    /**
     * Removes the particular query from its registry once done.
     * @param name Must holds the description of the query.
     */
    @Synchronized
    void done(String name) {
        if (PROCESSED.contains(name)) {
            try {
                LOCK.lock()
                if (PROCESSED.contains(name)) {
                    PROCESSED.remove(name)
                }
            }
            finally {
                LOCK.unlock()
            }
        }
    }
}
