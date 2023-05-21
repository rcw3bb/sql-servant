package xyz.ronella.tools.sql.servant.command;

/**
 * A utility class responsible for executing a command.
 *
 * @author Ron Webb
 * @since 1.3.0
 */
final public class Invoker {
    private Invoker() {}

    /**
     * Executes an implementation of ICommandO.
     *
     * @param cmd An implementation of ICommandO
     * @return A typed output that resulted to the execution of cmd.
     */
    public static <T> T invoke(ICommandO<T> cmd) {
        return cmd!=null ? cmd.get() : null;
    }
}
