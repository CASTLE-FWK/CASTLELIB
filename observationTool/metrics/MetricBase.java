package observationTool.metrics;

import java.util.ArrayList;
import java.util.HashMap;

import experimentExecution.MetricInfo;
import experimentExecution.MetricVariableMapping;
import observationTool.VEntity;

public class MetricBase {

	protected String metricName;
	protected String metricDescription;
	protected ArrayList<MetricRequirement> requirements;
	protected MetricInfo mi;
	protected HashMap<String, MetricVariableMapping> metricVariableMappings;

	public MetricBase(String metricName, MetricInfo mi) {
		this.metricName = metricName;
		this.mi = mi;
		metricVariableMappings = this.mi.getMetricVariableMappings();
		requirements = new ArrayList<MetricRequirement>();
	}

	public enum MetricRequirement {
		AGENTS, INTERACTIONS, ENVIRONMENTS, GROUPS, AGENT, ENVIRONMENT, GROUP;
	}

	// Useful functions
	public boolean entityIsOfType(VEntity ve, String type) {
		return ve.getType().compareToIgnoreCase(type) == 0;
	}

	public boolean compareParameters(VEntity ve1, VEntity ve2, String paramName) {
		return (ve2.getParameterValueFromStringAsString(paramName)
				.compareToIgnoreCase(ve1.getParameterValueFromStringAsString(paramName)) == 0);
	}

	public boolean isParameterEqualToValue(VEntity v, String parameterName, String desiredValue) {
		if (v.containsParameter(parameterName)) {
			return v.getParameterValueFromStringAsString(parameterName).compareToIgnoreCase(desiredValue) == 0;
		} else {
			return false;
		}
		
	}

	
	
	//These need the requiste changes done in the Json, JsonParser, and MetricRunner
	public boolean entityIsOfType(VEntity ve, String[] types) {
		boolean is = false;
		for (String s : types) {
			is = entityIsOfType(ve, s);
		}
		return is;
	}
}
