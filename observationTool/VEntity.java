package observationTool;

import java.util.Comparator;
import java.util.HashMap;

import castleComponents.E;
import castleComponents.objects.Vector2;
import stdSimLib.Parameter;

//This is a virtual Agent. Identical to an Agent it just allows the MetricRunner to process already stored information

public class VEntity extends E {

	//TODO:
	//set colour
	//get position
	Vector2 position;
	String name = "";

	public VEntity(String name, String type, String uid) {
		super(type, uid);
		this.name = name;
	}

	public VEntity(VEntity v) {
		super(v.getType(), v.getID());
		this.name = v.getName();
		parameters = new HashMap<String, Parameter<?>>(v.getParameters());
	}

	public VEntity(E newV) {
		super(newV.getType(), newV.getID());
		this.name = newV.getID();
		parameters = new HashMap<String, Parameter<?>>(newV.getParameters());
	}

	public void addParameterFromString(String name, String type, String value) {
		if (name.compareToIgnoreCase("Position") == 0) {
			position = new Vector2(value);
		}
		super.addParameterFromString(name, type, value);
	}

	public Object getParameterValue(String paramName) {
		return getParameterValueFromString(paramName);
	}

	@Override
	public String toString() {
		return entityID.toString();
	}

	public static Comparator<VEntity> sortByName() {
		Comparator comp = new Comparator<VEntity>() {
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
