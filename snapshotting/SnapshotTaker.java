package snapshotting;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import stdSimLib.Agent;
import stdSimLib.Interaction;
import stdSimLib.InteractionServerRemote;
import stdSimLib.MetaModel;
import stdSimLib.Snapshot;
import stdSimLib.SystemDescription;
import stdSimLib.utilities.Utilities;

public class SnapshotTaker {
	
	String systemName;
	int snapshotIntervals;
	int totalTimeSteps;
	String interactionServerConnection;
	boolean debugMode = false;
	MetaModel metaModel;
	String executionName = "";
	String experimentName = "";
	String runName = "";
		
	InteractionServerRemote interactionServer;

	public SnapshotTaker(String systemName, int snapshotIntervals, int totalTimeSteps, String experimentName, String runName){
		this.systemName = systemName;
		this.snapshotIntervals = snapshotIntervals;
		this.totalTimeSteps = totalTimeSteps;
		metaModel = new MetaModel();
		this.experimentName = experimentName;
		this.runName = runName;
		
	}
	
	/**
	 * 
	 * This is called from the simulation and sends a list of current Agents and all
	 * the interactions that have occurred since the last snapshot. In then packages them
	 * into a Snapshot class and sends it to the InteractionServer where processing will occur.
	 * 
	 * @param agents The agents in the system at the time of the snapshot
	 * @param data The interactions that have occurred since the last snapshot
	 */
	public void takeSnapshot(ArrayList<Agent> agents, ArrayList<Interaction> data, double currentTimeStep){
		//System.out.println("agentSize/interactionSize "+agents.size()+"/"+data.size());
		
		sendSnapshotToServer(new Snapshot(generateID(),agents,data,currentTimeStep, snapshotIntervals, experimentName, runName));
		
	}
	
	public void setServer(String serverConnection){
		interactionServerConnection = serverConnection;
		if (executionName.length() <= 0 || executionName.endsWith("/")){
			executionName = Utilities.generateTimeStamp();
		}
		
		try {			
			interactionServer = (InteractionServerRemote)java.rmi.Naming.lookup(interactionServerConnection);
			System.out.println(interactionServer.checkServerLife());
			interactionServer.newExecution(systemName, executionName);									
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean checkServer(){
		
		return true;
	}
	
	public void toggleDebugMode(boolean dm){
		debugMode = dm;
	}
	
	public boolean sendSnapshotToServer(Snapshot toSend){
		try {
//			String toServer = metaModel.buildMetaModel(toSend.getAgents(), toSend.getInteractions(), toSend.getCurrentTime(), toSend.getSnapshotInterval());
			interactionServer.receiveSnapshot(toSend, toSend.getCurrentTime());
		} catch (RemoteException e) {		
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean sendSystemDescriptionToServer(SystemDescription sysDec){
		try {
//			String toServer = metaModel.buildMetaModel(toSend.getAgents(), toSend.getInteractions(), toSend.getCurrentTime(), toSend.getSnapshotInterval());
			interactionServer.receiveSystemDescription(sysDec);
		} catch (RemoteException e) {			
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public String generateID(){
		
		return "IID";
	}
	
	//TODO: Hook in to a UI or something
	/**
	 * Gets a result from the server if previous snapshot was showing emergence or really far fro emergence
	 * @param str
	 */
	public void result(String str){
		
	}

	/**
	 * @return the executionName
	 */
	public String getExecutionName() {
		return executionName;
	}

	/**
	 * @param executionName the executionName to set
	 */
	public void setExecutionName(String executionName) {
		this.executionName = executionName;
	}

}
