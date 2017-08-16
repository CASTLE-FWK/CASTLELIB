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
	
	
	public Logger(){
		
	}
	
	public void mute(){
		muted = true;
	}
	
	public void unmute(){
		muted = false;
	}
	
	public void setup(boolean isMuted, boolean toConsole, boolean toFile, String filePath){
		muted = isMuted;
		loggingToConsole = toConsole;
		loggingToFile = toFile;
		systemLogPath = filePath;
		
		if (loggingToFile){
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
			
		}
	}
	
	//Prints to file
	public void log(String str){
		if (!muted){
			if (loggingToFile){
				printToFile(str);
			}
			if (loggingToConsole){
				print(str);
			}
		}		
	}
	
	//Sets up the log path (should be fully automated)
	public void setUpLog(String str){
		systemLogPath = str;
		systemSpecPath = systemLogPath+"/systemInitialization.txt";
		systemLogDirPath = systemLogPath+"/systemLog.txt";
		systemOutputDirPath = systemLogPath+"/";
	}
	
	public void writeSystemSpecs(String sysName, String sysDescription, List<Parameter<?>> params){
		String out = "Simulation Initialization Details: \n";
		out += "Name: "+sysName+"\n";
		out += "Description: "+sysDescription+"\n";
		out += "Execution Start Time: "+Utilities.generateNiceTimeStamp()+"\n";
		out += "Initialization Parameters:\n";
		for (Parameter<?> p : params){
			out += "\t"+p.toString()+"\n";
		}
		out += "-----------------------------";
		if (loggingToConsole){
			print(out);
		}
		if (loggingToFile){
			Utilities.writeToFile(out, systemSpecPath);
		}
	}
}