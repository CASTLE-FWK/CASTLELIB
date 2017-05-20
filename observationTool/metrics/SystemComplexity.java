package observationTool.metrics;

import java.util.ArrayList;

import experimentExecution.SystemInfo;
import observationTool.results.AccuracyResults;
import observationTool.results.MetricResult;
import stdSimLib.Interaction;

/**
 * DESCRIPTION:
 * Determines the level of complexity (in terms of interactions) between two instances of the system execution.
 * 
 * LOGIC:
 * 	1: Get list of all interactions at step X. I(X)
 * 	2: Get list of all interactions at step Y (typically X+1) I(Y)
 * 	3a: Do (|I(Y)|/|I(X)|)*100;
 * 
 * REQUIREMENTS:
 * List of Interactions from 2 distinct steps from the same execution
 * 
 * 
 * @author lachlan
 *
 */
public class SystemComplexity extends MetricBase implements MetricInterface {
	
	double latestResults;

	public SystemComplexity() {
		super("SystemComplexity");
		// TODO Auto-generated constructor stub
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
	
	public double getLatestResult(){
		return latestResults;
	}

	@Override
	public void runMetric(Object... params) {
		//Check the list of this is what we need
		int reqCount = 0;

//		for (Object obj : params){
//			if (obj instanceof ArrayList<?>){
//				reqCount++;
//			}
//		}
//		if (reqCount != params.length){
//			System.out.println(metricName+" will not run. Requirements not met.");
//			//Kill here
//			latestResults = -Double.MAX_VALUE;
//			return;
//		}
		
		//This is all hard-codey
//		ArrayList<Interaction> stepX = (ArrayList<Interaction>)params[0];
//		ArrayList<Interaction> stepY = (ArrayList<Interaction>)params[1];
		
		double interactionsInStepX = (int)params[0];
		double interactionsInStepY = (int)params[1];
		double complexity;
		if (interactionsInStepX == 0 && interactionsInStepY == 0){
			complexity = 0.0;
		} else {
			complexity = (interactionsInStepY/interactionsInStepX)*100 - 100;
		}
		
		
		if (complexity == Double.NaN){
			complexity = 0;
		} 
		if (Double.isInfinite(complexity)){
			complexity = 0;
		}
		
		latestResults = complexity;		
	}
	
	//I have no idea how to do this
	public double calculateAccuracy(MetricResult mr, String resType1, String resType2, double thresholdT1, double thresholdT2){
		AccuracyResults theResult = new AccuracyResults();
		Double[] resultsCalculated = mr.getResultFromString(resType1);
		Double[] resultsReal = mr.getResultFromString(resType2);
		theResult = new AccuracyResults();
//		System.out.println("****Accuracy Calculation for "+experimentName);
//		System.out.println(String.format("Results Type 1: %1$s Results Type 2: %2$s. Threshold1: %3$f. Threshold2: %4$f", 
//				resType1, resType2, thresholdT1, thresholdT2));
		
		//Check null
		if (resultsCalculated == null || resultsReal == null){
			System.out.println("Accuracy Calc Warning: One results set is null.");
			return -11;
		}

		//Check size
		int resultsCalculatedLength = resultsCalculated.length;
		int resultsRealLength = resultsReal.length;
		int maxLength = resultsCalculatedLength;
		if (resultsCalculatedLength != resultsRealLength){
			System.out.println("Accuracy Calc Warning: Result sets have differing lengths");
			if (resultsCalculatedLength < resultsRealLength){
				maxLength = resultsRealLength;
			}
		}		
		
		int numberOfRealInstances = 0;
		for (int i = 0; i < resultsReal.length; i++){
			if (resultsReal[i] == 1){
				numberOfRealInstances++;		
			}
		}
		theResult.setRealHits(numberOfRealInstances);
	
		double hits = 0.0;
//		System.out.println("max length: "+maxLength);
		//Start matching things
		for (int i = 0; i < maxLength; i++){
			if (resultsCalculated[i] == null){
				System.out.println("HERE1: "+i);
			}
			if (resultsReal[i] == null){
				System.out.println("HERE2: "+i);
			}
			hits++;
						
			
			if ((Math.abs(resultsCalculated[i]) >= thresholdT1) && resultsReal[i] >= thresholdT2){
				//True positive
				theResult.addTruePositive();
			} else if ((Math.abs(resultsCalculated[i]) < thresholdT1) && resultsReal[i] < thresholdT2){
				//True Negative
				theResult.addTrueNegative();
			} else if ((Math.abs(resultsCalculated[i]) >= thresholdT1) && resultsReal[i] < thresholdT2){
				//False Positive
				theResult.addFalsePositive();
			} else if ((Math.abs(resultsCalculated[i]) < thresholdT1) && resultsReal[i] >= thresholdT2){
				//False Negative
				theResult.addFalseNegative();
			} else {
				System.out.println("Uh-oh :( Issue with Metric Accuracy Result Calculation");
				System.exit(0);
			}			
		}		
		
		if (hits != theResult.sanitySum()){
			System.out.println("MISMATCH IN HITS!*(!@*#*!@#*!@&*^#!@&*#*&!@^#*!@&^#*&!@^#*&@!%#*!&@^#*!@%#%!@*#");
		}
		if (!theResult.checkSanity()){
			System.out.println("SANITY CHECK FAILED (H*!@D(*!H@D(*H!@(*DH!@(*D");
		}
				
		double acc = hits/(double)numberOfRealInstances; //This is basically the true positive accuracy
		double perc = acc * 100.0;
		
		return perc;
//		return (double)theResult.getGoodHits()/(double)theResult.getTotalHits();
//		return theResult.calculateTPR() * 100.0;
//		return theResult.calculateTruePositiveRatio() * 100.0;
	}

	@Override
	public void runMetric(SystemInfo si) {
		// TODO Auto-generated method stub
		
	}

}
