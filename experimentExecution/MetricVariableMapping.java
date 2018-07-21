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

	public void addTypeMap(String te, String tevn, String dv, String nm) {
		TypeMap tm = new TypeMap(te, tevn, dv, nm);
		typeMaps.add(tm);

		if (!entityMetaMap.containsKey(te)) {
			entityMetaMap.put(te, new ArrayList<TypeMap>());
		}
		entityMetaMap.get(te).add(tm);
	}

	public ArrayList<TypeMap> getSpecificTypeMap(String ent) {
//		System.err.println("stM: "+ent);
		return entityMetaMap.get(ent);
	}

	public boolean entityTypeIsContained(String t) {
		return entityMetaMap.containsKey(t);
	}

	public ArrayList<TypeMap> getTypeMaps() {
		return typeMaps;
	}

	public boolean entityIsOfType(VEntity ve) {
		return (entityTypeIsContained(ve.getEntityID().getEntityType()));
	}

	public boolean compareParameters(VEntity ve1, VEntity ve2) {
		// Do we care about types matching?
		String t = ve1.getEntityID().getEntityType();
		ArrayList<TypeMap> tmList = getSpecificTypeMap(t);
		int score = 0;
		for (TypeMap tn : tmList) {
			String paramName = tn.getTargetEntityVariableName();
			score += ve2.getParameterValueFromStringAsString(paramName)
					.compareToIgnoreCase(ve1.getParameterValueFromStringAsString(paramName));
		}
		//Changed to !=
		return (score != 0);
	}

	public boolean isParameterEqualToDesiredValue(VEntity v) {
		String t = v.getEntityID().getEntityType();
//		System.err.println("TTT: "+t);
//		System.exit(0);
		ArrayList<TypeMap> tmList = getSpecificTypeMap(t);
		int score = 0;
		for (TypeMap tn : tmList) {
			String parameterName = tn.getTargetEntityVariableName();
			String desiredValue = tn.getDesiredValue();
			if (v.getParameterValueFromStringAsString(parameterName) == null) {
				continue;
			}
			score += v.getParameterValueFromStringAsString(parameterName).compareToIgnoreCase(desiredValue);
		}
		//Changed to !=
		return (score != 0);
	}

	public String getDesiredValueFromName(String n) {
		ArrayList<TypeMap> tmList = getSpecificTypeMap(n);
		for (TypeMap tm : tmList) {
			if (tm.getTargetEntityVariableName().compareToIgnoreCase(n) == 0) {
				return tm.getDesiredValue();
			}
		}
		return null;
	}

	public String typeMapsToString() {
		String str = "typeMaps:[";
		for (TypeMap tm : typeMaps) {
			str += tm.getName();
		}
		str += "]";
		return str;
	}

	public String toString() {
		return "{" + metricVar + "[" + typeMapsToString() + "]}";
	}
}
