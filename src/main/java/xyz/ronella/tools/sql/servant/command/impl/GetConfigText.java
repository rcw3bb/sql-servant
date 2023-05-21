package xyz.ronella.tools.sql.servant.command.impl;

import org.apache.logging.log4j.Logger;
import xyz.ronella.tools.sql.servant.command.ICommandO;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * A command for reading the raw configuration file into text file.
 *
 * @author Ron Webb
 * @since 1.3.0
 */
public class GetConfigText implements ICommandO<String> {

    public final static Logger LOG = LogManager.getLogger(GetConfigText.class);

    private final String filename;

    /**
     * Creates an instance of the GetConfigText command.
     *
     * @param filename The filename of the configuration file.
     */
    public GetConfigText(String filename) {
        this.filename = filename;
    }

    @Override
    public String get() {
        final var file = new File(this.filename);

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Filename: %s", file.getAbsolutePath()));
        }

        if (file.exists() && file.canRead()) {
            final var sb = new StringBuilder();
            final Scanner scanner;

            try {
                scanner = new Scanner(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);

            }
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
                sb.append(System.lineSeparator());
            }
            scanner.close();

            return sb.toString();
        }
        else {
            return null;
        }
    }
}
