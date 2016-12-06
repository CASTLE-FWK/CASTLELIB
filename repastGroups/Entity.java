package repastGroups;


public class Entity implements Runnable {
	
	private int currentStep = -1;
	protected Logger logger;

	boolean ready = false;

	public Entity(String type, long uid){
		entityID = new EntityID(type,uid);
		currentPhase = Phase.SETUP;
		logger = new Logger();
		logger.mute();
	}

	protected EntityID entityID;
	
	public EntityID getEntityID(){
		return entityID;
	}
	 
	public void setEntityID(EntityID entityID){
		this.entityID = entityID;
	}

	private Phase currentPhase;
	
	public Phase getCurrentPhase(){
		return currentPhase;
	}
	 
	public void setCurrentPhase(Phase currentPhase){
		this.currentPhase = currentPhase;
	}



	public void setCurrentStep(int step){
		this.currentStep = step;
	}

	public int getCurrentStep(){
		return this.currentStep;
	}


//	public String announce(){
//		// return entityID.toString() +" is alive at step "+getCurrentStep();
//		return "";
//	}

	@Override
	public void run(){
//		System.out.println(announce());
	}

	public void initialize(){

	}

	public void phase_Setup(){

	}

	public void phase_Action(){

	}

	public void phase_Cleanup(){

	}


	//Messaging
	public void receiveMessage(Message msg){
		// System.out.println(entityID.toString() + ": "+msg);
		MessageType msgType = msg.getMessageType();

		switch(msgType){
			case CLOCK:
				setCurrentStep((Integer)msg.getContents());
			break;
			case PHASE:
				setCurrentPhase((Phase)msg.getContents());
			break;
			default:
				System.out.println("UH OH");
			break;
		}

	}

	public void sendMessage(){

	}
	
	
	//Logging
	public void muteLogger(){
		logger.mute();
	}
	
	public void unmuteLogger(){
		logger.unmute();
	}

	
}