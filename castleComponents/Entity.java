package castleComponents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;


import castleComponents.objects.Vector2;
import dataGenerator.OutputToJSON_Mongo;
import castleComponents.Enums.FeatureType;
import castleComponents.Interaction.InteractionType;
import stdSimLib.Parameter;
import stdSimLib.utilities.Utilities;

public class Entity implements Runnable {

	protected ArrayList<Trigger> actionTriggers;
	protected ArrayList<Trigger> actionTriggersToAdd;
	final String AGENT = "agent";
	protected ArrayList<Trigger> cleanupTriggers;
	protected ArrayList<Trigger> cleanupTriggersToAdd;
	final char COMMA = ',';
	private Phase currentPhase;
	private int currentStep = -1;
	protected OutputToJSON_Mongo dbOut;
	protected EntityID entityID;
	String entitySuperType = "";
	public String entityType = "";
	final String ENVIRONMENT = "environment";
	HashMap<String, Feature> featuresInLastInterval;
	final String GROUP = "group";
	HashMap<String, Interaction> interactionsInLastInterval;
	protected Logger logger;
	protected Output output;
	protected HashMap<String, Parameter<?>> parameters;
	protected Vector2 position = new Vector2();
	protected boolean ready = false;
	protected ArrayList<Trigger> setupTriggers;
	protected ArrayList<Trigger> setupTriggersToAdd;

	public Entity(String type, EntityID eid) {
		this.entityID = new EntityID(eid);
		this.entityType = type;
		init();
	}

	public Entity(String type, long uid) {
		entityID = new EntityID(type, uid);
		this.entityType = type;
		init();
	}

	public Entity(String type, String idAsString) {
		entityID = new EntityID(idAsString);
		this.entityType = type;
		init();
	}

	public void addCommunicationInteraction(Entity entityTo, String name) {
		addInteraction(entityTo, InteractionType.COMMUNICATION, name);
	}

	public void addFutureTrigger(Trigger tr, boolean unique) {
		if (unique) {
			for (Trigger t : actionTriggersToAdd) {
				if (t.getTriggerName().compareToIgnoreCase(tr.getTriggerName()) == 0) {
					return;
				}
			}
		}
		actionTriggersToAdd.add(tr);
	}

	public void addImmediateTrigger(Trigger tr, boolean unique) {
		if (unique) {
			for (Trigger t : actionTriggers) {
				if (t.getTriggerID().compareToIgnoreCase(tr.getTriggerID()) == 0) {
					return;
				}
			}
		}
		ListIterator<Trigger> i = actionTriggers.listIterator();
		i.add(tr);
	}

	public void addIndirectInteraction(Entity entityTo, String name) {
		addInteraction(entityTo, InteractionType.INDIRECT, name);
	}

	public void addInteraction(Entity entityTo, InteractionType type, String name) {
		Interaction interaction = new Interaction(this, entityTo, type, name);
		Interaction storedInteraction = interactionsInLastInterval.get(interaction.getID());
		if (storedInteraction == null) {
			interactionsInLastInterval.put(interaction.getID(), interaction);
		} else {
			storedInteraction.incrementOccurrence();
		}
	}
	
	public void addHelperInteraction(Entity from, Entity to, InteractionType type, String name) {
		Interaction interaction = new Interaction(from, to, type, name);
		Interaction storedInteraction = interactionsInLastInterval.get(interaction.getID());
		if (storedInteraction == null) {
			interactionsInLastInterval.put(interaction.getID(), interaction);
		} else {
			storedInteraction.incrementOccurrence();
		}
	}

	// For sending stats
	// [o] should be a reference! (Pass by reference is the only way this is
	// going to work w/out reflection)
	public <T> void addParameter(T o, String name) {
		parameters.put(name, new Parameter<T>(o, name));
	}

	public <T> void addParameter(T o, String name, String type) {
		parameters.put(name, new Parameter<T>(o, name, type));
	}

	public void addParameterFromString(String name, String type, String value) {
		parameters.put(name, new Parameter<String>(value, name, type));
	}

	public void addQueryInteraction(Entity entityTo, String name) {
		addInteraction(entityTo, InteractionType.QUERY, name);
	}

