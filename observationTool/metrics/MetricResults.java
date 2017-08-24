package observationTool.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import observationTool.results.AccuracyResults;

public class MetricResults {

	HashMap<String, MetricResult<?>> results;
	private String systemName;
	private String metricName;
	private AccuracyResults theResult;

	public MetricResults(String sysName, String metricName) {
		results = new HashMap<String, MetricResult<?>>();
		this.systemName = sysName;
		this.metricName = metricName;
	}

	public <T> void addMetricResult(String name, T result) {
		results.put(name, new MetricResult<T>(name, result));
	}
	
	public <T> void updateMetricResult(String name, T result, int time){
		MetricResult<T> resultToUpdate = (MetricResult<T>) results.get(name);
		if (resultToUpdate == null){
			results.put(name, new MetricResult<T>(name, result, time));
		} else {
			resultToUpdate.update(result, time);
		}
	}
	
	public <T> T getResult(String name){
		MetricResult<T> resultToUpdate = (MetricResult<T>) results.get(name);
		if (resultToUpdate == null){
			return null;
		} else {
			return resultToUpdate.getMetricResult();
		}
	}

	public MetricResult<?> getMetricResult(String name) {
		return results.get(name);
	}

	public String getMetricResultAsString(String name) {
		return getMetricResult(name).toString();
	}

	public ArrayList<String> getAllMetricResultsAsStringList() {
		ArrayList<String> metricResults = new ArrayList<String>();
		Iterator<Entry<String, MetricResult<?>>> it = results.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, MetricResult<?>> pair = (Map.Entry<String, MetricResult<?>>) it.next();
			metricResults.add(pair.getValue().toString());
		}
		return metricResults;
	}

	public String getAllMetricResultsAsString() {
		String out = "";
		Iterator<Entry<String, MetricResult<?>>> it = results.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, MetricResult<?>> pair = (Map.Entry<String, MetricResult<?>>) it.next();
			out += (pair.getValue().toString()) + "\n";
		}
		return out;
	}
}

class MetricResult<T> {
	String resultName;
	T metricResult;
	int timeOfResult = -1;

	public MetricResult(String rn, T mr, int t) {
		this.resultName = rn;
		this.metricResult = mr;
		this.timeOfResult = t;
	}
	
	public void update(T result, int time) {
		this.metricResult = result;
		this.timeOfResult = time;
		
	}

	public MetricResult(String rn, T mr) {
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

	public void setTimeOfResult(int t) {
		this.timeOfResult = t;
	}

	public int getTimeOfResult() {
		return timeOfResult;
	}

	@Override
	public String toString() {
		return resultName + "," + metricResult.toString();
	}

}
