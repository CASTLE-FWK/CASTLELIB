package interLib;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import casFeatures.CASStateOfInterest;
import castleComponents.objects.Vector2;

/**
 * This defines an agent.
 * All models in Repast that want to use the snapshotter should extend this.
 * But is not essential
 * 
 * Now with added Repast stuff for Repast focused models as everything will be analysed
 * using the PROPER meta-model.
 *
 */
public class Agent implements java.io.Serializable{

	/**
	 * TODO: Set up stuff for the MetaModel/Schema
	 */
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1484317127922617393L;

	/**
	 * A readable ID for the Agent
	 */
	protected String ID;
	
	/**
	 * The Agents initial position in 2D space.
	 */
	protected Vector2 position;
	protected Vector2 velocity;
	protected double minimumX, minimumY, maximumX, maximumY;	
	
	/**
	 * Defines the Color for an agent. (For easy viewing)
	 */
	protected Color agentsColor;
	
	/**
	 * Defines the String representation of an agent type.
	 * This is not required but allows for multiple types of agents.
	 */
	private String type;
	
	//TODO: Hopefully do away with this pesky variable
	private int snapshotInterval;
	
	protected boolean AGENT_ALIVE = true;
	protected boolean AGENT_DEAD = false;
	
	protected boolean isAliveInSystem = true;
	
	protected boolean DEBUG_MODE = false;
	
	protected static final String COMMA = ",";
	protected static final String NEW_LINE = "\n";
	
	//All agents possess this state. They may use it differently though.
	protected State<Boolean> lifeState = new State<Boolean>("Alive", AGENT_ALIVE, AGENT_DEAD);
	protected ArrayList<State<?>> allStates = new ArrayList<State<?>>();
	
	
	/*The Interaction Stuff*/
	ArrayList<Interaction> interactionsInLastInterval;
	
	/*The CAS Stuff*/
//	CASStateOfInterest stateOfInterest;
	
	//A DebugLog
	protected DebugLogger logger;
	
	//For the DB
	protected HashMap<String,Parameter<?>> parameters;
	
	//Interactions are specified manually for now.
	//The systemSpecifications model does also specifiy them
	//but for time purposes, they will be hardcoded strings.
	
	
	/**
	 * 
	 * @param ID
	 * @param position
	 */
	public Agent(String ID, Vector2 initialPosition){
		position = new Vector2(initialPosition);
		if (position == null){
			System.out.println("AOISDHASHD");
		}
		parameters = new HashMap<String,Parameter<?>>();
		
		velocity = new Vector2();
			
		this.ID = ID;
		
		interactionsInLastInterval = new ArrayList<Interaction>();
		allStates.add(lifeState);
		
//		stateOfInterest = new CASStateOfInterest();
		
		logger = new DebugLogger();

	}
	
	/**
	 * Call the Repast step() method to move this agent.
	 * The 'brains'/logic of an agent
	 */
	public void step(){}
	
	/**
	 * Called after all agent steps have occurred.
	 */
	public void post(){}
	
	/**
	 * It's nicer/cleaner to have a separate move function. 
	 */
	public void move(){
		if (DEBUG_MODE){
			System.out.println("MOVING "+toString());
		}
	}
	
	//TODO: Leaving this here just in case I have to go back to an old design.
	/**
	 * 
	 * @param space
	 * @param grid
	 */
	/*public void assignSpace(ContinuousSpace<Agent> space, Grid<Agent> grid){
		this.space = space;
		this.grid = grid;			
	}*/

	/**
	 * @return the agentsColor
	 */
	public Color getAgentsColor() {
		return agentsColor;
	}

	/**
	 * @param agentsColor the agentsColor to set
	 */
	public void setAgentsColor(Color agentsColor) {
		this.agentsColor = agentsColor;
	}
	
