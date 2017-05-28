package observationTool.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import castleComponents.objects.Vector2;
import experimentExecution.SystemInfo;
import interactionGraph.InteractionGraph;
import interactionGraph.Node;
import observationTool.MetricRunner_ED;
import observationTool.VEntity;
import observationTool.results.MetricResult;
import stdSimLib.utilities.RandomGen;
import stdSimLib.utilities.Utilities;

/**
 * DESCRIPTION:
 * 	Something I've made up. Find the clusters in a Interaction Graph, 
 * 		track cluster movement and somehow see if this corresponds to self-organisation
 * 	Issue: Relies on an undirected graph...
 * 	
 * 
 * LOGIC:
 * 	1: Make the SI graph from Step t
 *  2: Run HCS to identify the clusters, Cx and tag them, if not tagged
 *  3: For each Cx, analyse SOME*H*O*W
 * 
 * REQUIREMENTS:
 *	• All Agents and Interactions at time t to build an Interaction Graph
 *	• 
 * 
 * 
 * @author lachlan
 *
 */

public class ClusterTrack implements MetricInterface{

	public String metricName;
	StringBuilder sb;
	HashMap<String, Integer> prevIDs;
	
	public ClusterTrack() {
		// TODO Auto-generated constructor stub
		metricName = "ClusterTrack";
//		clusters = new ArrayList<Cluster>();
		sb = new StringBuilder();
		prevIDs = new HashMap<String, Integer>();
	}

	@Override
	public void runMetric(Object... params) {
		// TODO Auto-generated method stub
		
	}
	
	public void Calculate(ArrayList<Cluster> previous, ArrayList<Cluster> current, double threshold){
		//Calculate likely movements
//		for (int i = 0; i < current.size(); i++){
//			System.out.println(current.get(i).getStats());
//		}
		//Return a list of Clusters who persisted?
		ArrayList<Cluster> persistentClusters = new ArrayList<Cluster>();
		
		//How can we examine individual clusters?
		
		
		for (int i= 0; i < previous.size(); i++){
			for (int j = 0; j < current.size(); j++){
				if (previous.get(i).getClusterStringID().compareToIgnoreCase(current.get(j).getClusterStringID()) == 0 && current.get(j).getClusterStringID().length()>0){
					persistentClusters.add(current.get(j));
					//No what?
					//Study the similarities of those clusters??
					//How have they changed since the last cluster study?
					//Do some community detection?
					
//					System.out.println(previous.get(i).getClusterStringID());
				}
//				if (previous.get(i).getCentroid().compareDistance(current.get(j).getCentroid()) <= threshold){
////					System.out.println("MAGIC: previous: "+previous.get(i).getStats()+" current: "+current.get(j).getStats());
//				}
			}
		}
		System.out.println("Number of persistent clusters (t -> t+1): "+ persistentClusters.size());
		for (int i = 0; i < persistentClusters.size(); i++){
			System.out.println(persistentClusters.get(i).getStats());
		}
	}
	
