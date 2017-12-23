package castleComponents.representations.Map2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import castleComponents.Entity;
import castleComponents.EntityID;
import castleComponents.objects.Vector2;
import stdSimLib.utilities.Utilities;

public class MapComponent {
	
	HashMap<String, Entity> containedEntities;
	Type theType;
	
	Vector2 position;
	
	//The ways to store types
	Map2D map;
	Park park;
	
	public MapComponent(){
		position = Vector2.NULL;
		containedEntities = new HashMap<String, Entity>();
		theType = Type.UNSET;
	}
	
	public MapComponent(Vector2 pos, Type t){
		containedEntities = new HashMap<String, Entity>();
		setPosition(pos);
		setType(t);
		if (t == Type.PARK){
			this.park = new Park();
		}
	}
	
	public MapComponent(MapComponent mc) {
		setType(mc.getType());
		setPosition(mc.getPosition());
		containedEntities = new HashMap<String, Entity>();
		
		HashMap<String, Entity> oldHashMap = mc.getContainedEntities();
		Set<String> keys = oldHashMap.keySet();
		for (String k : keys) {
			containedEntities.put(k, oldHashMap.get(k));
		}
		containedEntities.putAll(mc.getContainedEntities());
	}
	
	public void setPosition(Vector2 p){
		this.position = new Vector2(p);
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
	
	public boolean removeEntity(String id){
		return (containedEntities.remove(id) != null);
	}
	
	public HashMap<String, Entity> getContainedEntities(){
		return containedEntities;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Entity> getContainedEntitiesAsList(){
		return (ArrayList<Entity>) Utilities.getMapAsList(containedEntities);
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
	
	public boolean checkForEntity(EntityID eID){
		return containedEntities.containsKey(eID.toString());
	}
	
}