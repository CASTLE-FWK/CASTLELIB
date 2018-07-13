package experimentExecution;

import java.util.HashMap;

public class SystemInfo {
	String systemName;
	SystemConfiguration configuration;
	String systemLocation;
	int numberOfSteps;
	
	HashMap<String, String> initParams;
	
	boolean fromDB = true;

	public SystemInfo(String systemName, String configName, String configDims, String systemDBID, String locationType) {
		this.systemName = systemName;
		this.configuration = new SystemConfiguration(configName, configDims);
		this.systemLocation = systemDBID;
		fromDB = (locationType.compareToIgnoreCase("file") != 0);
		initParams = new HashMap<String, String>();
	}
	
	public void setInitParams(HashMap<String, String> ip) {
		for (String s : ip.keySet()) {
			initParams.put(s, ip.get(s));
		}
	}
	
	public String getParamFromName(String st) {
		return initParams.get(st);
	}
	
	public HashMap<String, String> getInitParams(){
		return initParams;
	}

	// TrainingSystem
	public SystemInfo(String systemDBID) {
		this.systemLocation = systemDBID;
	}

	public String getSystemName() {
		return systemName;
	}

	public String getConfigurationName() {
		return configuration.getConfigName();
	}

	public String getSystemDataLocation() {
		return systemLocation;
	}

	public String getConfigurationDimensions() {
		return configuration.getConfigDimensions();
	}

	public String getConfigurationString() {
		return configuration.toString();
	}

	public int getNumberOfSteps() {
		return numberOfSteps;
	}

	public void setNumberOfSteps(int numberOfSteps) {
		this.numberOfSteps = numberOfSteps;
	}

	@Override
	public String toString() {
		String str = "Name: " + systemName + "\n";
		str += "Configuration: " + configuration.toString() + "\n";
		str += "System DB ID: " + systemLocation;
		return str;
	}

	public boolean isFromDB() {
		return fromDB;
	}
}

class SystemConfiguration {
	String configName;
	String configDimensions;

	public SystemConfiguration(String name, String dims) {
		configName = name;
		configDimensions = dims;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getConfigDimensions() {
		return configDimensions;
	}

	public void setConfigDimensions(String configDimensions) {
		this.configDimensions = configDimensions;
	}

	@Override
	public String toString() {
		return configName.concat(configDimensions);
	}
}
