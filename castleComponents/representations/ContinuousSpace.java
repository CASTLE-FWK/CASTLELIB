package castleComponents.representations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import castleComponents.objects.Vector2;
import stdSimLib.Utilities;


//THIS DOESNT WORK THIS WAY, NEED A BETTER WAY (grids with subgrids?)
public class ContinuousSpace<T> {
	
	HashMap<T, Vector2> entityLocationMap;
	Vector2 dimensions;
	
	
	public ContinuousSpace(Vector2 dimensions){
		this.dimensions = dimensions;
	}
	
	public void add(T obj, Vector2 pos){
		entityLocationMap.put(obj, pos);
	}
	
	public boolean remove(T obj){
		return (entityLocationMap.remove(obj) != null);
	}
	
	public void moveTo(T obj, Vector2 newPos){
		add(obj, newPos);
	}
	
	public void moveByVector(T obj, Vector2 shiftPos){
		add(obj, entityLocationMap.get(obj).add(shiftPos));
	}
	
	public List<T> itemsAroundPos(Vector2 pos, double dist){
		ArrayList<T> items = new ArrayList<T>();
		ArrayList<Vector2> possiblePositions = pos.possibleOffsets(dist);
		for (Vector2 v : possiblePositions){
			items.addAll(getObjectsAtLocation(v.getX(), v.getY()));
		}

		return items;
	}
	
	public double getDistance(T obj1, T obj2){
		return Utilities.calculateDistance2D(entityLocationMap.get(obj1), entityLocationMap.get(obj2));
	}
	
	public List<T> getNeighbors(Vector2 pos, Vector2 depth){
		
		return null;
	}
	
	public List<T> getObjectsAtLocation(double x, double y){
		ArrayList<T> objs = new ArrayList<T>();
		Vector2 loc = new Vector2(x,y);
		for (Entry<T, Vector2> entry : entityLocationMap.entrySet()) {
			
	        if (entry.getValue().compare(loc)) {
	            objs.add(entry.getKey());
	        }
	    } 
		return objs;
	}
	
	
	
	
	public Vector2 getDimensions(){
		return dimensions;
	}
}
