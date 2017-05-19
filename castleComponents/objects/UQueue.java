package castleComponents.objects;

public class UQueue<T> extends Queue<T>{

	public UQueue() {
		super();
	}
	public void enq(T item){
		if (!queue.contains(item)){
			queue.add(item);
		}
	}

}
