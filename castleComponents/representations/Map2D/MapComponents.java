package castleComponents.representations.Map2D;

import java.util.HashMap;

import castleComponents.Entity;

public class MapComponents {
	
	HashMap<String, Entity> containedEntities;
	Type theType;
	
	//The ways to store types
	Map2D map;
	Park park;
	
	public MapComponents(){
		containedEntities = new HashMap<String, Entity>();
	}
	
	public void setType(Type t){
		theType = t;
	}
	
	public boolean addEntity(Entity e){
		if (containedEntities.put(e.getID(), e) == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public HashMap<String, Entity> getContainedEntities(){
		return containedEntities;
	}
	
	public Type getType(){
		return theType;
	}
	
	public Map2D getMap(){
		return map;
	}
	
	public Park getPark(){
		return park;
	}
	public void setPark(Park p){
		this.park = p;
	}
	
	public boolean setMap(Map2D map){
		if (this.map == null){
			this.map = map;
			return true;
		} else {
			return false;
		}
	}
	
}
enum Type {
	ROAD_L, ROAD_R, TURN_L, TURN_R, NOGO, PARK, MAP
}