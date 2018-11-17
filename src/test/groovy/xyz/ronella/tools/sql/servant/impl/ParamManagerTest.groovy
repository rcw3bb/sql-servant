package xyz.ronella.tools.sql.servant.impl

import org.junit.Test
import xyz.ronella.tools.sql.servant.conf.ParamConfig

class ParamManagerTest {

    @Test
    void singleParamTest() {
        String dummyText = '%%%TOKEN1%%%'
        String expected = 'TOKEN1'

        assert expected==ParamManager.applyParams([new ParamConfig(name : 'TOKEN1', value: 'TOKEN1')] as ParamConfig[],
                dummyText)
    }

    @Test
    void multipleParamsTest() {
        String dummyText = "%%%TOKEN1%%% %%%TOKEN2%%%"
        String expected = "TOKEN1 TOKEN2"

        assert expected==ParamManager.applyParams([new ParamConfig(name : 'TOKEN1', value: 'TOKEN1'),
            new ParamConfig(name: 'TOKEN2', value: 'TOKEN2')] as ParamConfig[], dummyText)
    }

}
