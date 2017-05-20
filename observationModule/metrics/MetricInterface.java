package observationModule.metrics;

import experimentExecution.MetricInfo;
import experimentExecution.SystemInfo;
import observationModule.results.MetricResult;

public interface MetricInterface {
	
	public String getMetricInformation();
	public MetricResult getMetricResults();
	public void runMetric(SystemInfo si);
	public void runMetric(Object... blah);
}
