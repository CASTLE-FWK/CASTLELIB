package observationTool.metrics;

import observationTool.DataCollector_FileSystem;
import observationTool.results.MetricResult;

public interface MetricInterface {

	public void run();

	public MetricResult getResults();

	// This will become generic at some point
	public void setCollector(DataCollector_FileSystem dfs);
}
