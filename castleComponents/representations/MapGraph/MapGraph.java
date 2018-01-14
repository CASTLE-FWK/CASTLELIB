package castleComponents.representations.MapGraph;

import java.util.HashMap;
import java.util.HashSet;

import castleComponents.Entity;
import castleComponents.objects.Range2D;
import castleComponents.objects.Vector2;
import castleComponents.objects.List;
import castleComponents.representations.LayoutParameters;
import stdSimLib.utilities.Dijkstra;
import stdSimLib.utilities.Utilities;

public class MapGraph {
	// Look at all the stuff I have to clone
	HashMap<Long, Node> nodes;
	HashMap<Vector2, Node> nodesMap;
	HashMap<Long, Link> links;
	HashMap<String, Edge> edges;
	Vector2 geoBoundingBox_Min;
	Vector2 geoBoundingBox_Max;
	Vector2 boundingBox_Min;
	Vector2 boundingBox_Max;
	HashMap<String, Entity> entitiesInMap;
	List<Node> transitPoints;
	List<TrafficLight> trafficLightLocations;
	List<Node> carParkNodes;
	long id = -1;
	List<MapGraph> subMaps;
	Range2D range;
	Range2D geoRange;
	// Dijkstra's wow
	Dijkstra dksa;

	public final String TRAFFIC_SIGNAL = "traffic_signals";
	public final int DECIMAL_PLACES = 7;

	public MapGraph() {
		links = new HashMap<Long, Link>();
		edges = new HashMap<String, Edge>();
		nodes = new HashMap<Long, Node>();
		nodesMap = new HashMap<Vector2, Node>();
		entitiesInMap = new HashMap<String, Entity>();
		transitPoints = new List<Node>(); // TODO Populate this list
		trafficLightLocations = new List<TrafficLight>();
		carParkNodes = new List<Node>(); // TODO Populate this list
		id = -1;
		subMaps = new List<MapGraph>();
		dksa = new Dijkstra();
	}

	public MapGraph(MapGraph mg) {
		clone(mg);
	}

