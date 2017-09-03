package castleComponents.representations.Map2D;

import java.util.ArrayList;
import java.util.List;

import castleComponents.Entity;
import castleComponents.objects.Range2D;
import castleComponents.objects.Vector2;
import castleComponents.representations.Grid;
import stdSimLib.utilities.Utilities;

public class Map2D {
	
	//How can we do this
	//Can use a grid! 
	Grid<MapComponents> theGridMap;
	Range2D range;
	String name;
	boolean open = true;
	int scale = 1;
	
	public Map2D(String name, boolean isOpen, int scale){
		this.name = name;
		this.open = isOpen;
		this.scale = scale;
		theGridMap = new Grid<MapComponents>();
		
	}
	
	public Map2D(Vector2 gridDims){
		theGridMap = new Grid<MapComponents>();
		theGridMap.init(gridDims, MapComponents.class);
	}
	
	public void setRange(Range2D r){
		this.range = r;
		//Apply this to Grid
	}
	
	public void setRange(Vector2 a, Vector2 b, Vector2 c, Vector2 d){
		range = new Range2D(a,b,c,d);
		//Apply this to Grid
	}
	
	public Map2D(String parsedMapFile){
		
	}
	
	public void importMap(String parsedMapFile){
		
	}
	
	public Vector2 getPositionOfEntity(Entity e){
		//Find entity and return its position
		//Cycle through grid, and check with each containedEntities map
		ArrayList<MapComponents> mapComponents = new ArrayList<MapComponents>(theGridMap.getEntities());
		String eID = e.getID();
		for (MapComponents mc : mapComponents) {
			if (mc.checkForEntity(eID)){
				return mc.getPosition();
			}
		}

		//Otherwise return the NULL vector2
		return Vector2.NULL;
	}
	
	//This should return states
	public Outcome moveTo(Entity e, Vector2 pos){
		//Move an entity to a particular location
		if (!range.containsPoint(pos)){
			return Outcome.OUT_OF_BOUNDS;
		}
		
		if (isNoGo(pos)){
			return Outcome.INVALID;
		}

		//Surely there are some more bad cases here
		
		//This will be slow
		MapComponents oldMC = getMapComponent(getPositionOfEntity(e));
		oldMC.removeEntity(e.getID());
		
		MapComponents mc = getMapComponent(pos);
		mc.addEntity(e);
		
		return Outcome.VALID;
		
	}
	
	public Outcome moveToWithVelocity(Entity e, Vector2 pos, Vector2 vel){
		Vector2 newPos = pos.add(vel);
		return moveTo(e,newPos);		
	}
	
	//This is a standard range
	public int countEntitiesInRange(Vector2 pos, Vector2 range){
		
	}
	
	//This is a total range (i.e. 360° vis)
	public int countEntitiesInRange(Vector2 pos, int range){
		
	}
	
	public boolean isRoad(Vector2 pos){
		MapComponents m = getMapComponent(pos);
		return (m.getType() == Type.ROAD_L || m.getType() == Type.ROAD_R ||
				m.getType() == Type.TURN_L || m.getType() == Type.TURN_R);				
	}
	
	public boolean isPark(Vector2 pos){
		MapComponents m = getMapComponent(pos);
		return (m.getType() == Type.PARK);
		
	}
	public boolean isNoGo(Vector2 pos){
		MapComponents m = getMapComponent(pos);
		return (m.getType() == Type.NOGO);
		
	}
	
	
//	public boolean isType(String typeName){
//		//
//	}
	
	public boolean addMapSection(Map2D map, Vector2 pos){
		return getMapComponent(pos).setMap(map);
	}
	
	public Map2D getMapSection(Vector2 pos){
		return getMapComponent(pos).getMap();
	}
	
	@SuppressWarnings("unchecked")
	public List<Entity> getEntitiesAtPos(Vector2 pos){
		MapComponents m = getMapComponent(pos);
		return (List<Entity>) Utilities.getMapAsList(m.getContainedEntities());
	}
	
	public MapComponents getMapComponent(Vector2 pos){
		return theGridMap.getEntityAtPos(pos);
	}
	
	public boolean addEntity(Entity e, Vector2 pos){
		MapComponents m = getMapComponent(pos);
		return m.addEntity(e);
	}
	
	public Park getParkAtPos(Vector2 pos){		
		if (isPark(pos)){
			MapComponents m = getMapComponent(pos);
			return m.getPark();
		} else {
			return null;
		}
	}
	
	public boolean entityParking(Entity e, Vector2 pos){
		//check if park
		Park p = getParkAtPos(pos);
		if (p != null) {
			//check if spaces
			if (p.freeSpaces()){
				//Then Park
				//TODO
				return true;
			}
		}
		return false;
	}

}

enum Outcome {
	OUT_OF_BOUNDS, INVALID, VALID;
}
