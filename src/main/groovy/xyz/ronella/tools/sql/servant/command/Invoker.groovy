package xyz.ronella.tools.sql.servant.command

class Invoker {
    private Invoker() {}

    static <T> T invoke(ICommandO<T> cmd) {
        return cmd ? cmd.get() : null
    }
}
