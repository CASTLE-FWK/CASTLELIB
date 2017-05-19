package interLib;


public interface InteractionServerRemote extends java.rmi.Remote{
	
	public int newExecution(String systemName, String executionName) throws java.rmi.RemoteException;
	
	public void receiveSnapshot(Snapshot snapshot, int currentTime) throws java.rmi.RemoteException;
	
	public void receiveSystemDescription(SystemDescription sysDec) throws java.rmi.RemoteException;
	
	public String checkServerLife() throws java.rmi.RemoteException;
	
	public String informResult() throws java.rmi.RemoteException;
	
	

}
