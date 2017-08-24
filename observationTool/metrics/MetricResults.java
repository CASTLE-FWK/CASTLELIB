package observationTool.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class MetricResults {

	HashMap<String, MetricResult<?>> results;
	
	public MetricResults(){
		results = new HashMap<String, MetricResult<?>>();
	}
	
	public <T> void addMetricResult(String name, T result){
		results.put(name, new MetricResult<T>(name, result));
	}
	
	public MetricResult<?> getMetricResult(String name){
		return results.get(name);
	}
	
	public String getMetricResultAsString(String name){
		return getMetricResult(name).toString();
	}
	
	public ArrayList<String> getAllMetricResultsAsStringList(){
		ArrayList<String> metricResults = new ArrayList<String>();
		Iterator<Entry<String, MetricResult<?>>> it = results.entrySet().iterator();
		while (it.hasNext()){
			Map.Entry<String, MetricResult<?>> pair = (Map.Entry<String, MetricResult<?>>)it.next();
			metricResults.add(pair.getValue().toString());
		}
		return metricResults;
	}
	
	public String getAllMetricResultsAsString(){
		String out = "";
		Iterator<Entry<String, MetricResult<?>>> it = results.entrySet().iterator();
		while (it.hasNext()){
			Map.Entry<String, MetricResult<?>> pair = (Map.Entry<String, MetricResult<?>>)it.next();
			out += (pair.getValue().toString()) + "\n";
		}
		return out;
	}
}

class MetricResult<T>{
	String resultName;
	T metricResult;
	public MetricResult(String rn, T mr){
		this.resultName = rn;
		this.metricResult = mr;
	}
	public String getResultName() {
		return resultName;
	}
	public void setResultName(String resultName) {
		this.resultName = resultName;
	}
	public T getMetricResult() {
		return metricResult;
	}
	public void setMetricResult(T metricResult) {
		this.metricResult = metricResult;
	}
	
	@Override
	public String toString(){
		return resultName+","+metricResult.toString();
	}
	
}
