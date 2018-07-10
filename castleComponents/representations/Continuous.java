package castleComponents.representations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import castleComponents.objects.Range2D;
import castleComponents.objects.Vector2;
import stdSimLib.utilities.Utilities;

public class Continuous<E> implements Representation<E> {

	ConcurrentHashMap<E, Vector2> entityLocationMap;
	Vector2 dimensions;

	public int size() {
		return entityLocationMap.size();
	}

	public Continuous() {
		dimensions = new Vector2();
		entityLocationMap = new ConcurrentHashMap<E, Vector2>();
	}

	public Continuous(Vector2 dimensions) {
		this.dimensions = dimensions;
		entityLocationMap = new ConcurrentHashMap<E, Vector2>();
	}

	public void setDimensions(Vector2 dims) {
		dimensions = new Vector2(dims);
		entityLocationMap = new ConcurrentHashMap<E, Vector2>();
	}

	public void setDimensions(double x, double y) {
		dimensions = new Vector2(x, y);
	}

	public void add(E obj, Vector2 pos) {
		entityLocationMap.put(obj, pos);
	}

	public boolean remove(E obj) {
		return (entityLocationMap.remove(obj) != null);
	}

	public boolean moveTo(E obj, Vector2 newPos) {
		if (entityLocationMap.containsKey(obj)) {
			add(obj, newPos);
			return true;
		}
		return false;
	}

	public void moveByVector(E obj, Vector2 shiftPos) {
		add(obj, entityLocationMap.get(obj).add(shiftPos));
	}

	public ConcurrentHashMap<E, Vector2> getEntityLocationMap() {
		return entityLocationMap;
	}

	/**
	 * This finds neighbours in a rectangle & is not very useful for things with
	 * limited sight
	 * 
	 * @param pos
	 * @param dist
	 * @return
	 */
	// TODO: rename to get neighbours in bounding box
	public castleComponents.objects.List<E> getNeighborsFromVector(Vector2 pos, double dist) {
		castleComponents.objects.List<E> items = new castleComponents.objects.List<E>();
		
		//Add clauses for dimensions

		Range2D range = new Range2D(new Vector2(pos.getX() - dist, pos.getY() - dist),
				new Vector2(pos.getX() + dist, pos.getY() - dist), new Vector2(pos.getX() - dist, pos.getY() + dist),
				new Vector2(pos.getX() + dist, pos.getY() + dist));

		// Find all entities that intersect here
		for (E v : entityLocationMap.keySet()) {
			if (range.containsPoint(entityLocationMap.get(v))) {
				items.add(v);
			}
		}

		return items;
	}

	public double getDistance(E obj1, E obj2) {
		return Utilities.calculateDistance2D(entityLocationMap.get(obj1), entityLocationMap.get(obj2));
	}

	public List<E> getObjectsAtLocation(double x, double y) {
		ArrayList<E> objs = new ArrayList<E>();
		Vector2 loc = new Vector2(x, y);
		for (Entry<E, Vector2> entry : entityLocationMap.entrySet()) {

			if (entry.getValue().compare(loc)) {
				objs.add(entry.getKey());
			}
		}
		return objs;
	}

	public Vector2 getDimensions() {
		return dimensions;
	}

	@Override
	public List<E> getEntities() {
		List<E> out = new ArrayList<E>();
		Iterator<Entry<E, Vector2>> it = entityLocationMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			out.add((E) pair.getKey());
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
		if (objects.length > 1) {
			if (objects[0] instanceof Vector2) {
				setDimensions(new Vector2((Vector2) objects[0]));
				return true;
			}
			return false;
		}
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

	@Override
	public boolean addEntity(E e, Vector2 p) {
		// TODO Auto-generated method stub
		add(e,p);
		return true;
	}
	
	public String toString() {
		return getClass() +"=dims: "+dimensions+", population size:"+size();
	}

	// public void updatePositions(){
	// ArrayList<E> list = (ArrayList<E>) getEntities();
	// for (E e : list){
	// entityLocationMap.put(e, e.getPosition());
	// }
	// }
}
