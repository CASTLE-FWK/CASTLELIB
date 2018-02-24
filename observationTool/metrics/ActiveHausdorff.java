package observationTool.metrics;

import experimentExecution.SystemInfo;
import observationTool.DataCollector_FileSystem;
import observationTool.results.MetricResult;

/**
 * REQUIREMENTS:
 * 
 * @author lachlan
 *
 */

public class ActiveHausdorff implements MetricInterface {

	public String metricName;

	public ActiveHausdorff() {
		// TODO Auto-generated constructor stub
		metricName = "ActiveHausdorff";
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MetricResult getResults() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCollector(DataCollector_FileSystem dfs) {
		// TODO Auto-generated method stub
		
	}

}
