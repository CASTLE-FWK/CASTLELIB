package castleComponents.representations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import castleComponents.Entity;

public class Network implements Representation {

	ArrayList<Entity> allEntities;
	HashMap<String, NetworkNode> nodes;
	ArrayList<NetworkEdge> edges;
	
	public Network(){
		allEntities = new ArrayList<Entity>();
		nodes = new HashMap<String, NetworkNode>();
		edges = new ArrayList<NetworkEdge>();
	}
	
	public void addNode(Entity e){
		allEntities.add(e);
		nodes.put(e.getID(), new NetworkNode(e));
	}
 
	
	@Override
	public List<Entity> getEntities() {
		return allEntities;
	}
	
	public NetworkNode findNode(String name){
		return nodes.get(name);
	}
	
	public void addEdge(NetworkEdge e){
		edges.add(e);
		e.getStart().addOutgoingEdge(e);
		e.getEnd().addIncomingEdge(e);
	}

}