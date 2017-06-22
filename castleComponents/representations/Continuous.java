package castleComponents.representations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import castleComponents.Entity;
import castleComponents.objects.Vector2;
import stdSimLib.utilities.Utilities;

public class Continuous<E> implements Representation<E>{
	
	HashMap<Entity, Vector2> entityLocationMap;
	Vector2 dimensions;
	
	public Continuous(){
		dimensions = new Vector2();
		entityLocationMap = new HashMap<Entity, Vector2>();
	}
	
	public Continuous(Vector2 dimensions){
		this.dimensions = dimensions;
		entityLocationMap = new HashMap<Entity, Vector2>();
	}
	
	public void setDimensions(Vector2 dims){
		dimensions = new Vector2(dims);
		entityLocationMap = new HashMap<Entity, Vector2>();
	}
	public void setDimensions(double x, double y){
		dimensions = new Vector2(x,y);
	}
	
	public void add(Entity obj, Vector2 pos){
		entityLocationMap.put(obj, pos);
	}
	
	public boolean remove(Entity obj){
		return (entityLocationMap.remove(obj) != null);
	}
	
	public void moveTo(Entity obj, Vector2 newPos){
		add(obj, newPos);
	}
	
	public void moveByVector(Entity obj, Vector2 shiftPos){
		add(obj, entityLocationMap.get(obj).add(shiftPos));
	}
	
	public List<Entity> getNeighborsFromPosition(Vector2 pos, double dist){
		ArrayList<Entity> items = new ArrayList<Entity>();
		ArrayList<Vector2> possiblePositions = pos.possibleOffsets(dist);
		for (Vector2 v : possiblePositions){
			items.addAll(getObjectsAtLocation(v.getX(), v.getY()));
		}

		return items;
	}
	
	public double getDistance(Entity obj1, Entity obj2){
		return Utilities.calculateDistance2D(entityLocationMap.get(obj1), entityLocationMap.get(obj2));
	}
	
	public List<Entity> getObjectsAtLocation(double x, double y){
		ArrayList<Entity> objs = new ArrayList<Entity>();
		Vector2 loc = new Vector2(x,y);
		for (Entry<Entity, Vector2> entry : entityLocationMap.entrySet()) {
			
	        if (entry.getValue().compare(loc)) {
	            objs.add(entry.getKey());
	        }
	    } 
		return objs;
	}
	
	public Vector2 getDimensions(){
		return dimensions;
	}

	@Override
	public List<E> getEntities() {
		List<Entity> out = new ArrayList<Entity>();
		Iterator<Entry<Entity, Vector2>> it = entityLocationMap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        out.add((Entity) pair.getKey());
	    }
	    return (List<E>) out;
	}


	public boolean addEntity(E e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeEntity(E e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeEntityByID(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean initialize(Object... objects) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean initializeEntity(Object... objects) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean initializeEntities(Object... objects) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addEntities(List<E> es) {
		// TODO Auto-generated method stub
		return false;
	}
	
//	public void updatePositions(){
//		ArrayList<Entity> list = (ArrayList<Entity>) getEntities();
//		for (Entity e : list){
//			entityLocationMap.put(e, e.getPosition());
//		}
//	}
}
