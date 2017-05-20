package stdSimLib;

import java.util.ArrayList;

import castleComponents.objects.Vector2;

//TODO: This is used for parsing and creating MetaModels. Used in various things...
public class MetaModel {
	
	static final String SNAPSHOT = "snapshot";
	static final String TIME = "time";
	static final String INTERVAL = "interval";
	static final String AGENTS = "agents";
	static final String AGENT = "agent";
	static final String STATES = "states";
	static final String INTERACTIONS = "interactions";
	static final String METRICS = "metrics";
	static final String NEW_LINE = "\n";
	static final String TAB = "\t";
	static final String OPENING_TAG = "<";
	static final String GREATER_THAN = ">";
	static final String CLOSING_TAG = "/>";
	static final String FRONT_CLOSING_TAG = "</";
	static final String SPACE = " ";
	static final String ID = "id";
	static final String TYPE = "type";
	static final String POSITION = "position";
	static final String VELOCITY = "velocity";
	static final String QUOTE = "\"";
	static final String EQUALS = "=";
	static final String COMMA = ",";
	static final String COLOUR = "color";
	
	/*State types*/
	static final String BOOLEAN = "boolean";
	static final String STRING = "string";
	static final String INTEGER = "integer";
	static final String LONG = "long";
	static final String DOUBLE = "double";
	
	/*Stuff for parsing a MetaModel*/
	boolean snapshotParsed = false;
	ArrayList<Agent> agents;
	ArrayList<Interaction> interactions;
	ArrayList<MetricScore> metricScores;
	int currentTime;
	int snapshotInterval;
	String experimentName;
	String runName;
	String pathOfMetaModel = "";
	
	/*Parsing States*/
	final int PARSE_INITIAL = 0;
	final int PARSE_SNAPSHOTINFO = 1;
	final int PARSE_AGENTS = 2;
	final int PARSE_AGENT = 3;
	final int PARSE_INTERACTIONS = 4;
	final int PARSE_STATE = 5;
	final int PARSE_SCORES = 6;
	int currentParsingState = PARSE_INITIAL;
	
	
	public MetaModel(){
		agents = new ArrayList<Agent>();
		interactions = new ArrayList<Interaction>();
		metricScores = new ArrayList<MetricScore>();
	}
	
	
	public MetaModel(String fromString, String path){
		agents = new ArrayList<Agent>();
		interactions = new ArrayList<Interaction>();
		metricScores = new ArrayList<MetricScore>();
		pathOfMetaModel = path;
		parseMetaModel(fromString);
	}
	
