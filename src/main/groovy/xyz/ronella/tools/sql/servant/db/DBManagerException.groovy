package xyz.ronella.tools.sql.servant.db

class DBManagerException extends Exception {
    DBManagerException(String message) {
        super(message)
    }

    DBManagerException() {
        super()
    }
}
