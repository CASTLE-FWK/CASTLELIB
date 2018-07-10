package observationTool;

import interactionGraph.InteractionGraph;
import observationTool.metrics.ChanGoLInterMetric;
import observationTool.metrics.ClusterTrack;
import observationTool.metrics.Entropy;
import observationTool.metrics.MSSE_State;
import observationTool.metrics.MetricParameters;
import observationTool.metrics.OToole14Metric;
import observationTool.metrics.SelfAdaptiveSystems;
import observationTool.metrics.SimpleStatistic;
import observationTool.metrics.SystemComplexity;
import observationTool.metrics.transportationNetwork.Counter;
import observationTool.results.AccuracyResults;
import observationTool.results.MetricResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import stdSimLib.Interaction;
import stdSimLib.utilities.RandomGen;
import stdSimLib.utilities.Utilities;

import org.bson.Document;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import castleComponents.objects.Vector2;
import castleComponents.representations.Grid;
import experimentExecution.Experiment;
import experimentExecution.JsonParser;
import experimentExecution.MetricInfo;
import experimentExecution.MetricVariableMapping;
import experimentExecution.SystemInfo;

public class MetricRunner {

	static String db;
	static String collectionID;
	// static String systemName = "";
	static String experimentID = "";
	static String initCriteria = "";

	static String experimentDirRoot = "NOT A DIRECTORY";
	static String resultsDirRoot = "STILL NOT A DIRECTORY";

	static int[] realEvents_emergence;
	static int[] realEvents_stability;
	static int[] realEvents_criticality;
	static int[] realEvents_adaptability;
	static String realEventsNameEm = "Real Event Occurred (Emergence)";
	static String realEventsNameSt = "Real Event Occurred (Stability)";
	static String realEventsNameCr = "Real Event Occurred (Criticality)";
	static String realEventsNameAd = "Real Event Occurred (Adaptability)";

	static String REAL_EM_LOCATION = "/realEmergence.txt";
	static String REAL_ST_LOCATION = "/realStability.txt";
	static String REAL_CR_LOCATION = "/realCriticality.txt";
	static String REAL_AD_LOCATION = "/realAdaptability.txt";

	static int areaX;
	static int areaY;

	static boolean quiet = false;
	static boolean testing = false;
	static boolean noAccuracyCalculations = false;

	// static ArrayList<MetricResult> allResults;
	static MetricResult currentResult;

	public static void main(String[] args) {
		String analysisToRun = args[0];
		testing = Boolean.parseBoolean(args[1]);
		db = "simulations"; // WRONG

		JsonObject experimentMeta = JsonParser.parseFileAsJson(analysisToRun);

		experimentDirRoot = experimentMeta.get("experiments-directory").asString();
		resultsDirRoot = experimentMeta.get("results-directory").asString();
		JsonArray experimentFiles = experimentMeta.get("experiment-files").asArray();

		ExecutorService masterES = Executors.newFixedThreadPool(experimentFiles.size());
		for (JsonValue jo : experimentFiles) {
			masterES.execute(new Runnable() {
				@Override
				public void run() {

					String line = jo.asString();
					String experimentName = "";
					// The file now contains a list of paths to experiment JSON files
					StringBuilder toTheDoc = new StringBuilder();

					// Print everything out to a MetricResult object
					ArrayList<MetricResult> allResults = new ArrayList<MetricResult>();
					DataCollector_FileSystem collector = new DataCollector_FileSystem(db);
					String notes = "#Using the grad difference with a step size between 1 and 20, maximising average of F1 Score";

					toTheDoc.append(notes + "\n");
					toTheDoc.append(
							"System Name\tMetric Name\tSO Type\tThreshold\tTP/Real\tAccuracy\tSpecificity\tSensitivity\tPrecision\tActual Events\tTrue Positives\tFalse Positives\tTrue Negatives\tFalse Negatives\n");
					Experiment exp = JsonParser.parseExperiment(experimentDirRoot.concat(line));
					print(exp.toString());
					ArrayList<SystemInfo> theTestSystems = exp.getTestSystems();
					System.out.println("Number of Systems to Analyse: " + theTestSystems.size());

					double runtime = System.currentTimeMillis();
					ExecutorService es = Executors.newFixedThreadPool(theTestSystems.size());
					ConcurrentHashMap<String, ArrayList<MetricResult>> threadResultsStore = new ConcurrentHashMap<String, ArrayList<MetricResult>>();

					for (int test = 0; test < theTestSystems.size(); test++) {
						SystemInfo currTestSystem = theTestSystems.get(test);
						String experimentDataLocation = currTestSystem.getSystemDataLocation();
						collector.setCollection(experimentDataLocation);
						currTestSystem.setNumberOfSteps(collector.getTerminationStep());

						currentResult = new MetricResult(currTestSystem.getConfigurationString(), "AllMetrics",
								currTestSystem.getNumberOfSteps(), currTestSystem, resultsDirRoot);
						collector.restart(); // TODO how to handle this with Mongo
						DataCollector_FileSystem newColl = new DataCollector_FileSystem(collector);
						es.execute(new Runnable() {
							@Override
							public void run() {
								threadResultsStore.put(currTestSystem.getSystemDataLocation(),
										runAnalysis(exp, currTestSystem, newColl));
							}
						});

						// allResults.add(//currentResult); // Lets hope PBR actually behaves
						// allResults.addAll(theseResults); //TODO This wont work threaded
					}
					es.shutdown();
					while (!es.isTerminated()) {
						// Busy wait
					}

					// Append all the results
					for (ArrayList<MetricResult> mr : threadResultsStore.values()) {
						MetricResult prim = mr.get(0);
						for (int i = 1; i < mr.size(); i++) {
							prim.append(mr.get(i));
						}
						allResults.add(prim);
					}

					// collector.close();
					runtime = System.currentTimeMillis() - runtime;
					println("Total runtime: %1$f seconds", runtime / 1000);
					toTheDoc.append("\n#runtime\t" + runtime);
					String dirTimeStamp = resultsDirRoot + exp.getExperimentID().replaceAll("\\s+", "") + "_"
							+ Utilities.generateTimeID() + "/";
					// Write results to file
					if (!testing) {
						Utilities.writeToFile(toTheDoc.toString(),
								dirTimeStamp + "metricresults_" + Utilities.generateTimeID() + ".tsv", false);
					}
					System.out.println("Number of results: " + allResults.size());
					for (MetricResult r : allResults) {
						Utilities.writeToFile(r.resultsToString(),
								dirTimeStamp + r.getExperimentName().replaceAll("\\s+", "") + "_allMetrics.tsv", false);
					}
				}
			});
		}
		masterES.shutdown();
		while (!masterES.isTerminated()) {
			// Busy wait
		}
		System.out.println("All Metric Runner processes have finished");
	}

	public static void ex() {
		System.out.println("****EXITING*&**@!*@");
		System.exit(0);
	}

	// This is what we want to thread
	public static ArrayList<MetricResult> runAnalysis(Experiment e, SystemInfo thisTestSystem,
			DataCollector_FileSystem collector) {
		SystemInfo theTestSystem = thisTestSystem;
		String experimentID = e.getExperimentID();
		String experimentDataLocation = theTestSystem.getSystemDataLocation();
		String systemName = theTestSystem.getSystemName();
		// MetricRunner.systemName = systemName; // TODO: Think about this issue

		String systemConfiguration = theTestSystem.getConfigurationName();
		MetricRunner.initCriteria = theTestSystem.getConfigurationString();
		String systemString = theTestSystem.getConfigurationDimensions();

		collector.setCollection(experimentDataLocation);

		// HashMap<String, String> sysParams = collector.getInitialisationParameters();
		// areaX = 0;
		// areaY = 0;
		String initName = "";
		int numberOfAgents = 0;
		// if (sysParams.containsKey("Size (X)")) {
		// areaX = Integer.parseInt(sysParams.get("Size (X)"));
		// }
		// if (sysParams.containsKey("Size (Y)")) {
		// areaY = Integer.parseInt(sysParams.get("Size (Y)"));
		// }
		// if (sysParams.containsKey("initPath")) {
		// initName = sysParams.get("initPath");
		// }
		// if (sysParams.containsKey("cellPopulation")) {
		// numberOfAgents = Integer.parseInt("cellPopulation");
		// }

		// Print out dataset information
		println("*******DATASET INFORMATION*******");
		println("System Name: " + systemName);
		println("Experiment ID: " + experimentID);
		println("InitPath: " + initName);
		println("Number of Agents: " + numberOfAgents);
		println("InitCrit: " + theTestSystem.getConfigurationString());
		if (systemName.length() <= 0) {
			println("NO SYSTEM NAME ENTERED");
			System.exit(0);
		}

		// LETS BUILD VAGENTS
		int totalNumberOfSteps = collector.getTerminationStep();
		if (testing) {
			totalNumberOfSteps = 1000;
		}
		theTestSystem.setNumberOfSteps(totalNumberOfSteps);

		// Prep real events arrays
		realEvents_emergence = new int[totalNumberOfSteps];
		Arrays.fill(realEvents_emergence, 0);
		realEvents_stability = new int[totalNumberOfSteps];
		Arrays.fill(realEvents_stability, 0);
		realEvents_criticality = new int[totalNumberOfSteps];
		Arrays.fill(realEvents_criticality, 0);
		realEvents_adaptability = new int[totalNumberOfSteps];
		Arrays.fill(realEvents_adaptability, 0);

		// Add the real events to the current results
		// currentResult.addResultType(realEventsNameEm);
		// currentResult.addResultType(realEventsNameSt);
		// currentResult.addResultType(realEventsNameCr);
		// currentResult.addResultType(realEventsNameAd);

		String realEventFileRoot = resultsDirRoot;
		String realEventEmergenceFile = experimentDataLocation + REAL_EM_LOCATION;
		List<String> realEm = Utilities.parseFileLineXLine(realEventEmergenceFile);
		for (String s : realEm) {
			if (s.contains("-")) {
				String[] ss = s.split("-");
				int start = Integer.parseInt(ss[0]);
				int fin = Integer.parseInt(ss[1]);
				for (int i = start; i <= fin; i++) {
					if (i >= realEvents_emergence.length) {
						break;
					} else {
						realEvents_emergence[i] = 1;
					}
				}
			} else {
				int i = Integer.parseInt(s);
				realEvents_emergence[i] = 1;
			}
		}

		String realEventStabilityFile = experimentDataLocation + REAL_ST_LOCATION;
		List<String> realSt = Utilities.parseFileLineXLine(realEventStabilityFile);
		for (String s : realSt) {
			if (s.contains("-")) {
				String[] ss = s.split("-");
				int start = Integer.parseInt(ss[0]);
				int fin = Integer.parseInt(ss[1]);
				for (int i = start; i <= fin; i++) {
					if (i >= realEvents_stability.length) {
						break;
					} else {
						realEvents_stability[i] = 1;
					}
				}
			} else {
				int i = Integer.parseInt(s);
				realEvents_stability[i] = 1;
			}
		}
		String realEventCriticalityFile = experimentDataLocation + REAL_CR_LOCATION;
		List<String> realCr = Utilities.parseFileLineXLine(realEventCriticalityFile);
		for (String s : realCr) {
			if (s.contains("-")) {
				String[] ss = s.split("-");
				int start = Integer.parseInt(ss[0]);
				int fin = Integer.parseInt(ss[1]);
				for (int i = start; i <= fin; i++) {
					if (i >= realEvents_criticality.length) {
						break;
					} else {
						realEvents_criticality[i] = 1;
					}
				}
			} else {
				int i = Integer.parseInt(s);
				realEvents_criticality[i] = 1;
			}
		}
		String realEventAdaptabilityFile = experimentDataLocation + REAL_AD_LOCATION;
		List<String> realAd = Utilities.parseFileLineXLine(realEventAdaptabilityFile);
		for (String s : realAd) {
			if (s.contains("-")) {
				String[] ss = s.split("-");
				int start = Integer.parseInt(ss[0]);
				int fin = Integer.parseInt(ss[1]);
				for (int i = start; i <= fin; i++) {
					if (i >= realEvents_adaptability.length) {
						break;
					} else {
						realEvents_adaptability[i] = 1;
					}
				}
			} else {
				int i = Integer.parseInt(s);
				realEvents_adaptability[i] = 1;
			}
		}

		// Import corresponding real event files
		// This is from GoL testing

		// if (systemConfiguration.compareToIgnoreCase("random") != 0) {
		// realEventEmergenceFile = realEventFileRoot.concat(systemConfiguration +
		// "/events_emergence.tsv"); realEventStabilityFile =
		// realEventFileRoot.concat(systemConfiguration + "/events_stability.tsv");
		// realEventCriticalityFile = realEventFileRoot.concat(systemConfiguration +
		// "/events_criticality.tsv"); realEventAdaptabilityFile =
		// realEventFileRoot.concat(systemConfiguration + "/events_adaptability.tsv"); }
		// else { realEventEmergenceFile = realEventFileRoot .concat(systemConfiguration
		// + "/events_emergence" + systemString + ".tsv"); realEventStabilityFile =
		// realEventFileRoot .concat(systemConfiguration + "/events_stability" +
		// systemString + ".tsv"); realEventCriticalityFile = realEventFileRoot
		// .concat(systemConfiguration + "/events_criticality" + systemString + ".tsv");
		// realEventAdaptabilityFile = realEventFileRoot .concat(systemConfiguration +
		// "/events_adaptability" + systemString + ".tsv"); } String line = ""; try {
		// BufferedReader br =
		// Utilities.getFileAsBufferedReader(realEventEmergenceFile); while ((line =
		// br.readLine()) != null) { if (line.startsWith("#")) { continue; } int
		// eventNumber = Integer.parseInt(line); realEvents_emergence[eventNumber] = 1;
		// currentResult.addResultAtStep(realEventsNameEm, 1, eventNumber); }
		//
		// line = ""; br = Utilities.getFileAsBufferedReader(realEventStabilityFile);
		// while ((line = br.readLine()) != null) { if (line.startsWith("#")) {
		// continue; } int eventNumber = Integer.parseInt(line);
		//
		// realEvents_stability[eventNumber] = 1;
		// currentResult.addResultAtStep(realEventsNameSt, 1, eventNumber); }
		//
		// line = ""; br = Utilities.getFileAsBufferedReader(realEventCriticalityFile);
		// while ((line = br.readLine()) != null) { if (line.startsWith("#")) {
		// continue; } int eventNumber = Integer.parseInt(line);
		//
		// realEvents_criticality[eventNumber] = 1;
		// currentResult.addResultAtStep(realEventsNameCr, 1, eventNumber); } line =
		// ""; br = Utilities.getFileAsBufferedReader(realEventAdaptabilityFile); while
		// ((line = br.readLine()) != null) { if (line.startsWith("#")) { continue; }
		//
		// int eventNumber = Integer.parseInt(line);
		// realEvents_adaptability[eventNumber] = 1;
		// currentResult.addResultAtStep(realEventsNameAd, 1, eventNumber); }
		//

		ArrayList<MetricInfo> metricsToRun = e.getMetrics();
		ArrayList<MetricResult> metricResults = new ArrayList<MetricResult>();
		ConcurrentHashMap<String, ArrayList<MetricResult>> threadedResultStorage = new ConcurrentHashMap<String, ArrayList<MetricResult>>();
		ExecutorService es4 = Executors.newFixedThreadPool(metricsToRun.size());

		HashSet<String> enabledMetrics = e.getEnabledMetrics();
		boolean usingAllMetrics = e.isUsingAllMetrics();
		System.out.println(e.metricsBeingUsed());

		for (MetricInfo mi : metricsToRun) {
			String metricName = mi.getMetricName();
			if (!threadedResultStorage.containsKey(metricName)) {
				threadedResultStorage.put(metricName, new ArrayList<MetricResult>());
			}

			if (!usingAllMetrics) {
				if (enabledMetrics.contains(metricName)) {

					es4.execute(new Runnable() {
						@Override
						public void run() {
							ArrayList<MetricResult> mrs = metricRunner(theTestSystem, mi, systemString, collector);
							threadedResultStorage.get(mi.getMetricName()).addAll(mrs);

						}
					});
					// metricResults.addAll(metricRunner(theTestSystem, mi, systemString,
					// collector));

				}

			} else {
				es4.execute(new Runnable() {

					@Override
					public void run() {
						ArrayList<MetricResult> mrs = metricRunner(theTestSystem, mi, systemString, collector);
						threadedResultStorage.get(mi.getMetricName()).addAll(mrs);

					}
				});
				// metricResults.addAll(metricRunner(theTestSystem, mi, systemString,
				// collector));
			}
		}

		es4.shutdown();
		// Once finished add all the metricResults
		while (!es4.isTerminated()) {
		}
		for (ArrayList<MetricResult> mr : threadedResultStorage.values()) {
			metricResults.addAll(mr);
		}

		println("metricsToRun is " + metricsToRun.size());
		println("finished. " + metricResults.size() + " have been stored.");
		return metricResults;
	}

