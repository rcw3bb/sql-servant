package xyz.ronella.tools.sql.servant.parser

/**
 * The template of creating how the query must be tidied.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
interface IQueryParser {

    /**
     * Must hold the implementation how the query must be tidied up.
     *
     * @param query The query to clean.
     * @return The updated query.
     */
    String queryParse(String query)

}
