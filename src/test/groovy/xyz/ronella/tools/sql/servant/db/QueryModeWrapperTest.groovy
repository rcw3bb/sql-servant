package xyz.ronella.tools.sql.servant.db

import org.junit.Test

class QueryModeWrapperTest {

    @Test(expected = QueryModeException.class)
    void testInvalidMode() {
        new QueryModeWrapper('invalid').mode
    }

    @Test(expected = QueryModeException.class)
    void testNullMode() {
        new QueryModeWrapper(null).mode
    }

    @Test
    void testValidMode() {
        assert QueryMode.SCRIPT==new QueryModeWrapper('script').mode
    }
}
