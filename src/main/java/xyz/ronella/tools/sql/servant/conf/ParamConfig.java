package xyz.ronella.tools.sql.servant.conf;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Holds the configuration for parameters.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
@NoArgsConstructor
@Data
public class ParamConfig {

    /**
     * Holds the name of the parameter.
     */
    private String name;

    /**
     * Hols the description of the parameter.
     */
    private String description;

    /**
     * Holds the value of the parameter.
     */
    private String value;

    /**
     * Holds the external filename of the param configuration.
     *
     * @since 1.3.0
     */
    private String filename;
}
