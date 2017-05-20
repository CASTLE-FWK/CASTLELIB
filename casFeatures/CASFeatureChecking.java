package casFeatures;

import java.util.ArrayList;

import casFeatures.CASLib.CAS_Inspection_Level;
import casFeatures.CASLib.CAS_Rule_Exception;
import stdSimLib.Agent;
import stdSimLib.Environment;

/**
 * This class is to check CAS features
 * Use cases:
 * type = strict //All features are inspected and checked
 * type = lenient exceptions = none //Same as strict (why?)
 * type = lenient exceptions = diversity //Same as strict but ignores diversity checks
 * @author lachlanbirdsey
 *
 */
public class CASFeatureChecking {
	
	private CASRuleSet ruleSet;
	private CAS_Inspection_Level inspectionLevel;
	private CAS_Rule_Exception[] exceptions;
	
	private CASStateChecker stateChecker;
	
	private boolean doModularityCheck = true;
	private boolean doAdaptationCheck = true;
	private boolean doDiversityCheck = true;
	private ArrayList<ArrayList<Agent>> initialAgentList;
	private ArrayList<ArrayList<Agent>> currentAgentList;
	private ArrayList<ArrayList<Environment>> initialEnvironmentList;
	private ArrayList<ArrayList<Environment>> currentEnvironmentList;
	
	private int adaptationPassSteps = 0; //over total run
	int adaptationPass = 0; //perStep
	
	StabilityCalculator stabilityCalculator;
	
	private int currentStepNumber = 0;
	
	//Some form of storage is needed here
	
	public CASFeatureChecking(CASRuleSet ruleSet){
		this.ruleSet = ruleSet;
		inspectionLevel = this.ruleSet.getChecking();
		exceptions = this.ruleSet.getExceptions();
		for (CAS_Rule_Exception ex : exceptions){
			if (ex == CAS_Rule_Exception.MODULARITY) {
				doModularityCheck = false;
			} else if (ex == CAS_Rule_Exception.ADAPTATION) {
				doAdaptationCheck = false;
			} else if (ex == CAS_Rule_Exception.DIVERSITY) {
				doDiversityCheck = false;
			} else if (ex == CAS_Rule_Exception.NONE) {
				doModularityCheck = true;
				doAdaptationCheck = true;
				doDiversityCheck = true;
			}			
		}	
		
		String str = "Feature Checker:";
		str += "\nModularity Checks: " + doModularityCheck;
		str += "\nAdaptation Checks: " + doAdaptationCheck;
		str += "\nDiversity Checks: " + doDiversityCheck;
		System.out.println(str);
		
		stabilityCalculator = new StabilityCalculator(400); //where to set this???~??!?!
	}
	
	public void initialiseAgentList(ArrayList<ArrayList<Agent>> agentLists){
		this.initialAgentList = agentLists;
	}
	
	public void initialiseEnvironmentList(ArrayList<ArrayList<Environment>> environmentLists){
		this.initialEnvironmentList = environmentLists;
	}
	
	public void initialiseStateChecker(CASStateChecker cassc){
		this.stateChecker = cassc;
	}
	
	public void init(){
		adaptationPassSteps = 0;
	}
	
	public void newStep(int stepNumber){
		this.currentStepNumber = stepNumber;
	}
	
	//Returns false if a CAS feature is not upheld
	//I wonder if we can use this in the validation script for static validation
	//UPDATE 26/10/15: envLists addition will break things without envs (OR WILL IT?!?!?!)
	public boolean run(ArrayList<ArrayList<Agent>> agentLists, ArrayList<ArrayList<Environment>> envLists) {
		currentAgentList = agentLists;
		currentEnvironmentList = envLists;

		boolean modularityPass = true, diversityPass = true;
		if (doAdaptationCheck){
			adaptationPass = adaptationCheck();
			if (adaptationPass > 0) {
				adaptationPassSteps++;
			}
		}
		if (doModularityCheck){
			modularityPass = modularityCheck();
		}
		if (doDiversityCheck){
			diversityPass = diversityCheck();
			if (!diversityPass){
//				System.out.println("CAS is not diverse!");
			}
		}
		
		return (diversityPass && modularityPass && (adaptationPass > 0));
	}
	
	public boolean diversityCheck(){
		boolean pass = true;
		switch (inspectionLevel) {
			case STRONG:
				ArrayList<String> agentTypes = new ArrayList<String>();
				for (ArrayList<Agent> list : currentAgentList){
					for (Agent agent : list){
						if (!agentTypes.contains(agent.getType())){
							agentTypes.add(agent.getType());
						}
					}
				}
				//Only a single type of Agent is present
				if (agentTypes.size() <= 1){
					pass = false;
				} else {
					System.out.println("agentTypes: " + agentTypes.size());
				}
			break;
			case WEAK:
				pass = true;
			break;
			case NONE:
				pass =  true;
				
			break;
		}
		return pass;
	}
	
	public boolean modularityCheck(){
		boolean result = false;
		switch (inspectionLevel) {
			case STRONG:
				//How is this going to work?
			break;
			case WEAK:
				result = true;
			break;
			case NONE:
				result =  true;
				
			break;
		}
		return result;
	}
	//There is a faster way to do this, I really need to figure that out
	
	/**
	 * Returns the number of agents/envs whose states have changed since
	 * the last step
	 * @return
	 */
	public int adaptationCheck(){
		int result = 0;
		switch (inspectionLevel) {
			case STRONG:
				stateChecker.arm();
				int trackedAgents = 0;
				
				for (ArrayList<Agent> list : currentAgentList){
					for (Agent agent : list){
						stateChecker.registerAgentState_Curr(agent);
						trackedAgents++;
					}
				}
				int trackedEnvironments = 0;
				for (ArrayList<Environment> list : currentEnvironmentList){
					for (Environment env : list){
						stateChecker.registerEnvironmentState_Curr(env);
						trackedEnvironments++;
					}
				}
				result = stateChecker.compareStates();
//				stabilityCalculator.setNumberOfTrackedStates(trackedAgents + trackedEnvironments); 
//				stabilityCalculator.newValue(result, currentStepNumber); //I think it should be result to be passed...(that's what I print out i think...)
			break;
			case WEAK:
				result = 0;
			break;
			case NONE:
				result =  0;
				
			break;
		}
		return result;
	}
	
	public String stepWiseReport(){
//		String str = "adaptation change:,"+adaptationPass;
		String str = ""+adaptationPass;
		return str;
	}
	
	public int getAdaptationPass(){
		return adaptationPassSteps;
	}
}
