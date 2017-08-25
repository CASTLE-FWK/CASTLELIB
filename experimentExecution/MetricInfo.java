package experimentExecution;

import java.util.ArrayList;
import java.util.HashSet;

import observationTool.metrics.MetricParameters;

public class MetricInfo {
	String metricName;
	ArrayList<MetricParameters> metricParameters; // ???
	boolean needsTraining = false;
	ArrayList<SystemInfo> trainingSystems;
	HashSet<MetricVariableMap> metricVariableMappings;

	public MetricInfo(String metricName, boolean needsTraining) {
		trainingSystems = new ArrayList<SystemInfo>();
		this.metricName = metricName;
		this.needsTraining = needsTraining;
		metricParameters = new ArrayList<MetricParameters>();
		metricVariableMappings = new HashSet<MetricVariableMap>();
	}

	public void addTrainingSystems(ArrayList<SystemInfo> ts) {
		trainingSystems = ts;
	}

	public String getMetricName() {
		return metricName;
	}

	public ArrayList<MetricParameters> getMetricParameters() {
		return metricParameters;
	}

	public ArrayList<SystemInfo> getTrainingSystems() {
		return trainingSystems;
	}

	public boolean needsTraining() {
		return needsTraining;
	}

	public ArrayList<String> getTrainingSystemsDBIDS() {
		ArrayList<String> dbIDS = new ArrayList<String>();
		for (SystemInfo si : trainingSystems) {
			dbIDS.add(si.getSystemDBID());
		}

		return dbIDS;
	}

	public void addMetricParameters(MetricParameters mp) {
		metricParameters.add(mp);
	}

	@Override
	public String toString() {
		String str = metricName + " Information:\n";
		str += "Parameter Sets:\n" + parametersToString();
		str += "Number of training systems: " + trainingSystems.size();
		return str;
	}

	public String parametersToString() {
		String str = "";
		for (int i = 0; i < metricParameters.size(); i++) {
			str += "Parameter Set " + i + ": {";
			str += metricParameters.get(i).toString();
			str += "}\n";
		}
		return str;
	}
	
	public void addVariableMap(String metricVar, String targetEntity, String targetEntityVariableName){
		metricVariableMappings.add(new MetricVariableMap(metricVar, targetEntity, targetEntityVariableName));
	}
	
	
}

class MetricVariableMap{
	String metricVar;
	String targetEntity;
	String targetEntityVariableName;
	public MetricVariableMap(String mv, String te, String tevn){
		this.metricVar = mv;
		this.targetEntity = te;
		this.targetEntityVariableName = tevn;
	}
	public String getMetricVar() {
		return metricVar;
	}
	public void setMetricVar(String metricVar) {
		this.metricVar = metricVar;
	}
	public String getTargetEntity() {
		return targetEntity;
	}
	public void setTargetEntity(String targetEntity) {
		this.targetEntity = targetEntity;
	}
	public String getTargetEntityVariableName() {
		return targetEntityVariableName;
	}
	public void setTargetEntityVariableName(String targetEntityVariableName) {
		this.targetEntityVariableName = targetEntityVariableName;
	}
}