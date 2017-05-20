package casFeatures;
import java.util.Set;

import stdSimLib.Agent;
import stdSimLib.Environment;

import java.util.HashMap;
import java.util.HashSet;
/**
 * 
 * @author lachlanbirdsey
 *
 */
public class CASStateChecker {
	//Need to register the elements that are considered to be checked
	//I.e. ones presented in the state_block (but this is something else.)
	//Why the fuck don't I just use a hashmap. Because I have to iterate over it later and this placing
	//part only happens once.
	private HashMap<String, CASStateOfInterest> previousMap;
	private HashMap<String, CASStateOfInterest> currentMap;
	
	private CASRuleSet ruleSet;
	
	private int threshold = 0; //what is this for?
	
	public CASStateChecker(CASRuleSet ruleSet){
		this.ruleSet = ruleSet;
		previousMap = new HashMap<String,CASStateOfInterest>();
		currentMap = new HashMap<String,CASStateOfInterest>();
	}
	
	public void init(){
		previousMap = new HashMap<String,CASStateOfInterest>();
		currentMap = new HashMap<String,CASStateOfInterest>();
	}
	
	//Setting up the initial Map
	public <T> void registerAgentState_Init(Agent agent) {
		//If state does not already exist
		CASStateOfInterest currentStateSet = previousMap.get(agent.getID());
		if (currentStateSet == null){
			previousMap.put(agent.getID(), new CASStateOfInterest(agent.getStateOfInterest())); 
		}
	}
	
	public <T> void registerEnvironmentState_Init(Environment env) {
		//If state does not already exist
		CASStateOfInterest currentStateSet = previousMap.get(env.getID());
		if (currentStateSet == null){
			previousMap.put(env.getID(), env.getStateOfInterest());
		}	
	}
	
	public void arm(){
		currentMap = new HashMap<String,CASStateOfInterest>();
	}
	

	public void cleanUp(){
		//Clone current into previous
		copyCurrentIntoPrevious();
		
		//Clear Current
		currentMap = new HashMap<String,CASStateOfInterest>();
	}
	
	public <T> void registerAgentState_Curr(Agent agent) {
		currentMap.put(agent.getID(), new CASStateOfInterest(agent.getStateOfInterest()));
	}
	
	public <T> void registerEnvironmentState_Curr(Environment env) {
		currentMap.put(env.getID(), new CASStateOfInterest(env.getStateOfInterest()));	
	}
	
	public int compareStates(){
		Set<String> previousKeys = new HashSet<String>(previousMap.keySet());
		Set<String> currentKeys = new HashSet<String>(currentMap.keySet());
		int score = 0;
		boolean res = false;
		
		if (previousKeys.size() != currentKeys.size()){
			System.out.println("Mobile agents are present.");
			System.out.println("Keys in previous: " + previousKeys.size());
			System.out.println("Keys in current: " + currentKeys.size());
			//What the hell do we do here
		}
		
		for (String key : previousKeys){
			CASStateOfInterest currentStateSet, previousStateSet;
			previousStateSet = previousMap.get(key);
			currentStateSet = currentMap.get(key);
			
			if (previousStateSet == null){
				//Do something
				System.out.println("Previous is null");
			}
			
			if (currentStateSet == null){
				//Do something
				System.out.println("Current is null");
			}
			res = previousStateSet.compareMap(currentStateSet);
			if (res){
				score++;
			}
		}
		cleanUp();
		return score;
		//REMOVED 27/10/15: switching from a boolean system to an integer system
		/*if (score > 0){
			prepareNextStep();
			return true;
		} else {
			prepareNextStep();
			return false;
		}*/
	}

	public void copyCurrentIntoPrevious(){
		Set<String> currentKeys = new HashSet<String>(currentMap.keySet());
		previousMap = new HashMap<String,CASStateOfInterest>();
		for (String key : currentKeys){
			previousMap.put(key,new CASStateOfInterest(currentMap.get(key)));
		}
		
		currentMap = new HashMap<String,CASStateOfInterest>();
	}
	
	//Getters & Setters
	public HashMap<String, CASStateOfInterest> getInitialMap() {
		return previousMap;
	}

	public void setInitialMap(HashMap<String, CASStateOfInterest> initialMap) {
		this.previousMap = initialMap;
	}

	public HashMap<String, CASStateOfInterest> getCurrentMap() {
		return currentMap;
	}	
}