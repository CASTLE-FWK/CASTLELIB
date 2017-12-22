package stdSimLib;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import castleComponents.objects.List;

public class HashMap<K,T> {
	private ConcurrentHashMap<K, T> theMap = null;
	
	public HashMap(){
		theMap = new ConcurrentHashMap<K,T>();
		
	}
	
	public void addAll(HashMap<K,T> h){
		List<K> keys = h.getKeys();
		for (K k : keys) {
			put(k, h.get(k));
		}
	}
	public T add(K key, T value){
		return theMap.put(key,value);
	}

	public T put(K key, T value){
		return add(key, value);
	}
	public T get(K key){
		return theMap.get(key);
	}
	
	public boolean containsKey(K key){
		return theMap.containsKey(key);
	}
	
	public boolean containsValue(T value){
		return theMap.containsValue(value);
	}
	
	public void replace(K key, T value){
		theMap.replace(key, value);
	}
	
	public int size(){
		return theMap.size();
	}
	
	public List<K> getKeys(){
		return new List<K>(theMap.keySet());
		
	}

	public List<T> values() {
		return new List<T>(theMap.values());
	}
	
	
	
}
