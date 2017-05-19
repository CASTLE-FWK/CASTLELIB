package observationModule.metrics;

import observationModule.results.MetricResult;
import experiment.MetricInfo;
import experiment.SystemInfo;

public interface MetricInterface {
	
	public String getMetricInformation();
	public MetricResult getMetricResults();
	public void runMetric(SystemInfo si);
	public void runMetric(Object... blah);
}
