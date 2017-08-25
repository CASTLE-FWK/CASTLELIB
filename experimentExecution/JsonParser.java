package experimentExecution;

import com.eclipsesource.json.*;
import com.eclipsesource.json.JsonObject.Member;

import observationTool.metrics.MetricParameters;

import java.io.FileReader;
import java.util.ArrayList;

public class JsonParser {

	static String test = "test.json";

	public static void main(String[] args) {
	}

	public static Experiment parseExperiment(String filePath) {
		try {
			JsonObject object = Json.parse((new FileReader(filePath))).asObject();
			Experiment anExperiment = new Experiment(object.get("Experiment-id").asString(),
					object.get("Description").asString());

			JsonArray theSystems = object.get("Test-systems").asArray();

			for (int i = 0; i < theSystems.size(); i++) {
				JsonObject obj = theSystems.get(i).asObject();
				anExperiment.addTestSystem(new SystemInfo(obj.get("System-name").asString(),
						obj.get("Configuration").asObject().get("Configuration-name").asString(),
						obj.get("Configuration").asObject().get("Dimensions").asString(),
						obj.get("System-db-id").asString()));
			}
			anExperiment.addMetricInfos(parseMetricInfoForExperiment(object.get("Metrics-to-use").asArray()));

			return anExperiment;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ArrayList<MetricInfo> parseMetricInfoForExperiment(JsonArray arr) {
		ArrayList<MetricInfo> metricInfos = new ArrayList<MetricInfo>();
		for (int i = 0; i < arr.size(); i++) {
			JsonObject MetricInfoInfo = arr.get(i).asObject();
			MetricInfo mi = new MetricInfo(MetricInfoInfo.get("Metric-name").asString(),
					MetricInfoInfo.get("Is-trained").asBoolean());
			mi.addTrainingSystems(parseTrainingSets(MetricInfoInfo.get("Training-sets").asArray()));

			// Parse Metric parameters
			JsonArray params = MetricInfoInfo.get("Metric-parameter-values").asArray();
			MetricParameters mp = null;
			for (int j = 0; j < params.size(); j++) {
				mp = new MetricParameters();
				JsonObject item = params.get(j).asObject();
				for (Member member : item) {
					JsonValue itemVal = member.getValue();
					if (itemVal.isBoolean()) {
						mp.addParameter(member.getName(), member.getValue().asBoolean());
					} else if (itemVal.isNumber()) {
						mp.addParameter(member.getName(), member.getValue().asDouble());
					} else if (itemVal.isString()) {
						mp.addParameter(member.getName(), member.getValue().asString());
					} else {
						mp.addParameter(member.getName(), member.getValue());
					}
				}
				mi.addMetricParameters(mp);
			}
			parseMetricMappings(MetricInfoInfo.get("Metric-variable-mappings").asArray(), mi);
			metricInfos.add(mi);
		}
		return metricInfos;
	}

	public static ArrayList<SystemInfo> parseTrainingSets(JsonArray arr) {
		ArrayList<SystemInfo> systems = new ArrayList<SystemInfo>();
		for (int i = 0; i < arr.size(); i++) {
			JsonObject MetricInfoInfo = arr.get(i).asObject();
			systems.add(new SystemInfo(MetricInfoInfo.get("System-db-id").asString()));
		}
		return systems;
	}
	
	public static void parseMetricMappings(JsonArray arr, MetricInfo mi){
		for (int i = 0; i < arr.size(); i++){
			JsonObject item = arr.get(i).asObject();
			mi.addVariableMap(item.get("metric-variable").asString(), item.get("entity-type").asString(), item.get("entity-variable").asString());
		}
	}
}

// class SystemInfo{
// String systemName;
// String configuration;
// String systemDBID;
// public SystemInfo(String systemName, String configuration, String
// systemDBID){
// this.systemName = systemName;
// this.configuration = configuration;
// this.systemDBID = systemDBID;
// }
// }

// class Experiment{
// String experimentID;
// SystemInfo theSystem;
// String description;
// ArrayList<MetricInfo> MetricInfosUsed;

// public Experiment(String experimentID, String description, SystemInfo
// theSystem){
// MetricInfosUsed = new ArrayList<MetricInfo>();
// this.experimentID = experimentID;
// this.description = description;
// this.theSystem = theSystem;
// }

// public void addMetricInfos(ArrayList<MetricInfo> MetricInfos){
// MetricInfosUsed = MetricInfos;
// }

// public void addMetricInfo(MetricInfo m){
// MetricInfosUsed.add(m);
// }
// }

// class MetricInfo{
// String MetricInfoName;
// String MetricInfoParameters; //???
// boolean needsTraining = false;
// ArrayList<SystemInfo> trainingSystems;

// public MetricInfo(String MetricInfoName, String MetricInfoParameters, boolean
// needsTraining){
// trainingSystems = new ArrayList<SystemInfo>();
// this.MetricInfoName = MetricInfoName;
// this.MetricInfoParameters = MetricInfoParameters;
// this.needsTraining = needsTraining;
// }

// public void addTrainingSystems(ArrayList<SystemInfo> ts){
// trainingSystems = ts;
// }
// }