	public void clearInteractions() {
		interactionsInLastInterval.clear();
	}

	public boolean compareEntity(Entity entity) {
		return (("" + getID()).equalsIgnoreCase(entity.getID()));
	}

	public boolean dbIsNull() {
		return (dbOut == null);
	}

	public void errLog(Object o) {
		System.err.println(getType() + " Warning: " + o.toString());
	}

	public void final_call() {

	}

	public Phase getCurrentPhase() {
		return currentPhase;
	}

	public int getCurrentStep() {
		return this.currentStep;
	}

	public EntityID getEntityID() {
		return entityID;
	}

	public String getID() {
		return getEntityID().toString();
	}

	public Output getOutput() {
		return output;
	}

	public HashMap<String, Parameter<?>> getParameters() {
		return parameters;
	}

	public Object getParameterValueFromString(String paramName) {
		return parameters.get(paramName).getValue();
	}

	public String getParameterValueFromStringAsString(String paramName) {
		return parameters.get(paramName).getCurrentValue();
	}

	public List<Vector2> getPointsInVisionCone(Vector2 pos, double theta, Vector2 vRange) {
		ArrayList<Vector2> points = new ArrayList<Vector2>();
		double halfTheta = theta / 0.5;
		double lastAngle = 180 - 90 - halfTheta;
		double slope = pos.calculateSlope(vRange);

		// Boy this is some baaaad year 8 maths
		// Calcs for 1 half of triangle
		double adj = pos.calculateDistance(vRange.add((pos)));
		double opp = Math.tan(halfTheta) * adj;
		double hypot = Math.sqrt(Math.pow(adj, 2) + Math.pow(opp, 2)); // √
		double wideSide = opp * 2.0; // √

		double cY = Math.pow(adj, 2) + Math.pow(hypot, 2) - Math.pow(opp, 2);
		double cX = Math.sqrt((Math.pow(hypot, 2) - (Math.pow(cY, 2))));
		Vector2 point1 = new Vector2(cY, cX);
		Vector2 point2 = pos.getDifference(point1).negate();

		// Now we have the 3 points, we can iterate through to find each valid point
		// Root to p1
		double minX = Utilities.calculateMin(new double[] { pos.getX(), point1.getX() });
		double maxX = Utilities.calculateMax(new double[] { pos.getX(), point1.getX() });
		double minY = Utilities.calculateMin(new double[] { pos.getY(), point1.getY() });
		double maxY = Utilities.calculateMax(new double[] { pos.getY(), point1.getY() });

		// Root to p2

		return points;
	}

	public Vector2 getPosition() {
		return position;
	}

	public String getType() {
		return entityType;
	}

	public void init() {
		currentPhase = Phase.SETUP;
		parameters = new HashMap<String, Parameter<?>>();
		interactionsInLastInterval = new HashMap<String, Interaction>();
		featuresInLastInterval = new HashMap<String, Feature>();
		setupTriggers = new ArrayList<Trigger>();
		setupTriggersToAdd = new ArrayList<Trigger>();
		cleanupTriggers = new ArrayList<Trigger>();
		cleanupTriggersToAdd = new ArrayList<Trigger>();
		actionTriggers = new ArrayList<Trigger>();
		actionTriggersToAdd = new ArrayList<Trigger>();
		position = new Vector2();
	}

	public void initialise() {

	}

	public void log(String str) {
		if (output == null) {
			errLog("output is null");
		}
		output.log(this, getID() + ": " + str);
	}

	public boolean loggerIsNull() {
		return (logger == null);
	}

	public void logToConsole(String str) {
		System.out.println(getID() + ": " + str);
	}

	// Logging
	public void muteLogger() {
		logger.mute();
	}

	public String parametersToString() {
		StringBuilder sb = new StringBuilder("PARAMETERS [\n");
		for (String s : parameters.keySet()) {
			sb.append("\t").append(parameters.get(s)).append("\n");
		}
		sb.append("]\n");
		return sb.toString();
	}

	public void phase_Action() {

	}

	public void phase_Cleanup() {
		writeModelData();
	}

