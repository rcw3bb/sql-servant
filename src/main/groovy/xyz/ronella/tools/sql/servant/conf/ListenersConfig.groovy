package xyz.ronella.tools.sql.servant.conf

/**
 * The class that holds the configuration about the listeners.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
class ListenersConfig {

    /**
     * Holds the characters to be filtered to underscore.
     */
    String filter

    /**
     * Holds the invoker of the listener script generated.
     */
    String command

    /**
     * Holds the script to call if the particular query has started its processing.
     */
    String onStart

    /**
     * Holds the script to call if the header of the query output has arrived.
     */
    String onHeader

    /**
     * Holds the script to call if the data of the query output has arrived.
     */
    String onData

    /**
     * Holds the script to call of the particular query has completed processing.
     */
    String onComplete

    /**
     * Holds the external filename of the listener configuration.
     *
     * @since 1.3.0
     */
    String filename
}
