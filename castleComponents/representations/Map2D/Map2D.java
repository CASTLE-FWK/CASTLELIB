package castleComponents.representations.Map2D;

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
		//Otherwise return the NULL vector2
		
		
		return null;
	}
	
	//This should return states
	public boolean moveTo(Entity e, Vector2 pos){
		//Move an entity to a particular location
		//If pos is out of bounds 
		return true;
	}
	
	public Outcome moveToWithVelocity(Entity e, Vector2 pod, Vector2 vel){
		
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
	public int countEntitiesInRange(Vector2 pos, Vector2 range){
		
	}
	public int countEntitiesInRange(Vector2 pos, int range){
		
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
	OUT_OF_BOUNDS, INVALID;
}