	public void phase_Setup() {

	}

	public List<Interaction> publishInteractions() {
		List<Interaction> interactions = new ArrayList<Interaction>(interactionsInLastInterval.values());
		return interactions;
	}

	// Trigger pulling
	public void pullTriggers(List<Trigger> triggers) {
		for (Iterator<Trigger> iterator = triggers.iterator(); iterator.hasNext();) {
			Trigger t = iterator.next();
			t.trigger();
			if (t.isDead()) {
				iterator.remove();
			}
		}
	}

	// Messaging
	public void receiveMessage(Message<?> msg) {
		MessageType msgType = msg.getMessageType();

		switch (msgType) {
		case CLOCK:
			setCurrentStep((Integer) msg.getContents());
			break;
		case PHASE:
			setCurrentPhase((Phase) msg.getContents());
			break;
		default:
			throwCASTLEError("unknown message type", "receiveMessage", this.getClass().getName());
			break;
		}
	}

	@Override
	public void run() {
	}

	public void sendMessage() {

	}

	public void setCurrentPhase(Phase currentPhase) {
		this.currentPhase = currentPhase;
	}

	public void setCurrentStep(int step) {
		this.currentStep = step;
	}

	public void setDBOut(OutputToJSON_Mongo d) {
		dbOut = d;
	}

	public void setEntityID(EntityID entityID) {
		this.entityID = entityID;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public void setLogger(Logger l) {
		logger = l;
	}

	public void setOutput(Output out) {
		output = out;
	}

	public void setPosition(Vector2 p) {
		position = new Vector2(p);
	}

	public void throwCASTLEError(String desc, String location, String clazz) {
		System.out.println("CASTLE ERROR: " + desc + " at method: " + location + " in class: " + clazz);
	}

	public void unmuteLogger() {
		logger.unmute();
	}

	public void updateFeature(String nameOfFeatureCall, FeatureType featureType) {
		if (!featuresInLastInterval.containsKey(nameOfFeatureCall)) {
			featuresInLastInterval.put(nameOfFeatureCall, new Feature(nameOfFeatureCall, featureType));
		} else {
			featuresInLastInterval.get(nameOfFeatureCall).incrementOccurrence();
		}
	}
	
	public ArrayList<Feature> publishFeatures(){
		ArrayList<Feature> f = new ArrayList<Feature>(featuresInLastInterval.values());
		return f;
	}

	public <T> void updateParameter(String paramName, T value) {
		addParameter(value, paramName);
	}

	public StringBuilder writeEntityData() {
		StringBuilder sb = new StringBuilder();
		sb.append(entityType + "-ID" + COMMA + getID());
		sb.append(entityType + "-type" + COMMA + getType());
		sb.append(entityType + "-name" + COMMA + getID());
		sb.append("lifetime" + COMMA + "-1");

		//Dump parameter values
		Iterator<Entry<String, Parameter<?>>> it = getParameters().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Parameter<?>> pair = (Map.Entry<String, Parameter<?>>) it.next();
			Parameter<?> param = pair.getValue();
			sb.append("parameter-name" + COMMA + param.getName());
			sb.append("parameter-type" + COMMA + param.getType());
			sb.append("parameter-value" + COMMA + param.getCurrentValue());
		}

		//Dump interactions
		List<Interaction> entityInteractions = publishInteractions();
		if (entityInteractions != null) {
			for (Interaction inter : entityInteractions) {
				sb.append("interaction-from" + COMMA + inter.getEntityFrom().getID());
				sb.append("interaction-to" + COMMA + inter.getEntityTo().getID());
				sb.append("interaction-type" + COMMA + inter.getType());
			}
		}
		
		//Dump features
		List<Feature> entityFeatureCalls = publishFeatures();
		if (entityFeatureCalls != null) {
			for (Feature f : entityFeatureCalls) {
				sb.append("feature-name" + COMMA +f.getName());
				sb.append("feature-type" + COMMA + f.getFeatureType());
				sb.append("feature-call#" + COMMA + f.getOccurrence());
			}
		}

		return sb;
	}

	public void writeModelData() {
		output.writeModelData(this);
	}
}