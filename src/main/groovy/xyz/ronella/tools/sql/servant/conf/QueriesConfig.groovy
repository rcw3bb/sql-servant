package xyz.ronella.tools.sql.servant.conf

/**
 * Enhances the DefaultConfig definition to have Queries specific configuration.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
class QueriesConfig extends DefaultConfig {

    /**
     * Holds the description of the group of queries.
     */
    String description

    /**
     * Holds the series of queries to be executed.
     */
    String[] queries

    /**
     * Holds any child query to be executed after completing the execution of the
     * specified queries.
     */
    QueriesConfig next
}
