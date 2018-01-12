package castleComponents.representations.MapGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import castleComponents.Entity;
import castleComponents.objects.Range2D;
import castleComponents.objects.Vector2;
import castleComponents.objects.List;
import castleComponents.representations.LayoutParameters;
import stdSimLib.utilities.Dijkstra;

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

	public MapGraph() {
		links = new HashMap<Long, Link>();
		edges = new HashMap<String, Edge>();
		nodes = new HashMap<Long, Node>();
		nodesMap = new HashMap<Vector2, Node>();
		entitiesInMap = new HashMap<String, Entity>();
		transitPoints = new List<Node>(); // TODO Populate this list
		trafficLightLocations = new List<TrafficLight>(); // TODO populate this list
		carParkNodes = new List<Node>(); // TODO Populate this list
		id = -1;
		subMaps = new List<MapGraph>();
		dksa = new Dijkstra(new List<Node>(nodes.values()), new List<Edge>(edges.values()));
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

	/** How to move Entities around **/
	// TODO HERE
	public Outcome moveEntity(Entity e, Edge currEdge, double moveDist, double distanceAlongEdge, Route route) {
		Node nextNode = route.getNextNode();

		// Which distance are we looking at?

		// How do we update the position along the edge?
		double newDist = distanceAlongEdge + moveDist; // TODO Determine which direction this should be done in
		Outcome outcome = null;
		if (currEdge == null) {
			Node thisNode = route.getPrevNode();
			for (Edge ed : thisNode.getEdges()) {
				if (ed.isNodeConnected(nextNode)) {
					currEdge = ed;
					break;
				}
			}
		}

		if (newDist > currEdge.getDistanceInKM()) {
			errLog("edge length: " + currEdge.getDistanceInKM());
			double overMove = newDist - currEdge.getDistanceInKM();
			route.nodeVisted();
			Node next = route.getNextNode();
			if (next == null) {
				// Entity is at it's destination
				return new Outcome(OutcomeResult.FINISHED, overMove, nextNode, this, currEdge);
			}
			errLog("updated node");
			if (next.hasTrafficLight()) {
				errLog("THERES A TRAFFIC LIGHT HERE");
			}
			// Find edge that connects to next
			Node nextNext = route.getFollowingNode(next);
			currEdge = null;
			for (Edge ed : next.getEdges()) {
				if (ed.isNodeConnected(nextNext)) {
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
			outcome = new Outcome(OutcomeResult.VALID, newDist, nextNode, this, currEdge);
		}

		return new Outcome(OutcomeResult.VALID, newDist, nextNode, this, currEdge);

	}

	public List<Node> calculateRoute(Vector2 currPos, Vector2 destPos) {
		errLog("calculateRoute is incomplete");
		Node currNode = getNodeAtPosition(currPos);
		errLog("currNode pos: " + currPos);
		Node destNode = getNodeAtPosition(destPos);

		dksa.execute(currNode);
		List<Node> path = dksa.getPath(destNode);
		if (path == null) {
			errLog("path is null");
			System.exit(0);
		}
		return path;
	}

	public Vector2 calculateEntitiesPosition(Edge currEdge, double distanceAlongEdge, Node prevNode, Node destNode) {
		Vector2 prevPos = prevNode.getCoords();
		Vector2 destPos = destNode.getCoords();
		double edgeLen = currEdge.getDistanceInKM();
		double t = distanceAlongEdge / edgeLen;
		double x = ((1 - t) * prevPos.getX() + (t * destPos.getX()));
		double y = ((1 - t) * prevPos.getY() + (t * destPos.getY()));
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
		dksa = new Dijkstra(new List<Node>(nodes.values()), new List<Edge>(edges.values()));

		// This is weird - print random node
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

	// TODO
	public List<Entity> getEntitiesOfTypeAtPos(String type, Vector2 pos) {
		errLog("getEntitiesOfTypeAtPos is incomplete");
		return null;
	}

	// Note: This might return funky nulls
	public Node getNodeAtPosition(Vector2 pos) {
		return nodesMap.get(pos);
	}

	// TODO
	public void changeNodeType(Vector2 pos, String type) {
		errLog("changeNodeType is incomplete");
	}

	// TODO
	public Node findNearestNodeOfType(Vector2 pos, String type) {
		errLog("findNearestNodeOfType is incomplete");
		switch (type) {

		default:
			break;
		}

		return null;
	}

	public Node getNodeFromID(long id) {
		return nodes.get(id);
	}

	// TODO
	public MapGraph extractMapSection(Range2D range) {
		errLog("extractMapSection is incomplete");
		return null;
	}

	// TODO
	public void changeSectionOfMapToType(String eventName, String type) {
		errLog("changeSectionOfMapToType is incomplete");
	}

	// TODO
	public void replaceSectionOfMap(Range2D r, MapGraph oldMapData) {
		errLog("replaceSectionOfMap is incomplete");
	}

	// TODO
	public int countEntitiesInRangeWithType(Entity e, double speed, String type, Edge currEdge, Heading h) {
		errLog("countEntitiesInRangeWithType is incomplete. Is current task.");
		if (currEdge == null) {
			return 0;
		}
		HashSet<Entity> entsOnSameEdge = currEdge.getEntities();
		for (Entity ent : entsOnSameEdge) {
			if (e == ent) {
				continue;
			}
			// How can we do this?

		}

		return -1;
	}

	// TODO
	public Park getCarParkAtPosition(Vector2 pos) {
		errLog("getCarParkAtPosition is incomplete");
		return null;
	}

	public Vector2 getPositionOfEntity(Entity e) {
		return null;
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
	public ArrayList<Node> getNodesAsList() {
		return new ArrayList<Node>(nodes.values());
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
		for (Edge e : edges.values()) {
			e.getNodeA().addAdjacentNode(e.getNodeB());
			e.getNodeB().addAdjacentNode(e.getNodeA());
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
		for (Node n : nodes.values()) {
			if (n.getNodeType().compareToIgnoreCase(TRAFFIC_SIGNAL) == 0) {
				// Build a traffic light here
				n.setTrafficLight(true);
				trafficLightLocations.add(new TrafficLight(n.getCoords()));
			}
		}
	}
}