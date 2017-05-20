package experimentExecution;

import java.util.ArrayList;

public class Experiment{
	String experimentID;
	ArrayList<SystemInfo> theTestSystems;
	String description;
	ArrayList<MetricInfo> metricsUsed;

	public Experiment(String experimentID, String description){
		metricsUsed = new ArrayList<MetricInfo>();
		this.experimentID = experimentID;
		this.description = description;
		theTestSystems = new ArrayList<SystemInfo>();
	}

	public void addMetricInfos(ArrayList<MetricInfo> MetricInfos){
		metricsUsed = MetricInfos;
	}

	public void addMetricInfo(MetricInfo m){
		metricsUsed.add(m);
	}
	
	public ArrayList<SystemInfo> getTestSystems(){
		return theTestSystems;
	}
	
	public int numberOfTestSystems(){
		return theTestSystems.size();
	}
	
	public void addTestSystem(SystemInfo si){
		theTestSystems.add(si);
	}
	
	public ArrayList<MetricInfo> getMetrics(){
		return metricsUsed;
	}

	public String getExperimentID(){
		return experimentID;
	}
	
	public String experimentInfo(){
		String str = "TODO";
		
		return str;
	}
	
	public String toString(){
		String str = "Experiment: "+experimentID+"\n";
		str += "Description: "+description;
		str += "Testing Systems: ";
		for (int i = 0; i < theTestSystems.size(); i++){
			str += theTestSystems.get(i).toString()+"\n";
		}
		str += "Metrics: \n";
		for (int i = 0; i < metricsUsed.size(); i++){
			str += metricsUsed.get(i).toString()+"\n";
		}
		
		return str;
	}
}