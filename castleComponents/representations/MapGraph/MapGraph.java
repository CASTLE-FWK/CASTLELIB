package castleComponents.representations.MapGraph;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import castleComponents.Entity;
import castleComponents.objects.Range2D;
import castleComponents.objects.Vector2;
import castleComponents.objects.List;
import castleComponents.representations.LayoutParameters;
import stdSimLib.utilities.Dijkstra;
import stdSimLib.utilities.RandomGen;
import stdSimLib.utilities.SlowDijkstra;
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
	HashSet<Node> transitPoints;
	List<TrafficLight> trafficLightLocations;
	HashMap<Long, Node> carParkNodes;
	long id = -1;
	List<MapGraph> subMaps;
	Range2D range;
	Range2D geoRange;
	// Dijkstra's wow
	Dijkstra dksa;
	SlowDijkstra slowdksa;

	public final String TRAFFIC_SIGNAL = "traffic_signals";
	public final int DECIMAL_PLACES = 7;

	// Pathfinding flags
	public final int DIJKSTRA_SLOW = 0;
	public final int DIKSTRA_FAST = 1;
	private List<Node> leafNodes;

	public MapGraph() {
		links = new HashMap<Long, Link>();
		edges = new HashMap<String, Edge>();
		nodes = new HashMap<Long, Node>();
		nodesMap = new HashMap<Vector2, Node>();
		entitiesInMap = new HashMap<String, Entity>();
		transitPoints = new HashSet<Node>();
		trafficLightLocations = new List<TrafficLight>();
		carParkNodes = new HashMap<Long, Node>();
		id = -1;
		subMaps = new List<MapGraph>();
		slowdksa = null;
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
	}

	public void importMap(String path) {
		MapGraphParser mgp = new MapGraphParser(this);
		mgp.parseMapGraph(path);
	}

	public void setupMap(int numTransitPoints) {
		assignEdges();
		connectedComponents();
		System.out.println(ccStats());
		prune();
		clean();

		// Map Infrastructure building
		calculateBounds();
		normalise();
		buildLights();
		buildCarParks();
		generateTransitPoints(numTransitPoints);
		prunePhase2();

		// Simple validation
		nodeValidation();

		// Print some useful stats
		System.out.println(getTransitNodesAsString());
		System.out.println(range);
		System.out.println(toString());
	}

	public List<Node> findLeaves() {
		leafNodes = new List<Node>();
		for (Node n : nodes.values()) {
			if (n.getEdges().size() == 1) {
				leafNodes.add(n);
				if (n.isCarPark()) {
					addTransitPoint(n);
				}
			} else if (n.getEdges().size() == 0) {
				errLog("Stray edge not caught in validation. Investigate.");
			}
		}
		return leafNodes;
	}

	public List<Node> getLeafNodes() {
		return leafNodes;
	}

	public void streamToGephi(String url) {
		StreamToGephi stg = new StreamToGephi(url);
		String[] testOut = exportGraphAsJSON().split("\n");
		for (String s : testOut) {
			try {
				stg.sendAction(s);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void edgeSwap(Entity e, Edge oldEdge, Edge newEdge) {
		if (oldEdge != null) {
			if (oldEdge.getID() != newEdge.getID()) {
				oldEdge.removeEntity(e);
				newEdge.addEntity(e);
			}
		}
	}
	
	public void removeEntity(Entity e) {
		//TODO
	}

	/** How to move Entities around **/
	// TODO HERE
	public Outcome moveEntity(Entity e, Edge currEdge, double moveDist, double distanceAlongEdge, Route route) {
		Node nextNode = route.getNextNode();
		OutcomeResult or;
		if (nextNode.getNodeState().compareToIgnoreCase("NOGO") == 0) {
			or = OutcomeResult.NOGO;
			// No point continuing
			return new Outcome(or, distanceAlongEdge, nextNode, this, currEdge);
		}
		// Find the correct edge to be on
		Edge oldEdge = currEdge;
		if (currEdge == null) {
			// errLog("trying to find a new edge");
			Node thisNode = route.getPrevNode();
			// errLog(thisNode);
			for (Edge ed : thisNode.getEdges()) {
				errLog(ed);
				if (ed.isNodeConnected(nextNode)) {
					currEdge = ed;
					// Place on edge
					currEdge.addEntity(e);
					break;
				}
			}
		}
		if (currEdge == null) {
			errLog("currEdge remained null. theres an error**************");
			System.exit(0);
		}

		// How do we update the position along the edge?
		double newDist = distanceAlongEdge + moveDist; // TODO Determine which direction this should be done in

		// I think this logic is correct
		double distFromEnd = currEdge.getDistanceInKM() - distanceAlongEdge;
		if (moveDist >= distFromEnd) {
			if (nextNode.hasTrafficLight() && (e.getType().compareToIgnoreCase("vehicle") == 0)) {
				// errLog("THERES A TRAFFIC LIGHT HERE");
				if (nextNode.getTheTrafficLight().haveToStop(currEdge)) {
					newDist = distanceAlongEdge + distFromEnd; // Yes, I know what this does Clem Fandango
					route.setCurrentEdge(currEdge);
					route.setDistanceAlongEdge(newDist);
					route.setHeading(calculateHeading(route.getPrevNode(), route.getNextNode()));
					edgeSwap(e, oldEdge, currEdge);
					or = OutcomeResult.STOPPED;
					return new Outcome(or, newDist, nextNode, this, currEdge);
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
				or = OutcomeResult.FINISHED;
				return new Outcome(or, overMove, nextNode, this, currEdge);
			}
			nextNode = next;
			// Find edge that connects to next
			Node prevNode = route.getPrevNode();
			currEdge = null;
			for (Edge ed : next.getEdges()) {
				if (ed.isNodeConnected(prevNode)) {
					currEdge = ed;
					currEdge.addEntity(e);
					break;
				}
			}
			if (currEdge == null) {
				errLog("No new Edge found. Route generation was bad");
			}
			newDist = overMove;
			edgeSwap(e, oldEdge, currEdge);
			return moveEntity(e, currEdge, moveDist, newDist, route);
		} else if (newDist > 10013209) {
			// TODO going way out of bounds
			errLog("This should NEVER happend.");
		} else {
			route.setCurrentEdge(currEdge);
			edgeSwap(e, oldEdge, currEdge);
			route.setDistanceAlongEdge(newDist);
			route.setHeading(calculateHeading(route.getPrevNode(), route.getNextNode()));
			or = OutcomeResult.VALID;
			return new Outcome(or, newDist, nextNode, this, currEdge);
		}

		route.setCurrentEdge(currEdge);
		edgeSwap(e, oldEdge, currEdge);
		route.setDistanceAlongEdge(newDist);
		route.setHeading(calculateHeading(route.getPrevNode(), route.getNextNode()));
		or = OutcomeResult.VALID;
		return new Outcome(or, newDist, nextNode, this, currEdge);

	}

	public Heading calculateHeading(Node prevNode, Node nextNode) {
		Vector2 nextPos = nextNode.getCoords();
		Vector2 prevPos = prevNode.getCoords();
		double xDiff = nextPos.getX() - prevPos.getX();
		double yDiff = nextPos.getY() - prevPos.getY();
		double xH = (xDiff / Math.abs(xDiff));
		double yH = (yDiff / Math.abs(yDiff));
		return Heading.getHeadingFromDoubles(xH, yH);

	}

	// TODO add other pathfinders and use a strnig switch
	public List<Node> calculateRoute(Vector2 currPos, Vector2 destPos) {
		currPos.round(DECIMAL_PLACES);
		destPos.round(DECIMAL_PLACES);
		Node currNode = getNodeAtPosition(currPos);
		Node destNode = getNodeAtPosition(destPos);
		if (destNode == null || currNode == null) {
			errLog("calculateRoute: destNode pos: " + destPos + " is null " + (destNode == null));
			errLog("calculateRoute: currNode pos: " + currPos + " is null " + (currNode == null));
		}
		if (currPos.compare(destPos)) {
			// We have no destination
			return new List<Node>();
		} else {
			dksa.execute(currNode);
			List<Node> path = dksa.getPath(destNode);
			if (path == null) {
				errLog("path is null");
				errLog("currNode stats: " + currNode);
				// System.exit(0);
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
		if (currEdge == null) {
			if (prevNode != null) {
				return prevNode.getCoords();
			}
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
		// TODO Double check what this is actually doing
		Node cand = getNodeAtPosition(pos);
		if (cand != null) {
			// TODO Add here some how
			entitiesInMap.put(e.getID(), e);
			return true;
		} else {
			cand = findNearestNode(pos);
			entitiesInMap.put(e.getID(), e);
			return true;
		}
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

		printBounds();

		// errLog(range);
		dksa = new Dijkstra();
		slowdksa = new SlowDijkstra(new List<Node>(nodes.values()), new List<Edge>(edges.values()));
	}

	// TODO SubGraphs > 1
	public void createSubMaps(int num) {
		if (num == 1) {
			subMaps.add(this);
		} else {
			errLog("createSubMaps is incomplete");
		}
	}

	public boolean outOfGeoRange(Vector2 pos) {
		return !geoRange.containsPoint(pos);
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
		if (carParkNodes.size() == 0) {
			errLog("no car parks");
			System.exit(0);
		}
		if (carParkNodes.size() == 0) {
			System.out.println("There's only one car park");
			return carParkNodes.get(carParkNodes.keySet().toArray()[0]);
		}

		for (Node n : carParkNodes.values()) {
			double cand = v.calculateDistance(n.getCoords());
			if (n.getNodeState().compareToIgnoreCase("NOGO") == 0) {
				continue;
			}
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
			if (n.isCarPark()) {
				continue;
			}
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
		// 3: Contain in a new MapGraph
		MapGraph subGraph = new MapGraph();
		for (Node n : oldNodes) {
			edges.addAll(n.getEdges());
			subGraph.addNode(n);
		}
		// TODO how much do we clone?

		return subGraph;
	}

	public void changeNodeToType(long nodeID, String type) {
		Node n = getNodeFromID(nodeID);
		n.setNodeType(type);
	}

	public void changeSectionOfMapToType(String eventName, String type) {
		HashSet<Node> oldNodes = new HashSet<Node>(nodes.values());
		for (Node on : oldNodes) {
			on.setNodeState(type);
		}
	}

	public List<Entity> getEntitiesNearNode(Node n, double range) {
		List<Entity> ents = new List<Entity>();
		for (Edge e : n.getEdges()) {
			ents.addAll(e.getEntities());
		}

		return ents;
	}

	public List<Entity> getEntitiesInMovingRangeOfType(Entity e, double dist, double range, String type, Edge currEdge,
			Route route) {

		if (currEdge == null) {
			errLog(e.getID() + " is not on edge");
			return null;
		}

		List<Entity> neighbours = new List<Entity>();
		if (range <= 0) {
			return neighbours;
		}
		// dist should approach 0 over successive calls
		double rangeSpan = dist + range;
		// What is the overhang?
		double remainDist = rangeSpan - currEdge.getDistanceInKM();

		// errLog("getEntitiesInRangeOfType: " + e.getID() + " " + dist + " " + range +
		// " " + type + " " + remainDist + " "
		// + rangeSpan + " " + currEdge.getDistanceInKM() + " " + currEdge.getID());

		HashSet<Entity> entsOnSameEdge = currEdge.getEntities();
		// errLog("entities on this edge: " + entsOnSameEdge.size());
		for (Entity ent : entsOnSameEdge) {
			if (e.getID().compareToIgnoreCase(ent.getID()) == 0) {
				continue;
			}

			if (ent.getType().compareToIgnoreCase(type) == 0) {
				neighbours.add(ent);
			}
		}

		if (remainDist > 0 && remainDist <= range) {
			if (remainDist > 5) {
				errLog("I don't know what this is");
			}
			// Get the next edge in the route
			Edge nextEdge = route.getFollowingEdge(currEdge);
			if (nextEdge.getID() == currEdge.getID()) {
//				errLog("large error");
			}
			// Range has to decrease

			// TODO handle going across nodes
			neighbours.addAll(getEntitiesInMovingRangeOfType(e, remainDist, range - remainDist, type, nextEdge, route));

			// what if the node has a traffic light: Then stop iff can't pass

			// neighbours.add(ent);

		}

		return neighbours;
	}

	public List<Entity> getEntitiesFromEdge(Edge e) {
		return new List<Entity>(e.getEntities());
	}

	public Park getCarParkAtPosition(Vector2 pos) {
		Node n = getNodeAtPosition(pos);
		// errLog("pos looking for car park: "+pos);
		if (n.isCarPark()) {
			return n.getTheCarPark();
		} else {
			errLog("CarPark not found at " + pos);
			return null;
		}
	}

	public void printBounds() {
		System.out.println(
				"MapGraph Geo-Bounds      = [ min=" + geoBoundingBox_Min + ", max=" + geoBoundingBox_Max + " ]");
		System.out.println("MapGraph Relative Bounds = [ min=" + boundingBox_Min.round(7) + ", max="
				+ boundingBox_Max.round(7) + " ]");
	}

	public HashSet<Node> getTransitPoints() {
		return transitPoints;
	}

	public void addTransitPoint(Node n) {
		transitPoints.add(n);
	}

	public List<TrafficLight> getListOfTrafficLights() {
		return trafficLightLocations;
	}

	public void addTrafficLight(Node n) {
		errLog("addTrafficLight is incomplete");
		trafficLightLocations.add(null);
	}

	public HashSet<Node> getCarParkNodes() {
		return new HashSet<Node>(carParkNodes.values());
	}

	public List<Vector2> getCarParkLocations() {
		List<Vector2> v = new List<Vector2>();
		for (Node n : carParkNodes.values()) {
			v.add(n.getCoords());
		}
		return v;
	}

	public void addCarParkLocation(Node n) {
		carParkNodes.put(n.getID(), n);
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

	public Node findNearestNode(Vector2 pos) {
		if (getNodeAtPosition(pos) != null) {
			return getNodeAtPosition(pos);
		}
		List<Vector2> nodePos = new List<Vector2>(nodesMap.keySet());
		nodePos.sort(Vector2.sort());
		double minDist = Double.POSITIVE_INFINITY;
		Vector2 minVector = null;
		for (Vector2 v : nodePos) {
			double dist = v.calculateDistance(pos);
			if (dist > minDist) {
				break;
			}
			if (dist < minDist) {
				minDist = dist;
				minVector = new Vector2(v);
			}
		}
		return getNodeAtPosition(minVector);
	}

	public Vector2 findNearestValidPosition(Vector2 pos) {
		return findNearestNode(pos).getCoords();
	}

	public void assignEdges() {
		for (Link l : links.values()) {
			for (Edge e : l.getEdges()) {
				edges.put(e.getID(), e);
				Node na = e.getNodeA();
				Node nb = e.getNodeB();
				na.addEdge(e);
				nb.addEdge(e);
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

	// TODO Build human walking links from these
	public void buildCarParks() {
		for (Link l : links.values()) {
			boolean found = false;
			if (l.isCarParkArea()) {
				// Set each node in this link to car park?
				// No, find that exists in another non-parking way
				for (Node ns : l.getWayPoints()) {
					found = findNodeExistsInWay(ns, l);
					if (found) {
						ns.setCarPark(true);
						ns.createCarPark();
						addCarParkLocation(ns);
						// errLog("found an accessible car park");
						break;
					}
				}
			}
		}
	}

	public boolean findNodeExistsInWay(Node n, Link selfLink) {
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

	public void generateTransitPoints(int number) {
		List<Node> theNodes = new List<Node>(nodes.values());
		for (int i = 0; i < number; i++) {
			int randDirection = RandomGen.generateRandomRangeInteger(0, 3);
			if (randDirection == 0) {
				// Sort by x Min
				theNodes.sort(new Comparator<Node>() {
					@Override
					public int compare(Node a, Node b) {
						return (int) a.getCoords().getX() - (int) b.getCoords().getX();
					}
				});
				theNodes.resetNext();
				Node cand = theNodes.next();
				if (!transitPoints.add(cand)) {
					cand = theNodes.next();
				}
			} else if (randDirection == 1) {
				theNodes.sort(new Comparator<Node>() {
					@Override
					public int compare(Node a, Node b) {
						return -(int) a.getCoords().getX() - (int) b.getCoords().getX();
					}
				});
				theNodes.resetNext();
				Node cand = theNodes.next();
				if (!transitPoints.add(cand)) {
					cand = theNodes.next();
				}
			} else if (randDirection == 2) {
				theNodes.sort(new Comparator<Node>() {
					@Override
					public int compare(Node a, Node b) {
						return (int) a.getCoords().getY() - (int) b.getCoords().getY();
					}
				});
				theNodes.resetNext();
				Node cand = theNodes.next();
				if (!transitPoints.add(cand)) {
					cand = theNodes.next();
				}
			} else if (randDirection == 3) {
				theNodes.sort(new Comparator<Node>() {
					@Override
					public int compare(Node a, Node b) {
						return -(int) a.getCoords().getY() - (int) b.getCoords().getY();
					}
				});
				theNodes.resetNext();
				Node cand = theNodes.next();
				if (!transitPoints.add(cand)) {
					cand = theNodes.next();
				}
			}
		}
	}

	public Node getRandomTransitNode() {
		return (Node) transitPoints.toArray()[RandomGen.generateRandomRangeInteger(0, transitPoints.size() - 1)];
	}

	public String getTransitNodesAsString() {
		String str = "TransitNodes [";
		for (Node n : transitPoints) {
			str += n.toString() + ",";
		}
		str += " ]";
		return str;
	}

	public final double SCALER = 200.0;

	public String exportGraphAsGEXF() {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"<gexf xmlns=\"http://www.gexf.net/1.2draft\" version=\"1.2\" xmlns:viz=\"http://www.gexf.net/1.2draft/viz\" >\n");
		sb.append("\t<meta lastmodifieddate=\"2009-03-20\">\n");
		sb.append("\t\t<creator>Gexf.net</creator>\n");
		sb.append("\t\t<description>MapGraph</description>\n");
		sb.append("\t</meta>\n");

		sb.append("\t<graph mode=\"static\" defaultedgetype=\"mixed\">");

		sb.append("\n");
		sb.append("\t\t<nodes>");
		sb.append("\n");
		for (Node n : nodes.values()) {
			sb.append("\t\t\t<node id=\"" + n.getID() + "\" label=\"" + n.getID() + "\">\n");
			Vector2 pos = n.getCoords();
			sb.append("\t\t\t\t<viz:position x=\"" + pos.getX() * SCALER + "\" y=\"" + pos.getY() * SCALER
					+ "\" z=\"0.0\"/>\n");
			sb.append("\t\t\t\t<viz:size value=\"0.2\"/>\n");
			if (n.isCarPark()) {
				sb.append("\t\t\t\t<viz:color r=\"255\" g=\"0\" b=\"255\" a=\"0.99\"/>)\n");
			} else if (n.getEdges().size() <= 2) {
				sb.append("\t\t\t\t<viz:color r=\"0\" g=\"0\" b=\"0\" a=\"0.0\"/>)\n");
			} else {
				sb.append("\t\t\t\t<viz:color r=\"0\" g=\"0\" b=\"0\" a=\"0.99\"/>)\n");
			}
			sb.append("\t\t\t</node>\n");
		}

		sb.append("\t\t</nodes>");
		sb.append("\n");
		sb.append("\t\t<edges>");
		sb.append("\n");
		int edgeIDCounter = 0;
		edgeIDCounter = 0;
		for (Edge e : edges.values()) {
			int prevI = 0;
			Node a = e.getNodeA();
			Node b = e.getNodeB();
			boolean isOneWay = e.isOneWay();
			boolean isHumanAccessible = e.isHumanAccessible();
			String type = "undirected";
			if (isOneWay) {
				type = "directed";
			}
			sb.append("\t\t\t<edge id=\"" + edgeIDCounter + "\" source=\"" + a.getID() + "\" target=\"" + b.getID()
					+ "\" type=\"" + type + "\">\n");
			sb.append("\t\t\t\t<viz:thickness value=\"0.99\"/>\n");
			if (isHumanAccessible) {
				sb.append("\t\t\t\t<viz:color r=\"157\" g=\"213\" b=\"78\" a=\"0.99\"/>\n");
			} else {
				sb.append("\t\t\t\t<viz:color r=\"0\" g=\"0\" b=\"0\" a=\"0.99\"/>\n");
			}
			sb.append("\t\t</edge>\n");
			edgeIDCounter++;
		}

		sb.append("\t\t</edges>");
		sb.append("\n");
		sb.append("\t</graph>\n");
		sb.append("</gexf>");
		return sb.toString();
	}

	public String exportGraphAsCSVString() {
		StringBuilder sb = new StringBuilder();
		for (Link l : links.values()) {
			for (int i = 0; i < l.getWayPoints().size() - 1; i++) {
				sb.append(l.getWayPoints().get(i).getID() + ";");
			}
			sb.append(l.getWayPoints().getLast().getID());
			sb.append("\n");
		}
		return sb.toString();
	}

	public String exportGraphAsJSON() {
		StringBuilder sb = new StringBuilder();
		// Nodes
		for (Node n : nodes.values()) {
			Vector2 pos = n.getCoords();
			StringBuilder nodePos = new StringBuilder("\"x\":").append(pos.getX() * SCALER).append(",\"y\":")
					.append(pos.getY() * SCALER).append(",\"z\":0.0");
			String color;
			double size = 0.5;
			if (n.isCarPark()) {
				color = "\"r\":0.99, \"g\":0.0, \"b\":0.99";
				size = 1.0;
			} else {
				color = "\"r\":0, \"g\":0.0, \"b\":0";
			}
			sb.append("{\"an\":{\"").append(n.getID()).append("\":{\"label\":null,\"size\":").append(size).append(",")
					.append(nodePos).append(",").append(color + "}}}");
			sb.append("\n");
		}

		for (Edge e : edges.values()) {
			Node a = e.getNodeA();
			Node b = e.getNodeB();
			boolean isOneWay = e.isOneWay();
			boolean isHumanAccessible = e.isHumanAccessible();
			String color = "\"r\":XX, \"g\":YY, \"b\":ZZ";

			if (!isHumanAccessible) {
				color = color.replace("XX", "" + 0.0).replace("YY", "" + 0.0).replace("ZZ", "" + 0.0);
			} else {
				color = color.replace("XX", "" + 0.5).replace("YY", "" + 0.1).replace("ZZ", "" + 0.9);
			}

			sb.append("{\"ae\":{\"").append(a.getID()).append("").append(b.getID()).append("\":{\"source\":\"")
					.append(a.getID()).append("\",\"target\": \"" + b.getID()).append("\",\"directed\":")
					.append(isOneWay).append(",").append(color).append("}}}");
			sb.append("\n");
		}

		return sb.toString();
	}

	public void clean() {
		List<Long> deadNodes = new List<Long>();
		for (Long l : nodes.keySet()) {
			Node n = nodes.get(l);
			if (n.getEdges().size() == 0) {
				deadNodes.add(l);
			}
		}
		for (Long l : deadNodes) {
			nodes.remove(l);
		}
	}

	HashMap<Long, Integer> componentChecker;
	HashSet<Long> pruneSet;
	int ccCounter = 0;

	public int nodeHasBeenSeen(Node n) {
		Integer i = componentChecker.get(n.getID());
		if (i == null) {
			return -1;
		} else {
			return i;
		}
	}

	public void connectedComponents() {
		componentChecker = new HashMap<Long, Integer>();
		ccCounter = -1;
		for (Node n : nodes.values()) {
			// If N hasn't been seen
			if (nodeHasBeenSeen(n) == -1) {
				ccCounter++;
				componentChecker.put(n.getID(), ccCounter);
				dfs(n);
			}
		}
	}

	public void dfs(Node n) {
		for (Node a : n.getAdjacentNodes()) {
			if (nodeHasBeenSeen(a) == -1) {
				componentChecker.put(a.getID(), ccCounter);
				dfs(a);
			}
		}
	}

	public String ccStats() {
		HashMap<Integer, Integer> ccCount = new HashMap<Integer, Integer>();
		for (Long l : componentChecker.keySet()) {
			int i = componentChecker.get(l);
			Integer ccCountRes = ccCount.get(i);
			if (ccCountRes == null) {
				ccCount.put(i, 1);
			} else {
				ccCount.put(i, ccCountRes + 1);
			}
		}
		int largestKey = -1;
		int largestAmount = -Integer.MAX_VALUE;
		String str = "CC Stats [";
		for (Integer hsn : ccCount.keySet()) {
			if (ccCount.get(hsn) > largestAmount) {
				largestAmount = ccCount.get(hsn);
				largestKey = hsn;
			}
		}
		str += "total_number_of_nodes: " + nodes.size() + ", total_number_of_edges: " + edges.size() + ", max_key: "
				+ largestKey + ", number_of_nodes: " + largestAmount;
		pruneSet = new HashSet<Long>();
		for (Long l : componentChecker.keySet()) {
			int v = componentChecker.get(l);
			if (v != largestKey) {
				pruneSet.add(l);
			}
		}
		for (Link l : links.values()) {
			for (int i = 0; i < l.getWayPoints().size() - 1; i++) {

			}
		}
		str += ", nodes to prune: " + pruneSet.size();
		str += "]";
		return str;
	}

	HashSet<Edge> edgesToRemoveOver;

	public void prune() {
		edgesToRemoveOver = new HashSet<Edge>();
		errLog("Pruning (phase 1): nodes-pre: " + nodes.size() + " edges-pre: " + edges.size());
		for (Long l : pruneSet) {
			List<Edge> edgesToRemove = nodes.get(l).getEdges();
			nodes.remove(l);
			for (Edge e : edgesToRemove) {
				edges.remove(e.getID());
				edgesToRemoveOver.add(e);
			}
		}
		errLog("Pruning (phase 2): nodes-pos: " + nodes.size() + " edges-pos: " + edges.size());
	}

	public void prunePhase2() {
		errLog("Pruning (phase 2): removing stray edges");
		for (Edge e : edgesToRemoveOver) {
			Node a = e.getNodeA();
			Node b = e.getNodeB();
			a.removeEdgeWithID(e.getID());
			b.removeEdgeWithID(e.getID());
		}
		errLog("Pruning (phase 2): complete");
	}

	public Node getRandomNode() {
		Object[] n = nodes.values().toArray();
		return (Node) n[RandomGen.generateRandomRangeInteger(0, n.length - 1)];
	}
	
	public Node getRandomVehicleAccessibleNode() {
		boolean va = false;
		Node n = null;
		while (!va) {
			n = getRandomNode();
			va = n.isVehicleAccessible();
		}
		return n;
	}

	public Node getRandomHumanAccessibleNode() {
		Node n = getRandomNode();
		while (!n.isHumanAccessible()) {
			n = getRandomNode();
		}
		return n;
	}

	public void nodeValidation() {
		nodesMap.clear();
		for (Node n : nodes.values()) {
			nodesMap.put(n.getCoords(), n);
			if (n.getEdges().size() <= 0) {
				errLog(n.getID() + " has no edges");
			}
			for (Edge e : n.getEdges()) {
				Node a = e.getNodeA();
				Node b = e.getNodeB();
				if (!nodes.containsKey(a.getID()) || !nodes.containsKey(b.getID())) {
					errLog("missing node");
				}
			}
			for (Edge e : n.getIncomingEdges()) {
				Node a = e.getNodeA();
				Node b = e.getNodeB();
				if (!nodes.containsKey(a.getID()) || !nodes.containsKey(b.getID())) {
					errLog("missing node");
				}
			}
			for (Edge e : n.getEdges()) {
				Node a = e.getNodeA();
				Node b = e.getNodeB();
				if (!nodes.containsKey(a.getID()) || !nodes.containsKey(b.getID())) {
					errLog("missing node");
				}
			}
			if (n.getCoords() == null) {
				errLog(n.getID() + " has null coords");
			}
		}

		List<Long> keysToRemove = new List<Long>();
		for (Long l : carParkNodes.keySet()) {
			if (!nodes.containsKey(l)) {
				keysToRemove.add(l);
			}
		}

		for (Long l : keysToRemove) {
			carParkNodes.remove(l);
		}

		errLog("car parks: " + carParkNodes.size());
		slowdksa = new SlowDijkstra(new List<Node>(nodes.values()), new List<Edge>(edges.values()));
	}
}