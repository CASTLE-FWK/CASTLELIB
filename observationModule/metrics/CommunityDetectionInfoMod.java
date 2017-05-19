package observationModule.metrics;

import java.util.ArrayList;

import observationModule.results.MetricResult;
import experiment.SystemInfo;

/**
 * DESCRIPTION:
 * 	Detects communities based on the ...
 * 	
 * 
 * LOGIC:
 * 	1: Take list of Agents and Interactions, and build an AdjacencyMatrix
 *  2: 
 *  3: 
 * 
 * REQUIREMENTS:
 *	• List of Agents
 *	• Interaction Graph
 * 
 * 
 * @author lachlan
 *
 */
public class CommunityDetectionInfoMod extends MetricBase implements MetricInterface {

	double finalResult;
	
	public CommunityDetectionInfoMod() {
		super("CommunityDetection");
		// TODO Auto-generated constructor stub
	}

	public Object metricResults() {
		// TODO Auto-generated method stub
		return finalResult;
	}

	@Override
	public void runMetric(Object... params) {
		// TODO Auto-generated method stub
		int reqCount = 0;
		for (Object obj : params){
			if (obj instanceof ArrayList<?>){
				reqCount++;
			}
		}
		
		if (reqCount != params.length){
			System.out.println(metricName+" will not run. Requirements not met.");
			finalResult = -Double.MAX_VALUE;
			return;
		}
	}

	@Override
	public String getMetricInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetricResult getMetricResults() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void runMetric(SystemInfo si) {
		// TODO Auto-generated method stub
		
	}

}
