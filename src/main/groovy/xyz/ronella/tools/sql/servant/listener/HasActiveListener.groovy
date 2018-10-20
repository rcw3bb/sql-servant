package xyz.ronella.tools.sql.servant.listener

import xyz.ronella.tools.sql.servant.IValidator
import xyz.ronella.tools.sql.servant.conf.ListenersConfig

/**
 * The utility class that validates if an instance of the ListenerConfig as active listener.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
class HasActiveListener implements IValidator<ListenersConfig> {

    /**
     * Test if the active listener instance has an active listener.
     *
     * @param listener An instance of ListenersConfig.
     * @return Returns true if there as at least one active listener.
     */
    @Override
    boolean test(ListenersConfig listener) {
        boolean[] listeners = []
        listener.with {
            listeners = [onStart ? onStart.length() > 0 : false,
                         onHeader ? onHeader.length() > 0 : false,
                         onData ? onData.length() > 0 : false,
                         onComplete ? onComplete.length() > 0 : false]
        }
        listeners.find {it}
    }

}
