package castleComponents.representations.MapGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import castleComponents.objects.Vector2;

public class MapGraph {
	HashMap<Long, Node> nodesMap;
	ArrayList<Link> links;

	public MapGraph() {
		links = new ArrayList<Link>();
		nodesMap = new HashMap<Long, Node>();
	}

	public void normalise() {
		Vector2 agg = new Vector2();
		HashSet<Node> nodes = new HashSet<Node>(nodesMap.values());
		for (Node n : nodes) {
			agg.add(n.getCoords());
		}
		agg.divide(nodes.size());
		for (Node n : nodes) {
			n.normalise(agg);
		}
	}

	public void sortLinks() {
		Collections.sort(links);
	}

	// todo change
	public ArrayList<Node> sortNodesOnLinkCount() {
		ArrayList<Node> nodes = new ArrayList<Node>(nodesMap.values());
		Collections.sort(nodes, new Comparator<Node>() {
			@Override
			public int compare(Node n1, Node n2) {
				if (n1.getTotalWeight() < n2.getTotalWeight()) {
					return -1;
				} else if (n1.getTotalWeight() < n2.getTotalWeight()) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		return nodes;
	}

	/**
	 * @return the nodes
	 */
	public ArrayList<Node> getNodesAsList() {
		return new ArrayList<Node>(nodesMap.values());
	}

	/**
	 * @return the links
	 */
	public ArrayList<Link> getLinks() {
		return links;
	}

	/**
	 * @param links
	 *            the links to set
	 */
	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}

	public void addNode(Node n) {
		nodesMap.put(n.getID(), n);
	}

	public Node findNode(Long id) {
		return nodesMap.get(id);
	}

	public void addLink(Link e) {
		links.add(e);
	}

	@Override
	public String toString() {
		return "MapGraph [ size of nodesMap: " + nodesMap.size() + ", size of links: " + links.size() + " ]";
	}
}
