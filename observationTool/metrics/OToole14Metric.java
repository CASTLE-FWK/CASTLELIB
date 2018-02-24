package observationTool.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.distribution.TDistribution;

import castleComponents.objects.Vector2;
import castleComponents.representations.Grid;
import experimentExecution.MetricInfo;
import experimentExecution.SystemInfo;
import observationTool.DataCollector_FileSystem;
import observationTool.VEntity;
import observationTool.results.MetricResult;

/*
 * DESCRIPTION:
 * 	.
 * 	
 * 
 * LOGIC:
 * 	1:Each agent records its Alive state, as well as the Alive states of it's neighbours (X, Y)
 *  2: X-hat and Y-hat are the sample mean for the X and Y properties
 *  3: 
 * 
 * REQUIREMENTS:
 *	• Each step (in order) and agents contained within
 * 
 * 
 * @author lachlan
 *
 */

public class OToole14Metric extends MetricBase implements MetricInterface {

	public int maxWindowSize = 20;
	public int windowTruncateSize = 5;
	public int currentWindowSize = 0;
	public int currentStep = 0;
	public double latestResult;
	public Grid<VEntity> theGrid;
	public HashMap<String, AgentWindow> agentWindows;
	public HashMap<String, Double> latestAgentResults;
	public boolean newResultsFlag = false;
	public double signifThreshold = 0.05;
	public int numberOfAgents = 0;
	public TDistribution tDist;

	public double blah;

	public ArrayList<Double> percentageSignif;

	public OToole14Metric(int maxWindowSize, int windowTruncateSize, MetricInfo mi) {
		super("OToole14Metric", mi);
		this.maxWindowSize = maxWindowSize;
		this.windowTruncateSize = windowTruncateSize;
		agentWindows = new HashMap<String, AgentWindow>();
		latestAgentResults = new HashMap<String, Double>();
	}

	public void setup(ArrayList<VEntity> agents, Vector2 dimensions) {
		// 1: Build agents into grid
		numberOfAgents = agents.size();
		theGrid = new Grid<VEntity>(VEntity.class, (int) dimensions.getX(), (int) dimensions.getY());
		for (VEntity agt : agents) {
			theGrid.addCell(agt, agt.getPosition());
			agentWindows.put(agt.getID(), new AgentWindow(agt.getID(), maxWindowSize));
		}
		percentageSignif = new ArrayList<Double>();
		tDist = new TDistribution(numberOfAgents - 2);
	}

