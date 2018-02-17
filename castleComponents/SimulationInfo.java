package castleComponents;

public class SimulationInfo {

	String systemName = "";
	String description = "";
	String executionID = "";
	String timeStamp = "";
	
	public SimulationInfo(String sysName, String desc, String timeStamp){
		this.systemName = sysName;
		this.description = desc;
		this.timeStamp = timeStamp;
		this.executionID = this.systemName+"-"+this.timeStamp;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExecutionID() {
		return executionID;
	}

	public void setExecutionID(String executionID) {
		this.executionID = executionID;
	}

	public String getTimeStamp() {
		return timeStamp;
	}
}
