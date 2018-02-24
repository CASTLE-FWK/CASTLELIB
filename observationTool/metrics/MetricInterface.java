package observationTool.metrics;

import observationTool.results.MetricResult;

public interface MetricInterface {

	public void run();
	public MetricResult[] getResults();
}
