package castleComponents.objects;

import java.util.Comparator;
import java.util.PriorityQueue;

public class PQueue<T> {
	protected PriorityQueue<T> pQueue;
	
	public PQueue(Comparator<T> comp) {
//		pQueue = new PriorityBlockingQueue<T>();
		pQueue = new PriorityQueue<T>(comp);
	}
	
	public T deq(){
		return pQueue.remove();
	}
	
	public boolean empty(){
		return pQueue.isEmpty();
	}
	
	public boolean enq(T item){
		return pQueue.add(item);
	}
	
	public int size(){
		return pQueue.size();
	}
	
}
