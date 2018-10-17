package xyz.ronella.tools.sql.servant.async

import groovy.transform.Synchronized

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class ProcessedHolder {

    private final static List<String> PROCESSED = []
    private final static Lock LOCK = new ReentrantLock()

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
