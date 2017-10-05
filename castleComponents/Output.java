package castleComponents;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import dataGenerator.OutputToJSON_Mongo;
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
	
	MongoCollection<Document> currentCollection;

	MongoClient mongoClient;
	MongoDatabase db;

	public Output() {}

	public Output(boolean ltf, boolean ltc, boolean ltd) {
		loggingToFile = ltf;
		loggingToConsole = ltc;
		loggingToDB = ltd;
	}

	public void initialiseLoggingPath(String str, boolean isDir) {
		Utilities.createFile(str, isDir);
	}

	public void sendLogToFile(String filePath, String log, boolean append) {
		if (loggingToFile) {
			new ThreadedFileWriter(filePath, log, append).run();
		}
	}

	public void sendLogToDB(String log) {

	}

	public void setUpDB(String systemName, String executionID, String databaseName) {
		this.executionID = executionID;
		DBName = systemName;
		currentPath = URL + DBName;
//		this.dbID = dbID;

		mongoClient = new MongoClient();
		db = mongoClient.getDatabase(databaseName);
		DBName = DBName + "_" + executionID;
		currentCollection = getCurrentCollectionFromDB(DBName);
		System.out.println("MongoDB collection is at " + DBName);
	}

	public MongoCollection<Document> getCurrentCollectionFromDB(String name) {
		return this.db.getCollection(name);
	}

	public void insertOneToDB(Document doc) {
		currentCollection.insertOne(doc);
	}

	public void sendLogToConsole(String log) {
		System.out.println(log);
	}
	
	public void newStep(){
		
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