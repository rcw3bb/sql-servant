package xyz.ronella.tools.sql.servant.conf;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The class that holds the configuration about the listeners.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
@NoArgsConstructor
@Data
public class ListenersConfig {

    /**
     * Holds the characters to be filtered to underscore.
     */
    private String filter;

    /**
     * Holds the invoker of the listener script generated.
     */
    private String command;

    /**
     * Holds the script to call if the particular query has started its processing.
     */
    private String onStart;

    /**
     * Holds the script to call if the header of the query output has arrived.
     */
    private String onHeader;

    /**
     * Holds the script to call if the data of the query output has arrived.
     */
    private String onData;

    /**
     * Holds the script to call of the particular query has completed processing.
     */
    private String onComplete;

    /**
     * Holds the external filename of the listener configuration.
     *
     * @since 1.3.0
     */
    private String filename;

}