	public static MetricResult Metric_SystemComplexity(SystemInfo si, MetricInfo mi,
			DataCollector_FileSystem collector) {
		int totalNumberOfSteps = si.getNumberOfSteps();
		String initCrit = si.getConfigurationString();

		StringBuilder sb = new StringBuilder();
		announce("System Complexity");
		String metricName = "SystemComplexity";
		String resultsName = metricName + ": " + "Change in Interaction Frequency";
		SystemComplexity sc = new SystemComplexity(mi);
		String systemName = si.getSystemName();
		sb.append("#" + systemName + " System Complexity Results\n");
		sb.append("#step\tresult\n");
		long runtime = System.currentTimeMillis();
		System.out.println("TOTAL NUMBER OF STEPS: " + totalNumberOfSteps);

		MetricResult scResult = new MetricResult(systemName, metricName, totalNumberOfSteps, si, resultsDirRoot);
		scResult.addResultType(resultsName);
		scResult.addResultType(realEventsNameEm);
		scResult.addResultType(realEventsNameAd);
		scResult.addResultType(realEventsNameSt);
		scResult.addResultType(realEventsNameCr);

		// //currentResult.addResultType(metricName);
		for (int time = 1; time < totalNumberOfSteps - 1; time++) {
			int stepT = collector.countInteractionsInStep(time);
			int stepTM1 = collector.countInteractionsInStep(time - 1);
			sc.runMetric(stepTM1, stepT);
			double currentResults = (double) sc.getLatestResult();
			scResult.addResultAtStep(resultsName, currentResults, time);
			scResult.addResultAtStep(realEventsNameEm, realEvents_emergence[time], time);
			scResult.addResultAtStep(realEventsNameSt, realEvents_stability[time], time);
			scResult.addResultAtStep(realEventsNameCr, realEvents_criticality[time], time);
			scResult.addResultAtStep(realEventsNameAd, realEvents_adaptability[time], time);
			sb.append(time + "\t" + currentResults + "\t" + realEvents_emergence[time] + "\n");
			// //currentResult.addResultAtStep(metricName, //currentResults, time);
		}
		runtime = System.currentTimeMillis() - runtime;
		sb.append("#runtime\t" + runtime);
		scResult.addRuntime(runtime);

		// Utilities.writeToFile(sb.toString(),
		// resultsDirRoot+systemName+"/systemComplexity"+initCrit+".tsv");
		// Utilities.writeToFile(scResult.resultsToString(),
		// resultsDirRoot+systemName+"/systemComplexity"+initCrit+"RESULTPRINT.tsv");

		// Determine threshold ranges
		double stdDev = scResult.calculateSTDDev(resultsName);
		double minThresh = scResult.calculateMean(resultsName) - stdDev;

		double maxThresh = scResult.calculateMax(resultsName);

		calculateAccuracy(metricName, resultsName, scResult, 0, 2 * stdDev, 0.025);

		return scResult;
	}

	public static MetricResult Metric_ChanGoLIM(SystemInfo si, MetricInfo mi, DataCollector_FileSystem collector) {
		int totalNumberOfSteps = si.getNumberOfSteps();
		String initCrit = si.getConfigurationString();
		StringBuilder sb = new StringBuilder();
		long runtime = 0;
		// Run metric: ChanGoL Interaction Metric
		String resultsNameA = "iT";
		String resultsNameB = "zT";
		announce("WKV CHAN 2011 Interaction Metric");
		String metricName = "Chan GoL Interaction Metric";
		ChanGoLInterMetric chanGoL = new ChanGoLInterMetric(mi);
		String systemName = si.getSystemName();
		MetricResult chanGoLResult = new MetricResult(systemName, metricName, totalNumberOfSteps, si, resultsDirRoot);
		chanGoLResult.addResultType(resultsNameA);
		chanGoLResult.addResultType(resultsNameB);
		chanGoLResult.addResultType(realEventsNameEm);
		chanGoLResult.addResultType(realEventsNameSt);
		chanGoLResult.addResultType(realEventsNameCr);
		chanGoLResult.addResultType(realEventsNameAd);

		// //currentResult.addResultType(metricName + ": " + resultsNameA);
		// //currentResult.addResultType(metricName + ": " + resultsNameB);
		sb.append("#" + systemName + " Chan11 GoL Interaction Metric Results\n");
		sb.append("#step\tZt\tIt\n");
		// Run setup
		chanGoL.setup(collector.buildVAgentList(0), totalNumberOfSteps);
		// ArrayList<Double> yitList = new ArrayList<Double>();
		int[] YiTBuckets = new int[10];
		runtime = System.currentTimeMillis();
		// Requirements: List of agents at each step and the step before
		for (int time = 1; time < totalNumberOfSteps; time++) {
			ArrayList<VEntity> stepT = collector.buildVAgentList(time); // Agents from step t
			ArrayList<VEntity> stepTM1 = collector.buildVAgentList(time - 1); // Agents from step t-1
			chanGoL.calculateResults(stepTM1, stepT, time); // Calculate the results
			// double[] res = chanGoL.getResultArray(i); //Results at each step as an array
			// (Zt, It, Yit)
			double getIT = chanGoL.getResult_It();
			long getZT = chanGoL.getResult_Zt();
			chanGoLResult.addResultAtStep(resultsNameA, getIT, time);
			chanGoLResult.addResultAtStep(resultsNameB, getZT, time);
			chanGoLResult.addResultAtStep(realEventsNameEm, realEvents_emergence[time], time);
			chanGoLResult.addResultAtStep(realEventsNameSt, realEvents_stability[time], time);
			chanGoLResult.addResultAtStep(realEventsNameCr, realEvents_criticality[time], time);
			chanGoLResult.addResultAtStep(realEventsNameAd, realEvents_adaptability[time], time);
			// currentResult.addResultAtStep(metricName + ": " + resultsNameA, getIT,
			// time);
			// currentResult.addResultAtStep(metricName + ": " + resultsNameB, getZT,
			// time);

		}
		double[] yit = chanGoL.getResult_Yit(totalNumberOfSteps - 1);
		for (double d : yit) {
			if (d >= 0.0 && d <= 0.1) {
				YiTBuckets[0]++;
			} else if (d >= 0.0 && d <= 0.2) {
				YiTBuckets[1]++;
			} else if (d > 0.2 && d <= 0.3) {
				YiTBuckets[2]++;
			} else if (d > 0.3 && d <= 0.4) {
				YiTBuckets[3]++;
			} else if (d > 0.4 && d <= 0.5) {
				YiTBuckets[4]++;
			} else if (d > 0.5 && d <= 0.6) {
				YiTBuckets[5]++;
			} else if (d > 0.6 && d <= 0.7) {
				YiTBuckets[6]++;
			} else if (d > 0.7 && d <= 0.8) {
				YiTBuckets[7]++;
			} else if (d > 0.8 && d <= 0.9) {
				YiTBuckets[8]++;
			} else if (d > 0.9 && d <= 1.0) {
				YiTBuckets[9]++;
			}
		}
		runtime = System.currentTimeMillis() - runtime;
		sb.append("\n#runtime\t" + runtime);
		chanGoLResult.addRuntime(runtime);
		// Utilities.writeToFile(sb.toString(),
		// resultsDirRoot+systemName+"/chan11GoL"+initCrit+".tsv");
		// Utilities.writeToFile(chanGoLResult.resultsToString(),
		// resultsDirRoot+systemName+"/chan11GoL"+initCrit+"RESULTSPRINT.tsv");

		sb = new StringBuilder();
		sb.append("#" + systemName + " Chan11 GoL Interaction Metric Results: Histogram Data\n");
		sb.append("#bucketStart\tnumOfAgents\n");
		sb.append("0.0\t" + YiTBuckets[0] + "\n");
		sb.append("0.1\t" + YiTBuckets[1] + "\n");
		sb.append("0.2\t" + YiTBuckets[2] + "\n");
		sb.append("0.3\t" + YiTBuckets[3] + "\n");
		sb.append("0.4\t" + YiTBuckets[4] + "\n");
		sb.append("0.5\t" + YiTBuckets[5] + "\n");
		sb.append("0.6\t" + YiTBuckets[6] + "\n");
		sb.append("0.7\t" + YiTBuckets[7] + "\n");
		sb.append("0.8\t" + YiTBuckets[8] + "\n");
		sb.append("0.9\t" + YiTBuckets[9] + "\n");

		runtime = System.currentTimeMillis() - runtime;
		sb.append("\n#runtime\t" + runtime);
		// Utilities.writeToFile(sb.toString(),
		// resultsDirRoot+systemName+"/chan11GoL"+initCrit+"_histogram.tsv");
		double stdDev = chanGoLResult.calculateSTDDev(resultsNameA);
		double minThresh = chanGoLResult.calculateMean(resultsNameA) - stdDev;
		double maxThresh = chanGoLResult.calculateMax(resultsNameA);

		calculateAccuracy("Chan11", resultsNameA, chanGoLResult, 0, 2 * stdDev, 0.025);
		return chanGoLResult;

	}

