package castleComponents;

import java.util.List;

import stdSimLib.Parameter;
import stdSimLib.utilities.Utilities;

public class Logger{

	private boolean loggingToFile;
	private boolean muted = true;
	private boolean loggingToConsole;
	
	//Output paths
	private String systemLogPath;
	private String systemSpecPath;
	private String systemLogDirPath;
	private String systemOutputDirPath;
	private String sysName;
	
	private StringBuilder stringBuilder;
	
	
	public Logger(){
		
	}
	
	public void mute(){
		muted = true;
	}
	
	public void unmute(){
		muted = false;
	}
	
	public void setup(boolean isMuted, boolean toConsole, boolean toFile, String filePath, String sysName){
		muted = isMuted;
		loggingToConsole = toConsole;
		loggingToFile = toFile;
		systemLogPath = filePath;
		this.sysName = sysName;
		
		if (loggingToFile){
			stringBuilder = new StringBuilder();
			setUpLog(systemLogPath);
		}
	}
	
	public void enableLoggingToFile(){
		loggingToFile = true;
	}
	
	public void disableLoggingToFile(){
		loggingToFile = false;
	}
	
	public void enableLoggingToConsole(){
		loggingToConsole = true;
	}
	public void disableLoggingToConsole(){
		loggingToConsole = false;
	}
	
	//Prints to terminal
	public void print(String str){
		System.out.println(str);
	}
	
	//Will always print to file, even if muted
	public void printToFile(String str){
		if (systemLogPath.length() > 0){
			Utilities.writeToFile(str, systemLogDirPath, true);
		} else {
			print("You can't write to a file that hasn't been specified.");
		}
	}
	
	public void newStep(int stepNumber){
		if (loggingToConsole){
			print("Step "+stepNumber);
		}
		if (loggingToFile){
			if (stringBuilder.length() != 0){
				printToFile(stringBuilder.toString());
			}
			stringBuilder = new StringBuilder();			
			stringBuilder.append("Step "+stepNumber+"\n");
		}
	}
	
	
	//This can overwrite things since the SYSTEM has power
	//TODO
	public void systemLog(String str){
		
	}
	
	
	//Prints to file
	public void log(String str){
		if (!muted){
			if (loggingToFile){
				stringBuilder.append(str+"\n");
			}
			if (loggingToConsole){
				print(str);
			}
		}		
	}
	
	//Sets up the log path (should be fully automated)
	public void setUpLog(String str){
		systemLogPath = str;
		systemOutputDirPath = systemLogPath+"/"+sysName+"-"+Utilities.generateTimeStamp();
		systemSpecPath = systemOutputDirPath+"/systemInitialization.txt";
		systemLogDirPath = systemOutputDirPath+"/systemLog.txt";
		
		
		if (loggingToFile){
			//Create directories and initial files
			System.out.println("Create directories and files for logging");
			System.out.println("Log directory at "+systemLogPath);
			Utilities.createFile(systemLogPath, true);
			Utilities.createFile(systemOutputDirPath, true);
			
			Utilities.createFile(systemSpecPath, false);
			
			Utilities.createFile(systemLogDirPath, false);			
		}
	}
	
	public void writeSystemSpecs(String sysName, String sysDescription, List<Parameter<?>> params) {
		String out = "Simulation Initialization Details: \n";
		out += "Name: "+sysName+"\n";
		out += "Description: "+sysDescription+"\n";
		out += "Execution Start Time: "+Utilities.generateNiceTimeStamp()+"\n";
		out += "Initialization Parameters:\n";
		for (Parameter<?> p : params){
			out += "\t"+p.toString()+"\n";
		}
		out += "----------------------------------------";
		if (loggingToConsole){
			print(out);
		}
		if (loggingToFile){
			Utilities.writeToFile(out, systemSpecPath, false);
		}
	}
}