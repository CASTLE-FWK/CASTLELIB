package castleComponents.objects;

import java.util.ArrayList;
import java.util.Collection;

public class List<T> extends ArrayList<T>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 947819955040569421L;
	
	int nextCounter = 0;
	
	public T next() {
		int currNext = nextCounter;
		nextCounter++;
		if (nextCounter == size()-1) {
			nextCounter = 0;
		}
		return get(currNext);
	}
	
	public List(Collection<? extends T> x) {
		super(x);
	}

	public List() {
		super();
	}
	
	public void initialize(int size) {
		//Do nothing for now
	}
	
	public void addEntity(T t) {
		add(t);
	}
	
	public T peek() {
		return super.get(0);
	}
	
}
