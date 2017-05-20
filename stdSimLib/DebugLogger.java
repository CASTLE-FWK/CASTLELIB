package stdSimLib;

public class DebugLogger {
	
	private boolean logging;
	private boolean muted;
	private String logPath;
	
	public DebugLogger(){}
	
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
	
	//Prints to file
	public void log(String str){
		if (!muted){
			
		}
		
	}
	
	//Sets up the log path (should be fully automated)
	public void setUpLog(String str){
		
	}

}
