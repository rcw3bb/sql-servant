package xyz.ronella.tools.sql.servant;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SQLServant {

    public static void main(String... args) {
        final var argsMgr = CliBuilder.build(args);

        if (argsMgr.shouldExit()) {
            return;
        }

        final var properties = new Properties();

        InputStream sqlsrvntIS = null;
        try {
            try {
                sqlsrvntIS = SQLServant.class.getClassLoader().getResourceAsStream("sqlsrvnt.properties");
                properties.load(sqlsrvntIS);
            } finally {
                if (sqlsrvntIS != null) {
                    sqlsrvntIS.close();
                }
            }
        }
        catch(IOException ioe) {
            throw new RuntimeException(ioe);
        }

        if (argsMgr.getVersion()) {
            String version = properties.getProperty("version", "");
            String year = properties.getProperty("year", "2018");
            System.out.printf("SQL Servant %s [%s]%n", version, year);
            return;
        }

        final var cliArgs = argsMgr.toCliArgs();

        final var config = new ConfigByEnv(new Config(cliArgs.getConfDir(), cliArgs.getConfig(), cliArgs.getEnvironment())).createConfigByEnv(cliArgs);
        new QueryServant(config).perform(cliArgs);
    }
}
