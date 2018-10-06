package xyz.ronella.tools.sql.servant.db

class DBMissingValueException extends DBManagerException {
    DBMissingValueException() {
        super()
    }

    DBMissingValueException(String message) {
        super(message)
    }
}
