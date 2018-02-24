package observationTool.metrics;

import java.util.ArrayList;

import experimentExecution.MetricInfo;
import experimentExecution.SystemInfo;
import observationTool.DataCollector_FileSystem;
import observationTool.results.MetricResult;

/**
 * DESCRIPTION: Detects communities based on the ...
 * 
 * 
 * LOGIC: 1: Take list of Agents and Interactions, and build an AdjacencyMatrix
 * 2: 3:
 * 
 * REQUIREMENTS: • List of Agents • Interaction Graph
 * 
 * 
 * @author lachlan
 *
 */
public class CommunityDetectionInfoMod extends MetricBase implements MetricInterface {

	double finalResult;

	public CommunityDetectionInfoMod(MetricInfo mi) {
		super("CommunityDetection", mi);
		// TODO Auto-generated constructor stub
	}

	public Object metricResults() {
		// TODO Auto-generated method stub
		return finalResult;
	}

	public void runMetric(Object... params) {
		// TODO Auto-generated method stub
		int reqCount = 0;
		for (Object obj : params) {
			if (obj instanceof ArrayList<?>) {
				reqCount++;
			}
		}

		if (reqCount != params.length) {
			System.out.println(metricName + " will not run. Requirements not met.");
			finalResult = -Double.MAX_VALUE;
			return;
		}
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
