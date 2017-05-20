package observationTool.metrics;

import java.util.ArrayList;



public class MetricBase {
	
	String metricName;
	String metricDescription;
	ArrayList<MetricRequirement> requirements;
	
	
	public MetricBase(String metricName){
		this.metricName = metricName;
		requirements = new ArrayList<MetricRequirement>();
	}
	
	public enum MetricRequirement {
		AGENTS, INTERACTIONS, ENVIRONMENTS, GROUPS, AGENT, ENVIRONMENT, GROUP;
	}

}
