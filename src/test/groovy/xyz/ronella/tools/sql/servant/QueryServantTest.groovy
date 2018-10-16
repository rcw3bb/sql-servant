package xyz.ronella.tools.sql.servant

import org.h2.tools.DeleteDbFiles
import org.junit.Test

class QueryServantTest {

    final def testDefaultQueryServant = new QueryServant(new Config('./src/test/resources/conf','ss-default'))
    final def testH2QueryServant = new QueryServant(new Config('./src/test/resources/conf','sample-h2'))
    final def testH2ErrorQueryServant = new QueryServant(new Config('./src/test/resources/conf','error-h2'))

    @Test
    void testOptionNoop() {
        testDefaultQueryServant.perform(new CliArgs(noop: true))
    }

    @Test
    void testWithH2DB() {
        def eraseDB = {
            if (new File('./src/test/db/test1.mv.db').exists()) {
                DeleteDbFiles.execute('./src/test/db/', 'test1', true)
                DeleteDbFiles.execute('./src/test/db/', 'test2', true)
                DeleteDbFiles.execute('./src/test/db/', 'test3', true)
             }
        }
        try {
            eraseDB.call()
            testH2QueryServant.perform(new CliArgs(params: ['name' : 'nam%']))
        }
        finally {
            eraseDB.call()
        }
    }

    @Test
    void testWithH2DBError() {
        def eraseDB = {
            if (new File('./src/test/db/test4.mv.db').exists()) {
                DeleteDbFiles.execute('./src/test/db/', 'test4', true)
            }
        }
        try {
            eraseDB.call()
            testH2ErrorQueryServant.perform(new CliArgs())
        }
        finally {
            eraseDB.call()
        }
    }

}
