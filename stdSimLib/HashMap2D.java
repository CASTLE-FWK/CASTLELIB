package stdSimLib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import castleComponents.objects.Vector2;

/**
 * This is basically a 2D Array that takes type T
 * An unfortunate expansion to the language
 * 
 * alternatively, it's only ints, and numbers are used to represent things
 * For now, that seems logical, since we basically only want bools
 * Expansion will be easy (both for generator and logic) HAHAH WE HAVE TO USE TYPES NOW
 * @author lachlan
 * @param <T>
 *
 */
public class HashMap2D<T> {
	
	private int dimensionX = 0;
	private int dimensionY = 0;
//	private ArrayList<ArrayList<Integer>> theMap = null;
	private ConcurrentHashMap<Vector2, T> theMap = null;
	
	public HashMap2D(){}
	
	public HashMap2D(Vector2 size){
		dimensionX = (int)size.getX();
		dimensionY = (int)size.getY();
		setUpMap();
	}
	
	public HashMap2D(int x, int y){
		dimensionX = x;
		dimensionY = y;
		setUpMap();
	}
	
	public int getSize(){
		return theMap.size();
	}
	
	private void setUpMap(){
		theMap = new ConcurrentHashMap<Vector2, T>(dimensionX*dimensionY);
	}
	
	public T getElementAtPosition(Vector2 pos){
		T element = theMap.get(pos);
//		theMap.remove(pos);
		return element; 
	}
	
	public T getElementAtPosition(int x, int y){
		Vector2 newVec = new Vector2(x,y);
		return getElementAtPosition(newVec);
	}
	
	public boolean peek(Vector2 vec){
		return theMap.containsKey(vec);
	}
	
	public T get(int x, int y){
		return getElementAtPosition(x,y);
	}
	
	public T get(Vector2 pos){
		return getElementAtPosition(pos);
	}
	
	public void setElementAtPosition(int x, int y, T newElement){
		Vector2 newVec = new Vector2(x,y);
		setElementAtPosition(newVec, newElement);
	}
	
	public void setElementAtPosition(Vector2 pos, T newElement){
		theMap.put(pos,newElement);
	}
	
	public void forEach(Function<T,Void> fn){
		Iterator it = theMap.entrySet().iterator();
		while (it.hasNext()){
			Map.Entry pair = (Map.Entry)it.next();
			fn.apply((T)pair.getValue());
		}
	}
	
	public void remove(Vector2 key){
		theMap.remove(key);
	}
	
	//I have no idea how to do this, so I'm going
	//to hardcode this, ewww.
	public void removeAll(){
		Iterator it = theMap.entrySet().iterator();
		while (it.hasNext()){
			Map.Entry pair = (Map.Entry)it.next();
			
		}
	}
	
	public Iterator getIterator(){
		return theMap.entrySet().iterator();
	}
	
	public Vector2 findNeighbourPlusXPosition(Vector2 vec, int offset){
		
		Iterator it = theMap.entrySet().iterator();
		while (it.hasNext()){
			Map.Entry pair = (Map.Entry)it.next();
			Vector2 tmpVec = (Vector2)pair.getKey();
			ArrayList<Vector2> offsets = vec.possibleOffsets(offset);
			java.util.Collections.shuffle(offsets);
			for (Vector2 vecOffset : offsets){
				if (vecOffset.equals(tmpVec)){
//					System.out.println("YAY");
					return (new Vector2(tmpVec));
				}
			}
		}
		return null;
	}
	
	public int size(){
		return theMap.size();
	}
}
