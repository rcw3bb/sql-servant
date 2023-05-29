import org.junit.Test
import xyz.ronella.tools.sql.servant.SQLServant

class MainScriptTest {

    @Test
    void testMainScriptForDefault() {
        new SQLServant().main()
    }

}