	public static MetricResult Metric_OToole14(SystemInfo si, MetricInfo mi, DataCollector_FileSystem collector) {
		int totalNumberOfSteps = si.getNumberOfSteps();
		String initCrit = si.getConfigurationString();
		// Run metric: OToole 2014 Emergence Detection
		StringBuilder sb = new StringBuilder();
		String metricName = "OToole 2014";
		String resultsName = metricName + ": " + "Percentage of Statistically Significant Agents";
		long runtime = 0;
		print("*******O'Toole 2014 Emergence Detection*******");
		int maxWindowSize = 20;
		int windowTruncateSize = 5;
		String systemName = si.getSystemName();
		OToole14Metric oToole = new OToole14Metric(maxWindowSize, windowTruncateSize, mi);
		MetricResult oTooleResult = new MetricResult(systemName, metricName, totalNumberOfSteps, si, resultsDirRoot);
		oTooleResult.addResultType(resultsName);
		oTooleResult.addResultType(realEventsNameEm);
		oTooleResult.addResultType(realEventsNameSt);
		oTooleResult.addResultType(realEventsNameCr);
		oTooleResult.addResultType(realEventsNameAd);

		// //currentResult.addResultType(metricName);

		oToole.setup(collector.buildVAgentList(0), new Vector2(areaX, areaY));
		sb.append("#" + systemName + " O'Toole 2014 Emergence Detection Results\n");
		sb.append("# windowSize: " + maxWindowSize + "\twindowTruncateSize: " + windowTruncateSize + "\n");
		sb.append("#step\tresult\n");
		sb.append("");
		runtime = System.currentTimeMillis();
		ArrayList<Double> resultStats = new ArrayList<Double>();
		double res = 0.0;
		for (int time = 0; time < totalNumberOfSteps; time++) {
			oToole.run(collector.buildVAgentList(time), time);
			// Print results
			if (oToole.resultsReady()) {
				res = oToole.getLatestResults();
				resultStats.add(res);
				oTooleResult.addResultAtStep(resultsName, res, time);
				oTooleResult.addResultAtStep(realEventsNameEm, realEvents_emergence[time], time);
				oTooleResult.addResultAtStep(realEventsNameSt, realEvents_stability[time], time);
				oTooleResult.addResultAtStep(realEventsNameCr, realEvents_criticality[time], time);
				oTooleResult.addResultAtStep(realEventsNameAd, realEvents_adaptability[time], time);
			} else {
				oTooleResult.addResultAtStep(resultsName, res, time);
				oTooleResult.addResultAtStep(realEventsNameEm, realEvents_emergence[time], time);
				oTooleResult.addResultAtStep(realEventsNameSt, realEvents_stability[time], time);
				oTooleResult.addResultAtStep(realEventsNameCr, realEvents_criticality[time], time);
				oTooleResult.addResultAtStep(realEventsNameAd, realEvents_adaptability[time], time);
			}
			// //currentResult.addResultAtStep(metricName, res, time);

		}
		StringBuilder sb2 = new StringBuilder();
		sb2.append("#min\t" + oTooleResult.calculateMin(resultsName) + "\n");
		sb2.append("#max\t" + oTooleResult.calculateMax(resultsName) + "\n");
		sb2.append("#mean\t" + oTooleResult.calculateMean(resultsName) + "\n");
		sb2.append("#SD\t" + oTooleResult.calculateSTDDev(resultsName) + "\n");

		runtime = System.currentTimeMillis() - runtime;
		oTooleResult.addRuntime(runtime);
		sb.append("\n#runtime\t" + runtime);
		// Utilities.writeToFile(sb.toString(),
		// resultsDirRoot+systemName+"/oToole14"+initCrit+".tsv");
		// Utilities.writeToFile(oTooleResult.resultsToString().concat("\n"+sb2.toString()),
		// resultsDirRoot+systemName+"/oToole14"+initCrit+"RESULTSPRINT.tsv");
		double stdDev = oTooleResult.calculateSTDDev(resultsName);
		double minThresh = oTooleResult.calculateMean(resultsName) - stdDev;
		double maxThresh = oTooleResult.calculateMax(resultsName);

		calculateAccuracy("OToole14", resultsName, oTooleResult, 0, 2 * stdDev, 0.025);
		return oTooleResult;
	}

	public static MetricResult Metric_MSSE(SystemInfo si, MetricInfo mi, MetricParameters mps,
			DataCollector_FileSystem collector) {
		StringBuilder sb = new StringBuilder();
		long runtime = 0;

		int totalNumberOfSteps = si.getNumberOfSteps();
		String initCrit = si.getConfigurationString();
		announce("MSSE");
		String metricName = "MSSE";
		String systemName = si.getSystemName();
		MetricResult msseResult = new MetricResult(systemName, metricName, totalNumberOfSteps, si, resultsDirRoot);
		sb = new StringBuilder();
		sb.append("#" + systemName + " MSSE Results\n");
		sb.append("#step\tresult\n");
		String lqName = "Life Quad";
		String iqName = "Interaction Quad";
		msseResult.addResultType(lqName);
		msseResult.addResultType(iqName);
		msseResult.addResultType(realEventsNameEm);
		msseResult.addResultType(realEventsNameSt);
		msseResult.addResultType(realEventsNameCr);
		msseResult.addResultType(realEventsNameAd);

		StringBuilder sb2 = new StringBuilder(sb.toString());

		// Step 1: Define states of the system (For Game of Life)
		// Split system into <x,y> and count the Alive states in each
		int numGridsX = ((Double) mps.getParameterValue("Num-grids-X")).intValue();
		int numGridsY = ((Double) mps.getParameterValue("Num-grids-Y")).intValue();
		int numGrids = numGridsX * numGridsY;
		print("number of Grids %1$d: ", numGrids);
		MSSE_State lifeQuad = new MSSE_State("LifeQuad", numGrids);
		MSSE_State interQuad = new MSSE_State("InterQuad", numGrids);
		int quadSizeX = areaX / 2;
		int quadSizeY = areaY / 2;

		// //currentResult.addResultType("MSSE (LQ) " + numGridsX + "x" + numGridsY);
		// //currentResult.addResultType("MSSE (IQ) " + numGridsX + "x" + numGridsY);

		// Step 2: "Train" on N replications
		// Get training IDs
		ArrayList<String> trainingSets = mi.getTrainingSystemsDBIDS();
		MSSE_State[][] allLifeQuads = new MSSE_State[trainingSets.size()][];
		MSSE_State[][] allInterQuads = new MSSE_State[trainingSets.size()][];
		runtime = System.currentTimeMillis();
		int systemCounter = 0;
		// Cycle through other collections
		for (String str : trainingSets) {
			// Load up and get basic info
			println("Training from: " + str);
			collector.setCollection(str);

			HashMap<String, String> thisSysParams = collector.getInitialisationParameters();
			int thisAreaX = 0;
			int thisAreaY = 0;
			int thisNumberOfAgents = 0;
			String thisInitName = "";
			if (thisSysParams.containsKey("Size (X)")) {
				thisAreaX = Integer.parseInt(thisSysParams.get("Size (X)"));
			}
			if (thisSysParams.containsKey("Size (Y)")) {
				thisAreaY = Integer.parseInt(thisSysParams.get("Size (Y)"));
			}
			if (thisSysParams.containsKey("initPath")) {
				thisInitName = thisSysParams.get("initPath");
			}
			if (thisSysParams.containsKey("cellPopulation")) {
				thisNumberOfAgents = Integer.parseInt(thisSysParams.get("cellPopulation"));
			}
			double thisUnitAsPercentage = 100.0 / (double) thisNumberOfAgents;
			int thisTotalNumberOfSteps = collector.getTerminationStep();
			allLifeQuads[systemCounter] = new MSSE_State[thisTotalNumberOfSteps];
			allInterQuads[systemCounter] = new MSSE_State[thisTotalNumberOfSteps];

			MSSE_State thisLifeQuad = new MSSE_State("LifeQuad", numGridsX * numGridsY);
			MSSE_State thisInterQuad = new MSSE_State("InterQuad", numGridsX * numGridsY);
			int thisQuadSizeX = thisAreaX / numGridsX;
			int thisQuadSizeY = thisAreaY / numGridsY;

			// Calculate the lifeQuad and interQuad for this system
			for (int t = 0; t < thisTotalNumberOfSteps; t++) {
				Grid<VEntity> theGrid = new Grid<VEntity>(VEntity.class, thisAreaX, thisAreaY);
				ArrayList<VEntity> agents = collector.buildVAgentList(t);
				HashMap<String, ArrayList<Interaction>> interactionMap = collector.getAgentInteractionMap(t);
				for (VEntity agt : agents) {
					theGrid.addCell(agt, agt.getPosition());
				}
				thisLifeQuad.reset();
				thisInterQuad.reset();

				// Calculate state at time t for system blah
				for (int i = 0; i < thisAreaX; i++) {
					for (int j = 0; j < thisAreaY; j++) {
						VEntity tmpAgt = theGrid.getEntityAtXY(i, j);
						int xPos = (int) Math.floor((double) i / (double) thisQuadSizeX);
						int yPos = (int) Math.floor((double) j / (double) thisQuadSizeY);
						int tuplePos = yPos + (xPos * numGridsX);
						if (tuplePos >= numGridsX * numGridsY) {
							tuplePos = numGridsX * numGridsY - 1;
						}
						if (tmpAgt.getParameterValueFromStringAsString("Alive").compareToIgnoreCase("true") == 0) {
							thisLifeQuad.setTupleAtX(tuplePos,
									thisLifeQuad.getTupleValueAtX(tuplePos) + thisUnitAsPercentage);
						}
						thisInterQuad.setTupleAtX(tuplePos,
								thisInterQuad.getTupleValueAtX(tuplePos) + interactionMap.get(tmpAgt.getName()).size());
					}
				}
				allLifeQuads[systemCounter][t] = new MSSE_State(thisLifeQuad);
				allInterQuads[systemCounter][t] = new MSSE_State(thisInterQuad);
			}
			systemCounter++;
		}

		// Calculate likelihoods (count the number of replications in which that state
		// was observed)
		// Step 3: Calculate SE for those states against Trained values
		// initCriteria = initCrit;

		collector.setCollection(si.getSystemDataLocation());

		HashMap<String, String> sysParams = collector.getInitialisationParameters();
		String initName = "";
		int numberOfCells = 0;
		int numberOfAgents = 0;
		if (sysParams.containsKey("Size (X)")) {
			areaX = Integer.parseInt(sysParams.get("Size (X)"));
		}
		if (sysParams.containsKey("Size (Y)")) {
			areaY = Integer.parseInt(sysParams.get("Size (Y)"));
		}
		if (sysParams.containsKey("initPath")) {
			initName = sysParams.get("initPath");
		}
		if (sysParams.containsKey("cellPopulation")) {
			numberOfAgents = Integer.parseInt(sysParams.get("cellPopulation"));
		}

		double unitAsPercentage = 100.0 / (double) numberOfAgents;
		// Calculate the lifeQuad and interQuad (not done yet) for this system
		MSSE_State[] currentLifeQuads = new MSSE_State[totalNumberOfSteps];
		MSSE_State[] currentInterQuads = new MSSE_State[totalNumberOfSteps];
		for (int t = 0; t < totalNumberOfSteps; t++) {
			Grid<VEntity> theGrid = new Grid<VEntity>(VEntity.class, areaX, areaY);
			ArrayList<VEntity> agents = collector.buildVAgentList(t);
			for (VEntity agt : agents) {
				theGrid.addCell(agt, agt.getPosition());
			}
			HashMap<String, ArrayList<Interaction>> interactionMap = collector.getAgentInteractionMap(t);
			lifeQuad.reset();
			interQuad.reset();
			// Calculate state at time t for system blah
			for (int i = 0; i < areaX; i++) {
				for (int j = 0; j < areaY; j++) {
					VEntity tmpAgt = theGrid.getEntityAtXY(i, j);
					int xPos = (int) Math.floor((double) i / (double) quadSizeX);
					int yPos = (int) Math.floor((double) j / (double) quadSizeY);
					int tuplePos = yPos + (xPos * numGridsX);
					if (tuplePos >= numGridsX * numGridsY) {
						tuplePos = numGridsX * numGridsY - 1;
					}
					if (tmpAgt.getParameterValueFromStringAsString("Alive").compareToIgnoreCase("true") == 0) {
						lifeQuad.setTupleAtX(tuplePos, lifeQuad.getTupleValueAtX(tuplePos) + unitAsPercentage);
					}
					interQuad.setTupleAtX(tuplePos,
							interQuad.getTupleValueAtX(tuplePos) + interactionMap.get(tmpAgt.getName()).size());
				}
			}
			currentLifeQuads[t] = new MSSE_State(lifeQuad);
			currentInterQuads[t] = new MSSE_State(interQuad);
			// Magic SE stuff is done here

			double runningLQSE = 0.0;
			double runningIQSE = 0.0;
			int totalStatesAtT = allLifeQuads.length + 1;
			double normaliser = Math.log((double) totalStatesAtT);
			for (int i = 0; i < allLifeQuads.length; i++) {
				// Calculate if hit has occurred
				double LQcount = 0;
				double IQcount = 0;
				if (allLifeQuads[i][t].compareState(currentLifeQuads[t])) {
					LQcount++;
				}
				if (allInterQuads[i][t].compareState(currentInterQuads[t])) {
					IQcount++;
				}
				for (int k = 0; k < allLifeQuads.length; k++) {
					if (i != k) {
						if (allLifeQuads[i][t].compareState(allLifeQuads[k][t])) {
							LQcount++;
						}
						if (allInterQuads[i][t].compareState(allInterQuads[k][t])) {
							IQcount++;
						}
					}
				}
				double tmpPi = LQcount / (double) totalStatesAtT;
				if (tmpPi == 0) {
					tmpPi = 1;
				}
				runningLQSE += tmpPi * Math.log(tmpPi);

				tmpPi = IQcount / (double) totalStatesAtT;
				if (tmpPi == 0) {
					tmpPi = 1;
				}
				runningIQSE += tmpPi * Math.log(tmpPi);
			}
			double normalisedLQResults = (-runningLQSE) / normaliser;
			double normalisedIQResults = (-runningIQSE) / normaliser;
			sb.append(t + "\t" + normalisedLQResults + "\t" + realEvents_emergence[t] + "\n");
			msseResult.addResultAtStep(lqName, normalisedLQResults, t);
			msseResult.addResultAtStep(iqName, normalisedIQResults, t);
			// //currentResult.addResultAtStep("MSSE (LQ) " + numGridsX + "x" + numGridsY,
			// normalisedLQResults, t);
			// //currentResult.addResultAtStep("MSSE (IQ) " + numGridsX + "x" + numGridsY,
			// normalisedIQResults, t);
			msseResult.addResultAtStep(realEventsNameEm, realEvents_emergence[t], t);
			msseResult.addResultAtStep(realEventsNameSt, realEvents_stability[t], t);
			msseResult.addResultAtStep(realEventsNameCr, realEvents_criticality[t], t);
			msseResult.addResultAtStep(realEventsNameAd, realEvents_adaptability[t], t);
			// sb2.append(t + "\t" + normalisedIQResults + "\t" + realEvents_emergence[t] +
			// "\n");
			runningLQSE = 0.0;
			runningIQSE = 0.0;
		}
		runtime = System.currentTimeMillis() - runtime;
		sb.append("\n#runtime\t" + runtime);
		sb2.append("\n#runtime\t" + runtime);
		msseResult.addRuntime(runtime);
		// Utilities.writeToFile(sb.toString(),
		// resultsDirRoot+systemName+"/msse"+initCrit+"_lifeQuad.tsv");
		// Utilities.writeToFile(sb2.toString(),
		// resultsDirRoot+systemName+"/msse"+initCrit+"_interQuad.tsv");

		calculateAccuracy("MSSE (LQ) " + numGridsX + "x" + numGridsY, lqName, msseResult, 0, 1, 0.025);
		calculateAccuracy("MSSE (IQ) " + numGridsX + "x" + numGridsY, iqName, msseResult, 0, 1, 0.025);
		return msseResult;
	}

