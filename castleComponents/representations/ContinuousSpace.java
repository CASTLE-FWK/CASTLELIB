package castleComponents.representations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import castleComponents.objects.Vector2;


//THIS DOESNT WORK THIS WAY, NEED A BETTER WAY (grids with subgrids?)
public class ContinuousSpace<T> {

	HashMap<T,Vector2> spaceMap;	
	public ContinuousSpace(){
		
	}
	
	public void add(T obj, Vector2 pos){
		spaceMap.put(obj, pos);
	}
	
	public void remove(T obj){
		spaceMap.remove(obj);
	}
	
	public List<T> itemsAroundPos(Vector2 pos, double dist){
		ArrayList<T> items = new ArrayList<T>();
//		HashSet<T> sorted
		
		
		return items;
	}
}
