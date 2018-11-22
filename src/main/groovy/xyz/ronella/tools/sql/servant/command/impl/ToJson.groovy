package xyz.ronella.tools.sql.servant.command.impl

import com.google.gson.Gson
import xyz.ronella.tools.sql.servant.command.ICommandO

/**
 * A command for translating the text configuration file to JSON objects.
 *
 * @param <T> Type of the JSON object.
 *
 * @author Ron Webb
 * @since 1.3.0
 */
class ToJson<T> implements ICommandO<T> {

    private String strJson
    private Class<T> expectedClass

    /**
     * Creates an instance of ToJson
     * @param strJson The JSON in string form.
     * @param expectedClass The expected JSON object after translating the strJson.
     */
    ToJson(String strJson, Class<T> expectedClass) {
        this.strJson = strJson
        this.expectedClass = expectedClass
    }

    @Override
    T get() {
        return (strJson && expectedClass) ? new Gson().fromJson(strJson, expectedClass) : null
    }
}
