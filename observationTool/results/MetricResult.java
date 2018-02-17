package observationTool.results;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import experimentExecution.SystemInfo;
import stdSimLib.utilities.Utilities;

//TODO: Need to do something about results stored as Strings...
public class MetricResult {
	private String systemName;
	private String metricName;
	private HashSet<String> resultNames;
	private HashSet<String> stringResultNames;
	private HashMap<String, Double[]> results;
	private HashMap<String, String[]> stringResults;
	private double runtime = 0.0;
	private int numberOfSteps = 0;
	private AccuracyResults theResult;
	private int numberOfRealInstances = 0;
	String[] printingOrder;

	private String resultsDir = "/Users/lachlan/repos/interlib/observationModule/results/";

	//TODO: SI HACK IS LAZY. NEED TO MAKE NICER
	public MetricResult(String sysName, String metricName, int totalNumberOfSteps, SystemInfo si, String resultsDir) {
		this.systemName = sysName;
		this.metricName = metricName;
		resultNames = new HashSet<String>();
		stringResultNames = new HashSet<String>();
		results = new HashMap<String, Double[]>();
		stringResults = new HashMap<String, String[]>();
		this.numberOfSteps = totalNumberOfSteps;
		printingOrder = new String[0];
		this.resultsDir = resultsDir;

		this.resultsDir += sysName.replaceAll("\\s+", "") + "/" + metricName.replaceAll("\\s+", "") + "/best/"
				+ si.getConfigurationString() + "_";
	}

	public void setPrintingOrder(String... headers) {
		printingOrder = headers;
	}

	public void addPrintingOrder(String str) {
		printingOrder = Arrays.copyOf(printingOrder, printingOrder.length + 1);
		printingOrder[printingOrder.length - 1] = str;
	}

	public void addResultType(String resultName) {
		resultNames.add(resultName);
		addPrintingOrder(resultName);
		Double[] d = new Double[numberOfSteps];
		Arrays.fill(d, 0.0);
		results.put(resultName, d);
	}

	public void addStringResultType(String resultName) {
		stringResultNames.add(resultName);
		String[] s = new String[numberOfSteps];
		Arrays.fill(s, "");
		stringResults.put(resultName, s);
	}

	public void addResult(String resultName, Double[] res) {
		resultNames.add(resultName);
		results.put(resultName, res);
	}

	public void addResult(String resultName, Double res) {
		resultNames.add(resultName);
		Double[] b = { res };
		results.put(resultName, b);
	}

	public void addResultAtStep(String resultName, double res, int step) {
		Double[] d = results.get(resultName);
		d[step] = res;
		results.put(resultName, d);
	}

	public void addStringResultAtStep(String resultName, String res, int step) {
		String[] s = stringResults.get(resultName);
		s[step] = res;
		stringResults.put(resultName, s);
	}

	public boolean addRunningResult(String resultName, double res) {
		Double[] d = results.get(resultName);
		if (d == null) {
			addResult(resultName, res);
			return false;
		}
		Double[] newD = Arrays.copyOf(d, d.length + 1);
		newD[newD.length - 1] = res;
		results.put(resultName, newD);
		return true;
	}

	public String getExperimentName() {
		return systemName;
	}

	public void addRuntime(double run) {
		this.runtime = run;
	}

	public void setExperimentName(String experimentName) {
		this.systemName = experimentName;
	}

	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public HashSet<String> getResultNames() {
		return resultNames;
	}

	public HashMap<String, Double[]> getResult() {
		return results;
	}

	public Double[] getResultFromString(String str) {
		return results.get(str);
	}

	public AccuracyResults getLastResult() {
		return theResult;
	}