	public HashMap<String, Double> examineClusters(ArrayList<Cluster> clusters){
//		double 
		double averageClusterStateDensity = 0.0;
		double averageDensity = 0.0;
		double averageArea = 0.0;
		double counter = 0;
		HashMap<String, Double> results = new HashMap<String,Double>();
		ArrayList<Double> csds = new ArrayList<Double>();
		ArrayList<Double> ads = new ArrayList<Double>();
		for (Cluster c : clusters){
			if (c.getNumAgentsInCluster() > 1){
				counter++;
//				System.out.println("CSD: "+clusterStateDensity(c) +" AD: "+c.getDensity());
				double csd = clusterStateDensity(c);
				averageClusterStateDensity += csd;				
				averageDensity += c.getDensity();
				averageArea += c.getArea();
				csds.add(clusterStateDensity(c));
				ads.add(c.getDensity());
				
				if (prevIDs.get(c.getShortID()) == null){
					prevIDs.put(c.getShortID(),1);
//					System.out.println(c.getClusterStringID()+" exists already!");
				} else {
					prevIDs.put(c.getShortID(),prevIDs.get(c.getShortID())+1);
				}
			}			
		}
		averageDensity = averageDensity / counter;
		averageClusterStateDensity = averageClusterStateDensity / counter;
		averageArea = averageArea / counter;
		double clustersIntersecting = clusterIntersect(clusters);
//		System.out.println("average cluster state density: " + averageClusterStateDensity);
//		System.out.println("average cluster agent density: "+averageDensity);
//		return averageClusterStateDensity+"\t"+averageDensity+"\t"+Utilities.calculateSTDDev(csds)+"\t"+Utilities.calculateSTDDev(ads)+"\t"
//				+Utilities.calculateMax(csds)+"\t"+Utilities.calculateMax(ads)+"\t"+Utilities.calculateMin(csds)
//				+"\t"+Utilities.calculateMin(ads)+"\t"+counter+"\t"+averageArea+"\t"+prevIDs.size()+"\t"+clustersIntersecting;
		results.put("averageClusterStateDensity",averageClusterStateDensity);
		results.put("averageAgentDensity", averageDensity);
		results.put("STDDEVAgentStateDensity", Utilities.calculateSTDDev(csds));
		results.put("STDDEVAgentDensity", Utilities.calculateSTDDev(ads));
		results.put("MaxAgentStateDensity", Utilities.calculateMax(csds));
		results.put("MinAgentStateDensity", Utilities.calculateMin(csds));
		results.put("MaxAgentDensity", Utilities.calculateMax(ads));
		results.put("MinAgentDensity", Utilities.calculateMin(ads));
		results.put("RunningClusterCount", counter);
		results.put("AverageArea", averageArea);
		results.put("RunningUniqueClusters", (double)prevIDs.size());
		results.put("ClustersIntersecting",clustersIntersecting);
		
		return results;
		
	}
	
	public double clusterStateDensity(Cluster c){
		double density = c.getDensity();
		double area = c.getArea();
		double result = 0.0;
		double countAlive = 0;
		ArrayList<Node> cNodes = c.getNodes();
		double numNodes = cNodes.size();
		for (Node n : cNodes){
			VEntity v = n.getVAgent();
			//Used for the ED
//			boolean res = MetricRunner_ED.busyOrFull(v);
//			if (res){
//				countAlive++;
//			}
			
			//THIS IF FOR Game Of LIFE (WE NEED TO MAKE THIS GENERIC. AFTER THE PAPER IS DONE
			//TODO: IHAISUDH****
			if (v.getParameterValueFromStringAsString("Alive").compareToIgnoreCase("True") == 0){
				countAlive++;
			}			
		}
		
		result = countAlive / area;
		
		return result;
	}
	
	public double clusterIntersect(ArrayList<Cluster> clusters){
		double res = 0.0;
		for (int i = 0; i < clusters.size(); i++){
			Cluster c = clusters.get(i);
			for (int j = i+1; j < clusters.size(); j++){
				Cluster d = clusters.get(j);
				if (c == d){
					continue;
				}
				if (c.getNumAgentsInCluster() < 2 || d.getNumAgentsInCluster() < 2){
					continue;
				}
				Vector2 cCen = c.getCentroid();
				double cxMax = cCen.getX() + (c.getWidth()/2.0);
				double cyMax = cCen.getY() + (c.getHeight()/2.0);
				double cxMin = cCen.getX() - (c.getWidth()/2.0);
				double cyMin = cCen.getY() - (c.getHeight()/2.0);
				
				Vector2 dCen = d.getCentroid();
				double dxMax = dCen.getX() + (d.getWidth()/2.0);
				double dyMax = dCen.getY() + (d.getHeight()/2.0);
				double dxMin = dCen.getX() - (d.getWidth()/2.0);
				double dyMin = dCen.getY() - (d.getHeight()/2.0);
				
				if ((dxMin > cxMax || dxMax < cxMin || dyMax > cyMin || dyMin < cyMax)){
					res++;
				}	
			}
		}
		return res;
	}
	