	public static MetricResult Metric_BR(SystemInfo si, MetricInfo mi, MetricParameters mps,
			DataCollector_FileSystem collector) {
		// What is the next metric to go here
		// Run Metric: Bandwidth Recognition
		int totalNumberOfSteps = si.getNumberOfSteps();
		String initCrit = si.getConfigurationString();
		announce("Bandwidth Recognition");
		long runtime = 0;
		String metricName = "Limited Bandwidth Recognition";
		String systemName = si.getSystemName();
		MetricResult brResult = new MetricResult(systemName, metricName, totalNumberOfSteps, si, resultsDirRoot);
		String MLSPname = "Most Likely System Probability";
		String cmName = "Correct Match";
		String smName = "System Match";
		String stableMatch = "Stable Match";
		String criticalMatch = "Critical Match";
		String emergenceMatch = "Emergence Match";
		String adaptabilityMatch = "Adaptability Match";
		brResult.addResultType(MLSPname);
		brResult.addResultType(realEventsNameEm);
		brResult.addResultType(realEventsNameSt);
		brResult.addResultType(realEventsNameCr);
		brResult.addResultType(realEventsNameAd);
		brResult.addResultType(cmName);
		brResult.addResultType(stableMatch);
		brResult.addResultType(criticalMatch);
		brResult.addResultType(emergenceMatch);
		brResult.addStringResultType(smName);

		// //currentResult.addResultType(stableMatch);
		// //currentResult.addResultType(criticalMatch);
		// //currentResult.addResultType(emergenceMatch);
		// //currentResult.addResultType(adaptabilityMatch);

		// Step 1: Bring in labelled Training data sets
		ArrayList<String> trainingSetsBR = mi.getTrainingSystemsDBIDS();
		int sampleSize = 0;
		double percentageToGet = (Double) mps.getParameterValue("Percentage-to-get");
		int samplingFrequency = ((Double) mps.getParameterValue("Sampling-frequency")).intValue();
		HashSet<String> types = new HashSet<String>();
		HashMap<String, HashMap<Integer, Double>> feature1Types = new HashMap<String, HashMap<Integer, Double>>();
		HashMap<String, HashMap<Integer, Double>> feature2Types = new HashMap<String, HashMap<Integer, Double>>();
		runtime = System.currentTimeMillis();
		for (String str : trainingSetsBR) {
			str = str.replace("+", "");
			println("Training from: " + str);
			collector.setCollection(str);
			runtime = System.currentTimeMillis();
			int thisAreaX = 0;
			int thisAreaY = 0;
			String thisInitName = "";
			HashMap<String, String> sysParams = collector.getInitialisationParameters();
			String initName = "";
			int thisNumberOfCells = 0;
			int numberOfAgents = 0;
			if (sysParams.containsKey("Size (X)")) {
				thisAreaX = Integer.parseInt(sysParams.get("Size (X)"));
			}
			if (sysParams.containsKey("Size (Y)")) {
				thisAreaY = Integer.parseInt(sysParams.get("Size (Y)"));
			}
			if (sysParams.containsKey("initPath")) {
				thisInitName = sysParams.get("initPath");
			}
			types.add(thisInitName);
			thisNumberOfCells = thisAreaX * thisAreaY;
			// Step 2: Calculate feature likelihoods for the dataset
			sampleSize = (int) (thisNumberOfCells * percentageToGet);
			int thisTotalNumberOfSteps = collector.getTerminationStep();
			HashMap<Integer, Integer> feature1Hits = new HashMap<Integer, Integer>();
			HashMap<Integer, Integer> feature2Hits = new HashMap<Integer, Integer>();

			// POpulate feature1hits
			for (int p = 0; p < 9; p++) {
				feature1Hits.put(p, 0);
			}
			// Populate feature2hits
			for (int p = 0; p < 2; p++) {
				feature2Hits.put(p, 0);
			}
			for (int t = 1; t < thisTotalNumberOfSteps; t = t + samplingFrequency) {
				HashMap<String, VEntity> previousAgents = collector.buildVAgentMap(t - 1);
				ArrayList<VEntity> agents = collector.buildVAgentList(t);
				Grid<VEntity> theGrid = new Grid<VEntity>(VEntity.class, thisAreaX, thisAreaY);
				for (VEntity agt : agents) {
					theGrid.addCell(agt, agt.getPosition());
				}
				for (int i = 0; i < sampleSize; i++) {
					VEntity tmpAgent = agents.get(RandomGen.generateRandomRangeInteger(0, agents.size() - 1));
					// Get Feature 1: # of Live Neighbours
					int aliveNeighbours = 0;
					ArrayList<VEntity> neighbours = (ArrayList<VEntity>) theGrid
							.getNeighbours((int) tmpAgent.getPosition().getX(), (int) tmpAgent.getPosition().getY(), 1);
					for (VEntity v : neighbours) {
						if (v.getParameterValueFromStringAsString("Alive").compareToIgnoreCase("true") == 0) {
							aliveNeighbours++;
						}
					}
					if (feature1Hits.get(aliveNeighbours) != null) {
						feature1Hits.put(aliveNeighbours, feature1Hits.get(aliveNeighbours) + 1);
					} else {
						feature1Hits.put(aliveNeighbours, 1);
					}

					// Get Feature 2: Changed state since last hit
					int changed = 0;
					if (t != 0) {
						String tmpAgentName = tmpAgent.getName();
						VEntity prevTmpAgent = previousAgents.get(tmpAgentName);
						if (tmpAgent.getParameterValueFromStringAsString("Alive")
								.compareToIgnoreCase(prevTmpAgent.getParameterValueFromStringAsString("Alive")) == 0) {
							changed = 1;
						}
					}
					if (feature2Hits.get(changed) != null) {
						feature2Hits.put(changed, feature2Hits.get(changed) + 1);
					} else {
						feature2Hits.put(changed, 1);
					}

				}
				// Calculate likelihood of Feature1 and Feature2

			}
			// Calculate likelihood of Feature1
			int total = 0;
			for (int f1 = 0; f1 < 9; f1++) {
				total += feature1Hits.get(f1);
			}
			HashMap<Integer, Double> F1Likelihoods = new HashMap<Integer, Double>();
			for (int f1 = 0; f1 < 9; f1++) {
				F1Likelihoods.put(f1, (double) feature1Hits.get(f1) / (double) total);
			}
			if (feature1Types.get(thisInitName) != null) {
				HashMap<Integer, Double> old = feature1Types.get(thisInitName);
				for (int f1 = 0; f1 < 9; f1++) {
					double newVal = (F1Likelihoods.get(f1) + old.get(f1)) / 2.0;
					F1Likelihoods.put(f1, newVal);
				}
			} else {
				feature1Types.put(thisInitName, F1Likelihoods);
			}

			// Calculate likelihood of Feature2
			total = 0;
			for (int f2 = 0; f2 < 2; f2++) {
				if (feature2Hits.get(f2) != null)
					total += feature2Hits.get(f2);
			}
			HashMap<Integer, Double> F2Likelihoods = new HashMap<Integer, Double>();
			for (int f2 = 0; f2 < 2; f2++) {
				if (feature2Hits.get(f2) != null)
					F2Likelihoods.put(f2, (double) feature2Hits.get(f2) / (double) total);
			}
			if (feature2Types.get(thisInitName) != null) {
				HashMap<Integer, Double> old = feature2Types.get(thisInitName);
				for (int f2 = 0; f2 < 2; f2++) {
					double newVal = (F2Likelihoods.get(f2) + old.get(f2)) / 2.0;
					F2Likelihoods.put(f2, newVal);
				}
			} else {
				feature2Types.put(thisInitName, F2Likelihoods);
			}
		}

		// Step 4: Calculate for the current sample
		// initCriteria = initCrit;
		//
		collector.setCollection(si.getSystemDataLocation());

		HashMap<String, String> sysParams = collector.getInitialisationParameters();
		String initName = "";
		int numberOfCells = 0;
		if (sysParams.containsKey("Size (X)")) {
			areaX = Integer.parseInt(sysParams.get("Size (X)"));
		}
		if (sysParams.containsKey("Size (Y)")) {
			areaY = Integer.parseInt(sysParams.get("Size (Y)"));
		}
		if (sysParams.containsKey("initPath")) {
			initName = sysParams.get("initPath");
		}

		HashMap<Integer, Integer> feature1Hits = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> feature2Hits = new HashMap<Integer, Integer>();
		HashMap<String, Double> results = new HashMap<String, Double>();
		for (int t = 1; t < totalNumberOfSteps; t = t + 1) {
			HashMap<String, VEntity> previousAgents = collector.buildVAgentMap(t - 1);
			ArrayList<VEntity> agents = collector.buildVAgentList(t);
			Grid<VEntity> theGrid = new Grid<VEntity>(VEntity.class, areaX, areaY);
			for (VEntity agt : agents) {
				theGrid.addCell(agt, agt.getPosition());
			}
			double runningMultip = 1.0;
			for (int i = 0; i < sampleSize; i++) {
				VEntity tmpAgent = agents.get(RandomGen.generateRandomRangeInteger(0, agents.size() - 1));
				// Get Feature 1: # of Live Neighbours
				int aliveNeighbours = 0;
				ArrayList<VEntity> neighbours = (ArrayList<VEntity>) theGrid
						.getNeighbours((int) tmpAgent.getPosition().getX(), (int) tmpAgent.getPosition().getY(), 1);
				for (VEntity v : neighbours) {
					if (v.getParameterValueFromStringAsString("Alive").compareToIgnoreCase("true") == 0) {
						aliveNeighbours++;
					}
				}
				if (feature1Hits.get(aliveNeighbours) != null) {
					feature1Hits.put(aliveNeighbours, feature1Hits.get(aliveNeighbours) + 1);
				} else {
					feature1Hits.put(aliveNeighbours, 1);
				}

				// Get Feature 2: Changed state since last hit
				int changed = 0;
				if (t > 0) {
					String tmpAgentName = tmpAgent.getName();
					VEntity prevTmpAgent = previousAgents.get(tmpAgentName);
					if (tmpAgent.getParameterValueFromStringAsString("Alive")
							.compareToIgnoreCase(prevTmpAgent.getParameterValueFromStringAsString("Alive")) == 0) {
						changed = 1;
					}
				}
				if (feature2Hits.get(changed) != null) {
					feature2Hits.put(changed, feature2Hits.get(changed) + 1);
				} else {
					feature2Hits.put(changed, 1);
				}

				// Calculate per agent here
				for (String b : types) {
					// Feature1
					double f1 = feature1Types.get(b).get(aliveNeighbours);
					double f2 = 0;
					if (feature2Types.get(b).get(changed) != null)
						f2 = feature2Types.get(b).get(changed);

					double res = f1 * f2;
					if (results.get(b) != null) {
						results.put(b, results.get(b) * res);
					} else {
						results.put(b, res);
					}
				}
			}
			// Calculate the max
			double max = -Double.MAX_VALUE;
			String typeMax = "";
			for (String b : types) {
				if (results.get(b) > max) {
					max = results.get(b);
					typeMax = b;
				}
				results.put(b, 1.0);
			}
			String maxName = parseModelName(typeMax);
			String thisName = parseModelName(initName);
			int correct = 0;
			if (maxName.compareTo(thisName) == 0) {
				correct = 1;
			}
			String stableFile = "Stable";
			String criticalFile = "Critical";
			if (maxName.startsWith(stableFile)) {
				brResult.addResultAtStep(stableMatch, 1, t);
				brResult.addResultAtStep(criticalMatch, 0, t);
				brResult.addResultAtStep(emergenceMatch, 0, t);

				// currentResult.addResultAtStep(stableMatch, 1, t);
				// currentResult.addResultAtStep(criticalMatch, 0, t);
				// currentResult.addResultAtStep(emergenceMatch, 0, t);
			} else if (maxName.startsWith(criticalFile)) {
				brResult.addResultAtStep(stableMatch, 0, t);
				brResult.addResultAtStep(criticalMatch, 1, t);
				brResult.addResultAtStep(emergenceMatch, 0, t);

				// currentResult.addResultAtStep(stableMatch, 0, t);
				// currentResult.addResultAtStep(criticalMatch, 1, t);
				// currentResult.addResultAtStep(emergenceMatch, 0, t);
			} else {
				brResult.addResultAtStep(stableMatch, 0, t);
				brResult.addResultAtStep(criticalMatch, 0, t);
				brResult.addResultAtStep(emergenceMatch, 1, t);

				// currentResult.addResultAtStep(stableMatch, 0, t);
				// currentResult.addResultAtStep(criticalMatch, 0, t);
				// currentResult.addResultAtStep(emergenceMatch, 1, t);
			}

			brResult.addResultAtStep(MLSPname, max, t);
			brResult.addResultAtStep(realEventsNameEm, realEvents_emergence[t], t);
			brResult.addResultAtStep(realEventsNameSt, realEvents_stability[t], t);
			brResult.addResultAtStep(realEventsNameCr, realEvents_criticality[t], t);
			brResult.addResultAtStep(realEventsNameAd, realEvents_adaptability[t], t);
			brResult.addResultAtStep(cmName, correct, t);
			brResult.addStringResultAtStep(smName, maxName, t); // TODO: This
		}
		runtime = System.currentTimeMillis() - runtime;
		brResult.addRuntime(runtime);
		// Utilities.writeToFile(brResult.resultsToString(),resultsDirRoot+systemName+"/BR"+initCrit+".tsv");
		double accBr = brResult.accuracyCalculationForString(smName, smName, parseModelName(initName),
				parseModelName(initName));
		println("accBR: " + accBr);
		println("Most common feature: " + brResult.calculateStringMode(smName));

		calculateAccuracy("Limited Bandwidth Recognition: Emergence", emergenceMatch, brResult, 0, 2, 1.0);
		calculateAccuracy("Limited Bandwidth Recognition: Stability", stableMatch, brResult, 0, 2, 1.0);
		calculateAccuracy("Limited Bandwidth Recognition: Critical", criticalMatch, brResult, 0, 2, 1.0);

		return brResult;

	}

