package stdSimLib;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import castleComponents.objects.List;

public class HashMap<K, T> extends ConcurrentHashMap<K, T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public HashMap() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
		super(initialCapacity, loadFactor, concurrencyLevel);
	}

	public HashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public HashMap(int initialCapacity) {
		super(initialCapacity);
	}

	public HashMap(Map<? extends K, ? extends T> m) {
		super(m);
	}

	public void addAll(HashMap<K, T> h) {
		List<K> keys = h.getKeys();
		for (K k : keys) {
			put(k, h.get(k));
		}
	}

	public T add(K key, T value) {
		return super.put(key, value);
	}

	public T put(K key, T value) {
		return add(key, value);
	}

	public T replace(K key, T value) {
		return super.replace(key, value);
	}

	public int size() {
		return super.size();
	}

	public List<K> getKeys() {
		return new List<K>(super.keySet());

	}

	public List<T> values() {
		return new List<T>(super.values());
	}
	
	public T get(Object v) {
		return super.remove(v);
	}

}