	public String analyseClustersAndPrint(ArrayList<Cluster> clusters){
//		System.out.println("Number of clusters: "+clusters.size());
		for (Cluster c : clusters){
			if (c.getNumAgentsInCluster() > 1){
				c.calculateDimensions();
				c.calculateCentroid();
				c.createStringID();
//				sb.append(c.getClusterStringID()+"\t"+c.getCentroid().toString()+"\n");
				if (prevIDs.get(c.getShortID()) == null){
					prevIDs.put(c.getShortID(),1);
//					System.out.println(c.getClusterStringID()+" exists already!");
				} else {
					prevIDs.put(c.getShortID(),prevIDs.get(c.getShortID())+1);
				}
//				System.out.println(c.getStats());
//				if (c.getNumAgentsInCluster() == 205){
//					c.createStringID();
//					sb.append(c.getClusterStringID()+"\t"+c.getCentroid().toString()+"\n");					
//				}
			}			
		}
		return printClusters(clusters);
	}
	
	public void zarf(){
//		System.out.println(sb.toString());
		int maxCount = 0;
		int sum = 0;
		String maxID = "";
		for (Map.Entry<String, Integer> entry : prevIDs.entrySet())
		{
			sum++; 
			if (entry.getValue() > maxCount){
				maxCount = entry.getValue();
				maxID = entry.getKey();
			}
//		    System.out.println(entry.getKey() + "/" + entry.getValue());
		}
		System.out.println("Number of unique clusters (unique cluster count): "+sum);
		System.out.println("Most frequent cluster (steps): "+maxCount);
	}
	
	public String printClusters(ArrayList<Cluster> clusters){
		String str = "";
		for (Cluster c : clusters){
			if (c.getNumAgentsInCluster() > 1){
				str += c.forGNUPlot()+"\n";
			}
		}
		
		return str;
	}
	
	public ArrayList<Cluster> dfsHelper(InteractionGraph g){
		HashMap<Node, Boolean> discovered = new HashMap<Node, Boolean>();
		ArrayList<Cluster> clusters = new ArrayList<Cluster>();
		Cluster tmpCluster;
		int clusterCounter = 0;
		for (Node n : g.getNodes()){
			if (discovered.get(n) == null){
				clusterCounter++;
				tmpCluster = new Cluster(clusterCounter);
				clusters.add(tmpCluster);
				
				discovered.put(n, true);
				
				dfs(n, discovered, tmpCluster);
			}
		}
		
		for (Cluster c : clusters){
			c.calculateDimensions();
			c.createStringID();
		}
		
		return clusters;
		
	}
	
	public void dfs(Node n, HashMap<Node, Boolean> disc, Cluster c){
		for (Node u : n.getConnectedNodes()){
			if (disc.get(u) == null){
				c.addNode(u);
				disc.put(u, true);
				dfs(u, disc, c);
			}
		}
	}
	
	public InteractionGraph HCS(InteractionGraph ig){
		
		
		return null;
	}
	
	public void MinCut(InteractionGraph ig){
			
	}
	