	// TODO
	public void clone(MapGraph mg) {
		errLog("clone is incomplete");
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getID() {
		return this.id;
	}

	public Range2D getRange() {
		return range;
	}

	public void initialize(String pathToMapFile, LayoutParameters lp) {
		importMap(pathToMapFile);

		// Populate edges list
		HashSet<Link> theLinks = new HashSet<Link>(links.values());
		for (Link l : theLinks) {
			List<Edge> lEdges = l.getEdges();
			for (Edge e : lEdges) {
				edges.put(e.getID(), e);
			}
		}
	}

	public void importMap(String path) {
		MapGraphParser mgp = new MapGraphParser(this);
		mgp.parseMapGraph(path);
	}

	public void edgeSwap(Entity e, Edge oldEdge, Edge newEdge) {
		if (oldEdge != null) {
			if (oldEdge.getID() != newEdge.getID()) {
				oldEdge.removeEntity(e);
				newEdge.addEntity(e);
			}
		}
	}

	/** How to move Entities around **/
	// TODO HERE
	public Outcome moveEntity(Entity e, Edge currEdge, double moveDist, double distanceAlongEdge, Route route) {
		Node nextNode = route.getNextNode();
		// Find the correct edge to be on
		Edge oldEdge = currEdge;
		if (currEdge == null) {
			Node thisNode = route.getPrevNode();
			for (Edge ed : thisNode.getEdges()) {
				if (ed.isNodeConnected(nextNode)) {
					currEdge = ed;
					break;
				}
			}
		}
		if (currEdge == null) {
			errLog("currEdge remained null. theres an error**************");
		}
		// How do we update the position along the edge?
		double newDist = distanceAlongEdge + moveDist; // TODO Determine which direction this should be done in

		// I think this logic is correct
		double distFromEnd = currEdge.getDistanceInKM() - distanceAlongEdge;
		if (moveDist >= distFromEnd) {
			if (nextNode.hasTrafficLight()) {
				errLog("THERES A TRAFFIC LIGHT HERE");
				if (nextNode.getTheTrafficLight().haveToStop(currEdge)) {
					newDist = distanceAlongEdge + distFromEnd; // Yes, I know what this does Clem Fandango
					route.setCurrentEdge(currEdge);
					route.setDistanceAlongEdge(newDist);
					route.setHeading(calculateHeading(route.getPrevNode(), route.getNextNode()));
					edgeSwap(e, oldEdge, currEdge);
					// errLog("Honk" + nextNode.getTheTrafficLight().getNumberOfPatterns());
					// errLog("Honk" + nextNode.getTheTrafficLight().getTimeLeft());
					return new Outcome(OutcomeResult.STOPPED, newDist, nextNode, this, currEdge);
				}
			}
			double overMove = newDist - currEdge.getDistanceInKM();
			route.nodeVisted();
			Node next = route.getNextNode();
			if (next == null) {
				// Entity is at it's destination
				route.setCurrentEdge(currEdge);
				edgeSwap(e, oldEdge, currEdge);
				route.setDistanceAlongEdge(overMove);
				return new Outcome(OutcomeResult.FINISHED, overMove, nextNode, this, currEdge);
			}
			nextNode = next;
			// Find edge that connects to next
			Node prevNode = route.getPrevNode();
			currEdge = null;
			for (Edge ed : next.getEdges()) {
				if (ed.isNodeConnected(prevNode)) {
					currEdge = ed;
					break;
				}
			}
			if (currEdge == null) {
				errLog("No new Edge found. Route generation was bad");
			}
			newDist = overMove;
		} else if (newDist > 1001209) {
			// TODO going way out of bounds
		} else {
			route.setCurrentEdge(currEdge);
			edgeSwap(e, oldEdge, currEdge);
			route.setDistanceAlongEdge(newDist);
			route.setHeading(calculateHeading(route.getPrevNode(), route.getNextNode()));
			return new Outcome(OutcomeResult.VALID, newDist, nextNode, this, currEdge);
		}

		route.setCurrentEdge(currEdge);
		edgeSwap(e, oldEdge, currEdge);
		route.setDistanceAlongEdge(newDist);
		route.setHeading(calculateHeading(route.getPrevNode(), route.getNextNode()));
		return new Outcome(OutcomeResult.VALID, newDist, nextNode, this, currEdge);

	}

	public Heading calculateHeading(Node prevNode, Node nextNode) {
		Vector2 nextPos = nextNode.getCoords();
		Vector2 prevPos = prevNode.getCoords();
		double xDiff = nextPos.getX() - prevPos.getX();
		double yDiff = nextPos.getY() - prevPos.getY();
		int xH = (int) (xDiff / Math.abs(xDiff));
		int yH = (int) (yDiff / Math.abs(yDiff));
		return Heading.getHeadingFromInts(xH, yH);

	}

	public List<Node> calculateRoute(Vector2 currPos, Vector2 destPos) {
		currPos.round(DECIMAL_PLACES);
		destPos.round(DECIMAL_PLACES);
		Node currNode = getNodeAtPosition(currPos);
		errLog("currNode pos: " + currPos + " is null " + (currNode == null));
		Node destNode = getNodeAtPosition(destPos);
		errLog("destNode pos: " + destPos + " is null " + (destNode == null));
		if (currPos.compare(destPos)) {
			// We have no destination
			return new List<Node>();
		} else {
			dksa.execute(currNode);
			List<Node> path = dksa.getPath(destNode);
			if (path == null) {
				errLog("path is null");
				System.exit(0);
			}
			return path;
		}
	}

	public Vector2 calculateEntitiesPosition(Edge currEdge, double distanceAlongEdge, Node prevNode, Node destNode) {
		Vector2 prevPos = null;
		Vector2 destPos;
		if (prevNode == null) {
			errLog("previous node is null. ");
		} else {
			prevPos = prevNode.getCoords();
		}
		if (destNode == null) {
			destPos = new Vector2(prevPos);
		} else {
			destPos = destNode.getCoords();
		}

		double edgeLen = currEdge.getDistanceInKM();
		double t = distanceAlongEdge / edgeLen;
		double x = ((1 - t) * prevPos.getX() + (t * destPos.getX()));
		double y = ((1 - t) * prevPos.getY() + (t * destPos.getY()));
		x = Utilities.roundDoubleToXDP(x, DECIMAL_PLACES);
		y = Utilities.roundDoubleToXDP(y, DECIMAL_PLACES);
		return new Vector2(x, y);
	}

	public boolean addEntity(Entity e, Vector2 pos) {
		// Is pos oob?
		// Find the Node closest to pos and add there
		Node cand = getNodeAtPosition(pos);
		if (cand != null) {
			// TODO Add here some how
			entitiesInMap.put(e.getID(), e);
			return true;
		}
		errLog("cant add entity to " + pos);

		return false;
	}

	public boolean addEntityFromGeoCoords(Entity e, Vector2 gPos) {
		Vector2 rPos = convertFromGeoToRelational(gPos);
		return addEntity(e, rPos);
	}

	// TODO Do reverse
	public Vector2 convertFromGeoToRelational(Vector2 gPos) {
		double geoBBWidth = geoBoundingBox_Max.getX() - geoBoundingBox_Min.getX();
		double geoBBHeight = geoBoundingBox_Max.getY() - geoBoundingBox_Min.getY();
		double bbWidth = Link.calculateCoordinateDistance(
				new Vector2(geoBoundingBox_Min.getX(), geoBoundingBox_Max.getY()), geoBoundingBox_Max);
		double bbHeight = Link.calculateCoordinateDistance(
				new Vector2(geoBoundingBox_Max.getX(), geoBoundingBox_Min.getY()), geoBoundingBox_Max);
		double xPerc = (gPos.getX() - geoBoundingBox_Min.getX()) / geoBBWidth;
		double yPerc = (gPos.getY() - geoBoundingBox_Min.getY()) / geoBBHeight;
		xPerc = bbWidth * xPerc;
		yPerc = bbHeight * yPerc;
		return new Vector2(xPerc, yPerc);
	}

	public void calculateBounds() {
		HashSet<Node> theNodes = new HashSet<Node>(nodes.values());
		Vector2 min = new Vector2(Double.MAX_VALUE, Double.MAX_VALUE);
		Vector2 max = new Vector2(-Double.MAX_VALUE, -Double.MAX_VALUE);

		for (Node n : theNodes) {
			Vector2 coords = n.getGeoCoords();
			if (coords.getX() < min.getX()) {
				min.setX(coords.getX());
			}
			if (coords.getX() > max.getX()) {
				max.setX(coords.getX());
			}
			if (coords.getY() < min.getY()) {
				min.setY(coords.getY());
			}
			if (coords.getY() > max.getY()) {
				max.setY(coords.getY());
			}
		}
		geoBoundingBox_Min = new Vector2(min);
		geoBoundingBox_Max = new Vector2(max);
		printBounds();

	}

	public void normalise() {
		HashSet<Node> theNodes = new HashSet<Node>(nodes.values());
		nodesMap.clear();
		double geoBBWidth = geoBoundingBox_Max.getX() - geoBoundingBox_Min.getX();
		double geoBBHeight = geoBoundingBox_Max.getY() - geoBoundingBox_Min.getY();
		double bbWidth = Link.calculateCoordinateDistance(
				new Vector2(geoBoundingBox_Min.getX(), geoBoundingBox_Max.getY()), geoBoundingBox_Max);
		double bbHeight = Link.calculateCoordinateDistance(
				new Vector2(geoBoundingBox_Max.getX(), geoBoundingBox_Min.getY()), geoBoundingBox_Max);

		for (Node n : theNodes) {
			Vector2 coords = n.getGeoCoords();
			double xPerc = (coords.getX() - geoBoundingBox_Min.getX()) / geoBBWidth;
			double yPerc = (coords.getY() - geoBoundingBox_Min.getY()) / geoBBHeight;
			xPerc = bbWidth * xPerc;
			yPerc = bbHeight * yPerc;
			xPerc = Utilities.roundDoubleToXDP(xPerc, DECIMAL_PLACES);
			yPerc = Utilities.roundDoubleToXDP(yPerc, DECIMAL_PLACES);

			n.setCoords(new Vector2(xPerc, yPerc));
			nodesMap.put(n.getCoords(), n);
		}

		boundingBox_Min = new Vector2(0, 0);
		boundingBox_Max = new Vector2(bbWidth, bbHeight);
		range = new Range2D(boundingBox_Min, new Vector2(boundingBox_Min.getX(), boundingBox_Max.getY()),
				new Vector2(boundingBox_Max.getX(), boundingBox_Min.getY()),
				new Vector2(boundingBox_Max.getX(), boundingBox_Max.getY()));

		geoRange = new Range2D(geoBoundingBox_Min, new Vector2(geoBoundingBox_Min.getX(), geoBoundingBox_Max.getY()),
				new Vector2(geoBoundingBox_Max.getX(), geoBoundingBox_Min.getY()),
				new Vector2(geoBoundingBox_Max.getX(), geoBoundingBox_Max.getY()));

		errLog(range);
		dksa = new Dijkstra();

		// This is weird - print random node
		// errLog(getNodeFromID(544503325));
		// errLog(getNodeFromID(1016136212));
		// errLog(getNodeFromID(253085762));
		// System.exit(0);
	}

	// TODO SubGraphs > 1
	public void createSubMaps(int num) {
		if (num == 1) {
			subMaps.add(this);
		} else {
			errLog("createSubMaps is incomplete");
		}
	}

	public List<MapGraph> getSubMapsAsList() {
		return subMaps;
	}

	public List<Range2D> getSubMapRangesAsList() {
		List<Range2D> ranges = new List<Range2D>();
		for (MapGraph mg : subMaps) {
			ranges.add(mg.getRange());
		}
		return ranges;
	}

	// Note: This might return funky nulls
	public Node getNodeAtPosition(Vector2 pos) {
		return nodesMap.get(pos);
	}

	public Node findClosestCarPark(Vector2 v) {
		Node minNode = null;
		double minDist = Double.MAX_VALUE;
		for (Node n : carParkNodes) {
			double cand = v.calculateDistance(n.getCoords());
			if (cand < minDist) {
				minNode = n;
				minDist = cand;
			}
		}
		return minNode;
	}

	public Node findNearestTransitPoint(Vector2 v) {
		Node minNode = null;
		double minDist = Double.MAX_VALUE;
		for (Node n : transitPoints) {
			double cand = v.calculateDistance(n.getCoords());
			if (cand < minDist) {
				minNode = n;
				minDist = cand;
			}
		}
		return minNode;
	}

	public Node getNodeFromID(long id) {
		return nodes.get(id);
	}

	// Event stuff
	public List<Node> extractNodesInRange(Range2D range) {
		// This is slow as heck. Should be done at a pre-process stage if possible
		List<Node> newNodes = new List<Node>();
		HashSet<Node> oldNodes = new HashSet<Node>(nodes.values());
		for (Node on : oldNodes) {
			if (range.containsPoint(on.getCoords())) {
				newNodes.add(on);
			}
		}

		return newNodes;
	}

	// TODO
	public MapGraph extractMapSectionShallow(Range2D range) {
		errLog("extractMapSection is incomplete. is task");
		// Plan: Extract submap
		// 1: Extract nodes in range
		List<Node> oldNodes = extractNodesInRange(range);
		// 2: Get all their edges
		List<Edge> edges = new List<Edge>();
		for (Node n : oldNodes) {
			edges.addAll(n.getEdges());
		}
		// 3: Contain in a new MapGraph
		MapGraph subGraph = new MapGraph();
		// TODO how much do we clone?

		return subGraph;
	}

	public void changeSectionOfMapToType(String eventName, String type) {
		HashSet<Node> oldNodes = new HashSet<Node>(nodes.values());
		for (Node on : oldNodes) {
			on.setNodeState(type);
		}
	}

	// TODO
	public List<Entity> getEntitiesInRangeOfType(Entity e, double dist, double range, String type, Edge currEdge,
			Route route) {

		if (currEdge == null) {
			return null;
		}
		List<Entity> neighbours = new List<Entity>();
		double rangeSpan = dist + range;
		double remainDist = currEdge.getDistanceInKM() - rangeSpan;
		HashSet<Entity> entsOnSameEdge = currEdge.getEntities();
		for (Entity ent : entsOnSameEdge) {
			if (e == ent) {
				continue;
			}

			if (ent.getType().compareToIgnoreCase(type) == 0) {
				neighbours.add(ent);
			}
		}

		if (rangeSpan > currEdge.getDistanceInKM()) {
			// Node followingNode = route.getFollowingNode(route.getNextNode());
			// Edge followingEdge = route.getFollowingEdge(route.getCurrentEdge());
			// TODO handle going across nodes
			errLog("getEntitiesInRangeOfType is incomplete. Especially here");
			// what if the node has a traffic light

			// neighbours.add(ent);

		}

		return neighbours;
	}

	public List<Entity> getEntitiesFromEdge(Edge e) {
		return new List<Entity>(e.getEntities());
	}

	public Park getCarParkAtPosition(Vector2 pos) {
		Node n = getNodeAtPosition(pos);
		if (n.isCarPark()) {
			return n.getTheCarPark();
		} else {
			errLog("CarPark not found at " + pos);
			return null;
		}
	}

	public String printMap() {
		String str = "";
		return str;
	}

	public void printBounds() {
		System.out.println("MapGraph Bounds [ min=" + geoBoundingBox_Min + ", max=" + geoBoundingBox_Max + " ]");
	}

	public List<Node> getTransitPoints() {
		return transitPoints;
	}

	public void addTransitPoint(Node n) {
		transitPoints.add(n);
	}

	public List<TrafficLight> getListOfTrafficLights() {
		return trafficLightLocations;
	}

	public void addTrafficLight(Node n) {
		// TODO something about adding traffic lights
		errLog("addTrafficLight is incomplete");
		trafficLightLocations.add(null);
	}

	public List<Node> getCarParkNodes() {
		return carParkNodes;
	}

	public List<Vector2> getCarParkLocations() {
		List<Vector2> v = new List<Vector2>();
		for (Node n : carParkNodes) {
			v.add(n.getCoords());
		}
		return v;
	}

	public void addCarParkLocation(Node n) {
		carParkNodes.add(n);
	}

	/**
	 * @return the nodes
	 */
	public List<Node> getNodesAsList() {
		return new List<Node>(nodes.values());
	}

	/**
	 * @return the links
	 */
	public HashMap<Long, Link> getLinks() {
		return links;
	}

	public void addNode(Node n) {
		nodes.put(n.getID(), n);
	}

	public Node findNode(Long id) {
		return nodes.get(id);
	}

	public void addLink(Link e) {
		links.put(e.getID(), e);
	}

	public void addEdge(Edge e) {
		edges.put(e.getID(), e);
	}

	@Override
	public String toString() {
		return "MapGraph [ size of nodesMap: " + nodes.size() + ", size of links: " + links.size()
				+ ", number of traffic lights: " + trafficLightLocations.size() + ", size in km: " + boundingBox_Max
				+ "]";
	}

	public void errLog(Object o) {
		System.err.println("MapGraph Warning: " + o.toString());
	}

	public boolean containsPoint(Vector2 v) {
		if (range == null) {
			errLog("range is null");
		}
		return (range.containsPoint(v));
	}

	public void assignEdges() {
		for (Link l : links.values()) {
			for (Edge e : l.getEdges()) {
				Node na = e.getNodeA();
				Node nb = e.getNodeB();
				na.addAdjacentNode(nb);
				nb.addAdjacentNode(na);
				if (l.isOneWay()) {
					na.addOutgoingEdge(e);
					nb.addIncomingEdge(e);
				} else {
					na.addOutgoingEdge(e);
					na.addIncomingEdge(e);
					nb.addIncomingEdge(e);
					nb.addOutgoingEdge(e);
				}
			}
		}
	}

	public void extractEdges() {
		for (Link l : links.values()) {
			List<Edge> lEdges = l.getEdges();
			for (Edge e : lEdges) {
				edges.put(e.getID(), e);
				Node a = e.getNodeA();
				Node b = e.getNodeB();
				a.addEdge(e);
				b.addEdge(e);
			}
		}
	}

	public void buildLights() {
		long idCounter = 0;
		for (Node n : nodes.values()) {
			if (n.getNodeType().compareToIgnoreCase(TRAFFIC_SIGNAL) == 0) {
				// Build a traffic light here
				TrafficLight tl = new TrafficLight(n.getCoords(), idCounter);
				n.setTrafficLight(true);
				n.setTheTrafficLight(tl);
				tl.setParentNode(n);
				tl.createRandomPatterns();
				trafficLightLocations.add(tl);
				idCounter++;
			}
		}
	}

	public void buildCarParks() {
		for (Link l : links.values()) {
			boolean found = false;
			if (l.isCarParkArea()) {
				// Set each node in this link to car park?
				// No, find that exists in another non-parking way
				for (Node ns : l.getWayPoints()) {
					found = findNodeInWay(ns, l);
					if (found) {
						ns.setCarPark(true);
						ns.createCarPark();
						addCarParkLocation(ns);
						errLog("found an accesible car park");
						break;
					}
				}
			}
		}
	}

	public boolean findNodeInWay(Node n, Link selfLink) {
		for (Link l : links.values()) {
			if (l == selfLink) {
				continue;
			}
			for (Node ns : l.getWayPoints()) {
				if (ns.getID() == n.getID()) {
					return true;
				}
			}
		}
		return false;
	}

}