package experiment;

public class SystemInfo{
	String systemName;
	SystemConfiguration configuration;
	String systemDBID;
	int numberOfSteps;
	
	public SystemInfo(String systemName, String configName, String configDims, String systemDBID){
		this.systemName = systemName;
		this.configuration = new SystemConfiguration(configName, configDims);
		this.systemDBID = systemDBID;
	}
	
	//TrainingSystem
	public SystemInfo(String systemDBID){
		this.systemDBID = systemDBID;
	}

	public String getSystemName(){
		return systemName;
	}
	public String getConfigurationName(){
		return configuration.getConfigName();
	}
	public String getSystemDBID(){
		return systemDBID;
	}
	
	public String getConfigurationDimensions(){
		return configuration.getConfigDimensions();
	}
	
	public String getConfigurationString(){
		return configuration.toString();
	}

	public int getNumberOfSteps() {
		return numberOfSteps;
	}

	public void setNumberOfSteps(int numberOfSteps) {
		this.numberOfSteps = numberOfSteps;
	}
	
	@Override
	public String toString(){
		String str = "Name: "+systemName+"\n";
		str += "Configuration: "+configuration.toString()+"\n";
		str += "System DB ID: "+systemDBID;
		return str;
	}
}

class SystemConfiguration{
	String configName;
	String configDimensions;
	public SystemConfiguration(String name, String dims){
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
	public String toString(){
		return configName.concat(configDimensions);
	}
}
