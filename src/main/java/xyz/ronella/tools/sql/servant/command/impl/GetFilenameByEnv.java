package xyz.ronella.tools.sql.servant.command.impl;

import xyz.ronella.tools.sql.servant.command.ICommandO;

import java.io.File;
import java.util.regex.Pattern;

/**
 * A command for making the filename environment aware if applicable.
 *
 * @author Ron Webb
 * @since 2.1.0
 */
public class GetFilenameByEnv implements ICommandO<String> {

    private final String filename;
    private final String env;

    /**
     * Creates an instance of the GetFilenameByEnv command.
     *
     * @param filename The filename of the configuration file.
     * @param env Specifies the environment.
     */
    public GetFilenameByEnv(final String filename, final String env) {
        this.filename = filename;
        this.env = env;
    }

    @Override
    public String get() {
        if (null!=filename) {
            final var file = new File(filename);
            if (file.exists()) {
                if (null != env) {
                    final var absoluteFilename = file.getAbsolutePath();
                    final var pattern = "^(.*)(\\.\\w*)$";
                    final var compiledPattern = Pattern.compile(pattern);
                    final var tokenize = compiledPattern.matcher(absoluteFilename);

                    if (tokenize.matches()) {
                        final var prefix = tokenize.group(1);
                        final var suffix = tokenize.group(2);
                        final var envFileString = String.format("%s.%s%s", prefix, env, suffix);
                        final var envFile = new File(envFileString);
                        if (envFile.exists()) {
                            return envFile.getAbsolutePath();
                        }
                    }
                }
                return filename;
            }
        }

        return null;
    }
}
