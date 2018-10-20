package xyz.ronella.tools.sql.servant.impl

import xyz.ronella.tools.sql.servant.IValidator
import xyz.ronella.tools.sql.servant.conf.ParamConfig

/**
 * The class that manages the parameters.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
class ParamManager implements IValidator<String> {

    private final static String DEFAULT_DELIMITER = '%%%'

    /**
     * Generates the parameter token.
     *
     * @param paramName The name of the parameter.
     * @return The tokenized parameter.
     */
    static String getParamToken(String paramName) {
        "${DEFAULT_DELIMITER}${paramName}${DEFAULT_DELIMITER}"
    }

    /**
     * Applies the parameters to the query.
     *
     * @param params The parameters to be applied to the query.
     * @param query The actual query to apply the parameters.
     * @return The query with interpolated parameter tokens.
     */
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

    /**
     * Applies the parameters to the query.
     *
     * @param params The parameters to be applied to the query.
     * @param query The actual query to apply the parameters.
     * @return The query with interpolated parameter tokens.
     */
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

    /**
     * The implementation for testing if the query was tidied successfully.
     *
     * @param query The query to be tested.
     * @return Returns true if everything is good.
     */
    @Override
    boolean test(String query) {
        if (query && query.find("${DEFAULT_DELIMITER}\\w*?${DEFAULT_DELIMITER}")) {
            return false
        }
        return true
    }
}
