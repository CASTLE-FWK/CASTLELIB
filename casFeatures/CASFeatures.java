package casFeatures;

import java.util.ArrayList;

import stdSimLib.Agent;
import stdSimLib.Environment;

//This is the main CAS Class
public class CASFeatures {

	private String CASName;
	private CASRuleSet ruleSet;
	private CASFeatureChecking featureChecking;
	private CASStateChecker stateChecker;
	private ArrayList<ArrayList<Agent>> initialMapOfAgentLists; //This is the initial list
	private ArrayList<ArrayList<Agent>> currentMapOfAgentLists; //This is a current list
	private ArrayList<ArrayList<Environment>> initialMapOfEnvLists; //This is the initial list
	private ArrayList<ArrayList<Environment>> currentMapOfEnvLists; //This is a current list
	private int numOfAgentLists = 0;
	
	private int currentStepNumber = 0;
	
	public CASFeatures(String name, CASRuleSet ruleSet){
		CASName = name;
		
		//CAS Parameters
		this.ruleSet = ruleSet;
		System.out.println("Ruleset is:\n"+this.ruleSet.toString()+"\n");
		
		featureChecking = new CASFeatureChecking(ruleSet);		
		
		initialMapOfAgentLists = new ArrayList<ArrayList<Agent>>();
		currentMapOfAgentLists = new ArrayList<ArrayList<Agent>>();
		initialMapOfEnvLists = new ArrayList<ArrayList<Environment>>();
		currentMapOfEnvLists = new ArrayList<ArrayList<Environment>>();
		stateChecker = new CASStateChecker(ruleSet);
		
		currentStepNumber = 0;
	}
	
	public void init(){
		featureChecking.init();
		stateChecker.init();
		featureChecking.initialiseAgentList(initialMapOfAgentLists);
		featureChecking.initialiseEnvironmentList(initialMapOfEnvLists);
		featureChecking.initialiseStateChecker(stateChecker);
		currentStepNumber = 0;
	}
	
	public void addAgentList(String agentType, ArrayList<Agent> agList){
		initialMapOfAgentLists.add(agList);		
	}
	
	public void addEnvironmentList(String envType, ArrayList<Environment> envList){
		initialMapOfEnvLists.add(envList);
	}
	
	public void newCheck(int stepNumber){
		this.currentStepNumber = stepNumber;
		currentMapOfAgentLists.clear();
		currentMapOfEnvLists.clear();
	}
	
	public void addAgentListForNewCheck(String agentType, ArrayList<Agent> agList){		
		currentMapOfAgentLists.add(agList);
	}
	
	public void addEnvironmentListForNewCheck(String agentType, ArrayList<Environment> agList){		
		currentMapOfEnvLists.add(agList);
	}

	/*****CAS State Checking*****/
	//Initial State Collection
	public <T> void registerAgentState_Init(Agent agent){
		stateChecker.registerAgentState_Init(agent);
	}
	public <T> void registerEnvironmentState_Init(Environment env){
		stateChecker.registerEnvironmentState_Init(env);
	}
	
	//Running State Collection
	public <T> void registerAgentState_Curr(Agent agent){
		stateChecker.registerAgentState_Curr(agent);
	}
	public <T> void registerEnvironmentState_Curr(Environment env){
		stateChecker.registerEnvironmentState_Curr(env);
	}
	
	/*****CAS Feature Checking*****/
	
	
	public void runChecks(){
		//Logically, Type checks and Feature checks should be done
		//in parallel since that's easy and faster BUT
		//we need to organise feature checks first, along with
		//how we actually plan on doing this
		
		//Feature inspection
		//This needs to be enhanced from a Boolean because multiple things
		featureChecking.newStep(currentStepNumber);
		boolean result = featureChecking.run(currentMapOfAgentLists, currentMapOfEnvLists);
		
		//State Inspection (if we really need to do this)
	}
	
	public String stepWiseReport(){
		String str = "";
		str += featureChecking.stepWiseReport();
		
		return str;
	}
	
	public String finalCall(){
		return "adaptation acheieved: "+featureChecking.getAdaptationPass();
	}

	public String getCASName() {
		return CASName;
	}

	public void setCASName(String cASName) {
		CASName = cASName;
	}

	public CASFeatureChecking getFeatureChecking() {
		return featureChecking;
	}

	public void setFeatureChecking(CASFeatureChecking featureChecking) {
		this.featureChecking = featureChecking;
	}

	public CASStateChecker getTypeChecker() {
		return stateChecker;
	}

	public void setTypeChecker(CASStateChecker typeChecker) {
		this.stateChecker = typeChecker;
	}
	
}
