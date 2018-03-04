package experimentExecution;

import java.util.ArrayList;
import java.util.HashMap;

import observationTool.VEntity;

public class MetricVariableMapping {
	private String metricVar;

	private ArrayList<TypeMap> typeMaps;
	private HashMap<String, ArrayList<TypeMap>> entityMetaMap;

	public MetricVariableMapping(String mv) {
		setMetricVar(mv);
		typeMaps = new ArrayList<TypeMap>();
		entityMetaMap = new HashMap<String, ArrayList<TypeMap>>();
	}

	public String getMetricVar() {
		return metricVar;
	}

	public void setMetricVar(String metricVar) {
		this.metricVar = metricVar;
	}

	public void addTypeMap(String te, String tevn, String dv) {
		TypeMap tm = new TypeMap(te, tevn, dv);
		typeMaps.add(tm);

		if (!entityMetaMap.containsKey(te)) {
			entityMetaMap.put(te, new ArrayList<TypeMap>());
		}
		entityMetaMap.get(te).add(tm);
	}

	public ArrayList<TypeMap> getSpecificTypeMap(String ent) {
		return entityMetaMap.get(ent);
	}

	public boolean entityTypeIsContained(String t) {
		return entityMetaMap.containsKey(t);
	}

	public ArrayList<TypeMap> getTypeMaps() {
		return typeMaps;
	}

	public boolean entityIsOfType(VEntity ve) {
		return (entityTypeIsContained(ve.getType()));
	}

	public boolean compareParameters(VEntity ve1, VEntity ve2) {
		// Do we care about types matching?
		String t = ve1.getType();
		ArrayList<TypeMap> tmList = getSpecificTypeMap(t);
		int score = 0;
		for (TypeMap tn : tmList) {
			String paramName = tn.getTargetEntityVariableName();
			score += ve2.getParameterValueFromStringAsString(paramName)
					.compareToIgnoreCase(ve1.getParameterValueFromStringAsString(paramName));
		}
		return (score == 0);
	}

	public boolean isParameterEqualToDesiredValue(VEntity v) {
		String t = v.getType();
		ArrayList<TypeMap> tmList = getSpecificTypeMap(t);
		int score = 0;
		for (TypeMap tn : tmList) {
			String parameterName = tn.getTargetEntityVariableName();
			String desiredValue = tn.getDesiredValue();
			score += v.getParameterValueFromStringAsString(parameterName).compareToIgnoreCase(desiredValue);
		}
		return (score == 0);
	}

	public String typeMapsToString() {
		String str = "typeMaps:[";
		for (TypeMap tm : typeMaps) {
			str += tm.toString();
		}
		str += "]";
		return str;
	}
	
	public String toString() {
		return "{"+metricVar+"["+typeMapsToString()+"]}";
	}
}
