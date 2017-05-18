package repastGroups.representations;

import java.util.ArrayList;
import repastGroups.Enums;
import repastGroups.Entity;

public class LayoutParameters {
	Enums.RepresentationTypes representationType;
	Class<?> clazz;
	ArrayList<Entity> containedEntities;
	private boolean allowPhantoms = false;
	
	public LayoutParameters(){
		
	}
	
	public void setRepresentationType(Enums.RepresentationTypes rt){
		this.representationType = rt;
	}
	
	public LayoutParameters(Enums.RepresentationTypes type){
		this.representationType = type;
	}
	
	public void addEntityType(Class<?> clazz){
		this.clazz = clazz;
	}
	
	public Class<?> getEntityType(){
		return clazz;
	}
	
	public boolean allowPhantoms() {
		return allowPhantoms;
	}
	
	public void setAllowPhantoms(boolean b){
		allowPhantoms = b;
	}

	//
	public void addContainedEntities(Representation r){
		ArrayList<Entity> entities = (ArrayList<Entity>) r.getEntities();
		containedEntities.addAll(entities);		
	}	
	
	public ArrayList<Entity> getContainedEntities(){
		return containedEntities;
	}
	
}