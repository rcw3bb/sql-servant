package xyz.ronella.tools.sql.servant.parser.impl

import xyz.ronella.tools.sql.servant.parser.QueryParserException

class ScriptQueryParserException extends QueryParserException {

    ScriptQueryParserException(String message) {
        super(message)
    }

}
