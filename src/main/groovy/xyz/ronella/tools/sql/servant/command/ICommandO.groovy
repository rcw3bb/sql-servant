package xyz.ronella.tools.sql.servant.command

import java.util.function.Supplier

interface ICommandO<TYPE_OUTPUT> extends Supplier<TYPE_OUTPUT> {
}
