package castleComponents.representations.Map2D;

import java.util.HashMap;

import castleComponents.Entity;

public class MapComponents {
	
	HashMap<String, Entity> containedEntities;
	Types theType;
	
	public MapComponents(){
		containedEntities = new HashMap<String, Entity>();
	}
	
	public void setType(Types t){
		theType = t;
	}
	
	public boolean addEntity(Entity e){
		if (containedEntities.put(e.getID(), e) == null) {
			return false;
		} else {
			return true;
		}
	}
	
}
enum Types {
	ROAD_L, ROAD_R, TURN_L, TURN_R, NOGO, PARK
}