	public String buildMetaModel(ArrayList<Agent> agents, ArrayList<Interaction> interactions, ArrayList<MetricScore> metricScores, int currentTime, int snapshotInterval, String experimentName, String runName){
//		String mm = "";	
		StringBuilder metaModelString = new StringBuilder();
//		mm += buildingTimeEntry(currentTime, snapshotInterval, experimentName, runName) + NEW_LINE;
		metaModelString.append(buildingTimeEntry(currentTime, snapshotInterval, experimentName, runName) + NEW_LINE);
//		mm += tabAcross(1) + openTag(AGENTS) + NEW_LINE;
		metaModelString.append(tabAcross(1) + openTag(AGENTS) + NEW_LINE);
//		
		for (Agent agent : agents){			
//			mm += tabAcross(2) + openTag(AGENT) + NEW_LINE;
			metaModelString.append(tabAcross(2) + openTag(AGENT) + NEW_LINE);
//			mm += agent.publishAgentInformation(tabAcross(3) + OPENING_TAG, CLOSING_TAG) + NEW_LINE;
			metaModelString.append(agent.publishAgentInformation(tabAcross(3) + OPENING_TAG, CLOSING_TAG) + NEW_LINE);
//			mm += tabAcross(3) + openTag(STATES) + NEW_LINE;
			metaModelString.append(tabAcross(3) + openTag(STATES) + NEW_LINE);
//			mm += agent.publishAgentStates(tabAcross(4) + OPENING_TAG, CLOSING_TAG, COMMA);	//No new Line here as the publishAgentStates method handles it.
			metaModelString.append(agent.publishAgentStates(tabAcross(4) + OPENING_TAG, CLOSING_TAG, COMMA));
//			mm += tabAcross(3) + closeTag(STATES) + NEW_LINE;
			metaModelString.append(tabAcross(3) + closeTag(STATES) + NEW_LINE);
//			mm += tabAcross(2) + closeTag(AGENT) + NEW_LINE;
			metaModelString.append(tabAcross(2) + closeTag(AGENT) + NEW_LINE);
		}
//		
//		mm += tabAcross(1) + closeTag(AGENTS) + NEW_LINE;
		metaModelString.append(tabAcross(1) + closeTag(AGENTS) + NEW_LINE);
//		mm += tabAcross(1) + openTag(INTERACTIONS) + NEW_LINE;
		metaModelString.append(tabAcross(1) + openTag(INTERACTIONS) + NEW_LINE);
//		
		for (Interaction interaction : interactions){
//			mm += interaction.publishInteraction(tabAcross(2) + OPENING_TAG, CLOSING_TAG) + NEW_LINE;
			metaModelString.append(interaction.publishInteraction(tabAcross(2) + OPENING_TAG, CLOSING_TAG) + NEW_LINE);
		}
//		mm += tabAcross(1) + closeTag(INTERACTIONS) + NEW_LINE;
		metaModelString.append(tabAcross(1) + closeTag(INTERACTIONS) + NEW_LINE);
		
//		//Publish metric scores here
//		mm += tabAcross(1) + openTag(METRICS) + NEW_LINE;
		metaModelString.append(tabAcross(1) + openTag(METRICS) + NEW_LINE);
		for (MetricScore scores : metricScores){
//			mm += scores.toMetaModel(tabAcross(2) + OPENING_TAG, CLOSING_TAG) + NEW_LINE;
			metaModelString.append(scores.toMetaModel(tabAcross(2) + OPENING_TAG, CLOSING_TAG) + NEW_LINE);
		}
//		mm += tabAcross(1) + closeTag(METRICS) + NEW_LINE;
		metaModelString.append(tabAcross(1) + closeTag(METRICS) + NEW_LINE);
				
//		mm += closeTag(SNAPSHOT) + NEW_LINE;
		metaModelString.append(closeTag(SNAPSHOT) + NEW_LINE);
				
		return metaModelString.toString();
	}
	
	public void parseMetaModel(String string){	
		agents = new ArrayList<Agent>();
		interactions = new ArrayList<Interaction>();
		currentParsingState = PARSE_INITIAL;
		String[] lines = string.split(NEW_LINE);
		String tempString = "";
		for (String str : lines){
			str = str.replaceAll("\\s","");
			if (isOpening(str, SNAPSHOT)) {
				currentParsingState = PARSE_SNAPSHOTINFO;	
				
			} else if (isOpening(str, AGENTS)) {
				currentParsingState = PARSE_AGENTS;
				
			} else if (isOpening(str, AGENT)) {
				currentParsingState = PARSE_AGENT;
				
			} else if (isClosed(str, AGENT)){
				parseAgent(tempString);
				tempString = "";
				currentParsingState = PARSE_AGENTS;
				
			} else if (isOpening(str, INTERACTIONS)){
				currentParsingState = PARSE_INTERACTIONS;
				continue;
				
			} else if (isClosed(str, INTERACTIONS)){
				//End Of Useful Stuff
				currentParsingState = PARSE_INITIAL;
			} else if (isOpening(str, METRICS)){
				currentParsingState = PARSE_SCORES;
				continue;
				
			} else if (isClosed(str, METRICS)){
				currentParsingState = PARSE_INITIAL;
				break;
			}
				
			switch(currentParsingState){
			case PARSE_INITIAL:
				break;
				
			case PARSE_SNAPSHOTINFO:
				parseSnapshotInformation(str);				
				break;
				
			case PARSE_AGENTS:				
				break;
				
			case PARSE_AGENT:
				tempString += str + NEW_LINE;
				break;
				
			case PARSE_INTERACTIONS:
				parseInteraction(str);
				break;
			
			case PARSE_SCORES:
				parseMetricScore(str);
				break;
				
			default:			
				break;
			
			}			
		}	
		
		snapshotParsed = true;
	}
	
