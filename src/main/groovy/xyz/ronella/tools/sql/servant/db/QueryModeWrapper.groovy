package xyz.ronella.tools.sql.servant.db

/**
 * The class responsible for converting the configured mode to an enumeration
 * equivalent.
 *
 * @author Ron Webb
 * @since 2018-10-07
 */
class QueryModeWrapper {
    private String mode

    /**
     * Creates an instance of QueryModeWrapper.
     *
     * @param mode The string representation of the mode.
     */
    QueryModeWrapper(String mode) {
        this.mode = mode
    }

    /**
     * Creates the instance of QueryMode enumeration.
     *
     * @return An instance of QueryMode enumeration.
     */
    QueryMode getMode() {
        switch (this.mode) {
            case 'stmt':
                QueryMode.STATEMENT
                break
            default:
                QueryMode.QUERY
        }
    }
}