	public double accuracyCalculation(String resType1, String resType2, double thresholdT1, double thresholdT2,
			int window, boolean saveToFile) {
		Double[] resultsCalculated = results.get(resType1);
		Double[] resultsReal = results.get(resType2);
		theResult = new AccuracyResults();
		//		System.out.println("****Accuracy Calculation for "+experimentName);
		//		System.out.println(String.format("Results Type 1: %1$s Results Type 2: %2$s. Threshold1: %3$f. Threshold2: %4$f", 
		//				resType1, resType2, thresholdT1, thresholdT2));

		//Check null
		if (resultsCalculated == null || resultsReal == null) {
			System.out.println("Accuracy Calc Warning: One results set is null.");
			return -11;
		}

		//Check size
		int resultsCalculatedLength = resultsCalculated.length;
		int resultsRealLength = resultsReal.length;
		int maxLength = resultsCalculatedLength;
		if (resultsCalculatedLength != resultsRealLength) {
			System.out.println("Accuracy Calc Warning: Result sets have differing lengths");
			if (resultsCalculatedLength < resultsRealLength) {
				maxLength = resultsRealLength;
			}
		}

		int[] totalSystemEvents = new int[maxLength];
		Arrays.fill(totalSystemEvents, 0);

		ArrayList<Integer> realStepsWithSODetected = new ArrayList<Integer>();
		ArrayList<Integer> realStepsWithNoSODetected = new ArrayList<Integer>();
		numberOfRealInstances = 0;
		for (int i = 0; i < resultsReal.length; i++) {
			if (resultsReal[i] == 1) {
				numberOfRealInstances++;
				realStepsWithSODetected.add(i);
			} else {
				realStepsWithNoSODetected.add(i);
			}
		}

		theResult.setRealHits(numberOfRealInstances);
		theResult.setNumberOfRealInstances(numberOfRealInstances);

		double gradientYSize = (double) window;

		ArrayList<Integer> stepsWithSODetected = new ArrayList<Integer>();
		ArrayList<Integer> stepsWithNoSODetected = new ArrayList<Integer>();

		int[] positiveHits = new int[maxLength];

		//		System.out.println("max length: "+maxLength);
		//Start matching things
		for (int i = window; i < maxLength; i += window) {
			if (resultsCalculated[i] == null) {
				System.out.println("HERE1: " + i);
			}
			if (resultsReal[i] == null) {
				System.out.println("HERE2: " + i);
			}

			//Calculate gradient
			//y is 1 (at the moment, unless we make it adaptive)
			double x = Math.abs(resultsCalculated[i] - resultsCalculated[i - window]);
			//			double grad = x;
			double grad = gradientYSize / x;
			//			if (grad > 1){
			////					System.out.println("gg: "+grad+" x: "+x+" gys: "+gradientYSize);
			//			}
			if (grad <= thresholdT1) {
				stepsWithSODetected.add(i);
			} else {
				stepsWithNoSODetected.add(i);
			}

		}

		//See how accurate the classifying was
		for (int i : stepsWithSODetected) {
			positiveHits[i] = 1;
			for (int j : realStepsWithSODetected) {
				if (i == j) {
					theResult.addTruePositive();
					break;
				}
			}
			for (int j : realStepsWithNoSODetected) {
				if (i == j) {
					theResult.addFalsePositive();
					break;
				}
			}
		}

		for (int i : stepsWithNoSODetected) {
			for (int j : realStepsWithSODetected) {
				if (i == j) {
					theResult.addFalseNegative();
					break;
				}
			}
			for (int j : realStepsWithNoSODetected) {
				if (i == j) {
					theResult.addTrueNegative();
					break;
				}
			}
		}

		if (saveToFile) {
			String toFileString = "#Steps\tRealEvents\tDetectedEvents\n";
			//We need to send to file the positive detections against the actual detections
			for (int i = 0; i < maxLength; i++) {
				toFileString += i + "\t" + resultsReal[i] + "\t" + positiveHits[i] + "\n";
			}
			Utilities.writeToFile(toFileString,
					resultsDir + resType1.replaceAll("\\s+", "") + "_" + resType2.replaceAll("\\s+", "") + ".tsv", false);
		}

		//Calculate Fowlkes-Mallows index
		//		double arg1 = (double)theResult.getTruePositives() / ((double)theResult.getTruePositives() + theResult.getFalsePositives());
		//		double arg2 = (double)theResult.getTruePositives() / ((double)theResult.getTruePositives() + theResult.getFalseNegatives());
		//		double fmi = Math.sqrt(arg1 * arg2);
		//		return fmi;

		//		return theResult.calculateTruePositiveRatio() * 100.0;
		//		return theResult.calculateACC() * 100.0;
		//		return theResult.calculatePPV() * 100.0;
		//		if (theResult.getNumberOfRealInstances() == 0){
		//			return -1;
		//		} else {
		//			return ((double)(theResult.getTruePositives() - theResult.getFalsePositives())/(double)theResult.getNumberOfRealInstances())*100;
		//		}
		//		

		//Used for results generated on the morning of 13/09
		// return (double)(theResult.getTruePositives()/(double)theResult.getNumberOfRealInstances())* 100.0;

		return theResult.F1Score() * 100.0;

		//Used for results generated on the evening of 13/09
		//		double tpreal = (double)(theResult.getTruePositives()/(double)theResult.getNumberOfRealInstances());
		//		double acc = theResult.calculateACC();

		//		return ((tpreal+acc)/2.0)*100.0;

	}

