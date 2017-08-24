package observationTool.metrics;

import experimentExecution.MetricInfo;

public abstract class AbstractMetric {

	MetricResults metricResults;
	MetricInfo metricInfo;
	
	public <T> void addMetricResults(String name, T result){
		metricResults.addMetricResult(name, result);
	}
	
	public abstract void setup(Object... params);
	public abstract void calculateResults(Object... params);
	
	public MetricResults getMetricResults(){
		return metricResults;
	}
	
	public MetricInfo getMetricInfo(){
		return metricInfo;
	}
	
}
