package observationTool.metrics;

import java.util.ArrayList;

import experimentExecution.MetricInfo;
import experimentExecution.SystemInfo;
import observationTool.DataCollector_FileSystem;
import observationTool.results.MetricResult;

/**
 * DESCRIPTION: Detects communities based on the concept of synchronization
 * Probably could use some of this logic for other community methods, but not
 * sure just yet
 * 
 * LOGIC: 1: Take list of Agents and build an Interaction graph 2: Detect hubs
 * (Nodes with the greatest interaction count) 3: Build 2 Adjacency Lists: 1
 * with the Hubs, 1 as normal (Build this into the IG class) 4:
 * 
 * REQUIREMENTS: • List of Agents • Interaction Graph
 * 
 * 
 * @author lachlan
 *
 */
public class CommunityDetection extends MetricBase implements MetricInterface {

	double finalResult;

	public CommunityDetection(MetricInfo mi) {
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
