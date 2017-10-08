package castleComponents.representations.Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import castleComponents.objects.Vector2;
import castleComponents.representations.Representation;

public class Network<E> implements Representation<E> {

	ArrayList<E> allEntities;
	HashMap<String, NetworkNode<E>> nodes;
	ArrayList<NetworkEdge> edges;
	
	public Network(){
		allEntities = new ArrayList<E>();
		nodes = new HashMap<String, NetworkNode<E>>();
		edges = new ArrayList<NetworkEdge>();
	}
	
	public void addNode(E e, String id){
		allEntities.add(e);
		nodes.put(id, new NetworkNode<E>(e));
	}
 
	@Override
	public List<E> getEntities() {
		return allEntities;
	}
	
	public NetworkNode<E> findNode(String name){
		return nodes.get(name);
	}
	
	public void addEdge(NetworkEdge e){
		edges.add(e);
		e.getStart().addOutgoingEdge(e);
		e.getEnd().addIncomingEdge(e);
	}

	public boolean addEntity(E e) {
		return false;
	}

	@Override
	public boolean removeEntity(E e) {
		return false;
	}

	@Override
	public boolean removeEntityByID(String id) {
		return false;
	}

	@Override
	public boolean initialize(Object... objects) {
		return false;
	}

	@Override
	public boolean initializeEntity(Object... objects) {
		return false;
	}

	@Override
	public boolean initializeEntities(Object... objects) {
		return false;
	}

	@Override
	public boolean addEntities(List<E> es) {
		return false;
	}

	@Override
	public boolean addEntity(E e, Vector2 p) {
		return false;
	}

}