	//How many centroids? Space divided by 4,5,6,7...X?
	public ArrayList<Cluster> KMeans(InteractionGraph ig, int numClusters, Vector2 totalSpace){
		ArrayList<Cluster> clusters = new ArrayList<Cluster>();
		ArrayList<Node> nodes = ig.getNodes();
		//Random init
		clusters.clear();
		for (int i = 0; i < numClusters; i++){
			Cluster cluster = new Cluster(i);
			Vector2 newVec = new Vector2(RandomGen.generateRandomRangeDouble(0, totalSpace.getX()), 
					RandomGen.generateRandomRangeDouble(0, totalSpace.getY()));
//			System.out.println("newVec: "+newVec);
			cluster.setCentroid(new Vector2(newVec));
			clusters.add(cluster);
		}
		
		boolean finished = false;
		ArrayList<Vector2> prevCentroids = new ArrayList<Vector2>();
		ArrayList<Vector2> newCentroids = new ArrayList<Vector2>();
		int numSteps = 0;
		//Do iteration
		while (!finished){
			prevCentroids.clear();
			newCentroids.clear();
			
			//Reset cluster lists and get previous centroids
			for (Cluster cluster : clusters){
				prevCentroids.add(cluster.getCentroid());
				cluster.clear();
			}
			
			//Assign clusters
			double max = Double.MAX_VALUE;
			double min = max;
			int clusterAssignment = 0;
			double distance = 0.0;
			
			//Assign nodes to clusters
			for (Node node : nodes){
				min = max;
				for (int i = 0; i < numClusters; i++){
					Cluster c = clusters.get(i);
					distance = Utilities.calculateDistance2D(node.getPosition(), c.getCentroid());
					if (distance < min){
						min = distance;
						clusterAssignment = i;
					}
				}
				clusters.get(clusterAssignment).addNode(node);
			}
			
			//Calculate new centroids
			for (Cluster cluster : clusters){
				double sumX = 0.0;
				double sumY = 0.0;
				ArrayList<Node> theseNodes = cluster.getNodes();
//				int nodeCount = theseNodes.size();
				int nodeCount = 0;
				for (Node n : theseNodes){
					if (n.getVAgent().getParameterValueFromStringAsString("Alive").compareToIgnoreCase("true") == 0 ){
						Vector2 v = n.getPosition();
						sumX += v.getX();
						sumY += v.getY();
						nodeCount++;
					}
//					Vector2 v = n.getPosition();
//					sumX += v.getX();
//					sumY += v.getY();
//					nodeCount++;
				}
								
				if (nodeCount > 0){
					double newX = sumX / (double)nodeCount;
					double newY = sumY / (double)nodeCount;
					cluster.setCentroid(new Vector2(newX, newY));					
				}
				newCentroids.add(cluster.getCentroid());
				
			}
			numSteps++;
			distance = 0.0;
			for (int i = 0; i < prevCentroids.size(); i++){
				distance += Utilities.calculateDistance2D(prevCentroids.get(i), newCentroids.get(i));
			}
			
			
			if (distance == 0){
				finished = true;
			}
			//DEBUG
			System.out.println("Num iterations: "+numSteps);
			System.out.println("Centroid distance: " + distance);
			
//			System.out.println("Cluster density: ");
			for (Cluster c : clusters){
				c.calculateDimensions();
				c.createStringID();
//				System.out.print(c.getDensity()+", ");
			}
//			System.out.println();
			
		}		
		return clusters;
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
	
	public void MinCutPhase(InteractionGraph ig){
		//
		double cutOfThePhase = 0.0;
		//Get all nodes
		ArrayList<Node> nodes = ig.sortNodesOnEdgeCount();
		InteractionGraph newIG = new InteractionGraph();
		
		
		//Add a random Node to newIG
		int randNodeIndex = RandomGen.generateRandomRangeInteger(0, nodes.size());
		newIG.addNode(nodes.get(randNodeIndex));	
		
		//Cut-of-the-phase: The cut-of-the-phase is the sum of the weight of all the edges connecting
		//	to the last vertex added to A
		cutOfThePhase = nodes.get(nodes.size()-1).getTotalWeight();
		
		//Add all nodes to newIG but merge the last two nodes
		for (int i = nodes.size() - 1; i >= 0; i++){
			if (i == randNodeIndex){
				continue;
			}
			newIG.addNode(nodes.get(i));
		}
	}	
}

class Cluster{
	Vector2 centroid;
	ArrayList<Node> nodes;//should be a hashset (or should it)
	HashSet<String> checkForDupNodes;
	int clusterID;
	String clusterIDString;
	
