package stdSimLib;

import java.util.ArrayList;

//Either build one in code or import(semi or full) from file
public class SystemDescription implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2438840233066464375L;
	static final String SNAPSHOT_INTERVAL = "snapshotInterval";
	static final String TOTAL_EXEC_STEPS = "totalExecutionSteps";
	static final String ENVIRONMENT = "environment";
	static final String MOBILE_AGENTS = "mobileAgents";
	static final String NAME = "name";
	static final String TIME = "time";
	static final String INTERVAL = "interval";
	static final String AGENTS = "agents";
	static final String AGENT = "agent";
	static final String STATES = "states";
	static final String INTERACTION_TYPES = "interactionTypes";
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
	static final String SYS_DEC = "systemDescription";
	
	String nameOfSystem;
	int snapshotInterval, totalExecutionSteps;
	ArrayList<State<?>> possibleAgentStates;
	ArrayList<String> possibleAgentInteractions;
	ArrayList<String> possibleAgentTypes;
	boolean mobileAgentsPresent = false;
	
	public SystemDescription(String nameOfSystem, int snapshotInterval, int totalExecutionSteps){
		this.nameOfSystem = nameOfSystem;
		this.snapshotInterval = snapshotInterval;
		this.totalExecutionSteps = totalExecutionSteps;
		possibleAgentStates = new ArrayList<State<?>>();
		possibleAgentInteractions = new ArrayList<String>();
		possibleAgentTypes = new ArrayList<String>();
	}

	public String publishSystemDescription(){
		String sysDec = OPENING_TAG + SYS_DEC + GREATER_THAN + NEW_LINE;
		sysDec += tabAcross(1) + OPENING_TAG + NAME + EQUALS + QUOTE + nameOfSystem + QUOTE + CLOSING_TAG + NEW_LINE;
		sysDec += tabAcross(1) + OPENING_TAG + SNAPSHOT_INTERVAL + EQUALS + QUOTE + snapshotInterval + QUOTE + CLOSING_TAG + NEW_LINE;
		sysDec += tabAcross(1) + OPENING_TAG + TOTAL_EXEC_STEPS + EQUALS + QUOTE + totalExecutionSteps + QUOTE + CLOSING_TAG + NEW_LINE;
		sysDec += tabAcross(1) + OPENING_TAG + STATES + GREATER_THAN + NEW_LINE;
		sysDec += publishAgentStates(tabAcross(2) + OPENING_TAG, CLOSING_TAG, COMMA);
		sysDec += tabAcross(1) + FRONT_CLOSING_TAG + STATES + GREATER_THAN + NEW_LINE;
		
		//TODO: ALL POSSIBLE INTERACTIONS (needed?)
		//sysDec += OPENING_TAG + INTERACTION_TYPES + GREATER_THAN + NEW_LINE;
		
		
		
		sysDec += tabAcross(1) + OPENING_TAG + ENVIRONMENT + GREATER_THAN + NEW_LINE;
		sysDec += tabAcross(2) + OPENING_TAG + MOBILE_AGENTS + EQUALS + QUOTE + mobileAgentsPresent + QUOTE + CLOSING_TAG + NEW_LINE;
		sysDec += tabAcross(1) + FRONT_CLOSING_TAG + ENVIRONMENT + GREATER_THAN + NEW_LINE;
		sysDec += FRONT_CLOSING_TAG + SYS_DEC + GREATER_THAN;
		
		return sysDec;
	}
	
	/**
	 * @return the nameOfSystem
	 */
	public String getNameOfSystem() {
		return nameOfSystem;
	}

	/**
	 * @param nameOfSystem the nameOfSystem to set
	 */
	public void setNameOfSystem(String nameOfSystem) {
		this.nameOfSystem = nameOfSystem;
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


	/**
	 * @return the totalExecutionSteps
	 */
	public int getTotalExecutionSteps() {
		return totalExecutionSteps;
	}


	/**
	 * @param totalExecutionSteps the totalExecutionSteps to set
	 */
	public void setTotalExecutionSteps(int totalExecutionSteps) {
		this.totalExecutionSteps = totalExecutionSteps;
	}


	/**
	 * @return the mobileAgentsPresent
	 */
	public boolean areMobileAgentsPresent() {
		return mobileAgentsPresent;
	}


	/**
	 * @param mobileAgentsPresent the mobileAgentsPresent to set
	 */
	public void setMobileAgentsPresent(boolean mobileAgentsPresent) {
		this.mobileAgentsPresent = mobileAgentsPresent;
	}
	
	public String publishAgentStates(String open, String close, String separator){
		String out = "";
		for (State<?> state : possibleAgentStates){
			out += state.publishStateWithType(open, close, separator) + NEW_LINE;
		}		
		return out;
	}
	
	public void addAgentStates(ArrayList<State<?>> states){
		for (State<?> state : states){
			possibleAgentStates.add(state);
		}
	}
	
	public String tabAcross(int x){
		String out = "";
		for (int i = 0; i < x; i ++){
			out += TAB;
		}		
		return out;
	}


}
