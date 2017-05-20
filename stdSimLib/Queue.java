package stdSimLib;

import java.util.LinkedList;

public class Queue<T> {
	private LinkedList<T> queue;
	
	public Queue() {
		queue = new LinkedList<T>();
	}
	
	public void enq(T item){
		queue.addLast(item);
	}
	
	public T deq() {
		return queue.removeFirst();
	}
	
	public boolean empty(){
		return queue.isEmpty();
	}
	
	public int size(){
		return queue.size();
	}
}
