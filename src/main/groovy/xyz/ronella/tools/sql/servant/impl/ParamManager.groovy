package xyz.ronella.tools.sql.servant.impl

import xyz.ronella.tools.sql.servant.IValidator
import xyz.ronella.tools.sql.servant.conf.ParamConfig

class ParamManager implements IValidator<String> {

    private final static String DEFAULT_DELIMITER = '%%%'

    static String getParamToken(String paramName) {
        "${DEFAULT_DELIMITER}${paramName}${DEFAULT_DELIMITER}"
    }

    static String applyParams(final Map<String, String> params, final String query) {
        String newQuery = query
        if (params) {
            params.forEach { ___key, ___value ->
                String paramName = "${getParamToken(___key)}"
                newQuery = query.replaceAll(paramName, ___value)
            }
        }
        newQuery
    }

    static String applyParams(final ParamConfig[] params, final String query) {
        String newQuery = query
        if (params) {
            newQuery = applyParams(
                    params.inject([:], {___result, ___item ->
                        ___result.put(___item.name, ___item.value)
                        ___result}) as Map<String, String>, query)
        }
        newQuery
    }

    @Override
    boolean test(String query) {
        if (query && query.find("${DEFAULT_DELIMITER}\\w*?${DEFAULT_DELIMITER}")) {
            return false
        }
        return true
    }
}