	public static MetricResult Metric_OscillatorDetect(SystemInfo si, MetricInfo mi,
			DataCollector_FileSystem collector) {
		int totalNumberOfSteps = si.getNumberOfSteps();
		String initCrit = si.getConfigurationString();
		announce("Oscillation Detector");
		String metricName = "Oscillation Detector";
		String resultsName = metricName + ": " + "Oscillations";
		StringBuilder theString = new StringBuilder();
		ArrayList<BitSet> bitsOverTime = new ArrayList<BitSet>(totalNumberOfSteps);
		int numOscillationsFound = 0;
		int distance = -1;
		int maxDistStart = -1;
		int consecutive = 0;
		int minDistance = totalNumberOfSteps;
		int oscillationSize = -1;
		int firstOscillationStart = -1;
		String systemName = si.getSystemName();
		MetricResult oscillResult = new MetricResult(systemName, resultsName, totalNumberOfSteps, si, resultsDirRoot);

		oscillResult.addResultType(realEventsNameEm);
		oscillResult.addResultType(realEventsNameSt);
		oscillResult.addResultType(realEventsNameCr);
		oscillResult.addResultType(realEventsNameAd);
		final String STATE_1 = "STATE_1";
		MetricVariableMapping mvm1 = mi.getMetricVariableMappings().get(STATE_1);
		resultsName = resultsName + "{" + mvm1.toString() + "}";
		oscillResult.addResultType(resultsName);
		// currentResult.addResultType(resultsName);

		double runtime = System.currentTimeMillis();

		boolean hit = false;
		int bitsetCounter = 0;
		for (int time = 1; time < totalNumberOfSteps; time++) {
			ArrayList<VEntity> agents = collector.buildVAgentList(time);
			Collections.sort(agents, VEntity.sortByName()); // Sort by name or position?
			BitSet bs = new BitSet(agents.size());
			for (int i = 0; i < agents.size(); i++) {
				if (mvm1.entityIsOfType(agents.get(i))) {
					bs.set(i, mvm1.isParameterEqualToDesiredValue(agents.get(i)));
				}
			}
			bitsOverTime.add(bitsetCounter, bs);

			oscillResult.addResultAtStep(realEventsNameEm, realEvents_emergence[time], time);
			oscillResult.addResultAtStep(realEventsNameSt, realEvents_stability[time], time);
			oscillResult.addResultAtStep(realEventsNameCr, realEvents_criticality[time], time);
			oscillResult.addResultAtStep(realEventsNameAd, realEvents_adaptability[time], time);

			for (int t = bitsetCounter - 1; t >= 0; t--) {
				BitSet xor = (BitSet) bitsOverTime.get(t).clone();
				xor.xor(bs);
				int diff = xor.length() - 1;
				if (diff == -1) {
					if (!hit) {
						oscillationSize = time - t - 1;
						numOscillationsFound++;
						int dist = time - t;
						if (dist > distance) {
							distance = dist;
							maxDistStart = t + 1;
						}

						if (dist < minDistance) {
							minDistance = dist;
						}

						hit = true;
					}
					if (t == time - 1) {
						consecutive++;
					}

					if (hit) {
						hit = false;
						break;
					}
				}
			}
			bitsetCounter++;
		}
		println("Oscillation size: %1$d", oscillationSize);
		// Find the oscillation size
		// BitSet start = bitsOverTime.get(maxDistStart);
		// for (int i = maxDistStart+1; i < totalNumberOfSteps-1; i++){
		// BitSet possible = (BitSet)bitsOverTime.get(i).clone();
		// possible.xor(start);
		// if (possible.length()-1 == -1){
		//// println("i: "+i);
		// oscillationSize = i - maxDistStart;
		// break;
		// }
		// }
		// println("Oscillation size: %1$d",oscillationSize);
		// If we know the start and the size, we can go through that way...
		// This is fucking cheap
		for (int i = 0; i < totalNumberOfSteps; i++) {
			if ((i - maxDistStart) % oscillationSize == 0) {
				oscillResult.addResultAtStep(resultsName, 1.0, i);
				// currentResult.addResultAtStep(resultsName, 1.0, i);
			} else {
				oscillResult.addResultAtStep(resultsName, 0.0, i);
				// currentResult.addResultAtStep(resultsName, 0.0, i);
			}
			// if (i == maxDistStart){
			// System.out.println("ddsk:" +i );
			// oscillResult.addResultAtStep(resultsName,1.0,i);
			// //currentResult.addResultAtStep(resultsName,1.0,i);
			// } else if (i > maxDistStart){
			// if ((i - maxDistStart) % oscillationSize == 0){
			// oscillResult.addResultAtStep(resultsName,1.0,i);
			// //currentResult.addResultAtStep(resultsName,1.0,i);
			// } else {
			// oscillResult.addResultAtStep(resultsName,0.0,i);
			// //currentResult.addResultAtStep(resultsName,0.0,i);
			// }
			// } else {
			// oscillResult.addResultAtStep(resultsName,0.0,i);
			// //currentResult.addResultAtStep(resultsName,0.0,i);
			// }
		}
		runtime = System.currentTimeMillis() - runtime;
		oscillResult.addRuntime(runtime);

		double stdDev = oscillResult.calculateSTDDev(resultsName);

		calculateAccuracy("OscillationDetector", resultsName, oscillResult, stdDev, 2.0, 0.025);
		calculateAccuracy("OscillationDetector", resultsName, oscillResult, 0, 1, 0.025);

		println("Oscillations detected: %1$d with max distance: %2$d starting from %3$d with %4$d consecutive hits. Min distance: %5$d",
				numOscillationsFound, distance, maxDistStart, consecutive, minDistance);
		println("Oscillation size: %1$d", oscillationSize);
		return oscillResult;

	}

