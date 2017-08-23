package castleComponents;

public class SimulationInfo {

	String systemName = "";
	String description = "";
	String executionID = "";	
	
	public SimulationInfo(String sysName, String desc, String executionID){
		this.systemName = sysName;
		this.description = desc;
		this.executionID = executionID;
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
}
