package experimentExecution;

public class TypeMap {
	private String targetEntity;
	private String targetEntityVariableName;
	private String desiredValue;
	private String name;

	// The over-engineering part
	// private HashMap<String, ArrayList<String>> typesAndVars;
	// typesAndVars = new HashMap<String, ArrayList<String>>();
	public TypeMap(String te, String tevn, String dv, String nm) {
		setTargetEntity(te);
		setTargetEntityVariableName(tevn);
		setDesiredValue(dv);
		setName(nm);
	}

	public void setTargetEntity(String targetEntity) {
		this.targetEntity = targetEntity;
	}

	public void setDesiredValue(String desiredValue) {
		this.desiredValue = desiredValue;
	}

	public void setTargetEntityVariableName(String targetEntityVariableName) {
		this.targetEntityVariableName = targetEntityVariableName;
	}

	public String getDesiredValue() {
		return desiredValue;
	}

	public String getTargetEntityVariableName() {
		return targetEntityVariableName;
	}

	public String getTargetEntityType() {
		return targetEntity;
	}

	@Override
	public String toString() {
		return "(targetEntity:" + targetEntity + ",targetEntityVariableName:" + targetEntityVariableName
				+ ",desiredValue:" + desiredValue + ")";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTargetEntity() {
		return targetEntity;
	}

}