	public static MetricResult Metric_TagAndTrack(SystemInfo si, MetricInfo mi, DataCollector_FileSystem collector) {
		int totalNumberOfSteps = si.getNumberOfSteps();
		String systemName = si.getSystemName();
		StringBuilder sb = new StringBuilder();
		announce("Tag and Track");
		ClusterTrack tt = new ClusterTrack(mi);
		String metricName = "TagAndTrack";
		MetricResult ttResult = new MetricResult(systemName, metricName, totalNumberOfSteps, si, resultsDirRoot);
		MetricVariableMapping mvm1 = mi.getMetricVariableMappings().get("STATE_1");
		String averageClusterStateDensityName = "averageClusterStateDensity" + mvm1.toString();
		String averageAgentDensityName = "averageAgentDensity" + mvm1.toString();
		String AverageAreaName = "AverageArea" + mvm1.toString();
		String RunningClusterCountName = "RunningClusterCount" + mvm1.toString();
		String ClustersIntersectingName = "ClustersIntersecting" + mvm1.toString();
		String STDDEVAgentStateDensityName = "STDDEVAgentStateDensity" + mvm1.toString();
		String STDDEVAgentDensityName = "STDDEVAgentDensity" + mvm1.toString();
		String MaxAgentStateDensityName = "MaxAgentStateDensity" + mvm1.toString();
		String MinAgentStateDensityName = "MinAgentStateDensity" + mvm1.toString();
		String MaxAgentDensityName = "MaxAgentDensity" + mvm1.toString();
		String MinAgentDensityName = "MinAgentDensity" + mvm1.toString();
		String RunningUniqueClustersName = "RunningUniqueClusters" + mvm1.toString();
		ttResult.addResultType(averageClusterStateDensityName);
		ttResult.addResultType(averageAgentDensityName);
		ttResult.addResultType(STDDEVAgentStateDensityName);
		ttResult.addResultType(STDDEVAgentDensityName);
		ttResult.addResultType(MaxAgentStateDensityName);
		ttResult.addResultType(MinAgentStateDensityName);
		ttResult.addResultType(MaxAgentDensityName);
		ttResult.addResultType(MinAgentDensityName);
		ttResult.addResultType(RunningClusterCountName);
		ttResult.addResultType(RunningUniqueClustersName);
		ttResult.addResultType(ClustersIntersectingName);
		ttResult.addResultType(AverageAreaName);

		ttResult.addResultType(realEventsNameEm);
		ttResult.addResultType(realEventsNameSt);
		ttResult.addResultType(realEventsNameCr);
		ttResult.addResultType(realEventsNameAd);

		// currentResult.addResultType("Cluster: " + averageClusterStateDensityName);
		// currentResult.addResultType("Cluster: " + averageAgentDensityName);
		// currentResult.addResultType("Cluster: " + AverageAreaName);
		// currentResult.addResultType("Cluster: " + RunningUniqueClustersName);
		// currentResult.addResultType("Cluster: " + ClustersIntersectingName);

		sb.append("#" + systemName + " Tag and Track Results\n");
		sb.append("#step\tresult\n");
		long runtime = System.currentTimeMillis();
		int numberClusters = 25;
		InteractionGraph igOld = null;
		InteractionGraph igNew = null;
		int k = 1;
		HashMap<String, Double> ctResults;
		for (int t = 0; t < totalNumberOfSteps; t = t + k) {
			// println("Time: "+t);
			// if (igOld == null){
			// igOld = collector.buildInteractionGraph(t-1);
			ttResult.addResultAtStep(realEventsNameEm, realEvents_emergence[t], t);
			ttResult.addResultAtStep(realEventsNameSt, realEvents_stability[t], t);
			ttResult.addResultAtStep(realEventsNameCr, realEvents_criticality[t], t);
			ttResult.addResultAtStep(realEventsNameAd, realEvents_adaptability[t], t);
			// }

			igNew = collector.buildInteractionGraph(t);
			ctResults = tt.examineClusters(tt.dfsHelper(igNew));
			double averageClusterStateDensity = ctResults.get(averageClusterStateDensityName);
			double averageAgentDensity = ctResults.get(averageAgentDensityName);
			double averageArea = ctResults.get(AverageAreaName);
			double runningClusterCount = ctResults.get(RunningClusterCountName);
			double clustersIntersecting = ctResults.get(ClustersIntersectingName);
			double STDDEVAgentStateDensity = ctResults.get(STDDEVAgentStateDensityName);
			double STDDEVAgentDensity = ctResults.get(STDDEVAgentDensityName);
			double MaxAgentStateDensity = ctResults.get(MaxAgentStateDensityName);
			double MinAgentStateDensity = ctResults.get(MinAgentStateDensityName);
			double MaxAgentDensity = ctResults.get(MaxAgentDensityName);
			double MinAgentDensity = ctResults.get(MinAgentDensityName);
			double RunningUniqueClusters = ctResults.get(RunningUniqueClustersName);

			ttResult.addResultAtStep(averageClusterStateDensityName, averageClusterStateDensity, t);
			ttResult.addResultAtStep(averageAgentDensityName, averageAgentDensity, t);
			ttResult.addResultAtStep(AverageAreaName, averageArea, t);
			ttResult.addResultAtStep(RunningClusterCountName, runningClusterCount, t);
			ttResult.addResultAtStep(ClustersIntersectingName, clustersIntersecting, t);
			ttResult.addResultAtStep(STDDEVAgentStateDensityName, STDDEVAgentStateDensity, t);
			ttResult.addResultAtStep(STDDEVAgentDensityName, STDDEVAgentDensity, t);
			ttResult.addResultAtStep(MaxAgentStateDensityName, MaxAgentStateDensity, t);
			ttResult.addResultAtStep(MinAgentStateDensityName, MinAgentStateDensity, t);
			ttResult.addResultAtStep(MaxAgentDensityName, MaxAgentDensity, t);
			ttResult.addResultAtStep(MinAgentDensityName, MinAgentDensity, t);
			ttResult.addResultAtStep(RunningUniqueClustersName, RunningUniqueClusters, t);
			sb.append(
					t + "\t" + averageClusterStateDensity + "\t" + averageAgentDensity + "\t" + STDDEVAgentStateDensity
							+ "\t" + STDDEVAgentDensity + "\t" + MaxAgentStateDensity + "\t" + MaxAgentDensity + "\t"
							+ MinAgentStateDensity + "\t" + MinAgentDensity + "\t" + runningClusterCount + "\t"
							+ averageArea + "\t" + RunningUniqueClusters + "\t" + clustersIntersecting + "\n");

			// //currentResult.addResultAtStep("Cluster: " + averageClusterStateDensityName,
			// averageClusterStateDensity, t);
			// //currentResult.addResultAtStep("Cluster: " + averageAgentDensityName,
			// averageAgentDensity, t);
			// //currentResult.addResultAtStep("Cluster: " + AverageAreaName, averageArea,
			// t);
			// //currentResult.addResultAtStep("Cluster: " + RunningUniqueClustersName,
			// RunningUniqueClusters, t);
			// //currentResult.addResultAtStep("Cluster: " + ClustersIntersectingName,
			// clustersIntersecting, t);

		}
		calculateAccuracy(averageClusterStateDensityName, averageClusterStateDensityName, ttResult, 0, 50.0, 0.025);
		calculateAccuracy(averageAgentDensityName, averageAgentDensityName, ttResult, 0, 50.0, 0.025);
		calculateAccuracy(AverageAreaName, AverageAreaName, ttResult, 0, 5000.0, 5);
		calculateAccuracy(RunningUniqueClustersName, RunningUniqueClustersName, ttResult, 0, 50.0, 0.025);
		calculateAccuracy(ClustersIntersectingName, ClustersIntersectingName, ttResult, 0, 5000.0, 5);

		tt.zarf();
		Utilities.writeToFile(sb.toString(), resultsDirRoot + systemName.replaceAll("\\s+", "") + "/"
				+ metricName.replaceAll("\\s+", "") + "/" + si.getConfigurationString() + "-densities-over-time.tsv",
				false);
		return ttResult;
	}

	public static MetricResult Metric_EntropyOverTime(MetricInfo mi, SystemInfo si, MetricParameters mp,
			DataCollector_FileSystem collector) {
		int totalNumberOfSteps = si.getNumberOfSteps();
		String initCrit = si.getConfigurationString();
		announce("Entropy Over Time");
		String metricName = "Entropy";
		String resultsName = metricName + ": " + "Entropy";
		String secName = "Shannon Entropy Change";
		String ceName = "Conditional Entropy";
		String systemName = si.getSystemName();
		MetricResult eotResult = new MetricResult(systemName, resultsName, totalNumberOfSteps, si, resultsDirRoot);
		Entropy entropyCalculator = new Entropy(mi);
		resultsName = resultsName + mp.toStringNS();
		ceName = ceName + mp.toStringNS();
		secName = secName + mp.toStringNS();

		eotResult.addResultType(resultsName);
		eotResult.addResultType(ceName);
		eotResult.addResultType(secName);
		eotResult.addResultType(realEventsNameEm);
		eotResult.addResultType(realEventsNameSt);
		eotResult.addResultType(realEventsNameCr);
		eotResult.addResultType(realEventsNameAd);

		// //currentResult.addResultType(resultsName);
		// //currentResult.addResultType(ceName);
		// //currentResult.addResultType(secName);
		double runtime = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder();

		// So, whats happening here?
		for (int time = 1; time < totalNumberOfSteps; time++) {
			eotResult.addResultAtStep(realEventsNameEm, realEvents_emergence[time], time);
			eotResult.addResultAtStep(realEventsNameSt, realEvents_stability[time], time);
			eotResult.addResultAtStep(realEventsNameCr, realEvents_criticality[time], time);
			eotResult.addResultAtStep(realEventsNameAd, realEvents_adaptability[time], time);

			ArrayList<VEntity> agents = collector.buildVAgentList(time);
			HashMap<String, VEntity> prevAgents = collector.buildVAgentMap(time - 1);
			double shannonEntropy = entropyCalculator.shannonEntropy_Neighbours(agents, new Vector2(areaX, areaY), mp);
			double shannonEntropyChange = entropyCalculator.shannonEntropy_Change(agents, prevAgents);
			double conditionalEntropy = entropyCalculator.conditionalEntropy(agents, prevAgents,
					new Vector2(areaX, areaY), mp);

			eotResult.addResultAtStep(resultsName, shannonEntropy, time);
			eotResult.addResultAtStep(secName, shannonEntropyChange, time);
			eotResult.addResultAtStep(ceName, conditionalEntropy, time);

			// //currentResult.addResultAtStep(resultsName, shannonEntropy, time);
			// //currentResult.addResultAtStep(secName, shannonEntropyChange, time);
			// //currentResult.addResultAtStep(ceName, conditionalEntropy, time);
			sb.append(time + "\t" + shannonEntropy + "\t" + shannonEntropyChange + "\t" + conditionalEntropy + "\t"
					+ realEvents_emergence[time] + "\t" + realEvents_stability[time] + "\t"
					+ realEvents_criticality[time] + "\n");
		}
		//
		calculateAccuracy("Entropy Over Time: Shannon Entropy", resultsName, eotResult, 0, 10.0, 0.025);
		calculateAccuracy("Entropy Over Time: ShannonEntropy(StateChange)", secName, eotResult, 0, 10.0, 0.025);
		calculateAccuracy("Entropy Over Time: Conditional Entropy", ceName, eotResult, 0, 10.0, 0.025);

		return eotResult;
		// Utilities.writeToFile(sb.toString(),
		// resultsDirRoot+systemName.replaceAll("\\s+","")+"/"+metricName.replaceAll("\\s+","")+"/"+si.getConfigurationString()+".tsv");
	}

	// The following XXX metrics come from Eberhardinger et al (2015)
	// Note: Some of these could actually be used as outputs for the above metrics.
	// Metrics for Adaptation Algorithms

