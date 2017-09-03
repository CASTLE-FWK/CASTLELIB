package castleComponents.representations.Map2D;

import java.util.List;

import castleComponents.Entity;
import castleComponents.objects.Range2D;
import castleComponents.objects.Vector2;
import castleComponents.representations.Grid;

public class Map2D {
	
	//How can we do this
	//Can use a grid! 
	Grid<MapComponents> theGridMap;
	Range2D range;
	
	public Map2D(){
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
	
	public boolean isRoad(Vector2:pos){
		
	}
	
	public boolean isPark(Vector2:pos){
		
	}
	public boolean isNoGo(Vector2:pos){
		
	}
	public int countEntitiesInRange(Vector2 pos, Vector2 range){
		
	}
	public int countEntitiesInRange(Vector2 pos, int range){
		
	}
	
	public boolean isType(String typeName){
		
	}
	
	public boolean addMapSection(Map2D map){
		
	}
	
	public Map2D getMapSection(Vector2 v){
					
	}
	
	public List<Entity> getEntitiesAtPos(Vector2 pos){
		
	}
	
	public boolean addEntity(Entity e){
		
	}
	
	public Park getParkAtPos(Vector2 pos){
		
	}
	
	public boolean entityParking(Entity e, Vector2 pos){
		
	}

}

enum Outcome {
	OUT_OF_BOUNDS, INVALID;
}
