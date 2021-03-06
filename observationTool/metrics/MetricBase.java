package observationTool.metrics;

import java.util.ArrayList;
import java.util.HashMap;

import experimentExecution.MetricInfo;
import experimentExecution.MetricVariableMapping;
import experimentExecution.TypeMap;
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
	public boolean entityIsOfType(VEntity ve, MetricVariableMapping mvm) {
		return mvm.entityIsOfType(ve);
	}

	public boolean compareParameters(VEntity ve1, VEntity ve2, MetricVariableMapping mvm) {
		return mvm.compareParameters(ve1, ve2);
	}

	public boolean isParameterEqualToDesiredValue(VEntity v, MetricVariableMapping mvm) {
		return mvm.isParameterEqualToDesiredValue(v);
	}

	public String getParameter(VEntity v, String s) {
		return v.getParameterValueFromStringAsString(s);
	}

	public HashMap<String, String> getAllParameters(VEntity v, MetricVariableMapping mvm) {
		ArrayList<TypeMap> types = mvm.getSpecificTypeMap(v.getType());
		HashMap<String, String> vals = new HashMap<String, String>();

		for (TypeMap t : types) {
			vals.put(t.getTargetEntityVariableName(), getParameter(v, t.getTargetEntityVariableName()));
		}

		return vals;
	}

	public ArrayList<String> getAllParameterNames(VEntity v, MetricVariableMapping mvm) {
		ArrayList<TypeMap> types = mvm.getSpecificTypeMap(v.getType());
		ArrayList<String> typeNames = new ArrayList<String>();
		for (TypeMap t : types) {
			typeNames.add(t.getTargetEntityVariableName());
		}
		return typeNames;
	}

	public int parseInt(String s) {
		return Integer.parseInt(s);
	}

	public double parseDouble(String s) {
		return Double.parseDouble(s);
	}

	public boolean parseBoolean(String s) {
		return Boolean.parseBoolean(s);
	}

	public String getMetricName() {
		return this.metricName;
	}

	public String mvmsToString() {
		String str = "{";
		for (MetricVariableMapping mvm : metricVariableMappings.values()) {
			str += mvm.toString();
		}
		str += "}";
		return str;
	}

	public void errLog(Object o) {
		System.err.println(this.metricName + " Warning: " + o.toString());
	}
}