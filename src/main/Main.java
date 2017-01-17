package main;

import testing.Test;
import training.Train;
import utils.JDBC;
import utils.VideoClass;
import gui.*;

import org.apache.commons.cli.*;

public class Main {

	public static void main(String[] args) throws Exception {
				
		CommandLine cmd = parseArguments(args);
		if (cmd.hasOption("train")) {
			VideoClass classA = new VideoClass("A", cmd.getOptionValue("listA"));
			VideoClass classB = new VideoClass("B", cmd.getOptionValue("listB"));
			new Train(classA, classB, cmd.getOptionValue("output"), true).train();
		} else if (cmd.hasOption("test")) {
			String url = cmd.getOptionValue("input");
			String configA = cmd.getOptionValue("configA");
			String configB = cmd.getOptionValue("configB");
			boolean verbose = (cmd.hasOption("verbose")) ? true : false;
			if (url.endsWith("txt")) {
				new Test(url, configA, configB, false).batchTest();
			} else {
				new Test(url, configA, configB, verbose).test();
			}	
		} else if (cmd.hasOption("initialize")) {
			JDBC.initializeDB();
		} else if (cmd.hasOption("update")) {
			String dataset = cmd.getOptionValue("input");
			JDBC.updateDB(dataset);
		} else if (cmd.hasOption("gui")) {
			GUI.run();
		}
	}

	private static CommandLine parseArguments(String[] args) {
			
		Options opts = new Options();
		
		OptionGroup group = new OptionGroup();
		group.addOption(new Option("trn", "train", false, "train a binary classification problem"));
		group.addOption(new Option("tst", "test", false, "predict the class of a xml file"));
		group.addOption(new Option("u", "update", false, "update database"));
		group.addOption(new Option("init", "initialize", false, "initialize database"));
		group.addOption(new Option("g", "gui", false, "use interface"));
		group.addOption(new Option("h", "help", false, "print help message"));
		opts.addOptionGroup(group);
		
		Option listA_opt = Option.builder("lA")
				.longOpt("listA")
				.argName("txt file")
				.hasArg()
				.desc("text file containing a list of xml file for class A, only for --train")
				.build();
		opts.addOption(listA_opt);
		
		Option listB = Option.builder("lB")
				.longOpt("listB")
				.argName("txt file")
				.hasArg()
				.desc("text file containing a list of xml file for class B, only for --train")
				.build();
		opts.addOption(listB);
		
		Option output_opt = Option.builder("o")
				.longOpt("output")
				.argName("folder")
				.hasArg()
				.desc("output folder for the training config files, only for --train")
				.build();
		opts.addOption(output_opt);
		
		Option input_opt = Option.builder("i")
				.longOpt("input")
				.argName("xml file")
				.hasArg()
				.desc("xml file for which compute the likelihood for class A and B, only for --test,"
						+ "dataset path for --update")
				.build();
		opts.addOption(input_opt);
		
		Option configA_opt = Option.builder("cA")
				.longOpt("configA")
				.argName("xml file")
				.hasArg()
				.desc("xml config file for class A, only for --test")
				.build();
		opts.addOption(configA_opt);
		
		Option configB_opt = Option.builder("cB")
				.longOpt("configB")
				.argName("xml file")
				.hasArg()
				.desc("xml config file for class B, only for --test")
				.build();
		opts.addOption(configB_opt);
		
		Option verbose_opt = Option.builder("v")
				.longOpt("verbose")
				.desc("whether or not display information, only for --test")
				.build();
		opts.addOption(verbose_opt);
		
		CommandLine cl = null;
		HelpFormatter formatter = new HelpFormatter();
	    try {
	        cl = new DefaultParser().parse(opts, args);
	    } catch (ParseException e) {
	        System.err.println(e.getMessage());
	        formatter.printHelp("foa", opts, true);
	        System.exit(0);
	    }   
	    if (cl.hasOption("h")) {
	        formatter.printHelp("foa", opts, true);
	        System.exit(0);
	    }  else if (cl.getOptions().length == 0) {
	    	System.err.println("No options given");
	    	formatter.printHelp("foa", opts, true);
	        System.exit(0);
	    } else if (cl.hasOption("train") && (!cl.hasOption("listA") ||!cl.hasOption("listB") || !cl.hasOption("o"))) {
	    	System.err.println("Missing options: [listA | listB | o]");
	    	formatter.printHelp("foa", opts, true);
	    	System.exit(0);
	    } else if (cl.hasOption("test") && (!cl.hasOption("i") ||!cl.hasOption("configA") || !cl.hasOption("configB"))) {
	    	System.err.println("Missing options: [i | configA | configB]");
	    	formatter.printHelp("foa", opts, true);
	    	System.exit(0);
	    } else if (cl.hasOption("update") && !cl.hasOption("i")) {
	    	System.err.println("Missing option: i");
	    	formatter.printHelp("foa", opts, true);
	    	System.exit(0);
	    }
	    return cl;
	}
	
}