	public double accuracyCalculationForString(String resType1, String resType2, String sMatch1, String sMatch2) {
		String[] stringResultsCalculated = stringResults.get(resType1);
		String[] stringResultsReal = stringResults.get(resType2);
		//		System.out.println("****String Accuracy Calculation for "+experimentName);
		//		System.out.println(String.format("Results Type 1: %1$s Results Type 2: %2$s. Threshold1: %3$s. Threshold2: %4$s", 
		//				resType1, resType2, sMatch1, sMatch2));

		//Check null
		if (stringResultsCalculated == null || stringResultsReal == null) {
			System.out.println("Accuracy Calc Warning: One stringResults set is null.");
			return -11;
		}

		//Check size
		int stringResultsCalculatedLength = stringResultsCalculated.length;
		int stringResultsRealLength = stringResultsReal.length;
		int maxLength = stringResultsCalculatedLength;
		if (stringResultsCalculatedLength != stringResultsRealLength) {
			System.out.println("Accuracy Calc Warning: Result sets have differing lengths");
			if (stringResultsCalculatedLength < stringResultsRealLength) {
				maxLength = stringResultsRealLength;
			}
		}

		double hits = 0.0;
		//		System.out.println("max length: "+maxLength);
		//Start matching things
		for (int i = 0; i < maxLength; i++) {
			if (stringResultsCalculated[i] == null) {
				System.out.println("HERE1: " + i);
			}
			if (stringResultsReal[i] == null) {
				System.out.println("HERE2: " + i);
			}
			if (stringResultsCalculated[i].compareToIgnoreCase(sMatch1) == 0
					&& stringResultsReal[i].compareToIgnoreCase(sMatch2) == 0) {
				hits++;
			}
		}

		double acc = hits / (double) maxLength;
		double perc = acc * 100.0;

		return perc;

	}

	public String getResultStats() {
		String str = "";
		for (String resultName : resultNames) {
			str += resultName + " size: " + results.get(resultName).length + "\n";
		}

		return str;
	}

	//How is this going to work?
	//Ideally, this should spew out GNUPlot compatible stuff

