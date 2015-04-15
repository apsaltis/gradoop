package org.gradoop.drivers;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.log4j.Logger;

/**
 * BulkDriver SuperClass for all Driver
 */
public abstract class BulkDriver extends Configured implements Tool {
  /**
   * Command line option for displaying help.
   */
  public static final String OPTION_HELP = "h";
  /**
   * Command line option for activating verbose.
   */
  public static final String OPTION_VERBOSE = "v";
  /**
   * Command line option to set the graph file input path.
   */
  public static final String OPTION_GRAPH_INPUT_PATH = "gip";
  /**
   * Command line option to set the path to store HFiles from Bulk Load.
   */
  public static final String OPTION_GRAPH_OUTPUT_PATH = "gop";
  /**
   * Command line option to set a custom argument.
   */
  public static final String OPTION_CUSTOM_ARGUMENT = "ca";
  /**
   * Class Logger
   */
  private static Logger LOG = Logger.getLogger(BulkDriver.class);
  /**
   * Verbose var for bulkload and buldwrite driver
   */
  private boolean verbose;
  /**
   * graph input path
   */
  private String inputPath;
  /**
   * graph output path
   */
  private String outputPath;
  /**
   * Giraph configuration
   */
  private Configuration conf;

  /**
   * Get Method for verbose var
   *
   * @return verbose
   */
  public boolean getVerbose() {
    return this.verbose;
  }

  /**
   * Get Method for input path
   *
   * @return inputPath
   */
  public String getInputPath() {
    return this.inputPath;
  }

  /**
   * Get Method for output path
   *
   * @return outputpath
   */
  public String getOutputPath() {
    return this.outputPath;
  }

  /**
   * Get Method for giraph Configuration
   *
   * @return conf
   */
  public Configuration getHadoopConf() {
    return this.conf;
  }

  /**
   * Method to parse the given arguments
   *
   * @param args arguments of the command line
   * @return int check number
   * @throws ParseException
   */
  public int parseArgs(String[] args) throws ParseException {
    conf = getConf();
    CommandLine cmd = ConfUtils.parseArgs(args);
    if (cmd.hasOption(OPTION_HELP)) {
      printHelp();
      return 0;
    }
    verbose = cmd.hasOption(OPTION_VERBOSE);
    inputPath = cmd.getOptionValue(OPTION_GRAPH_INPUT_PATH);
    outputPath = cmd.getOptionValue(OPTION_GRAPH_OUTPUT_PATH);
    if (cmd.hasOption(OPTION_CUSTOM_ARGUMENT)) {
      for (String caOptionValue : cmd.getOptionValues(OPTION_CUSTOM_ARGUMENT)) {
        for (String paramValue : Splitter.on(',').split(caOptionValue)) {
          String[] parts =
            Iterables.toArray(Splitter.on('=').split(paramValue), String.class);
          if (parts.length != 2) {
            throw new IllegalArgumentException("Unable to parse custom " +
              " argument: " + paramValue);
          }
          parts[1] = parts[1].replaceAll("\"", "");
          if (LOG.isInfoEnabled()) {
            LOG.info("###Setting custom argument [" + parts[0] + "] to [" +
              parts[1] + "] in GiraphConfiguration");
          }
          conf.set(parts[0], parts[1]);
        }
      }
    }
    return 1;
  }

  /**
   * Prints a help menu for the defined options.
   */
  private static void printHelp() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(ConfUtils.class.getName(), ConfUtils.OPTIONS, true);
  }

  /**
   * Configuration params for {@link org.gradoop.drivers.BulkLoadDriver}.
   */
  public static class ConfUtils {
    /**
     * Maintains accepted options for {@link org.gradoop.drivers.BulkLoadDriver}
     */
    protected static final Options OPTIONS;

    static {
      OPTIONS = new Options();
      OPTIONS.addOption(OPTION_HELP, "help", false, "Display help.");
      OPTIONS.addOption(OPTION_VERBOSE, "verbose", false,
        "Print console output during job execution.");
      OPTIONS.addOption(OPTION_GRAPH_INPUT_PATH, "graph-input-path", true,
        "Path to the input graph file.");
      OPTIONS.addOption(OPTION_GRAPH_OUTPUT_PATH, "graph-output-path", true,
        "Path to store HFiles in HDFS (used by Bulk Load)");
      OPTIONS.addOption(OPTION_CUSTOM_ARGUMENT, "customArguments", true,
        "provide custom" +
          " arguments for the job configuration in the form:" +
          " -ca <param1>=<value1>,<param2>=<value2> -ca <param3>=<value3> etc" +
          "." +
          " It can appear multiple times, and the last one has effect" +
          " for the same param.");
    }

    /**
     * Parses the given arguments.
     *
     * @param args command line arguments
     * @return parsed command line
     * @throws org.apache.commons.cli.ParseException
     */
    public static CommandLine parseArgs(final String[] args) throws
      ParseException {
      if (args.length == 0) {
        LOG.error("No arguments were provided (try -h)");
      }
      CommandLineParser parser = new BasicParser();
      CommandLine cmd = parser.parse(OPTIONS, args, true);
      return cmd;
    }
  }
}
