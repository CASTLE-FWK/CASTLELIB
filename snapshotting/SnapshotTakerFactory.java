package snapshotting;

import stdSimLib.SystemDescription;

/**
 * This creates a Snapshotter for a particular InteractionServer set up.
 * Sets up all the ugly stuff like dates and connections.
 * 
 *  
 * @author lollbirdsey
 *
 */

public class SnapshotTakerFactory {

	static String interactionServerPath;
	static int interactionServerPort;
	static SystemDescription systemDescription;
	
	//TODO: Create special NOT ACTUAL SEND MODE
	public static final String NO_SERVER = "NO_SERVER";
	
	/**
	 * Connect to server, check life status, other init variables
	 * @param interactionServerConnection
	 * @return
	 */
	
	//TODO: UN HARD CODE
	public static String rmiConnection;
	public static boolean setupSnapshotterFactory(String iSPath, int iSPort,  String iSFpath, SystemDescription sysDec){
		interactionServerPath = iSPath;
		interactionServerPort = iSPort;
		systemDescription = sysDec;
		
		rmiConnection = "rmi://" + iSPath + ":" + iSPort + "/InteractionServer";
		
		
		return true;
	}
	
	public static SnapshotTaker createSnapshotter(String systemName, int snapshotIntervals, int totalTimeSteps, String experimentName, String runName){
		SnapshotTaker snapShotter = new SnapshotTaker(systemName, snapshotIntervals, totalTimeSteps, experimentName, runName);
		snapShotter.setServer(rmiConnection);
		
		
		return snapShotter;
	}
	
	public static SnapshotTaker createSnapshotter(String systemName, int snapshotIntervals, int totalTimeSteps, String executionName, String experimentName, String runName){
		SnapshotTaker snapShotter = new SnapshotTaker(systemName, snapshotIntervals, totalTimeSteps, experimentName, runName);
		snapShotter.setExecutionName(executionName);
		snapShotter.setServer(rmiConnection);
		
		
		return snapShotter;
	}
	
	
	public static String generateTimeStamp(){
		String ts = "";
		
		
		return ts;
		
	}
	
	public boolean checkServer(int timeOut){
		
		return true;
	}
	
}
