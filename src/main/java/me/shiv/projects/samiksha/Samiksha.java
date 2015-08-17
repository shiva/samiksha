/**
 * 
 */
package me.shiv.projects.samiksha;

import java.io.File;
import java.util.LinkedList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;

/**
 * @author shiva
 *
 */
public class Samiksha {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Samiksha app = new Samiksha();

		app.processCommandLine(args);

		// if there are any files to process,
		// then index using lucene
		app.filelist.forEach(f -> 
			app.index.addOPMLToIndex(f));

		try {
			app.index.findAndDisplay(app.searchStr);
		} catch (org.apache.lucene.queryparser.classic.ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// run query and print results
	}

	

	private String searchStr;
	private LinkedList<File> filelist;
	private String cwd;
	private SamikshaIndex index;

	public Samiksha() {
		this.cwd = System.getProperty("user.dir");
		this.searchStr = null;
		try {
			this.index = new SamikshaIndex();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processCommandLine(String[] args) {
		Options options = constructOptions();
		parseArguments(args, options);
	}

	private void parseArguments(String[] args, Options options) {
		CommandLineParser parser = new DefaultParser();

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("help")) {
				printUsage(options);
				System.exit(0);
			}

			this.searchStr = line.getOptionValue("find");
			this.filelist = new LinkedList<File>();
			line.getArgList().forEach(arg -> addToFileList(arg));

		} catch (ParseException exp) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
		}
	}

	private boolean addToFileList(String arg) {
		File nf = new File(this.cwd, arg);

		if (nf.exists()) {
			this.filelist.add(nf);
			return true;
		}

		return false;
	}

	private static void printUsage(Options options) {
		String header = "Search for the given item from opml files\n\n";
		String footer = "\nPlease report issues at https://github.com/shiva/samiksha/issues";

		HelpFormatter formatter = new HelpFormatter();
		formatter.setArgName("[file1.opml] [file2.opml] ...");
		formatter.printHelp("samiksha", header, options, footer, true);
	}

	private static Options constructOptions() {
		Options options = new Options();

		Option help = new Option("h", "help", false, "print this message");
		Option version = new Option("v", "version", false, "print the version information and exit");
		Option quiet = new Option("q", "quiet", false, "be extra quiet");
		Option verbose = new Option("V", "verbose", false, "be extra verbose");
		Option debug = new Option("d", "debug", false, "print debugging information");

		/*
		 * Option logfile = Option.builder("logfile") .argName("logfile")
		 * .hasArg() .desc( "use given file for log" ) .build();
		 * 
		 * Option logger = Option.builder( "logger" ) .hasArg() .desc(
		 * "the class which it to perform " + "logging" ) .build();
		 *
		 */

		Option searchCriteria = Option.builder().longOpt("find").desc("the search string you are looking for")
				.argName("SEARCHSTR").hasArg().build();

		options.addOption(help);
		options.addOption(version);
		options.addOption(quiet);
		options.addOption(verbose);
		options.addOption(debug);
		options.addOption(searchCriteria);
		return options;
	}

}
