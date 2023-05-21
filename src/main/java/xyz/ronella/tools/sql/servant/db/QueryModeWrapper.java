package xyz.ronella.tools.sql.servant.db;

/**
 * The class responsible for converting the configured mode to an enumeration
 * equivalent.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
public class QueryModeWrapper {
    private final String mode;

    /**
     * Creates an instance of QueryModeWrapper.
     *
     * @param mode The string representation of the mode.
     */
    public QueryModeWrapper(String mode) {
        this.mode = mode;
    }

    /**
     * Creates the instance of QueryMode enumeration.
     *
     * @return An instance of QueryMode enumeration.
     */
    public QueryMode getMode() throws QueryModeException {

        switch (this.mode) {
            case "stmt" : return QueryMode.STATEMENT;
            case "query" : return QueryMode.QUERY;
            case "script" : return QueryMode.SCRIPT;
            case "sqs" : return QueryMode.SINGLE_QUERY_SCRIPT;
            default : throw new QueryModeException(String.format("Mode=%s", this.mode));
        }
    }
}