	public String openTag(String tag){
		return "<"+tag+">";
	}
	
	public String closeTag(String tag){
		return "</"+tag+">";
	}
	
	public String inlineTag(String tag){
		return "<"+tag+"/>";
	}
	public String insideQuotes(String contents){
		return "\""+contents+"\"";
	}
	
	public String buildingTimeEntry(int currentTime, int snapshotInterval, String experimentName, String runName){
		String str = OPENING_TAG + SNAPSHOT + SPACE + "currentTime=" +insideQuotes(""+currentTime) + SPACE + "intervalSize=" +insideQuotes(""+snapshotInterval);
		str += SPACE + "experimentName="+insideQuotes(experimentName) + SPACE + "runName=" + insideQuotes(runName) + GREATER_THAN;
		return str;
	}
	
	public String tabAcross(int x){
		String out = "";
		for (int i = 0; i < x; i ++){
			out += TAB;
		}		
		return out;
	}
	
	
	public Agent findAgentByID(String id){
		for (Agent agent : agents){
			if (agent.getID().compareTo(id) == 0){
				return agent;
			}
		}
		System.out.println("AGENT "+id+" NOT FOUND (MM)");
		return null;
	}
	
	/***PARSING FUNCTIONS***/

	/**
	 * Gets the current time and snapshot interval information
	 * @param str
	 */
	public void parseSnapshotInformation(String str){
		String[] contents = str.split(QUOTE);
		
		//1 contains the currentTime
		currentTime = Integer.parseInt(contents[1]);
		
		//3 contains the snapshotInterval
		snapshotInterval = Integer.parseInt(contents[3]);
		
		//5 contains the experiment information
		experimentName = contents[5];
		
		//7 contains the run name
		runName = contents[7];				
	}

	public void parseAgent(String str){
		String id = "";
		String type = "";
		Vector2 position = new Vector2();
		Vector2 velocity = new Vector2();
		Agent newAgent = null;
		int[] colour = null;
		ArrayList<State<?>> states = new ArrayList<State<?>>();
		int agentParsingMode = PARSE_AGENT;
		//String[] lines = str.replaceAll(OPENING_TAG, "").replaceAll(CLOSING_TAG, "").split(NEW_LINE);
		String[] lines = str.split(NEW_LINE);
		for (String string : lines){
			if (agentParsingMode == PARSE_AGENT){
				if (string.contains(ID)){
					id = string.split(QUOTE)[1];
				} else if (string.contains(TYPE)){
					type = string.split(QUOTE)[1];
				} else if (string.contains(POSITION)){
					position = new Vector2(string.split(QUOTE)[1]);
				} else if (string.contains(VELOCITY)){
					velocity = new Vector2(string.split(QUOTE)[1]);
				} else if (string.contains(STATES)){
					agentParsingMode = PARSE_STATE;
				} else if (string.contains(COLOUR)){
					colour = parseColour(string.split(QUOTE)[1]);
				}
			} else if (agentParsingMode == PARSE_STATE){				
				if (isClosed(string, STATES)){
					agentParsingMode = PARSE_AGENT;
				} else {
					String[] parseState = string.replaceAll(OPENING_TAG, "").replaceAll(CLOSING_TAG, "").split(QUOTE);
					String stateName = parseState[1];
					String stateType = parseState[3];				
					String stateValue = parseState[5];

					states.add(generateState(stateType, stateName, stateValue));
				}				
			}								
		}
		
		if (id.length() > 0 && type.length() > 0){
			newAgent = new Agent(id, position);
			newAgent.setType(type);
			newAgent.setVelocity(velocity);
			newAgent.setSnapshotInterval(snapshotInterval);
			newAgent.addStates(states, true);	
			newAgent.setAgentsColorFromInt(colour);
					
			agents.add(newAgent);		
		}
	}
	
