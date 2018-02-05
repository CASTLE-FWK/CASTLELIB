package castleComponents;

import java.util.ArrayList;

import dataGenerator.OutputToJSON_Mongo;
import stdSimLib.Parameter;
import stdSimLib.utilities.Utilities;

public class Output {

	private boolean loggingToFile;
	private boolean loggingToConsole;
	private boolean loggingToDB;

	private boolean writingModelDataToFile;
	private boolean writingModelDataToConsole;
	private boolean writingModelDataToDB;

	// DB Stuff
	private String dbPath;
	final String URL = "http://127.0.0.1:5984/"; // this isn't correct but it's
													// close
	String DBName = "default_DB_Name";
	String executionID = "";
	int currentStep = 0;
	String currentPath = "";

	SimulationInfo simInfo;

	OutputToJSON_Mongo dbOutput;
	boolean dbOutputMuted = false;

	private Logger logger;
	boolean loggerMuted = false;

	public Output(SimulationInfo si) {
		setSimulationInfo(si);
	}

	public void setSimulationInfo(SimulationInfo si) {
		simInfo = si;
	}

	public void setLoggerMuted(boolean m) {
		loggerMuted = m;
	}

	public boolean getLoggerMuted() {
		return loggerMuted;
	}

	public void setDBOutputMuted(boolean m) {
		dbOutputMuted = m;
	}

	public boolean getDBOutputMuted() {
		return dbOutputMuted;
	}

	public void setup(boolean ltf, boolean ltc, boolean ltd, String logFilePath, boolean wdf, boolean wdc,
			boolean wddb) {
		loggingToFile = ltf;
		loggingToConsole = ltc;
		loggingToDB = ltd;
		writingModelDataToFile = wdf;
		writingModelDataToConsole = wdc;
		writingModelDataToDB = wddb;

		forceToConsole("********SETTING UP LOGGING********" + "\n* logging to console: " + ltc + "\n* logging to file: "
				+ ltf + "\n* logging to database: " + ltd + "\n* writing execution data to console: " + wdc
				+ "\n* writing execution data to file: " + wdf + "\n* writing execution data to database: " + wddb
				+ "\n* writing to file path: " + logFilePath + "\n********FINISHED LOGGING SETUP********");

		logger.setup(logFilePath, simInfo.getSystemName());
		
	}
	
	public void systemInitialStatsToDB(ArrayList<Parameter<?>> params, String timeAsDate) {
		dbOutput.storeInitValues(params, timeAsDate);
	}

