package xyz.ronella.tools.sql.servant.db

/**
 * The query modes that are recognized by the servant.
 */
enum QueryMode {

    /**
     * The appropriate mode to use for the sql statements returns nothing
     * (e.g. insert or update). However, if don't care about the output of the
     * select statement, this one is also applicable.
     */
    STATEMENT,

    /**
     * The appropriate mode to use if the sql statements return some values.
     */
    QUERY,

    /**
     * The appropriate mode to use of the sql statements is contained in a script.
     */
    SCRIPT,

    /**
     * The appropriate mode to use of the sql statements is contained in a script
     * where the only entry is a query (i.e. select statement).
     *
     * @since 1.2.0
     */
    SINGLE_QUERY_SCRIPT
}