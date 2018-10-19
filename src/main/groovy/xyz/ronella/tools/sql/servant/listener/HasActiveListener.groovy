package xyz.ronella.tools.sql.servant.listener

import xyz.ronella.tools.sql.servant.IValidator
import xyz.ronella.tools.sql.servant.conf.ListenersConfig

class HasActiveListener implements IValidator<ListenersConfig> {

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
