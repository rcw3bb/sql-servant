package xyz.ronella.tools.sql.servant;

import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class CliBuilder {

    private Boolean noop;
    private Boolean iee;
    private Boolean ite;
    private Boolean parallel;
    private Boolean version;
    private String config;
    private String configDir;
    private String env;
    private Map<String, String> parameters;

    public boolean shouldExit() {
        return shouldExit;
    }

    public void setShouldExit(boolean shouldExit) {
        this.shouldExit = shouldExit;
    }

    private boolean shouldExit;

    private CliBuilder() {
    }

    public void setNoop(final Boolean noop) {
        this.noop = noop;
    }

    public Boolean getNoop() {
        return this.noop;
    }

    public Boolean getIgnoreExecutionException() {
        return iee;
    }

    public void setIgnoreExecutionException(final Boolean iee) {
        this.iee = iee;
    }

    public Boolean getIgnoreTaskException() {
        return ite;
    }

    public void setIgnoreTaskException(final Boolean ite) {
        this.ite = ite;
    }

    public Boolean getParallel() {
        return parallel;
    }

    public void setParallel(final Boolean parallel) {
        this.parallel = parallel;
    }

    public Boolean getVersion() {
        return version;
    }

    public void setVersion(final Boolean version) {
        this.version = version;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(final String config) {
        this.config = config;
    }

    public String getConfDir() {
        return configDir;
    }

    public void setConfDir(final String configDir) {
        this.configDir = configDir;
    }

    public String getEnvironment() {
        return env;
    }

    public void setEnvironment(final String env) {
        this.env = env;
    }

    public Map<String, String> getParams() {
        return parameters;
    }

    public void setParams(final Map<String, String> parameters) {
        this.parameters = parameters;
    }

    private static void addNOOPOption(Options options) {
        Option option = new Option("n", "noop", false
                , "Run without actually performing the queries.");
        option.setRequired(false);
        options.addOption(option);
    }

    private static void addIEEOption(Options options) {
        Option option = new Option("iee", "ignore-execution-exception", false
                , "Ignore the ExecutionException that can be thrown at the end.");
        option.setRequired(false);
        options.addOption(option);
    }

    private static void addITEOption(Options options) {
        Option option = new Option("ite", "ignore-task-exception", false
                , "Ignore the TaskException that can be thrown when a task failed.");
        option.setRequired(false);
        options.addOption(option);
    }

    private static void addParallelOption(Options options) {
        Option option = new Option("p", "parallel", false
                , "Run the actual queries in parallel.");
        option.setRequired(false);
        options.addOption(option);
    }

    private static void addVersionOption(Options options) {
        Option option = new Option("v", "version", false
                , "Shows the current version.");
        option.setRequired(false);
        options.addOption(option);
    }

    private static void addConfigOption(Options options) {
        Option option = new Option("c", "config", true
                , "Run a different configuration other than the default.");
        option.setArgName("config-name");
        option.setRequired(false);
        options.addOption(option);
    }

    private static void addConfigDirOption(Options options) {
        Option option = new Option("cd", "confdir", true
                , "Find the configuration in the specified directory.");
        option.setArgName("config-dir");
        option.setRequired(false);
        options.addOption(option);
    }

    private static void addEnvOption(Options options) {
        Option option = new Option("e", "env", true
                , "Find the configuration in the specified directory.");
        option.setArgName("environment");
        option.setRequired(false);
        options.addOption(option);
    }

    private static void addHelpOption(Options options) {
        Option option = new Option("h", "help", false
                , "Shows the help information.");
        option.setRequired(false);
        options.addOption(option);
    }

    private static void addParamsOption(Options options) {
        Option genericParam = new Option("P", true, "Assigns value to the query parameters found in the configuration. This can be used multiple times.");
        genericParam.setRequired(false);
        genericParam.setArgName("parameter=value");
        genericParam.setArgs(2);
        genericParam.setValueSeparator('=');
        options.addOption(genericParam);
    }

    public static CliBuilder build(String[] args) {
        var argManager = new CliBuilder();
        Options options = new Options();

        addHelpOption(options);
        addNOOPOption(options);
        addIEEOption(options);
        addITEOption(options);
        addParallelOption(options);
        addVersionOption(options);
        addConfigOption(options);
        addConfigDirOption(options);
        addEnvOption(options);
        addParamsOption(options);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        BiConsumer<CliBuilder, Options> showHelpInfo = (___argManager, ___options) -> {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("sqlsrvnt", ___options);
            ___argManager.setShouldExit(true);
        };

        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("help")) {
                showHelpInfo.accept(argManager, options);
            } else {
                argManager.setNoop(cmd.hasOption("noop"));
                argManager.setIgnoreExecutionException(cmd.hasOption("ignore-execution-exception"));
                argManager.setIgnoreTaskException(cmd.hasOption("ignore-task-exception"));
                argManager.setParallel(cmd.hasOption("parallel"));
                argManager.setVersion(cmd.hasOption("version"));
                argManager.setConfig(cmd.getOptionValue("config"));
                argManager.setConfDir(cmd.getOptionValue("confdir"));
                argManager.setEnvironment(cmd.getOptionValue("env"));

                final var params = cmd.getOptionValues("P");
                if (params!=null && params.length > 0) {
                    final var paramValue = new HashMap<String, String>();
                    final var listParams = Arrays.asList(params);

                    int idx = 0;
                    for (final var param : listParams) {
                        if (++idx % 2 == 0) {
                            paramValue.put(listParams.get(idx-2), param);
                        }
                    }
                    argManager.setParams(paramValue);
                }
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            showHelpInfo.accept(argManager, options);
        }

        return argManager;
    }

    public CliArgs toCliArgs() {

        final var cliArgs = new CliArgs();
        cliArgs.setConfDir(this.configDir);
        cliArgs.setConfig(this.config);
        cliArgs.setEnvironment(this.env);
        cliArgs.setIgnoreExecutionException(this.iee);
        cliArgs.setIgnoreTaskException(this.ite);
        cliArgs.setNoop(this.noop);
        cliArgs.setParallel(this.parallel);
        cliArgs.setParams(this.parameters);

        return cliArgs;
    }
}
