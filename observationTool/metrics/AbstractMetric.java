package observationTool.metrics;

import java.util.HashMap;

import experimentExecution.MetricInfo;
import stdSimLib.Parameter;

public abstract class AbstractMetric {

	MetricResults metricResults;
	MetricInfo metricInfo;
	HashMap<String, Parameter<?>> currentCalculationParameters;
	
	public <T> void addMetricResults(String name, T result){
		metricResults.addMetricResult(name, result);
	}
	
	public abstract void setup(HashMap<String, Parameter<?>> params);
	
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
	
}
