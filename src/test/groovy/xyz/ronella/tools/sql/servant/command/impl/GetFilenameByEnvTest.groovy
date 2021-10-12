package xyz.ronella.tools.sql.servant.command.impl

import org.junit.Assert
import org.junit.Test
import xyz.ronella.tools.sql.servant.command.Invoker

import java.nio.file.Paths

class GetFilenameByEnvTest {

    private static final String TEST_RESOURCES_DIR = './src/test/resources/envfile'

    @Test
    void testFileWithEnv1() {
        String env = "ENV1"
        String file = Paths.get(TEST_RESOURCES_DIR, 'sample-conf.json').toFile().absolutePath

        String filename = Invoker.invoke(new GetFilenameByEnv(file, env))
        Assert.assertTrue(filename.endsWith('sample-conf.ENV1.json'))
    }

    @Test
    void testFileWithNonExistentEnv() {
        String env = "ENVXX"
        String file = Paths.get(TEST_RESOURCES_DIR, 'sample-conf.json').toFile().absolutePath

        String filename = Invoker.invoke(new GetFilenameByEnv(file, env))
        Assert.assertTrue(filename.endsWith('sample-conf.json'))
    }

    @Test
    void testFileWithNullEnv() {
        String env = null
        String file = Paths.get(TEST_RESOURCES_DIR, 'sample-conf.json').toFile().absolutePath

        String filename = Invoker.invoke(new GetFilenameByEnv(file, env))
        Assert.assertTrue(filename.endsWith('sample-conf.json'))
    }

    @Test
    void testNullFileWithNullEnv() {
        String env = null
        File file = null

        String filename = Invoker.invoke(new GetFilenameByEnv(file, env))
        Assert.assertNull(filename)
    }

    @Test
    void testFileDoesntExists() {
        String env = null
        String file = Paths.get(TEST_RESOURCES_DIR, 'non-existent.json').toFile().absolutePath

        String filename = Invoker.invoke(new GetFilenameByEnv(file, env))
        Assert.assertNull(filename)
    }
}
