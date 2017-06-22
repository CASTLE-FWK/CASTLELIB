package castleComponents.representations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import castleComponents.Entity;

public class Network<E> implements Representation<E> {

	ArrayList<E> allEntities;
	HashMap<String, NetworkNode> nodes;
	ArrayList<NetworkEdge> edges;
	
	public Network(){
		allEntities = new ArrayList<E>();
		nodes = new HashMap<String, NetworkNode>();
		edges = new ArrayList<NetworkEdge>();
	}
	
	public void addNode(E e){
		allEntities.add(e);
		nodes.put(e.getID(), new NetworkNode(e));
	}
 
	
	@Override
	public List<E> getEntities() {
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

	@Override
	public boolean addEntity(E e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeEntity(E e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeEntityByID(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean initialize(Object... objects) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean initializeEntity(Object... objects) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean initializeEntities(Object... objects) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addEntities(List<E> es) {
		// TODO Auto-generated method stub
		return false;
	}

}