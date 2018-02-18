package observationTool.metrics;

import java.util.ArrayList;

import observationTool.VEntity;

public class MetricBase {

	String metricName;
	String metricDescription;
	ArrayList<MetricRequirement> requirements;

	public MetricBase(String metricName) {
		this.metricName = metricName;
		requirements = new ArrayList<MetricRequirement>();
	}

	public enum MetricRequirement {
		AGENTS, INTERACTIONS, ENVIRONMENTS, GROUPS, AGENT, ENVIRONMENT, GROUP;
	}

	public boolean entityIsOfType(VEntity ve, String type) {
		return ve.getType().compareToIgnoreCase(type) == 0;
	}

	public boolean compareParameters(VEntity ve1, VEntity ve2, String paramName) {
		return (ve2.getParameterValueFromStringAsString(paramName)
				.compareToIgnoreCase(ve1.getParameterValueFromStringAsString(paramName)) == 0);
	}

}
