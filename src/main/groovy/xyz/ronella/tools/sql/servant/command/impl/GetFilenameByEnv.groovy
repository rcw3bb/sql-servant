package xyz.ronella.tools.sql.servant.command.impl

import xyz.ronella.tools.sql.servant.command.ICommandO

/**
 * A command for making the filename environment aware if applicable.
 *
 * @author Ron Webb
 * @since 2.1.0
 */
class GetFilenameByEnv implements ICommandO<String> {

    private String filename
    private String env

    /**
     * Creates an instance of the GetFilenameByEnv command.
     *
     * @param filename The filename of the configuration file.
     * @param env Specifies the environment.
     */
    GetFilenameByEnv(String filename, String env) {
        this.filename = filename
        this.env = env
    }

    @Override
    String get() {
        if (null!=filename) {
            def file = new File(filename)
            if (file.exists()) {
                if (null != env) {
                    def absoluteFilename = file.absolutePath
                    def tokenize = absoluteFilename =~ /^(.*)(\.\w*)$/
                    if (tokenize.matches()) {
                        def prefix = tokenize[0][1]
                        def suffix = tokenize[0][2]
                        def envFileString = "${prefix}.${env}${suffix}"
                        def envFile = new File(envFileString)
                        if (envFile.exists()) {
                            return envFile.absolutePath
                        }
                    }
                }
                return filename
            }
        }

        return null
    }
}
