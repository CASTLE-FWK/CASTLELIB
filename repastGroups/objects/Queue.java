package repastGroups.objects;

import java.util.ArrayDeque;
import java.util.Iterator;

public class Queue<T> {
	protected ArrayDeque<T> queue;
	
	public Queue() {
		queue = new ArrayDeque<T>();
	}
	
	public void enq(T item){
		queue.addLast(item);
	}
	
	public T deq() {
		if (size() <= 0){
			return null;
		}
		return queue.removeFirst();
	}
	
	public boolean empty(){
		return queue.isEmpty();
	}
	
	public int size(){
		return queue.size();
	}
	
	public Iterator<T> getIterator(){
		return queue.iterator();
	}
	
	public boolean remove(T obj){
		return queue.remove(obj);
	}
}
