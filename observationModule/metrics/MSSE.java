package observationModule.metrics;

import java.util.ArrayList;

import experimentExecution.SystemInfo;
import observationModule.VEntity;
import observationModule.results.MetricResult;
import stdSimLib.Interaction;

/**
 * DESCRIPTION:
 * 	Multi-Scale Shannon Entropy (Parunak & Brueckner 2001)
 *  Define multiple scales of system states calculate the shannon entropy of them over time 
 *  For example: Ant Colony can have state defined as location-based or direction-based
 *  
 * 
 * LOGIC:
 * 	1: Define states for a particular system 
 * 	2: Implement those states (do this before running MSSE)
 *  3: "Train" a likelihood metric (e.g. run N replications to determine probabilities for each step)
 *  3a: For each training replication, determine the state at each step
 *  4: Calculate SE of current system step
 *  5: Normalise the output of SE by dividing by log(N)
 *   
 * 
 * REQUIREMENTS:
 *	• Current set: All Agents and Interactions (whatever the state definition requires)
 *	• Training set: All Agents and Interactions (whatever the state definition requires)
 * 
 * 
 * @author lachlan
 *
 */
public class MSSE extends MetricBase implements MetricInterface {

	double finalResult;
	
	public MSSE() {
		super("MSSE");
		// TODO Auto-generated constructor stub
	}



	public Object metricResults() {
		// TODO Auto-generated method stub
		return finalResult;
	}

	@Override
	public void runMetric(Object... params) {

	}
	
	
	
	public void train(int stepNumber, ArrayList<VEntity> agents, ArrayList<Interaction> interactions){
		
	}

	@Override
	public String getMetricInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetricResult getMetricResults() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void runMetric(SystemInfo si) {
		// TODO Auto-generated method stub
		
	}

}
