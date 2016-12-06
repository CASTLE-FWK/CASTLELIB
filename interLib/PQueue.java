package interLib;

import java.util.PriorityQueue;

public class PQueue<T> {
	protected PriorityQueue<T> pQueue;
	
	public PQueue() {
//		pQueue = new PriorityBlockingQueue<T>();
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