	/**
	 * "The intuition of WAT is that adaptation is responsible for keeping the
	 * controlled system working with as little disruption as possible by an
	 * adaptation algorithm."
	 * 
	 * How can we phrase that in terms of this? What is working time? What is
	 * adaptivity time? Time as existing: dead -> dead uses 1 unit of time
	 * alive/dead -> dead/alive uses x units of time alive -> alive uses 1 unit of
	 * time as well However, thisisCongested is just by looking at the states
	 * themselves, how can we impart the interactions that cause the change? Use the
	 * rules: 3|3|4. Similar to the entropy measure from before. Following the logic
	 * above: we just change the weightings
	 * 
	 * @param mi
	 * @param si
	 */
	// TODO: Put into its own Metric class file
	public static MetricResult Metric_KaddoumWAT(MetricInfo mi, SystemInfo si, DataCollector_FileSystem collector) {
		int totalNumberOfSteps = si.getNumberOfSteps();
		String initCrit = si.getConfigurationString();
		MetricVariableMapping mvm1 = mi.getMetricVariableMappings().get("STATE_1");
		announce("KaddoumWAT");
		String systemName = si.getSystemName();
		String metricName = "KaddoumWAT";
		String resultsName = metricName + ": " + "WAT" + mvm1.toString();

		StringBuilder sb = new StringBuilder();
		SelfAdaptiveSystems sas = new SelfAdaptiveSystems(mi);

		MetricResult watResult = new MetricResult(systemName, metricName, totalNumberOfSteps, si, resultsDirRoot);
		watResult.addResultType(resultsName);

		watResult.addResultType(realEventsNameEm);
		watResult.addResultType(realEventsNameSt);
		watResult.addResultType(realEventsNameCr);
		watResult.addResultType(realEventsNameAd);
		// //currentResult.addResultType(resultsName);

		double workingTime = 0.0; // I should find this exact formula
		double adaptivityTime = 0.0;
		double watScore = -1.0;

		for (int time = 1; time < totalNumberOfSteps; time++) {
			watResult.addResultAtStep(realEventsNameEm, realEvents_emergence[time], time);
			watResult.addResultAtStep(realEventsNameSt, realEvents_stability[time], time);
			watResult.addResultAtStep(realEventsNameCr, realEvents_criticality[time], time);
			watResult.addResultAtStep(realEventsNameAd, realEvents_adaptability[time], time);

			ArrayList<VEntity> agents = collector.buildVAgentList(time);
			HashMap<String, VEntity> prevAgents = collector.buildVAgentMap(time - 1);
			HashMap<String, ArrayList<Interaction>> interactions = collector.getAgentInteractionMap(time - 1);

			// workingTime = agents.size() * 8.0;
			workingTime = collector.countInteractionsInStep(time);

			watScore = sas.KaddoumWAT(agents, prevAgents, interactions, workingTime);

			watResult.addResultAtStep(resultsName, watScore, time);
			// //currentResult.addResultAtStep(resultsName, watScore, time);
			sb.append(time + "\t" + watScore + "\t" + realEvents_emergence[time] + "\t" + realEvents_stability[time]
					+ "\t" + realEvents_criticality[time] + "\n");

			// println(time+"\t"+watScore);
			adaptivityTime = 0.0;
		}

		calculateAccuracy("WAT", resultsName, watResult, 0, 100.0, 0.025);
		// Utilities.writeToFile(sb.toString(),
		// resultsDirRoot+systemName.replaceAll("\\s+","")+"/"+metricName.replaceAll("\\s+","")+"/"+si.getConfigurationString()+".tsv");
		return watResult;
	}

	// TODO This one needs to be ported across into the SAS class but its so very
	// nasty
	public static MetricResult Metric_VillegasAU(MetricInfo mi, SystemInfo si, DataCollector_FileSystem collector) {
		int totalNumberOfSteps = si.getNumberOfSteps();
		String initCrit = si.getConfigurationString();
		announce("VillegasAU");
		final String STATE_1 = "STATE_1";
		MetricVariableMapping mvm1 = mi.getMetricVariableMappings().get(STATE_1);
		String metricName = "VillegasAU";
		String resultsName = metricName + ": " + "VillegasAU";
		String mttrName = "MTTR" + mvm1.toString();
		String mttfName = "MTTF" + mvm1.toString();
		String aName = "Availability" + mvm1.toString();
		String uName = "Unavailability" + mvm1.toString();

		StringBuilder sb = new StringBuilder();
		String systemName = si.getSystemName();
		MetricResult auResult = new MetricResult(systemName, resultsName, totalNumberOfSteps, si, resultsDirRoot);

		// auResult.addResultType(resultsName);
		auResult.addResultType(aName);
		auResult.addResultType(uName);
		auResult.addResultType(realEventsNameEm);
		auResult.addResultType(realEventsNameSt);
		auResult.addResultType(realEventsNameCr);
		auResult.addResultType(realEventsNameAd);
		auResult.addResultType(mttrName);
		auResult.addResultType(mttfName);

		// //currentResult.addResultType(aName);
		// //currentResult.addResultType(uName);

		int consecutiveDowntime = 2; // The shortest amount of consecutive down time
		HashMap<String, Integer> theAgentsDowntime = new HashMap<String, Integer>();
		HashMap<String, Integer> theAgentsUptime = new HashMap<String, Integer>();

		for (int time = 1; time < totalNumberOfSteps; time++) {
			auResult.addResultAtStep(realEventsNameEm, realEvents_emergence[time], time);
			auResult.addResultAtStep(realEventsNameSt, realEvents_stability[time], time);
			auResult.addResultAtStep(realEventsNameCr, realEvents_criticality[time], time);
			auResult.addResultAtStep(realEventsNameAd, realEvents_adaptability[time], time);

			ArrayList<VEntity> agents = collector.buildVAgentList(time);
			HashMap<String, VEntity> prevAgents = collector.buildVAgentMap(time - 1);

			double MTTF = 0.0; // mean time to fail
			// What if we consider that a failure is a non-statechange for some consec
			// What if we consider that a faiure is the length of time with non-consecutive
			// changes
			int failCounter = 0;

			double MTTR = 0.0; // mean time to recover
			double A = 0.0;
			double U = 0.0;
			// What if we consider that a recovery is the length of a failure
			int recoveryCounter = 0;

			for (VEntity v : agents) {
				if (mvm1.entityIsOfType(v)) {
					boolean lifeState = mvm1.isParameterEqualToDesiredValue(v);
					VEntity pv = prevAgents.get(v.getName());
					if (pv == null) {
						// System.out.println("Agent didnt exist...");
						continue;
					}
					boolean prevState = mvm1.isParameterEqualToDesiredValue(pv);
					if (lifeState == prevState) {
						Integer agentUpTime = theAgentsUptime.get(v.getName());
						Integer agentDowntime = theAgentsDowntime.get(v.getName());

						if (agentUpTime == null) {
							theAgentsUptime.put(v.getName(), 0);
							// agentUpTime = theAgentsUptime.get(v.getName());
						}
						if (agentDowntime == null) {
							theAgentsDowntime.put(v.getName(), 0);
							// agentDowntime = theAgentsDowntime.get(v.getName());
						}
						if (agentDowntime != null && agentUpTime != null) {
							// Has now entered a "downtime" state
							if (agentDowntime == consecutiveDowntime) {
								MTTF += agentUpTime;
								failCounter++;
								theAgentsUptime.put(v.getName(), 0);
								theAgentsDowntime.put(v.getName(), agentDowntime + 1);
								// System.out.println("MTTF: "+MTTF);
							} else {
								// theAgentsUptime.put(v.getName(), 0);
								theAgentsDowntime.put(v.getName(), agentDowntime + 1);
							}
						}

					} else {
						Integer agentUpTime = theAgentsUptime.get(v.getName());
						Integer agentDowntime = theAgentsDowntime.get(v.getName());

						if (agentUpTime == null) {
							theAgentsUptime.put(v.getName(), 0);
							// agentUpTime = theAgentsUptime.get(v.getName());
						}
						if (agentDowntime == null) {
							theAgentsDowntime.put(v.getName(), 0);
							// agentDowntime = theAgentsDowntime.get(v.getName());
						}

						if (agentDowntime != null && agentUpTime != null) {
							if (agentDowntime >= consecutiveDowntime) {
								MTTR += agentDowntime;
								recoveryCounter++;
								theAgentsDowntime.put(v.getName(), 0);
								theAgentsUptime.put(v.getName(), agentUpTime + 1);
							} else {
								theAgentsDowntime.put(v.getName(), 0);
								theAgentsUptime.put(v.getName(), agentUpTime + 1);
							}
						}
					}
				}
			}

			if (failCounter == 0) {
				MTTF = 0;
			} else {
				MTTF = MTTF / (double) failCounter;
			}
			if (recoveryCounter == 0) {
				MTTR = 0;
			} else {
				MTTR = MTTR / (double) recoveryCounter;
			}

			if ((MTTR + MTTF) == 0) {
				A = 0.5;
				U = 0.5;
			} else {
				A = MTTF / (MTTF + MTTR); // availability
				U = MTTR / (MTTF + MTTR); // unavailability
				// A + U = 1
			}

			auResult.addResultAtStep(aName, A, time);
			auResult.addResultAtStep(uName, U, time);
			auResult.addResultAtStep(mttfName, MTTF, time);
			auResult.addResultAtStep(mttrName, MTTR, time);
			sb.append(time + "\t" + MTTR + "\t" + MTTF + "\t" + A + "\t" + U + "\t" + failCounter + "\t"
					+ recoveryCounter + "\t" + realEvents_emergence[time] + "\t" + realEvents_stability[time] + "\t"
					+ realEvents_criticality[time] + "\n");
			// println(time+"\t"+MTTR+"\t"+MTTF+"\t"+A+"\t"+U+"\t"+failCounter+"\t"+recoveryCounter);

			// //currentResult.addResultAtStep(aName, A, time);
			// //currentResult.addResultAtStep(uName, U, time);

		}
		calculateAccuracy("Villegas: Availability", aName, auResult, 0, 100.0, 0.025);
		calculateAccuracy("Villegas: Unavailability", uName, auResult, 0, 100.0, 0.025);
		// Utilities.writeToFile(sb.toString(),
		// resultsDirRoot+systemName.replaceAll("\\s+","")+"/"+metricName.replaceAll("\\s+","")+"/"+si.getConfigurationString()+".tsv");
		return auResult;
	}

	public static MetricResult Metric_PerfSit(MetricInfo mi, SystemInfo si, MetricParameters mp,
			DataCollector_FileSystem collector) {
		int totalNumberOfSteps = si.getNumberOfSteps();
		String initCrit = si.getConfigurationString();
		String systemName = si.getSystemName();
		announce("PerfSit");
		String metricName = "PerfSit";
		String resultsName = metricName + ": " + "PerfSit";
		StringBuilder sb = new StringBuilder();
		SelfAdaptiveSystems sas = new SelfAdaptiveSystems(mi);
		MetricResult perfsitResult = new MetricResult(systemName, metricName, totalNumberOfSteps, si, resultsDirRoot);
		resultsName = resultsName + mp.toStringNS();
		perfsitResult.addResultType(realEventsNameEm);
		perfsitResult.addResultType(realEventsNameSt);
		perfsitResult.addResultType(realEventsNameCr);
		perfsitResult.addResultType(realEventsNameAd);
		perfsitResult.addResultType(resultsName);
		// //currentResult.addResultType(resultsName);

		// Situation is a snapshot of size k >= 1
		// Each subsituation is a changing of state
		// the max cost to change state is some number
		// max cost of changing from alive to dead is 8
		// max cost of changing from dead to alive is 3 (it is also the only cost)

		double cMax = 0.0;
		double subsitSum = 0.0;

		for (int time = 1; time < totalNumberOfSteps; time++) {
			perfsitResult.addResultAtStep(realEventsNameEm, realEvents_emergence[time], time);
			perfsitResult.addResultAtStep(realEventsNameSt, realEvents_stability[time], time);
			perfsitResult.addResultAtStep(realEventsNameCr, realEvents_criticality[time], time);
			perfsitResult.addResultAtStep(realEventsNameAd, realEvents_adaptability[time], time);
			// cMax = 0.0;
			// subsitSum = 0.0;
			ArrayList<VEntity> agents = collector.buildVAgentList(time);
			HashMap<String, VEntity> prevAgents = collector.buildVAgentMap(time - 1);

			double perf = sas.PerfSit(agents, prevAgents, new Vector2(areaX, areaY), mp);
			sb.append(time + "\t" + perf + "\t" + realEvents_emergence[time] + "\t" + realEvents_stability[time] + "\t"
					+ realEvents_criticality[time] + "\n");
			perfsitResult.addResultAtStep(resultsName, perf, time);
			// //currentResult.addResultAtStep(resultsName, perf, time);
		}
		calculateAccuracy("Situation Perfomance", resultsName, perfsitResult, 0, 100.0, 0.025);
		// Utilities.writeToFile(sb.toString(),
		// resultsDirRoot+systemName.replaceAll("\\s+","")+"/"+metricName.replaceAll("\\s+","")+"/"+si.getConfigurationString()+".tsv");
		return perfsitResult;
	}