	// OIASHDKUASd
	public void run(ArrayList<VEntity> agents, int currentStep) {
		for (VEntity agt : agents) {
			theGrid.addCell(agt, agt.getPosition());
		}
		// Cycle through Grid
		VEntity[][] rawGrid = theGrid.getGrid();
		for (VEntity xGrid[] : rawGrid) {
			for (VEntity agt : xGrid) {
				AgentWindow agentWindow = agentWindows.get(agt.getID());

				// Get each Agent's life state (X)
				if (agt.getParameterValueFromStringAsString("Alive").compareToIgnoreCase("true") == 0) {
					agentWindow.addX(1, currentWindowSize);
				}

				// Get each Agent's count of neighbour life states (Y)
				ArrayList<VEntity> neighbours = (ArrayList<VEntity>) theGrid
						.getNeighbours((int) agt.getPosition().getX(), (int) agt.getPosition().getY(), 1);
				int neighbourCount = 0;
				for (VEntity v : neighbours) {
					if (v.getParameterValueFromStringAsString("Alive").compareToIgnoreCase("true") == 0) {
						neighbourCount++;
					}
				}
				agentWindow.addY(neighbourCount, currentWindowSize);

			}
		}

		// Add one to window size counter
		currentWindowSize++;
		if (currentWindowSize == maxWindowSize) {
			double runningSig = 0.0;
			// If current window size is 20 (or N)
			// Cycle through AgentWindows
			Iterator<Entry<String, AgentWindow>> it = agentWindows.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, AgentWindow> pair = (Map.Entry<String, AgentWindow>) it.next();
				// Calculate Xhat & Yhat
				AgentWindow aw = pair.getValue();
				double xHat = aw.calculateMeanX();
				double yHat = aw.calculateMeanY();

				// Calculate the covariance: sum(t->N) ((Xt - Xhat) * (Yt -
				// Yhat) / N)
				int[] xValues = aw.getX();
				int[] yValues = aw.getY();
				double cov = 0.0;

				for (int i = 0; i < xValues.length; i++) {
					double tmpX = xValues[i] - xHat;
					double tmpY = yValues[i] - yHat;
					cov += (tmpX * tmpY) / (double) maxWindowSize;
				}

				// Calculate the product moment coeffecient: cov(x,y) /
				// sqrt(stddev(x))*sqrt(stddev(y))
				double res = cov / (Math.sqrt(aw.stdDevX()) * Math.sqrt(aw.stdDevY()));
				double tVal = Math.abs((res * Math.sqrt((numberOfAgents - 2) / (1 - Math.pow(res, 2)))));
				double pval = 2 * tDist.cumulativeProbability(-tVal);

				if (pval < signifThreshold) {
					runningSig++;
				}

				// System.out.println("stdDevX: "+aw.stdDevX());
				// System.out.println("stdDevY: "+aw.stdDevY());
				latestAgentResults.put(aw.getAgentID(), res);

				// Remove first 5 (or K) entries of window
				aw.truncate(windowTruncateSize);
			}

			// Drop current window size to N-K
			currentWindowSize = maxWindowSize - windowTruncateSize;
			newResultsFlag = true;
			latestResult = (runningSig / numberOfAgents) * 100;
			percentageSignif.add(latestResult);

		}

	}

	public double test() {
		TDistribution tDist = new TDistribution(623);
		double res = 0.0252;
		double tVal = Math.abs((res * Math.sqrt((625 - 2) / (1 - Math.pow(res, 2)))));
		System.out.println("tVal: " + tVal);
		return 2 * tDist.cumulativeProbability(-tVal);
	}

	public boolean resultsReady() {
		return newResultsFlag;
	}

	public double getLatestResults() {
		newResultsFlag = false;
		return latestResult;
	}

	public ArrayList<Double> getLatestResultsAsList() {
		newResultsFlag = false;
		ArrayList<Double> res = new ArrayList<Double>();

		Iterator it = latestAgentResults.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Double> pair = (Map.Entry<String, Double>) it.next();
			res.add(pair.getValue());
		}

		return res;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MetricResult getResults() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCollector(DataCollector_FileSystem dfs) {
		// TODO Auto-generated method stub
		
	}


}

class AgentWindow {
	String agentID;
	int maxWindowSize;
	int X[];
	int Y[];

	AgentWindow(String agentID, int maxWindowSize) {
		this.agentID = agentID;
		this.maxWindowSize = maxWindowSize;
		X = new int[this.maxWindowSize];
		Y = new int[this.maxWindowSize];
	}

	void addX(int val, int position) {
		X[position] = val;
	}

	void addY(int val, int position) {
		Y[position] = val;
	}

	int[] getX() {
		return X;
	}

	int[] getY() {
		return Y;
	}

	String getAgentID() {
		return agentID;
	}

	void reset() {
		X = new int[this.maxWindowSize];
		Y = new int[this.maxWindowSize];
	}

	void truncate(int truncateSize) {
		truncateX(truncateSize);
		truncateY(truncateSize);
	}

	double calculateMeanX() {
		double sum = 0.0;
		for (int i = 0; i < X.length; i++) {
			sum += X[i];
		}
		return sum / (double) X.length;
	}

	double calculateMeanY() {
		double sum = 0.0;
		for (int i = 0; i < Y.length; i++) {
			sum += Y[i];
		}
		return sum / (double) Y.length;
	}

	void truncateX(int truncateSize) {
		for (int i = 0; i < maxWindowSize - truncateSize; i++) {
			X[i] = X[i + truncateSize];
		}
	}

	void truncateY(int truncateSize) {
		for (int i = 0; i < maxWindowSize - truncateSize; i++) {
			Y[i] = Y[i + truncateSize];
		}
	}

	double stdDevX() {
		double mean = calculateMeanX();
		double temp = 0.0;
		for (int i : X) {
			temp += (mean - i) * (mean - i);
		}
		double variance = temp / (double) maxWindowSize;
		return Math.sqrt(variance);
	}

	double stdDevY() {
		double mean = calculateMeanY();
		double temp = 0.0;
		for (int i : Y) {
			temp += (mean - i) * (mean - i);
		}
		double variance = temp / (double) maxWindowSize;
		return Math.sqrt(variance);
	}
}