package xyz.ronella.tools.sql.servant.command.impl

import com.google.gson.Gson
import xyz.ronella.tools.sql.servant.command.ICommandO

class ToJson<T> implements ICommandO<T> {

    private String strJson
    private Class<T> expectedClass

    ToJson(String strJson, Class<T> expectedClass) {
        this.strJson = strJson
        this.expectedClass = expectedClass
    }

    @Override
    T get() {
        return (strJson && expectedClass) ? new Gson().fromJson(strJson, expectedClass) : null
    }
}
