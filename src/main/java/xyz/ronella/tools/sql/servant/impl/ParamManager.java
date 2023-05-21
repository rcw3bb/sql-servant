package xyz.ronella.tools.sql.servant.impl;

import xyz.ronella.tools.sql.servant.IValidator;
import xyz.ronella.tools.sql.servant.conf.ParamConfig;
import xyz.ronella.trivial.decorator.Mutable;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The class that manages the parameters.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
public class ParamManager implements IValidator<String> {

    private final static String DEFAULT_DELIMITER = "%%%";

    /**
     * Generates the parameter token.
     *
     * @param paramName The name of the parameter.
     * @return The tokenized parameter.
     */
    public static String getParamToken(final String paramName) {
        return String.format("%s%s%s", DEFAULT_DELIMITER, paramName, DEFAULT_DELIMITER);
    }

    /**
     * Applies the parameters to the query.
     *
     * @param params The parameters to be applied to the query.
     * @param query The actual query to apply the parameters.
     * @return The query with interpolated parameter tokens.
     */
    public static String applyParams(final Map<String, String> params, final String query) {
        final var newQuery = new Mutable<>(query);
        if (params!=null) {
            params.forEach((___key, ___value) -> {
                final var paramName = String.format("%s", getParamToken(___key));
                newQuery.set(newQuery.get().replaceAll(paramName, ___value));
            });
        }
        return newQuery.get();
    }

    /**
     * Applies the parameters to the query.
     *
     * @param params The parameters to be applied to the query.
     * @param query The actual query to apply the parameters.
     * @return The query with interpolated parameter tokens.
     */
    public static String applyParams(final ParamConfig[] params, final String query) {
        final var newQuery = new Mutable<>(query);
        if (params!=null) {
            newQuery.set(
                    applyParams(Arrays.stream(params)
                            .collect(Collectors.toMap(ParamConfig::getName, ParamConfig::getValue)), query));
        }
        return newQuery.get();
    }

    /**
     * The implementation for testing if the query was tidied successfully.
     *
     * @param query The query to be tested.
     * @return Returns true if everything is good.
     */
    @Override
    public boolean test(final String query) {
        return query == null || !query.matches(String.format("%s\\w*?%s", DEFAULT_DELIMITER, DEFAULT_DELIMITER));
    }
}
