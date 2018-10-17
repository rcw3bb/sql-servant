package xyz.ronella.tools.sql.servant

import org.junit.Test
import xyz.ronella.tools.sql.servant.impl.ParamManager

class ValidateTest {

    @Test
    void testExistingParamToken() {
        assert !new Validate(new ParamManager()).check("test${ParamManager.getParamToken('TEST')}test")
    }

    @Test
    void testExistingParamTokenWithUnderscore() {
        assert !new Validate(new ParamManager()).check("test${ParamManager.getParamToken('TES_T')}test")
    }

    @Test
    void testExistingParamTokenWithAsterisk() {
        assert new Validate(new ParamManager()).check("test${ParamManager.getParamToken('TEST*')}test")
    }

}