	//TODO: Use the printingOrder array to print in consistent and correct order
	public String resultsToString() {
		StringBuilder sb = new StringBuilder();
		final String TAB = "\t";
		final String NEWLINE = "\n";
		Double[][] dd = new Double[resultNames.size()][];
		String[][] ss = new String[stringResultNames.size()][];
		int c = 0;
		sb.append("#".concat("step" + TAB));
		//Calculate max result size and print headers
		for (int i = 0; i < printingOrder.length; i++) {
			sb.append("#".concat(printingOrder[i]).concat(TAB));
			dd[c] = results.get(printingOrder[i]);
			c++;
		}
		//		for (String resName : resultNames){
		//			sb.append("#".concat(resName).concat(TAB));			
		//			dd[c] = results.get(resName);
		//			c++;
		//		}
		int s = 0;
		//		for (String resSName : stringResultNames){
		//			sb.append("#".concat(resSName).concat(TAB));
		//			ss[s] = stringResults.get(resSName);
		//			s++;
		//		}
		sb.append(NEWLINE);

		for (int i = 0; i < numberOfSteps; i++) {
			sb.append(i + TAB);
			for (int j = 0; j < printingOrder.length; j++) {
				if (i < dd[j].length) {
					sb.append(dd[j][i] + "".concat(TAB));
				}
			}
			//			for (int j = 0; j < printingOrder.length; j++){
			//				if (i < ss[j].length){
			//					sb.append(ss[j][i]+"".concat(TAB));
			//				}
			//			}
			sb.append(NEWLINE);
		}

		//Printing runtime
		sb.append("#runtime".concat(TAB).concat("" + runtime));
		return sb.toString();
	}

	public double calculateMin(String resultName) {
		Double[] d = results.get(resultName);
		if (d == null) {
			return Double.MAX_VALUE;
		}
		Double[] cd = Arrays.copyOf(d, d.length);
		Arrays.sort(cd);

		return cd[0];
	}

	public double calculateMax(String resultName) {
		Double[] d = results.get(resultName);
		if (d == null) {
			return -Double.MAX_VALUE;
		}
		Double[] cd = Arrays.copyOf(d, d.length);

		Arrays.sort(cd);

		return cd[cd.length - 1];
	}

	public double calculateMean(String resultName) {
		Double[] d = results.get(resultName);
		if (d == null) {
			return Double.MAX_VALUE;
		}
		Double[] cd = Arrays.copyOf(d, d.length);
		return Utilities.calculateMean(Arrays.asList(cd));
	}

	public double calculateMedian(String resultsName) {
		Double[] d = results.get(resultsName);
		if (d == null) {
			return Double.MAX_VALUE;
		}
		Double[] cd = Arrays.copyOf(d, d.length);
		return Utilities.calculateMedian(Arrays.asList(cd));
	}

	public double calculateSTDDev(String resultName) {
		Double[] d = results.get(resultName);
		if (d == null) {
			return Double.MAX_VALUE;
		}
		Double[] cd = Arrays.copyOf(d, d.length);
		return Utilities.calculateSTDDev(Arrays.asList(cd));
	}

	public double calculateMode(String resultName) {
		Double[] d = results.get(resultName);
		if (d == null) {
			return Double.MAX_VALUE;
		}
		Double[] cd = Arrays.copyOf(d, d.length);
		Arrays.sort(cd);
		int maxCount = 0;
		double maxVal = 0.0;
		for (int i = 0; i < cd.length; i++) {
			if (cd[i] != maxVal) {
				maxCount = 1;
				maxVal = cd[i];
			} else {
				maxCount++;
			}
		}
		return maxVal;
	}

	public String calculateStringMode(String resultName) {
		String[] d = stringResults.get(resultName);
		if (d == null) {
			return "";
		}
		String[] cd = Arrays.copyOf(d, d.length);
		Arrays.sort(cd);
		int maxCount = 0;
		String maxVal = "";
		for (int i = 0; i < cd.length; i++) {
			if (cd[i].compareToIgnoreCase(maxVal) != 0) {
				maxCount = 1;
				maxVal = cd[i];
			} else {
				maxCount++;
			}
		}
		return maxVal;
	}

	public int getNumberOfRealInstances() {
		return numberOfRealInstances;
	}

}
