package xyz.ronella.tools.sql.servant.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The class that tracks what has been processed.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
public class ProcessedHolder {

    private final static List<String> PROCESSED = new ArrayList<>();
    private final static Lock LOCK = new ReentrantLock();

    /**
     * Checks or adds if the particular query is being processed.
     *
     * @param name Must holds the description of the query.
     * @return Returns true of the query is being processed.
     */
    public boolean isProcessed(String name) {
        if (!PROCESSED.contains(name)) {
            try {
                LOCK.lock();
                if (!PROCESSED.contains(name)) {
                    PROCESSED.add(name);
                    return false;
                }
            }
            finally {
                LOCK.unlock();
            }
        }
        return true;
    }

    /**
     * Removes the particular query from its registry once done.
     * @param name Must holds the description of the query.
     */
    public void done(String name) {
        if (PROCESSED.contains(name)) {
            try {
                LOCK.lock();
                if (PROCESSED.contains(name)) {
                    PROCESSED.remove(name);
                }
            }
            finally {
                LOCK.unlock();
            }
        }
    }
}