	//Cluster information
	double minX;
	double maxX;
	double width;
	double height;
	double minY;
	double maxY;
	int agentsInCluster;
	double clusterDensity;
	
	
	public Cluster(int clusterID){
		this.clusterID = clusterID;
		centroid = new Vector2();
		nodes = new ArrayList<Node>();
		checkForDupNodes = new HashSet<String>();
	}
	
	public void createStringID(){
		clusterIDString = "";
		Collections.sort(nodes,Node.sortByVAgentName());
		for (Node n : nodes){
			clusterIDString = clusterIDString.concat(n.getVAgent().getName());
		}
	}
	
	public String getClusterStringID(){
		return clusterIDString;
	}
	
	public ArrayList<Node> getNodes(){
		return nodes;
	}
	
	public void addNode(Node n){
		if (checkForDupNodes.add(n.getName())){
			nodes.add(n);
		}
	}
	
	public void clear(){
		nodes = new ArrayList<Node>();
		checkForDupNodes = new HashSet<String>();
		minX = 0;
		minY = 0;
		maxX = 0;
		maxY = 0;
		width = 0;
		height = 0;
		clusterDensity = 0;
		agentsInCluster = 0;
		
	}
	
	public double getArea(){
		return (width * height);
	}
	
	public void setCentroid(Vector2 pos){
		centroid = new Vector2(pos);
	}
	
	public Vector2 getCentroid(){
		return centroid;
	}
	
	public void calculateCentroid(){
		double sumX = 0;
		double sumY = 0;
		for (Node n : nodes){
			Vector2 pos = n.getPosition();
			sumX = sumX + pos.getX();
			sumY = sumY + pos.getY();
		}
		
		sumX = sumX / (double)nodes.size();
		sumY = sumY / (double)nodes.size();
		setCentroid(new Vector2(sumX, sumY));
	}
	
	public int getNumAgentsInCluster(){
		agentsInCluster = nodes.size();
		return agentsInCluster;
	}
	
	public double getDensity(){
		return clusterDensity;
	}
	
	public String getShortID(){
		String str = "("+String.format("%1$f,%2$f",(double)Math.round(centroid.getX()), (double)Math.round(centroid.getY()))+")";
		str +=getNumAgentsInCluster()+","+getDensity()+","+getArea();
		return str;
	}
	
	public void calculateDimensions(){		
		if (getNumAgentsInCluster() > 0){
			//X
			Collections.sort(nodes,Node.sortByX());
			minX = nodes.get(0).getPosition().getX();
			maxX = nodes.get(nodes.size() - 1).getPosition().getX();
			width = maxX - minX;
			
			//Y
			Collections.sort(nodes,Node.sortByY());
			minY = nodes.get(0).getPosition().getY();
			maxY = nodes.get(nodes.size() - 1).getPosition().getY();
			height = maxY - minY;
			//Density
			double area = width * height;
			clusterDensity = (double)getNumAgentsInCluster() / area; //Assuming each agent takes up 1x1
		}
	}
	
	public double getWidth(){
		return width;
	}
	
	public double getHeight(){
		return height;
	}

	public String getStats(){
		String str = "centroid: "+centroid.toString();
		str += " density: "+clusterDensity;
		str += " width: "+width;
		str += "("+minX+","+maxX+")  ";
		str += " height: "+height;
		str += "("+minY+","+maxY+")";
		str += "area: " + (width * height);
		str += " number of nodes: " + nodes.size();
		
		return str;
	}
	
	//ID \t X \t Y \t w \t h
	public String forGNUPlot(){
		return getShortID()+"\t"+centroid.getX()+"\t"+centroid.getY()+"\t"+width+"\t"+height;
	}
}