	public void setLogger(Logger l) {
		logger = l;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setDatabaseOutput(OutputToJSON_Mongo o) {
		dbOutput = o;
	}

	public OutputToJSON_Mongo getDatabaseOutput() {
		return dbOutput;
	}

	public void initialiseLoggingPath(String str, boolean isDir) {
		Utilities.createFile(str, isDir);
	}

	public void log(Entity e, String str) {
		if (!loggerMuted) {
			if (loggingToConsole) {
				logger.logToConsole(str);
			}
			if (loggingToFile) {
				logger.logToFile(str);
			}
		}
		if (!dbOutputMuted) {
			if (loggingToDB) {
				dbOutput.exportEntity(e);
			}
		}
	}

	public void logWithOptionalWrite(String str) {
		logger.logWithOptionalWrite(str);
	}

	public void forceToConsole(String str) {
		logger.logToConsole(str);
	}

	public void writeModelData(Entity e) {
		if (!loggerMuted) {
			if (writingModelDataToConsole) {
				logger.logToConsole(e.writeEntityData().toString());
			}
			if (writingModelDataToFile) {
				logger.logToFile(e.writeEntityData());
			}
		}
		if (!dbOutputMuted) {
			if (writingModelDataToDB) {
				dbOutput.exportEntity(e);
			}
		}

	}

	public void sendLogToFile(String filePath, String log, boolean append) {
		if (loggingToFile) {
			new ThreadedFileWriter(filePath, log, append).run();
		}
	}

	public void setupDB(String systemName, String executionID, String databaseName) {
		if (loggingToDB || writingModelDataToDB) {
			if (dbOutput != null) {
				dbOutput.setupDB(systemName, executionID, databaseName);
				this.executionID = executionID;
				DBName = systemName;
				currentPath = URL + DBName;
				DBName = DBName + "_" + executionID;
			} else {
				dbOutputMuted = true;
				System.out.println("dbOutput is null for some reason.");
			}
		} else {
			dbOutputMuted = true;
			System.out.println("New database not created as nothing will be sent to it.");
		}
	}

	public void sendLogToConsole(String log) {
		System.out.println(log);
	}

	public void newStep(int ticks) {
		if (dbOutput != null) {
			if (!dbOutputMuted) {
				dbOutput.newStep();
			}
		}
		if (logger != null) {
			if (!loggerMuted) {
				logger.newStep(ticks);
			}
		}
	}

	public void endOfStep(int ticks) {
		if (dbOutput != null) {
			if (!dbOutputMuted) {
				dbOutput.endOfStep();
			}
		}
		if (logger != null) {
			if (!loggerMuted) {
				logger.endOfStep(ticks);
			}
		}
	}
	
	public void sendEndOfStepStats(int stepNumber, int totalSteps, long timeSinceLastStep, long elapsedTime) {
		if (dbOutput != null) {
			if (!dbOutputMuted) {
				dbOutput.exportSystemStep(stepNumber, totalSteps, timeSinceLastStep, elapsedTime);
			}
		}
	}

	public void endOfSimulation(int ticks, long elapsedTime, int desiredTotalTicks) {
		if (dbOutput != null) {
			if (!dbOutputMuted) {
				dbOutput.endOfSimulation(ticks, elapsedTime, desiredTotalTicks);
			}
		}
		if (logger != null) {
			if (!loggerMuted) {
				logger.endOfStep(ticks);
			}
		}
	}

	public void loggingToDB(String dbPath) {
		this.dbPath = dbPath;
		enableLoggingToDB();
	}

	public void enableLoggingToDB() {
		loggingToDB = true;
	}

	public void disableLoggingToDB() {
		loggingToDB = false;
	}

	public void enableLoggingToFile() {
		loggingToFile = true;
	}

	public void disableLoggingToFile() {
		loggingToFile = false;
	}

	public void enableLoggingToConsole() {
		loggingToConsole = true;
	}

	public void disableLoggingToConsole() {
		loggingToConsole = false;
	}

	public boolean isLoggingToFile() {
		return loggingToFile;
	}

	public boolean isLoggingToConsole() {
		return loggingToConsole;
	}

	public boolean isLoggingToDB() {
		return loggingToDB;
	}

	public void setLoggingToFile(boolean loggingToFile) {
		this.loggingToFile = loggingToFile;
	}

	public void setLoggingToConsole(boolean loggingToConsole) {
		this.loggingToConsole = loggingToConsole;
	}

	public void setLoggingToDB(boolean loggingToDB) {
		this.loggingToDB = loggingToDB;
	}

	public String getDbPath() {
		return dbPath;
	}

	public boolean isWritingModelDataToFile() {
		return writingModelDataToFile;
	}

	public void setWritingModelDataToFile(boolean writingModelDataToFile) {
		this.writingModelDataToFile = writingModelDataToFile;
	}

	public boolean isWritingModelDataToConsole() {
		return writingModelDataToConsole;
	}

	public void setWritingModelDataToConsole(boolean writingModelDataToConsole) {
		this.writingModelDataToConsole = writingModelDataToConsole;
	}

	public boolean isWritingModelDataToDB() {
		return writingModelDataToDB;
	}

	public void setWritingModelDataToDB(boolean writingModelDataToDB) {
		this.writingModelDataToDB = writingModelDataToDB;
	}
	public void errLog(Object o) {
		System.err.println("Output Warning: "+o.toString());
	}
}

class ThreadedFileWriter implements Runnable {

	String path;
	String strToWrite;
	boolean append;

	public ThreadedFileWriter(String path, String strToWrite, boolean append) {
		this.path = path;
		this.strToWrite = strToWrite;
		this.append = append;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		printToNewFile(strToWrite, path, append);
	}

	public void printToNewFile(String str, String filePath, boolean append) {
		Utilities.createFile(filePath, false);
		if (filePath.length() > 0) {
			Utilities.writeToFile(str, filePath, append);
		} else {
			System.out.println("You can't write to a file that hasn't been specified.");
		}
	}
}