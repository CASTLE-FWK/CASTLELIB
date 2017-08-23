package castleComponents;

import java.util.List;

import dataGenerator.OutputToJSON_Mongo;
import stdSimLib.Parameter;
import stdSimLib.utilities.Utilities;

public class Logger {

	private boolean loggingToFile;
	private boolean muted = true;
	private boolean loggingToConsole;

	// Output paths
	private String systemLogPath;
	private String systemSpecPath;
	private String systemLogDirPath;
	private String systemOutputDirPath;
	private String systemStepInfoDir;
	private String sysName;

	private StringBuilder stringBuilder;

	// For Database Stuff
	private String dbPath;
	private boolean loggingToDB;
	private OutputToJSON_Mongo mongoOutput;

	private Output output;

	public Logger() {
	}

	public Logger(Output op) {
		this.output = op;
	}

	public void setOutput(Output op) {
		this.output = op;
	}

	public void mute() {
		muted = true;
	}

	public void unmute() {
		muted = false;
	}

	public void setup(boolean isMuted, boolean toConsole, boolean toFile, String filePath, String sysName) {
		muted = isMuted;
		output.setLoggingToConsole(toConsole);
		output.setLoggingToFile(toFile);
		systemLogPath = filePath;
		this.sysName = sysName;

		if (loggingToFile) {
			stringBuilder = new StringBuilder();
			setUpLog(systemLogPath);
		}
	}

	public void newStep(int stepNumber) {
		output.sendLogToConsole("Step " + stepNumber);
		
		if (output.isLoggingToFile()) {
			if (stringBuilder.length() != 0) {
				output.sendLogToFile(systemStepInfoDir + "/Step" + stepNumber + ".txt", stringBuilder.toString(),
						false);
			}
			stringBuilder = new StringBuilder();
			stringBuilder.append("Step " + stepNumber + "\n");
		}
	}

	// This can overwrite things since the SYSTEM has power
	// TODO
	public void systemLog(String str) {

	}

	// Prints to file
	public void log(String str) {
		if (!muted) {
			if (output.isLoggingToFile()) {
				stringBuilder.append(str + "\n");
			}
			if (output.isLoggingToConsole()) {
				output.sendLogToConsole(str);
			}
		}
	}

	// Sets up the log path (should be fully automated)
	public void setUpLog(String str) {
		systemLogPath = str;
		systemOutputDirPath = systemLogPath + "/" + sysName + "-" + Utilities.generateTimeStamp();
		systemSpecPath = systemOutputDirPath + "/systemInitialization.txt";
		systemLogDirPath = systemOutputDirPath + "/systemLog.txt";
		systemStepInfoDir = systemOutputDirPath + "/steps";

		if (output.isLoggingToFile()) {
			// Create directories and initial files
			System.out.println("Create directories and files for logging");
			System.out.println("Log directory at " + systemLogPath);
			// Create main log dir if doesn't exist
			output.initialiseLoggingPath(systemLogPath, true);

			output.initialiseLoggingPath(systemOutputDirPath, true);
			output.initialiseLoggingPath(systemStepInfoDir, true);

			output.initialiseLoggingPath(systemSpecPath, false);

			output.initialiseLoggingPath(systemLogDirPath, false);
		}
	}

	public void writeSystemSpecs(String sysName, String sysDescription, List<Parameter<?>> params) {
		String out = "Simulation Initialization Details: \n";
		out += "Name: " + sysName + "\n";
		out += "Description: " + sysDescription + "\n";
		out += "Execution Start Time: " + Utilities.generateNiceTimeStamp() + "\n";
		out += "Initialization Parameters:\n";
		for (Parameter<?> p : params) {
			out += "\t" + p.toString() + "\n";
		}
		out += "----------------------------------------";
		
		output.sendLogToConsole(out);
		output.sendLogToFile(systemSpecPath, out, false);
	}
}