	public static MetricResult Metric_Counter(MetricInfo mi, SystemInfo si, MetricParameters mp,
			DataCollector_FileSystem collector) {
		int totalNumberOfSteps = si.getNumberOfSteps();
		String initCrit = si.getConfigurationString();
		Counter tcv = new Counter(mi);
		announce(tcv.getMetricName());
		String systemName = si.getSystemName();

		String countingType = (String) mp.getParameterValue("counting-type");

		tcv.setup(totalNumberOfSteps, countingType);
		tcv.setCollector(collector);
		MetricResult tcvResult = new MetricResult(systemName, tcv.getMetricName(), totalNumberOfSteps, si,
				resultsDirRoot);
		tcv.setResultStore(tcvResult);

		tcv.run();
		tcvResult = tcv.getResults();
		return tcvResult;
	}

	public static MetricResult Metric_SimpleStatistics(MetricInfo mi, SystemInfo si, MetricParameters mp,
			DataCollector_FileSystem collector) {
		int totalNumberOfSteps = si.getNumberOfSteps();
		String systemName = si.getSystemName();
		SimpleStatistic ss = new SimpleStatistic(mi);
		announce(ss.getMetricName());
		MetricResult ssResult = new MetricResult(systemName, ss.getMetricName(), totalNumberOfSteps, si,
				resultsDirRoot);
		ss.setCollector(collector);
		ss.setResultStore(ssResult);
		boolean entCount = (Boolean) mp.getParameterValue("counting-entities");
		boolean interCount = (Boolean) mp.getParameterValue("counting-interactions");

		ss.setup(totalNumberOfSteps, entCount, interCount);
		ss.run();

		ssResult = ss.getResults();

		return ssResult;

	}

	/**
	 * Calculate accuracy for a metric generated set of results and a corresponding
	 * real events array
	 * 
	 * @param metricName
	 * @param resultsName
	 * @param result
	 * @param lower
	 * @param upper
	 * @param increment
	 */
	public static void calculateAccuracy(String metricName, String resultsName, MetricResult result, double lower,
			double upper, double increment) {
		if (!noAccuracyCalculations) {
			double maxScoreEm = -Double.MAX_VALUE;
			double maxScoreSt = -Double.MAX_VALUE;
			double maxScoreCr = -Double.MAX_VALUE;
			double maxScoreAd = -Double.MAX_VALUE;
			double maxDEm = -1;
			double maxDSt = -1;
			double maxDCr = -1;
			double maxDAd = -1;
			int bestWindowEM = 1;
			int bestWindowSt = 1;
			int bestWindowCr = 1;
			int bestWindowAd = 1;
			AccuracyResults bestEm = new AccuracyResults();
			AccuracyResults bestSt = new AccuracyResults();
			AccuracyResults bestCr = new AccuracyResults();
			AccuracyResults bestAd = new AccuracyResults();

			print("Calculating accuracy for %1$s for %2$s with threshold range set between %3$f & %4$f with an increment of %5$f",
					metricName, resultsName, lower, upper, increment);
			// Note: Set window to 1 for the original tests
			for (double d = lower; d < upper; d = d + increment) {
				for (double db = 1.0; db < 2; db = db + 1.0) {
					for (int window = 1; window < 15; window++) {
						double accEm = result.accuracyCalculation(resultsName, realEventsNameEm, d, db, window, false);
						if (accEm > maxScoreEm) {
							maxScoreEm = accEm;
							maxDEm = d;
							bestWindowEM = window;
							bestEm = result.getLastResult().clone();
						}

						double accSt = result.accuracyCalculation(resultsName, realEventsNameSt, d, db, window, false);
						if (accSt > maxScoreSt) {
							maxScoreSt = accSt;
							maxDSt = d;
							bestWindowSt = window;
							bestSt = result.getLastResult().clone();
						}

						double accCr = result.accuracyCalculation(resultsName, realEventsNameCr, d, db, window, false);
						if (accCr > maxScoreCr) {
							maxScoreCr = accCr;
							maxDCr = d;
							bestWindowCr = window;
							bestCr = result.getLastResult().clone();
						}
						double accAd = result.accuracyCalculation(resultsName, realEventsNameAd, d, db, window, false);
						if (accAd > maxScoreAd) {
							maxScoreAd = accAd;
							maxDAd = d;
							bestWindowAd = window;
							bestAd = result.getLastResult().clone();
						}
					}
				}
			}

			if (maxScoreCr == -Double.MAX_VALUE) {
				maxScoreCr = -1.0;
			}
			if (maxScoreSt == -Double.MAX_VALUE) {
				maxScoreSt = -1.0;
			}
			if (maxScoreEm == -Double.MAX_VALUE) {
				maxScoreEm = -1.0;
			}

			// Send the best to files
			result.accuracyCalculation(resultsName, realEventsNameEm, maxDEm, 1, bestWindowEM, true);
			result.accuracyCalculation(resultsName, realEventsNameSt, maxDSt, 1, bestWindowSt, true);
			result.accuracyCalculation(resultsName, realEventsNameCr, maxDCr, 1, bestWindowCr, true);

			StringBuilder sb = new StringBuilder();
			String header = "Metric Name\tSO Type\tThreshold\tF1\tAccuracy\tSpecificity\tSensitivity\tPrecision\tActual Events\tTrue Positives\tFalse Positives\tTrue Negatives\tFalse Negatives\tWindow Size";
			String emResults = String.format(
					metricName
							+ "\tEmergence\t%1$f\t%2$f\t%3$f\t%4$f\t%5$f\t%6$f\t%7$d\t%8$d\t%9$d\t%10$d\t%11$d\t%12$d",
					maxDEm, maxScoreEm, bestEm.calculateACC() * 100.0, bestEm.calculateSPC() * 100.0,
					bestEm.calculateTPR() * 100.0, bestEm.calculatePPV() * 100.0, bestEm.getNumberOfRealInstances(),
					bestEm.getTruePositives(), bestEm.getFalsePositives(), bestEm.getTrueNegatives(),
					bestEm.getFalseNegatives(), bestWindowEM);
			String stResults = String.format(
					metricName
							+ "\tStability\t%1$f\t%2$f\t%3$f\t%4$f\t%5$f\t%6$f\t%7$d\t%8$d\t%9$d\t%10$d\t%11$d\t%12$d",
					maxDSt, maxScoreSt, bestSt.calculateACC() * 100.0, bestSt.calculateSPC() * 100.0,
					bestSt.calculateTPR() * 100.0, bestSt.calculatePPV() * 100.0, bestSt.getNumberOfRealInstances(),
					bestSt.getTruePositives(), bestSt.getFalsePositives(), bestSt.getTrueNegatives(),
					bestSt.getFalseNegatives(), bestWindowSt);
			String crResults = String.format(metricName
					+ "\tCriticality\t%1$f\t%2$f\t%3$f\t%4$f\t%5$f\t%6$f\t%7$d\t%8$d\t%9$d\t%10$d\t%11$d\t%12$d",
					maxDCr, maxScoreCr, bestCr.calculateACC() * 100.0, bestCr.calculateSPC() * 100.0,
					bestCr.calculateTPR() * 100.0, bestCr.calculatePPV() * 100.0, bestCr.getNumberOfRealInstances(),
					bestCr.getTruePositives(), bestCr.getFalsePositives(), bestCr.getTrueNegatives(),
					bestCr.getFalseNegatives(), bestWindowCr);
			String adResults = String.format(metricName
					+ "\tAdaptability\t%1$f\t%2$f\t%3$f\t%4$f\t%5$f\t%6$f\t%7$d\t%8$d\t%9$d\t%10$d\t%11$d\t%12$d",
					maxDAd, maxScoreAd, bestAd.calculateACC() * 100.0, bestAd.calculateSPC() * 100.0,
					bestAd.calculateTPR() * 100.0, bestAd.calculatePPV() * 100.0, bestAd.getNumberOfRealInstances(),
					bestAd.getTruePositives(), bestAd.getFalsePositives(), bestAd.getTrueNegatives(),
					bestAd.getFalseNegatives(), bestWindowAd);

			// Send the results to a nicer place...
			// Maybe a BIG static string builder
			// sb.append(header);
			sb.append(initCriteria + "\t" + emResults);
			sb.append("\n");
			sb.append(initCriteria + "\t" + stResults);
			sb.append("\n");
			sb.append(initCriteria + "\t" + crResults);
			sb.append("\n");
			sb.append(initCriteria + "\t" + adResults);
			// toTheDoc.append(sb + "\n");

			println(header);
			println(emResults);

			println(stResults);

			println(crResults);
			println(adResults);
		}
	}

	// TODO: Make these not magic
	public static ArrayList<MetricResult> metricRunner(SystemInfo testSystem, MetricInfo mi, String initString,
			DataCollector_FileSystem collector) {
		String metricName = mi.getMetricName();
		ArrayList<MetricResult> mr = new ArrayList<MetricResult>();
		switch (metricName) {
		case "System Complexity":
			return new ArrayList<MetricResult>(Arrays.asList(Metric_SystemComplexity(testSystem, mi, collector)));
		case "Chan GoL 11":
			return new ArrayList<MetricResult>(Arrays.asList(Metric_ChanGoLIM(testSystem, mi, collector)));
		case "OToole 14":
			return new ArrayList<MetricResult>(Arrays.asList(Metric_OToole14(testSystem, mi, collector)));
		case "Oscillation Detection":
			return new ArrayList<MetricResult>(Arrays.asList(Metric_OscillatorDetect(testSystem, mi, collector)));
		case "Tag & Track":
			return new ArrayList<MetricResult>(Arrays.asList(Metric_TagAndTrack(testSystem, mi, collector)));
		case "Multi-Scale-Shannon-Entropy":
			ArrayList<MetricParameters> mpset = mi.getMetricParameters();
			for (int i = 0; i < mpset.size(); i++) {
				mr.add(Metric_MSSE(testSystem, mi, mpset.get(i), collector));
			}
			return mr;
		case "Limited Bandwidth Recognition":
			ArrayList<MetricParameters> mpset_lbr = mi.getMetricParameters();
			for (int i = 0; i < mpset_lbr.size(); i++) {
				mr.add(Metric_BR(testSystem, mi, mpset_lbr.get(i), collector));
			}
			return mr;
		case "Entropy Over Time":
			ArrayList<MetricParameters> mpset_eot = mi.getMetricParameters();
			for (int i = 0; i < mpset_eot.size(); i++) {
				mr.add(Metric_EntropyOverTime(mi, testSystem, mpset_eot.get(i), collector));
			}
			return mr;
		case "KaddoumWAT":
			return new ArrayList<MetricResult>(Arrays.asList(Metric_KaddoumWAT(mi, testSystem, collector)));
		case "VillegasAU":
			return new ArrayList<MetricResult>(Arrays.asList(Metric_VillegasAU(mi, testSystem, collector)));
		case "PerfSit":
			ArrayList<MetricParameters> mpset_ps = mi.getMetricParameters();
			for (int i = 0; i < mpset_ps.size(); i++) {
				mr.add(Metric_PerfSit(mi, testSystem, mpset_ps.get(i), collector));
			}
			return mr;
		case "Counter":
			ArrayList<MetricParameters> mpset_cou = mi.getMetricParameters();
			MetricParameters mp = mpset_cou.get(0);
			return new ArrayList<MetricResult>(Arrays.asList(Metric_Counter(mi, testSystem, mp, collector)));
		case "SimpleStatistic":
			ArrayList<MetricParameters> mpset_ss = mi.getMetricParameters();
			MetricParameters mpss = mpset_ss.get(0);
			return new ArrayList<MetricResult>(Arrays.asList(Metric_SimpleStatistics(mi, testSystem, mpss, collector)));
		default:
			println("Metric name (%1$s) unknown: ", metricName);
			return null;

		}
	}

	public static void println(String str, Object... objs) {
		System.out.println(String.format(str, objs));
	}

	public static void print(String str, Object... objs) {
		if (!quiet) {
			System.out.println(String.format(str, objs));
		}
	}

	public static void announce(String str) {
		String s = "********" + str + "********";
		print(s);
	}

	public static String parseModelName(String str) {
		String[] split = str.split("/");
		return split[split.length - 1];
	}

}
