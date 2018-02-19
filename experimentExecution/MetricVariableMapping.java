package experimentExecution;

public class MetricVariableMapping {
	private String metricVar;
	private String targetEntity;
	private String targetEntityVariableName;
	private String desiredValue;

	public MetricVariableMapping(String mv, String te, String tevn, String dv) {
		setMetricVar(mv);
		setTargetEntity(te);
		setTargetEntityVariableName(tevn);
		setDesiredValue(dv);
	}

	public String getMetricVar() {
		return metricVar;
	}

	public void setMetricVar(String metricVar) {
		this.metricVar = metricVar;
	}

	public String getTargetEntityType() {
		return targetEntity;
	}

	public void setTargetEntity(String targetEntity) {
		this.targetEntity = targetEntity;
	}

	public String getTargetEntityVariableName() {
		return targetEntityVariableName;
	}

	public void setTargetEntityVariableName(String targetEntityVariableName) {
		this.targetEntityVariableName = targetEntityVariableName;
	}

	public String getDesiredValue() {
		return desiredValue;
	}

	public void setDesiredValue(String desiredValue) {
		this.desiredValue = desiredValue;
	}
}