package xyz.ronella.tools.sql.servant.listener;

import xyz.ronella.tools.sql.servant.IValidator;
import xyz.ronella.tools.sql.servant.conf.ListenersConfig;

import java.util.List;

/**
 * The utility class that validates if an instance of the ListenerConfig as active listener.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
public class HasActiveListener implements IValidator<ListenersConfig> {

    /**
     * Test if the active listener instance has an active listener.
     *
     * @param listener An instance of ListenersConfig.
     * @return Returns true if there is at least one active listener.
     */
    @Override
    public boolean test(ListenersConfig listener) {

        final var listeners = List.of(
                listener.getOnStart() != null && listener.getOnStart().length() > 0,
                listener.getOnHeader() != null && listener.getOnHeader().length() > 0,
                listener.getOnData() != null && listener.getOnData().length() > 0,
                listener.getOnComplete() != null && listener.getOnComplete().length() > 0
        );

        return listeners.stream().filter(Boolean::booleanValue).findFirst().orElse(false);
    }

}
