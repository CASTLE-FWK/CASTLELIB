package observationTool.metrics;

import java.util.HashMap;

import experimentExecution.MetricInfo;
import stdSimLib.Parameter;

public abstract class AbstractMetric {

	MetricResults metricResults;
	MetricInfo metricInfo;
	HashMap<String, Parameter<?>> currentCalculationParameters;
	boolean specialised = false;
	String specialisedSystems;
	
	/**
	 * Add in results and the variable they are to be stored in
	 * For example, if the algorithm returns two results, //TODO
	 * @param name
	 * @param result
	 */
	public <T> void addMetricResults(String name, T result){
		metricResults.addMetricResult(name, result);
	}
	/**
	 * Perform initializations that dependent on params here
	 * 
	 * @param params
	 */
	public abstract void setup(HashMap<String, Parameter<?>> params);
	
	public void notSpecialised(){
		setSpecialised(false);
	}
	
	public void isSpecialised(String systemNames){
		setSpecialised(true);
		setSpecialisedSystems(systemNames);
	}
	
	public void addParameter(Parameter<?> param){
		currentCalculationParameters.put(param.getName(), param);
	}
	
	public HashMap<String, Parameter<?>> prepareCalculation(Parameter<?>...parameters){
		for (Parameter<?> p : parameters){
			addParameter(p);
		}
		return currentCalculationParameters;
	};
	
	public abstract void calculateResults();
	
	public MetricResults getMetricResults(){
		return metricResults;
	}
	
	public MetricInfo getMetricInfo(){
		return metricInfo;
	}

	public boolean isSpecialised() {
		return specialised;
	}

	public void setSpecialised(boolean isSpecialised) {
		this.specialised = isSpecialised;
	}

	public String getSpecialisedSystems() {
		return specialisedSystems;
	}

	public void setSpecialisedSystems(String specialisedSystems) {
		this.specialisedSystems = specialisedSystems;
	}

	public void setMetricResults(MetricResults metricResults) {
		this.metricResults = metricResults;
	}

	public void setMetricInfo(MetricInfo metricInfo) {
		this.metricInfo = metricInfo;
	}
	
	public Object getParameter(String str){
		return currentCalculationParameters.get(str).getValue(); 
	}
	
	//Need to prepare the metric results map
	public <T> void addMetricResult(String name, T result){
		metricResults.addMetricResult(name, result);				
	}
	public <T> void updateMetricResult(String name, T result, int time){
		metricResults.updateMetricResult(name, result, time);
	}
	public <T> T getMetricResult(String name){
		return metricResults.getResult(name);
	}
}