	public void parseInteraction(String str){
		Interaction newInteraction;
		String agentFrom, agentTo, type;
		int count;
		String[] interactionSplit = str.replaceAll(OPENING_TAG, "").replaceAll(CLOSING_TAG, "").replaceAll(EQUALS, "").split(QUOTE);
		agentFrom = interactionSplit[1];
		agentTo = interactionSplit[3];
		type = interactionSplit[5];
		count = Integer.parseInt(interactionSplit[7]);
		
		newInteraction = new Interaction(findAgentByID(agentFrom), findAgentByID(agentTo), type);
		newInteraction.setOccurrence(count);
		
		interactions.add(newInteraction);
	}
	
	public void parseMetricScore(String str){
		MetricScore newMS;
		String metricName, metricConfig, referenceSSIF, result;
		String[] msSplit = str.replaceAll(OPENING_TAG, "").replaceAll(CLOSING_TAG, "").replaceAll(EQUALS, "").split(QUOTE);
		metricName = msSplit[1];
		metricConfig = msSplit[3];
		referenceSSIF = msSplit[5];
		result = msSplit[7];
		
		newMS = new MetricScore(metricName, metricConfig, referenceSSIF, result);
		
		metricScores.add(newMS);
	}
	
	public boolean isOpening(String str, String lookingFor){
		return (!str.contains("/")) && str.contains(lookingFor);  
	}
	
	public boolean isClosed(String str, String lookingFor){
		boolean bool = (str.endsWith(CLOSING_TAG) && str.contains(lookingFor)) ||
				(str.startsWith(FRONT_CLOSING_TAG) && str.contains(lookingFor));
	
		return bool;
	}
	
	public State<?> generateState(String stateType, String stateName, String stateValue){
		switch (stateType){
			case BOOLEAN:
				return new State<Boolean>(stateName, Boolean.parseBoolean(stateValue));
			case STRING:
				return new State<String>(stateName, stateValue);				
			case INTEGER:
				return new State<Integer>(stateName, Integer.parseInt(stateValue));				
			case DOUBLE:
				return new State<Double>(stateName, Double.parseDouble(stateValue));				
			case LONG:
				return new State<Long>(stateName, Long.parseLong(stateValue));
			default:
				return new State<String>(stateName, stateValue);
		}		
	}
	
	public int[] parseColour(String colourString){
		int[] out = new int[3];
		String[] values = colourString.split(COMMA);
		
		out[0] = Integer.parseInt(values[0]);
		out[1] = Integer.parseInt(values[1]);
		out[2] = Integer.parseInt(values[2]);
		
		return out;
	}

	/**Setters and getters**/
	
	/**
	 * @return the experimentName
	 */
	public String getExperimentName() {
		return experimentName;
	}

	/**
	 * @return the runName
	 */
	public String getRunName() {
		return runName;
	}
	
	/**
	 * @return the agents
	 */
	public ArrayList<Agent> getAgents() {
		return agents;
	}

	/**
	 * @param agents the agents to set
	 */
	public void setAgents(ArrayList<Agent> agents) {
		this.agents = agents;
	}

	/**
	 * @return the interactions
	 */
	public ArrayList<Interaction> getInteractions() {
		return interactions;
	}

	/**
	 * @param interactions the interactions to set
	 */
	public void setInteractions(ArrayList<Interaction> interactions) {
		this.interactions = interactions;
	}
	
	public int getCurrentTime(){
		return currentTime;
	}
	
	public int getSnapshotInterval(){
		return snapshotInterval;
	}

	/**
	 * @return the pathOfMetaModel
	 */
	public String getPathOfMetaModel() {
		return pathOfMetaModel;
	}

	/**
	 * @param pathOfMetaModel the pathOfMetaModel to set
	 */
	public void setPathOfMetaModel(String pathOfMetaModel) {
		this.pathOfMetaModel = pathOfMetaModel;
	}


	/**
	 * @return the metricScores
	 */
	public ArrayList<MetricScore> getMetricScores() {
		return metricScores;
	}	
}