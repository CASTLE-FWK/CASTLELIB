package castleComponents.representations.Map2D;

import java.util.HashMap;

import castleComponents.Entity;
import castleComponents.objects.Vector2;

public class MapComponents {
	
	HashMap<String, Entity> containedEntities;
	Type theType;
	
	Vector2 position;
	
	//The ways to store types
	Map2D map;
	Park park;
	
	public MapComponents(){
		containedEntities = new HashMap<String, Entity>();
	}
	
	public MapComponents(Vector2 pos, Type t){
		containedEntities = new HashMap<String, Entity>();
		setPosition(pos);
		setType(t);
		if (t == Type.PARK){
			this.park = new Park();
		}
	}
	
	public void setPosition(Vector2 p){
		this.position = p;
	}
	
	public Vector2 getPosition(){
		return this.position;
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
	
	public void removeEntity(String id){
		containedEntities.remove(id);
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
	
	public boolean checkForEntity(String eID){
		return containedEntities.containsKey(eID);
	}
	
}
enum Type {
	ROAD_H, ROAD_V, TURN_L, TURN_R, NOGO, PARK, MAP
}