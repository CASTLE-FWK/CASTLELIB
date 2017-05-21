package castleComponents.representations;

import java.util.ArrayList;
import java.util.List;

import castleComponents.Entity;
import castleComponents.Enums;

public class LayoutParameters {
	Enums.RepresentationTypes representationType;
	Class<?> clazz;
	ArrayList<Entity> containedEntities;
	private boolean allowPhantoms = false;
	
	public LayoutParameters(){
		containedEntities = new ArrayList<Entity>();
	}
	
	public LayoutParameters(Enums.RepresentationTypes type){
		this.representationType = type;
		containedEntities = new ArrayList<Entity>();
	}
	
	public void setRepresentationType(Enums.RepresentationTypes rt){
		this.representationType = rt;
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

	public void addContainedEntities(Representation r){
		List<Entity> entities = r.getEntities();
		containedEntities.addAll(entities);		
	}	
	
	public ArrayList<Entity> getContainedEntities(){
		return containedEntities;
	}
	
}