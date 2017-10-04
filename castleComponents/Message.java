package castleComponents;

/**
 * How can we use a message class to pass interactions?
 * What is an interaction?
 * 		Query: A "asks" B about something (so B needs the complementary function )
 * 		Communication: A "sends" something to B and B may do something with it
 */
public class Message<T> {
	

	public Message(MessageType mt, T contents, long time){
		this.time = time;
		this.contents = contents;
		this.messageType = mt;
	}

	public Message(MessageType mt, T contents, long time, E recipient){
		this.time = time;
		this.contents = contents;
		this.messageType = mt;	
		this.recipient = recipient;
	}

	private long time;
	
	public long getTime(){
		return time;
	}
	 
	public void setTime(long time){
		this.time = time;
	}

	private MessageType messageType;
	
	public MessageType getMessageType(){
		return messageType;
	}
	 
	public void setMessageType(MessageType messageType){
		this.messageType = messageType;
	}

	private T contents;
	
	public T getContents(){
		return contents;
	}
	 
	public void setContents(T contents){
		this.contents = contents;
	}

	private E recipient;
	
	public E getRecipient(){
		return recipient;
	}
	 
	public void setRecipient(E recipient){
		this.recipient = recipient;
	}

	

}