package observationModule.metrics;

import observationModule.results.MetricResult;
import experiment.SystemInfo;

/**
 * REQUIREMENTS:
 * @author lachlan
 *
 */

public class ActiveHausdorff implements MetricInterface{

	public String metricName;
	
	public ActiveHausdorff() {
		// TODO Auto-generated constructor stub
		metricName = "ActiveHausdorff";
	}


	@Override
	public void runMetric(Object... params) {
		// TODO Auto-generated method stub
		
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
