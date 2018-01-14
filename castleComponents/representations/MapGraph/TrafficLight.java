package castleComponents.representations.MapGraph;

import java.util.HashSet;

import castleComponents.objects.List;
import castleComponents.objects.Vector2;
import stdSimLib.utilities.RandomGen;

public class TrafficLight {
	Vector2 location;
	int timeLeftGreen = 0;
	List<Edge> listOfExitEdges;

	HashSet<Link> linksInvolved;
	Node parentNode;
	List<TrafficLightPattern> patterns;
	int currentPattern = 0;
	TrafficLightPattern activePattern = null;
	long id = -1;

	public void setParentNode(Node n) {
		this.parentNode = n;
	}

	public void createRandomPatterns() {
		// Get the connecting roads
		Edge a;
		Edge b;
		listOfExitEdges = new List<Edge>();
		patterns = new List<TrafficLightPattern>();
		HashSet<Edge> edgePairs = new HashSet<Edge>();
		HashSet<Link> linksIn = parentNode.getLinks();
		// Find adjacent nodes in link
		for (Link link : linksIn) {
			edgePairs = new HashSet<Edge>();
			Node[] ns = link.findAdjacentNodes(parentNode);
			if (ns[0] != null) {
				// Get connecting Edges
				a = ns[0].findEdgeWithNode(parentNode);
			} else {
				a = null;
			}
			if (ns[1] != null) {
				// Get connecting Edges
				b = ns[1].findEdgeWithNode(parentNode);
			} else {
				b = null;
			}
			edgePairs.add(a);
			edgePairs.add(b);
			int time = RandomGen.generateRandomRangeInteger(2, 5);
			patterns.add(new TrafficLightPattern(edgePairs, time));
		}
		if (patterns.size() == 1) {
			int time = RandomGen.generateRandomRangeInteger(2, 5);
			patterns.add(new TrafficLightPattern(new HashSet<Edge>(), time)); //Add a fake pattern
		}
		
	}

	public TrafficLight(Vector2 loc, long idd) {
		this.location = new Vector2(loc);
		this.id = idd;

	}

	public Vector2 getLocation() {
		return location;
	}

	public void setLocation(Vector2 location) {
		this.location = location;
	}

	public void start() {
		if (patterns.size() == 0) {
//			errLog("no patterns");
		} else {
			currentPattern = RandomGen.generateRandomRangeInteger(0, patterns.size() - 1);
			activePattern = patterns.get(currentPattern);
			timeLeftGreen = activePattern.getTime();
		}
	}

	public boolean haveToStop(Edge currEdge) {
		return activePattern.containsEdge(currEdge);
	}

	public int getTimeLeft() {
		return timeLeftGreen;
	}
	public void next() {
		if (patterns.size() > 0) {
			timeLeftGreen--;
			if (timeLeftGreen == 0) {
				getNextPattern();
				timeLeftGreen = activePattern.getTime();
			}
		}
	}
	
	public int getNumberOfPatterns() {
		return patterns.size();
	}

	public TrafficLightPattern getNextPattern() {
		currentPattern++;
		if (currentPattern > patterns.size() - 1) {
			currentPattern = 0;
		}
		activePattern = patterns.get(currentPattern);
		return activePattern;
	}

	public void errLog(Object o) {
		System.err.println("TrafficLight Warning: " + o.toString());
	}
}

class TrafficLightPattern {
	HashSet<Edge> edgePair;
	int time;

	public TrafficLightPattern(HashSet<Edge> e, int t) {
		edgePair = new HashSet<Edge>();
		this.time = t;
		this.edgePair.addAll(e);
	}

	public HashSet<Edge> getEdgePair() {
		return edgePair;
	}

	public void setEdgePair(HashSet<Edge> edgePair) {
		this.edgePair = edgePair;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public boolean containsEdge(Edge e) {
		return edgePair.contains(e);
	}
}
