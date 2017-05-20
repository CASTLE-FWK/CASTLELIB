package castleComponents;

import stdSimLib.Utilities;

public class Logger{

	private boolean logging;
	private boolean muted = true;
	
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
	
	public void enableLogWrite(){
		logging = true;
	}
	
	public void disableLogWrite(){
		logging = false;
	}
	
	//Prints to terminal
	public void print(String str){
		if (!muted)
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
			
		}
		
	}
	
	//Sets up the log path (should be fully automated)
	public void setUpLog(String str){
		systemLogPath = str;
		systemSpecPath = systemLogPath+"/systemSpec.txt";
		systemLogDirPath = systemLogPath+"/logs";
		systemOutputDirPath = systemLogPath+"/output";
	}
	
	public void systemSpecs(String str){
		Utilities.writeToFile(str, systemSpecPath);
	}

}