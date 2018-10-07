package xyz.ronella.tools.sql.servant.db

/**
 * The query modes that are recognized by the servant.
 */
enum QueryMode {

    /**
     * The appropriate mode to use for the sql statement that returns nothing
     * (e.g. insert or update). However, if don't care about the output of the
     * select statement, this one is also applicable.
     */
    STATEMENT,

    /**
     * This appropriate mode to use of the sql statement that returns some values
     * to the log file.
     */
    QUERY
}