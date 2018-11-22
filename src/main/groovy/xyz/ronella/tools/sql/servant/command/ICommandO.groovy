package xyz.ronella.tools.sql.servant.command

import java.util.function.Supplier

/**
 * The blueprint if implementing a command that only returns a typed output.
 *
 * @param <TYPE_OUTPUT> The typed output of the command.
 *
 * @author Ron Webb
 * @since 1.3.0
 */
interface ICommandO<TYPE_OUTPUT> extends Supplier<TYPE_OUTPUT> {
}
