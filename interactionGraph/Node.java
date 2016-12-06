package interactionGraph;

import interLib.Agent;
import interLib.Vector2;


public class Node {
	String name;
	String type;
	private double x;	//Because of my terribleness, these have to remain
	private double y;
	Vector2 position;
	
	/**
	 * Creates a new Node with name at position (x,y)
	 * Automatically determines the type from the name.
	 * @param  name Name of the Node
	 * @param  x    The x
	 * @param  y    The y
	 */
	public Node(String name, double x, double y){
		this.name = name;
		type = name.replaceAll("[0-9]", "");
		this.setX(x);
		this.setY(y);
		position = new Vector2(this.getX(),this.getY());
	}
	
	public Node(String name, Vector2 position){
		this.name = name;
		this.position = new Vector2(position);
		this.x = this.position.getX();
		this.y = this.position.getY();			
	}
	
	public Node(Agent agent){
		this.name = agent.getID();
		this.position = new Vector2(agent.getPosition());
		this.x = this.position.getX();
		this.y = this.position.getY();	
	}

	/**
	 * Creates a copy of an existing node.
	 * @param  cloneNode The node to copy.
	 */

	public Node(Node cloneNode){
		this.name = cloneNode.getName();
		this.type = cloneNode.getType();
		this.setX(cloneNode.getX());
		this.setY(cloneNode.getY());
		this.position = new Vector2(cloneNode.getPosition());
	}

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
	 * Returns the name of the Node.
	 * @return [description]
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Returns the type as a String of the Node.
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
	 * Returns the current position of the Node.
	 * @return [description]
	 */
	public Vector2 getPosition(){
		return position;
	}
	
	
	/**
	 * A handy-dandy String representation of the Node.
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
}