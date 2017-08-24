package experimentExecution;

import java.util.ArrayList;

import com.eclipsesource.json.JsonValue;

import observationTool.metrics.MetricParameters;

public class MetricInfo {
	String metricName;
	ArrayList<MetricParameters> metricParameters; // ???
	boolean needsTraining = false;
	ArrayList<SystemInfo> trainingSystems;

	public MetricInfo(String metricName, boolean needsTraining) {
		trainingSystems = new ArrayList<SystemInfo>();
		this.metricName = metricName;
		this.needsTraining = needsTraining;
		metricParameters = new ArrayList<MetricParameters>();
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
}