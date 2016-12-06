package interLib;

import interactionGraph.InteractionGraph;

import java.util.ArrayList; 

public class Snapshot implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4346517572442077647L;
	String snapshotID; //TODO: THIS IS USELESS?
	double currentTime;
	int snapshotInterval;
	String experimentName;
	String runName;
	String filePath;
	
	final int STATE_ORIGINAL = 0;
	final int STATE_IMPORTED = 1;
	int snapshotState = 0;
	
	/*Representations*/
	InteractionGraph igRepresentation = null;
	
	MetaModel metaModel;

	ArrayList<Agent> agents;
	ArrayList<Interaction> interactions;
	ArrayList<MetricScore> metricScores;

	public Snapshot(){
		agents = new ArrayList<Agent>();
		interactions = new ArrayList<Interaction>();
		metricScores = new ArrayList<MetricScore>();
	}
	
	public Snapshot(String id, ArrayList<Agent> agents, ArrayList<Interaction> interactions, double currentTime, int snapshotInterval, String experimentName, String runName){
		snapshotID = id;
		this.agents = new ArrayList<Agent>(agents);
		this.interactions = new ArrayList<Interaction>(interactions);
		this.metricScores = new ArrayList<MetricScore>();
		this.currentTime = currentTime;
		this.snapshotInterval = snapshotInterval;
		this.runName = runName;
		this.experimentName = experimentName;
	}
	
	/**
	 * Build a Snapshot from a MetaModel.
	 * @param mm
	 */
	public Snapshot(MetaModel mm){
		metaModel = mm;
		this.filePath = mm.getPathOfMetaModel();
		this.agents = mm.getAgents();
		this.interactions = mm.getInteractions();
		this.metricScores = mm.getMetricScores();
		this.currentTime = mm.getCurrentTime();
		this.snapshotInterval = mm.getSnapshotInterval();
		this.experimentName = mm.getExperimentName();
		this.runName = mm.getRunName();		
	}
	
	/**
	 * Used for cloning
	 * @param ss
	 */
	public Snapshot(Snapshot ss){
		this.agents = ss.getAgents();
		this.interactions = ss.getInteractions();
		this.metricScores = ss.getMetricScores();
		this.currentTime = ss.getCurrentTime();		
		this.snapshotInterval = ss.snapshotInterval;
		this.snapshotID = ss.getSnapshotID();
		this.experimentName = ss.getExperimentName();
		this.runName = ss.getRunName();
	}
	
	public void addNewMetricScore(String metricName, String metricConfiguration, String referenceID, String result){
		MetricScore newMS = new MetricScore(metricName, metricConfiguration, referenceID, result);
		for (MetricScore score : metricScores){
			if (score.compare(newMS)){
				//Uh-oh, comparison has already been done
				return;
			}				
		}
		metricScores.add(newMS);
	}
	
	public InteractionGraph getIGRepresentation(){
		if (igRepresentation == null){
			igRepresentation = new InteractionGraph(this);
		}
		return igRepresentation;
	}
	
	public String buildMetaModel(){
		if (metaModel == null){
			metaModel = new MetaModel();
		}
		
		return metaModel.buildMetaModel(agents, interactions, metricScores, (int)currentTime, snapshotInterval, experimentName, runName);
	}
	
	public Agent findAgentByID(String id){
		for (Agent agent : agents){
			if (agent.getID().compareToIgnoreCase(id) == 0){
				return agent;
			}
		}
		return null;
	}
	
	public String IDString(){
		return experimentName+" Run: "+runName + " CurrentTime: "+currentTime +" Interval: " + snapshotInterval;
	}
	
	public void addAgent(Agent ag){
		agents.add(ag);
	}
	
	public void addInteraction(Interaction in){
		interactions.add(in);
	}

	/**
	 * @return the snapshotID
	 */
	public String getSnapshotID() {
		return snapshotID;
	}

	/**
	 * @return the agents
	 */
	public ArrayList<Agent> getAgents() {
		return agents;
	}

	/**
	 * @return the interactions
	 */
	public ArrayList<Interaction> getInteractions() {
		return interactions;
	}

	/**
	 * @return the currentTime
	 */
	public int getCurrentTime() {
		return (int)currentTime;
	}

	/**
	 * @param currentTime the currentTime to set
	 */
	public void setCurrentTime(int currentTime) {
		this.currentTime = currentTime;
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
	
	public int getNumberOfAgents(){
		return agents.size();
	}
	
	public int getNumberOfInteractions(){
		return interactions.size();
	}

	/**
	 * @return the experimentName
	 */
	public String getExperimentName() {
		return experimentName;
	}

	/**
	 * @return the runName
	 */
	public String getRunName() {
		return runName;
	}

	/**
	 * @return the metricScores
	 */
	public ArrayList<MetricScore> getMetricScores() {
		return metricScores;
	}

	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * @return the snapshotState
	 */
	public int getSnapshotState() {
		return snapshotState;
	}

	/**
	 * @return the metaModel
	 */
	public MetaModel getMetaModel() {
		return metaModel;
	}

	/**
	 * @param experimentName the experimentName to set
	 */
	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}

	/**
	 * @param runName the runName to set
	 */
	public void setRunName(String runName) {
		this.runName = runName;
	}
	
}