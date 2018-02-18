package experimentExecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import observationTool.metrics.MetricParameters;

public class MetricInfo {
	String metricName;
	ArrayList<MetricParameters> metricParameters; // ???
	boolean needsTraining = false;
	ArrayList<SystemInfo> trainingSystems;
	HashMap<String, MetricVariableMapping> metricVariableMappings;

	public MetricInfo(String metricName, boolean needsTraining) {
		trainingSystems = new ArrayList<SystemInfo>();
		this.metricName = metricName;
		this.needsTraining = needsTraining;
		metricParameters = new ArrayList<MetricParameters>();
		metricVariableMappings = new HashMap<String, MetricVariableMapping>();
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
			dbIDS.add(si.getSystemDataLocation());
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

	public void addVariableMap(String metricVar, String targetEntity, String targetEntityVariableName) {
		metricVariableMappings.put(metricVar, new MetricVariableMapping(metricVar, targetEntity, targetEntityVariableName));
	}

	public HashMap<String, MetricVariableMapping> getMetricVariableMappings() {
		return metricVariableMappings;
	}
}