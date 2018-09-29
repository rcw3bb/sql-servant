package xyz.ronella.tools.sql.servant.db

class QueryModeWrapper {
    private String mode

    QueryModeWrapper(String mode) {
        this.mode = mode
    }

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
