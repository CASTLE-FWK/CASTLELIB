package castleComponents;

import java.util.List;

import stdSimLib.Parameter;
import stdSimLib.utilities.Utilities;

public class Logger {

	private boolean muted = true;

	// Output paths
	private String systemLogPath;
	private String systemSpecPath;
	private String systemLogDirPath;
	private String systemOutputDirPath;
	private String systemStepInfoDir;
	private String sysName;

	private StringBuilder stringBuilder;
	private Output output;

	private SimulationInfo simInfo;

	public Logger() {
	}

	public Logger(Output op, SimulationInfo simInfo) {
		this.output = op;
		this.simInfo = simInfo;
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

		if (output.isLoggingToFile()) {
			stringBuilder = new StringBuilder();
			setUpLog(systemLogPath);
		}
	}

	public void newStep(int stepNumber) {
		output.sendLogToConsole("Step " + stepNumber);
		stringBuilder = new StringBuilder();
		stringBuilder.append("Step " + stepNumber + "\n");
	}

	public void endOfStep(int stepNumber) {
		if (output.isLoggingToFile()) {
			if (stringBuilder.length() != 0) {
				output.sendLogToFile(systemStepInfoDir + "/Step" + stepNumber + ".txt", stringBuilder.toString(),
						false);
			}
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
				logToFile(str + "\n");
			}
			if (output.isLoggingToConsole()) {
				logToConsole(str);
			} else {
				System.out.println("oaso");
			}
		}
	}

	public void logToConsole(String str) {
		output.sendLogToConsole(str.toString());
	}

	public void log(StringBuilder str) {
		if (!muted) {
			if (output.isLoggingToFile()) {
				logToFile(str);
			}
			if (output.isLoggingToConsole()) {
				logToConsole(str.toString());
			}
		}
	}

	public void logToFile(StringBuilder sb) {
		stringBuilder.append(sb + "\n");
	}

	public void logToFile(String str) {
		stringBuilder.append(str + "\n");
	}

	// Sets up the log path (should be fully automated)
	public void setUpLog(String str) {
		systemLogPath = str;
		systemOutputDirPath = systemLogPath + "/" + simInfo.getExecutionID();
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
