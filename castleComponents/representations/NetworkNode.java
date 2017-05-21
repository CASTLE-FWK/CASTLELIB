package castleComponents.representations;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

import castleComponents.Entity;
import castleComponents.objects.Vector2;


public class NetworkNode {

	String name;
	String type;
	private double x;	//Because of my terribleness, these have to remain
	private double y;
	Vector2 position;
	int outgoingInteraction;
	int incomingInteraction;
	double outgoingTotalWeight;
	double incomingTotalWeight;
	ArrayList<NetworkEdge> incomingEdges;
	ArrayList<NetworkEdge> outgoingEdges;
	HashSet<NetworkNode> connectedNodes;
	
	//Store the agent information inside the NetworkNode (who cares about RAM)
	Entity entity = null;

	
	public NetworkNode(Entity entity){
		this.entity = entity;
		this.name = entity.getID();
		if (entity.getPosition() == null){
			this.position = new Vector2();
		} else {
			this.position = new Vector2(entity.getPosition());
		}
		this.x = this.position.getX();
		this.y = this.position.getY();	
		outgoingInteraction = 0;
		incomingInteraction = 0;
		outgoingTotalWeight = 0;
		incomingTotalWeight = 0;
		incomingEdges = new ArrayList<NetworkEdge>();
		outgoingEdges = new ArrayList<NetworkEdge>();
		connectedNodes = new HashSet<NetworkNode>();
	}

	/**
	 * Creates a copy of an existing node.
	 * @param  cloneNode The node to copy.
	 */

//	public NetworkNode(NetworkNode cloneNode){
//		this.name = cloneNode.getName();
//		this.type = cloneNode.getType();
//		this.setX(cloneNode.getX());
//		this.setY(cloneNode.getY());
//		this.position = new Vector2(cloneNode.getPosition());
//	}

	/**
	 * Subtracts the passed in Vector2 from the 
	 * Vector2 representing this Nodes position.
	 * Used for normalising a graph, generally around (0,0)
	 * @param normVector [description]
	 */
	public void normalise(Vector2 normVector){
		position.subtract(normVector);
		setX(getX() - normVector.getX());
		setY(getY() - normVector.getY());
	}
	
	/**
	 * Returns the name of the NetworkNode.
	 * @return [description]
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Returns the type as a String of the NetworkNode.
	 * @return [description]
	 */
	public String getType(){
		return type;
	}
	
	/**
	 * Multiplies the current position coordinates
	 * by a scalar.
	 * @param multiplier A scalar.
	 */
	public void multiplyCoords(double multiplier){
		position.setX(position.getX() * multiplier);
		position.setY(position.getY() * multiplier);
		setX(getX() * multiplier);
		setY(getY() * multiplier);
	}
	
	/**
	 * Returns the current position of the NetworkNode.
	 * @return [description]
	 */
	public Vector2 getPosition(){
		return position;
	}
	
	
	/**
	 * A handy-dandy String representation of the NetworkNode.
	 * (Why tabs?)
	 * @return [description]
	 */
	@Override
	public String toString(){
		String out = name + "\t" + getX() + "\t" + getY();
		return out;
	}
	
	public void newCoords(double newX, double newY){
		this.setX(newX);
		this.setY(newY);
		position = new Vector2(this.getX(),this.getY());
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	//Lots of stuff. 10/08/16
	
	public void addIncomingEdge(NetworkEdge e){
		incomingEdges.add(e);
		connectedNodes.add(e.getStart());
		addIncomingWeight(e.getWeight());
	}
	
	public void addOutgoingEdge(NetworkEdge e){
		outgoingEdges.add(e);
		connectedNodes.add(e.getEnd());
		addOutgoingWeight(e.getWeight());
	}
	
	public void incrementOutgoingInteractions(){
		outgoingInteraction++;
	}
	
	public void incrementIncomingInteractions(){
		incomingInteraction++;
	}
	
	public void addOutgoingWeight(double weight){
		outgoingTotalWeight += weight;		
	}
	
	public double getOutgoingWeight(){
		return outgoingTotalWeight;
	}
	
	public double getIncomingWeight(){
		return incomingTotalWeight;
	}
	
	public void addIncomingWeight(double weight){		
		incomingTotalWeight += weight;
	}
	
	public double getTotalWeight(){
		return outgoingTotalWeight + incomingTotalWeight;
	}
	 
	
	public int outGoingInteractions(){
		return outgoingInteraction;
	}
	
	public int incomingInteractions(){
		return incomingInteraction;
	}
	
	public int totalInteractions(){
		return outGoingInteractions() + incomingInteractions();
	}
	
	public void merge(NetworkNode n){
		
	}
	
	public static Comparator<NetworkNode> sortByX(){
		return new Comparator<NetworkNode>() {
			@Override
			public int compare(NetworkNode n1, NetworkNode n2) {
				Vector2 o1 = n1.getPosition();
				Vector2 o2 = n2.getPosition();
				if (o1.getX() > o2.getX()){
					return 1;
				} else if (o1.getX() < o2.getX()){
					return -1;
				} else {
					return 0;
				}
			}
		};
	}
	
	public static Comparator<NetworkNode> sortByY(){
		return new Comparator<NetworkNode>() {
			@Override
			public int compare(NetworkNode n1, NetworkNode n2) {
				Vector2 o1 = n1.getPosition();
				Vector2 o2 = n2.getPosition();
				if (o1.getY() > o2.getY()){
					return 1;
				} else if (o1.getY() < o2.getY()){
					return -1;
				} else {
					return 0;
				}
			}
		};
	}
	
	
	//************Only useful if they have VAgents stored
	
	
	public Entity getEntity(){
		return entity;
	}
	
	public static Comparator<NetworkNode> sortByVAgentName(){
		return new Comparator<NetworkNode>() {
			@Override
			public int compare(NetworkNode n1, NetworkNode n2) {
				String v1 = n1.getEntity().getID();
				String v2 = n2.getEntity().getID();
				return v1.compareToIgnoreCase(v2);
			}
		};
	}
	
	public HashSet<NetworkNode> getConnectedNodes(){
		return connectedNodes;
	}
	
	
}
