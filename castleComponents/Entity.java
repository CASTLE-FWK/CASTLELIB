package castleComponents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


import castleComponents.objects.Vector2;
import castleComponents.Interaction.InteractionType;
import stdSimLib.Parameter;


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
	
	Vector2 position;
	
	/*The Interaction Stuff*/
//	ArrayList<Interaction> interactionsInLastInterval;
	HashMap<String, Interaction> interactionsInLastInterval;

	public Entity(String type, long uid){
		entityID = new EntityID(type,uid);
		this.entityType = type;
		currentPhase = Phase.SETUP;
		parameters = new HashMap<String,Parameter<?>>();
		initTriggerLists();
		interactionsInLastInterval = new HashMap<String, Interaction>();
		position = new Vector2();
	}
	public Entity(String type, EntityID eid){
		this.entityID = new EntityID(eid);
		currentPhase = Phase.SETUP;
		this.entityType = type;
		parameters = new HashMap<String,Parameter<?>>();
		initTriggerLists();
		interactionsInLastInterval = new HashMap<String, Interaction>();
		position = new Vector2();
	}
	public Entity(String type, String idAsString){
		entityID = new EntityID(idAsString);
		currentPhase = Phase.SETUP;
		this.entityType = type;
		parameters = new HashMap<String,Parameter<?>>();
		initTriggerLists();
		interactionsInLastInterval = new HashMap<String, Interaction>();
		position = new Vector2();
	}
	
	public void setLogger(Logger l){
		logger = l;
	}
	
	public boolean loggerIsNull(){
		return (logger == null);
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
		if (unique){
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
		}
		ListIterator<Trigger> i = actionTriggers.listIterator();
		i.add(tr);
	}
	
	public void setCurrentStep(int step){
		this.currentStep = step;
	}

	public int getCurrentStep(){
		return this.currentStep;
	}


	@Override
	public void run(){
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
	public void receiveMessage(Message<?> msg){
		MessageType msgType = msg.getMessageType();

		switch(msgType){
			case CLOCK:
				setCurrentStep((Integer)msg.getContents());
			break;
			case PHASE:
				setCurrentPhase((Phase)msg.getContents());
			break;
			default:
				throwCASTLEError("unknown message type", "receiveMessage", this.getClass().getName());
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
	}
	
	public void addInteraction(Entity entityTo, InteractionType type, String name){
		Interaction interaction = new Interaction(this, entityTo, type, name);
		Interaction storedInteraction = interactionsInLastInterval.get(interaction.getID()); 
		if (storedInteraction == null){
			interactionsInLastInterval.put(interaction.getID(), interaction);
		} else {
			storedInteraction.incrementOccurrence();
		}
	}

	public void addQueryInteraction(Entity entityTo, String name){
		addInteraction(entityTo, InteractionType.QUERY, name);
	}
	
	public void addCommunicationInteraction(Entity entityTo, String name){
		addInteraction(entityTo, InteractionType.COMMUNICATION, name);
	}
	
	public void addIndirectInteraction(Entity entityTo, String name){
		addInteraction(entityTo, InteractionType.INDIRECT, name);
	}
	
	public List<Interaction> publishInteractions(){
		List<Interaction> interactions = new ArrayList<Interaction>(interactionsInLastInterval.values());
		return interactions;
	}
	
	public void clearInteractions(){
		interactionsInLastInterval.clear();
	}
	
	public boolean compareEntity(Entity entity){
		return ((""+getID()).equalsIgnoreCase(entity.getID()));
	}
	
	public Vector2 getPosition() {
		return position;
	}
	
	public void setPosition(Vector2 p) {
		position = new Vector2(p);
	}
	
	//Trigger pulling
	public void pullTriggers(List<Trigger> triggers){
		for (Iterator<Trigger> iterator = triggers.iterator(); iterator.hasNext();){
			Trigger t = iterator.next();
			t.trigger();
			if (t.isDead()){
				iterator.remove();
			}
		}
	}
	
	public void throwCASTLEError(String desc, String location, String clazz) {
		System.out.println("CASTLE ERROR: "+desc+" at method: "+location+" in class: "+clazz);
	}
}