	public void setAgentsColorFromInt(int[] colorRep){
		setAgentsColor(new Color(colorRep[0],colorRep[1],colorRep[2]));		
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	public boolean checkType(String testType){
		return type.equalsIgnoreCase(testType);
	}

	/**
	 * @return the iD
	 */
	public String getID() {
		return ID;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(Vector2 position) {
		this.position = new Vector2(position);		
	}
	
	/**
	 * @return the position
	 */
	public Vector2 getPosition() {
		return position;
	}

	/**
	 * @return the velocity
	 */
	public Vector2 getVelocity() {
		return velocity;
	}

	/**
	 * @param velocity the velocity to set
	 */
	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}

	/**
	 * @return the snapshotInterval
	 */
	public int getSnapshotInterval() {
		return snapshotInterval;
	}

	/**
	 * @param snapshotInterval the snapshotInterval to set
	 */
	public void setSnapshotInterval(int snapshotInterval) {
		this.snapshotInterval = snapshotInterval;
	}
	
	//Removes the agent from the context
	//I'm relying that Repast properly handle destruction of an agent
	public void destroyAgent(){	
		lifeState.setState(AGENT_DEAD);
//		isAliveInSystem = false;	//TODO: REMOVE THESE
		if (DEBUG_MODE){
			System.out.println("DESTROYING " + toString());
		}
	}
	
	public void setVelocityLimits(double minX, double maxX, double minY, double maxY){
		minimumX = minX;
		maximumX = maxX;
		
		minimumY = minY;
		maximumY = maxY;
	}

	/**
	 * Usually -maximumX
	 * @return the minimumX
	 */
	public double getMinimumX() {
		return minimumX;
	}

	/**
	 * Usually -maximumY
	 * @return the minimumY
	 */
	public double getMinimumY() {
		return minimumY;
	}

	/**
	 * @return the maximumX
	 */
	public double getMaximumX() {
		return maximumX;
	}

	/**
	 * @return the maximumY
	 */
	public double getMaximumY() {
		return maximumY;
	}
	
	/**
	 * Various initalising things can be placed in here
	 * Called after instantiation in the Repast Context usually.
	 */
	public void initialise(){
		if (DEBUG_MODE){
			System.out.println("CREATED "+toString());
		}	
	}
	
	public void initialiseSchedule(double startTime, double startDelay){}
	
	public boolean isAlive(){
		return lifeState.getCurrentState();
		
		//return isAliveInSystem;
	}
	
	public boolean compareState(State<?> state){
		for (State<?> theseStates : allStates){
			if (theseStates.compareState(state)){
				return true;
			}
		}
		return false;
	}
	
	
	
	
	/**
	 * Rules must correspond to rules in MetaModel
	 * @param numberOfRules
	 * @param rules
	 */
	//TODO: Implement this later
	/*public void setRules(double... rules){
		
	}*/
	
	public void addState(State<?> newState){
		allStates.add(newState);
	}
	
	public void addStates(ArrayList<State<?>> statesToAdd, boolean toClear){
		if (toClear){
			allStates.clear();
			allStates = new ArrayList<State<?>>(statesToAdd);
		} else {
			for (State<?> state : statesToAdd){
				allStates.add(state);
			}
		}
		
	}
	
	public String[] listStates(){
		String[] states = new String[allStates.size()];
		for (int i = 0; i < allStates.size(); i++){
			states[i] = allStates.get(i).getStateName();
		}		
		
		return states;
	}
	
	
	/*Interaction Methods*/
	public void interactionTo(Agent agentTo, String interactionType){
		Interaction checkingInteraction = checkForInteraction(agentTo, this, interactionType);
		if (checkingInteraction == null) {
			interactionsInLastInterval.add(new Interaction(this, agentTo, interactionType));
		} else {
			checkingInteraction.incrementOccurrence();
		}
	}
	
	public Interaction checkForInteraction(Agent agentTo, Agent agentFrom, String interactionType){
		for (Interaction interaction : interactionsInLastInterval){
			if (interaction.checkForSimilarity(agentFrom.getID(), agentTo.getID(), interactionType)){
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
	
	public String printStates(){
		return "";
	}
	
	public double getCurrentTickCount(){
		return -1.0;
	}
	
	/*****For Output/Printing Purposes******/
	/**
	 * 
	 * @param open
	 * @param close
	 * @return
	 */
	public String publishAgentInformation(String open, String close){
		String out = open + "id=\"" + getID() +"\"" + close + NEW_LINE;
		out += open + "type=\"" + getType() +"\"" + close + NEW_LINE;
		out += open + "position=\"" + getPosition().toString() +"\"" + close + NEW_LINE;
		out += open + "velocity=\"" + getVelocity().toString() +"\"" + close + NEW_LINE;
		out += open + "color=\"" + agentsColor.getRed() + COMMA + agentsColor.getGreen() + COMMA + agentsColor.getBlue() + "\"" + close; 
		return out;
	}
	
	public String publishAgentStates(String open, String close, String separator){
		String out = "";
		for (State<?> state : allStates){
			out += state.publishCurrentState(open, close, separator) + NEW_LINE;
		}		
		return out;
	}
	
	public ArrayList<State<?>> getAllStates(){
		return allStates;
	}
	
	public boolean compareAgent(Agent agent){
		return (ID.equalsIgnoreCase(agent.getID()));
	}
		
	/*****NEED SOME DEBUG METHODS HERE*****/
	
	public void toggleDebugMode(boolean debugMode){
		this.DEBUG_MODE = debugMode;
	}
	
	@Override
	public String toString(){
		return "ID: "+ ID +" Position: "+position.toString() +" Velocity: "+velocity.toString();			
	}
	
	/*****CAS Methods*****/
	public <T> void registerState(String stateName, T state){
//		stateOfInterest.registerNewState(stateName, state);
	}
	
	public <T> void updateState(String stateName, T state){
//		stateOfInterest.registerNewState(stateName, state);
	}
	
	public CASStateOfInterest getStateOfInterest(){
//		return stateOfInterest;
		return null;
	}
	
	public void print(String str){
		System.out.println(getID() + ": " + str);
	}
	

	/*****Logging Methods*****/
	public void disableLog(){
		logger.mute();
		logger.disableLogWrite();
	}
	
	public void muteLog(){
		logger.mute();
	}
	
	/*******Parameter Tracking*****/
	public HashMap<String,Parameter<?>> getParameters(){
		return parameters;
	}
	public <T> void addParameter(T o, String name){
		parameters.put(name,new Parameter<T>(o, name));
	}
	public <T> void updateParameter(String paramName, T value){
		addParameter(value, paramName);
//		parameters.put(paramName, parameters.get(paramName).updateValue(value));
	}
	
}
