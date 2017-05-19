package castleComponents;

import java.util.LinkedList;

public class MessageQueue{
	Queue<Message<?>> messageQueue;
	

	public MessageQueue(){
		messageQueue = new Queue<Message<?>>();
	}

	public void addNewMessage(Message<?> msg){
		messageQueue.enq(msg);
	}

	public Message<?> removeMessage(){
		return messageQueue.deq();
	}

	public boolean isEmpty(){
		return messageQueue.empty();
	}

class Queue<T> {
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

}