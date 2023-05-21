package xyz.ronella.tools.sql.servant.conf;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class QueriesConfig extends DefaultConfig {

    /**
     * Holds the series of queries to be executed.
     */
    private String[] queries;

    /**
     * Holds any child query to be executed after completing the execution of the
     * specified queries.
     */
    private QueriesConfig next;

}
