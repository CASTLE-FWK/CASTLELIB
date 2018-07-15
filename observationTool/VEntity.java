package observationTool;

import java.util.Comparator;

import castleComponents.Entity;
import castleComponents.EntityID;
import castleComponents.objects.Vector2;
import stdSimLib.Parameter;
import java.util.HashMap;

//This is a virtual Agent. Identical to an Agent it just allows the MetricRunner to process already stored information

public class VEntity extends Entity {

	//TODO:
	//set colour
	//get position
	Vector2 position;
	String name = "";

	public VEntity(String name, String type, String uid) {
		//Split the uid up into Type and id	
		super(type, uid);
		this.name = name;
		String[] s = uid.split("_");
		String parsedType = s[0];
		int parsedID = Integer.parseInt(s[1]);
		setEntityID(new EntityID(parsedType, parsedID));
	}
 
	public VEntity(VEntity v) {
		super(v.getType(), v.getID());
		this.name = v.getName();
		parameters = new HashMap<String, Parameter<?>>(v.getParameters());
	}

	public VEntity(Entity newV) {
		super(newV.getType(), newV.getID());
		this.name = newV.getID();
		HashMap<String, Parameter<?>> oldParams = newV.getParameters();
		for (String s : oldParams.keySet()) {
			addParameterFromString(s, oldParams.get(s).getType(), oldParams.get(s).getCurrentValue());
//			addParameter(s, oldParams.get(s).getType(), oldParams.get(s).getCurrentValue());
		}
		entityColor = newV.getEntityDisplay();
//		parameters = new HashMap<String, Parameter<?>>(newV.getParameters());
	}

	@Override
	public void addParameterFromString(String name, String type, String value) {
		if (name.compareToIgnoreCase("Position") == 0 || name.compareToIgnoreCase("Location") == 0 || name.compareToIgnoreCase("birdPosition") == 0) {
			position = new Vector2(value);
		}
		super.addParameterFromString(name, type, value);
	}

	public Object getParameterValue(String paramName) {
		return getParameterValueFromString(paramName);
	}

	@Override
	public String toString() {
		return super.toString();
	}

	public static Comparator<VEntity> sortByName() {
		Comparator<VEntity> comp = new Comparator<VEntity>() {
			@Override
			public int compare(VEntity va1, VEntity va2) {
				return va1.getID().compareTo(va2.getID());
			}
		};
		return comp;
	}

	@Override
	public Vector2 getPosition() {
		return position;
	}

	public String getName() {
		return name;
	}
}
