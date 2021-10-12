package xyz.ronella.tools.sql.servant

import org.h2.tools.DeleteDbFiles
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class QueryServantTest {

    final def testDefaultQueryServant = new QueryServant(new Config('./src/test/resources','ss-default'))
    final def testH2QueryServant = new QueryServant(new Config('./src/test/resources','sample-h2'))
    final def testH2ErrorQueryServant = new QueryServant(new Config('./src/test/resources','error-h2'))

    @Before
    void initTest() {
        QueryServant.hasError = false
    }


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
            testH2QueryServant.perform(new CliArgs(params: ['name' : 'nam%'], ignoreExecutionException: true, ignoreTaskException: true))
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
            testH2ErrorQueryServant.perform(new CliArgs(ignoreExecutionException: true, ignoreTaskException: true))
        }
        finally {
            eraseDB.call()
        }
    }

    @Test
    void testWithH2DBHasError() {
        def eraseDB = {
            if (new File('./src/test/db/test1.mv.db').exists()) {
                DeleteDbFiles.execute('./src/test/db/', 'test1', true)
                DeleteDbFiles.execute('./src/test/db/', 'test2', true)
                DeleteDbFiles.execute('./src/test/db/', 'test3', true)
            }
        }
        try {
            QueryServant.hasError = true
            eraseDB.call()
            Assert.assertThrows(ExecutionException.class) {
                testH2QueryServant.perform(new CliArgs(params: ['name' : 'nam%'], ignoreTaskException: true))
            }
        }
        finally {
            QueryServant.hasError = false
            eraseDB.call()
        }
    }

    @Test
    void testWithH2DBHasTaskError() {
        def eraseDB = {
            if (new File('./src/test/db/test1.mv.db').exists()) {
                DeleteDbFiles.execute('./src/test/db/', 'test1', true)
                DeleteDbFiles.execute('./src/test/db/', 'test2', true)
                DeleteDbFiles.execute('./src/test/db/', 'test3', true)
            }
        }
        try {
            eraseDB.call()
            Assert.assertThrows(TaskException.class) {
                testH2QueryServant.perform(new CliArgs(params: ['name' : 'nam%']))
            }
        }
        finally {
            eraseDB.call()
        }
    }

    @Test
    void testWithH2DBHasErrorIgnore() {
        def eraseDB = {
            if (new File('./src/test/db/test1.mv.db').exists()) {
                DeleteDbFiles.execute('./src/test/db/', 'test1', true)
                DeleteDbFiles.execute('./src/test/db/', 'test2', true)
                DeleteDbFiles.execute('./src/test/db/', 'test3', true)
            }
        }
        try {
            QueryServant.hasError = true
            eraseDB.call()
            testH2QueryServant.perform(new CliArgs(params: ['name' : 'nam%'], ignoreExecutionException: true))
        }
        finally {
            QueryServant.hasError = false
            eraseDB.call()
        }
    }

    @Test
    void testWithH2DBWithEnv() {
        def eraseDB = {
            if (new File('./src/test/db/test1.mv.db').exists()) {
                DeleteDbFiles.execute('./src/test/db/', 'test1', true)
                DeleteDbFiles.execute('./src/test/db/', 'test2', true)
                DeleteDbFiles.execute('./src/test/db/', 'test3', true)
            }
        }
        try {
            eraseDB.call()
            testH2QueryServant.perform(new CliArgs(params: ['name' : 'nam%'], ignoreExecutionException: true, ignoreTaskException: true))
        }
        finally {
            eraseDB.call()
        }
    }
}
