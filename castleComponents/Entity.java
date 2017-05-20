package castleComponents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import castleComponents.objects.Vector2;
import interLib.Parameter;


public class Entity implements Runnable {
	
	private int currentStep = -1;
	protected Logger logger;
	protected HashMap<String,Parameter<?>> parameters;
	
	protected ArrayList<Trigger> setupTriggers;
	protected ArrayList<Trigger> setupTriggersToAdd;
	
	protected ArrayList<Trigger> cleanupTriggers;
	protected ArrayList<Trigger> cleanupTriggersToAdd;
	
	protected ArrayList<Trigger> actionTriggers;
	protected ArrayList<Trigger> actionTriggersToAdd;

	boolean ready = false;
	
	/*The Interaction Stuff*/
	ArrayList<Interaction> interactionsInLastInterval;

	public Entity(String type, long uid){
		entityID = new EntityID(type,uid);
		this.entityType = type;
		currentPhase = Phase.SETUP;
		parameters = new HashMap<String,Parameter<?>>();
		logger = new Logger();
		logger.mute();
		initTriggerLists();
		interactionsInLastInterval = new ArrayList<Interaction>();
	}
	public Entity(String type, EntityID eid){
		this.entityID = new EntityID(eid);
		currentPhase = Phase.SETUP;
		this.entityType = type;
		parameters = new HashMap<String,Parameter<?>>();
		logger = new Logger();
		logger.mute();
		initTriggerLists();
		interactionsInLastInterval = new ArrayList<Interaction>();
	}
	public Entity(String type, String idAsString){
		entityID = new EntityID(idAsString);
		currentPhase = Phase.SETUP;
		this.entityType = type;
		parameters = new HashMap<String,Parameter<?>>();
		logger = new Logger();
		logger.mute();
		initTriggerLists();
		interactionsInLastInterval = new ArrayList<Interaction>();
	}
	
	public void initTriggerLists(){
		setupTriggers = new ArrayList<Trigger>();
		setupTriggersToAdd = new ArrayList<Trigger>();
		cleanupTriggers = new ArrayList<Trigger>();
		cleanupTriggersToAdd = new ArrayList<Trigger>();
		actionTriggers = new ArrayList<Trigger>();
		actionTriggersToAdd = new ArrayList<Trigger>();
	}
	

	protected EntityID entityID;
	
	public EntityID getEntityID(){
		return entityID;
	}
	 
	public void setEntityID(EntityID entityID){
		this.entityID = entityID;
	}
	
	public String getID(){
		return entityID.toString();
	}

	private Phase currentPhase;
	
	public Phase getCurrentPhase(){
		return currentPhase;
	}
	 
	public void setCurrentPhase(Phase currentPhase){
		this.currentPhase = currentPhase;
	}
	
	public String entityType = "";


	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	
	public String getType(){
		return entityType;
	}
	
	public void addFutureTrigger(Trigger tr, boolean unique){
//		triggersToAdd.ad
		if (unique){
//			for (Trigger t : triggers){
//				if (t.getTriggerName().compareToIgnoreCase(tr.getTriggerName()) == 0){
//					return;
//				}
//			}
			for (Trigger t : actionTriggersToAdd){
				if (t.getTriggerName().compareToIgnoreCase(tr.getTriggerName()) == 0){
					return;
				}
			}
		}
		actionTriggersToAdd.add(tr);
	}
	
	public void addImmediateTrigger(Trigger tr, boolean unique){
		if (unique){
			for (Trigger t : actionTriggers){
				if (t.getTriggerID().compareToIgnoreCase(tr.getTriggerID()) == 0){
					return;
				}
			}
//			for (Trigger t : triggersToAdd){
//				if (t.getTriggerID().compareToIgnoreCase(tr.getTriggerID()) == 0){
//					return;
//				}
//			}
		}
		ListIterator<Trigger> i = actionTriggers.listIterator();
		i.add(tr);
//		triggers.add(tr);
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

	public void initialise(){

	}

	public void phase_Setup(){

	}

	public void phase_Action(){

	}

	public void phase_Cleanup(){

	}
	
	public void final_call(){
		
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
	
	//For sending stats
	//[o] should be a reference! (Pass by reference is the only way this is going to work w/out reflection)
	public <T> void addParameter(T o, String name){
		parameters.put(name,new Parameter<T>(o, name));
	}
	
	public void addParameterFromString(String name, String type, String value){
		parameters.put(name, new Parameter<String>(value, name, type));
	}
	
	public HashMap<String,Parameter<?>> getParameters(){
		return parameters;
	}
	
	public Object getParameterValueFromString(String paramName){
		return parameters.get(paramName).getValue();
	}
	
	public String getParameterValueFromStringAsString(String paramName){
		return parameters.get(paramName).getCurrentValue();
	}
	public <T> void updateParameter(String paramName, T value){
		addParameter(value, paramName);
//		parameters.put(paramName, parameters.get(paramName).updateValue(value));
	}
	
	
	public void interactionTo(Entity entityTo, String interactionType){
		Interaction checkingInteraction = checkForInteraction(entityTo, this, interactionType);
		if (checkingInteraction == null) {
			interactionsInLastInterval.add(new Interaction(this, entityTo, interactionType));
		} else {
			checkingInteraction.incrementOccurrence();
		}
	}
	
	public Interaction checkForInteraction(Entity entityTo, Entity entityFrom, String interactionType){
		for (Interaction interaction : interactionsInLastInterval){
			if (interaction.checkForSimilarity(entityFrom.getID(), entityTo.getID(), interactionType)){
				return interaction;
			}
		}
		return null;
	}
	
	public List<Interaction> publishInteractions(){
		return interactionsInLastInterval;
	}
	
	
	public void clearInteractions(){
		interactionsInLastInterval.clear();
	}
	
	public boolean compareEntity(Entity entity){
		return ((""+getID()).equalsIgnoreCase(entity.getID()));
	}
	public Vector2 getPosition() {
		// TODO Auto-generated method stub
		return null;
	}
	

	
}