package castleComponents.representations.Map2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import castleComponents.Entity;
import castleComponents.EntityID;
import castleComponents.objects.Range2D;
import castleComponents.objects.Vector2;
import castleComponents.objects.List;
import castleComponents.representations.Grid;
import castleComponents.representations.LayoutParameters;
import stdSimLib.utilities.Utilities;

public class Map2D {

	// How can we do this
	// Can use a grid!
	Grid<MapComponent> theGridMap;
	Range2D range;
	String name;
	boolean open = false;
	int scale = 1;
	Vector2 dimensions;
	Vector2 size;

	// Map changing
	int changeCounter = 0;
	HashMap<String, SubMapStore> subMapStorage;

	List<Vector2> listOfCarParkLocations;
	List<Vector2> listOfMapTransitPoints;
	List<TrafficLight> listOfTrafficLights;

	public Map2D(String name, boolean isOpen, int scale) {
		constructInit();
		this.name = name;
		this.open = isOpen;
		this.scale = scale;
		range = new Range2D();
	}

	public Map2D(Vector2 gridDims) {
		constructInit();
		setDimensions(gridDims);
		theGridMap.init(gridDims, MapComponent.class);
		range = Range2D.createRange(new Vector2(0, 0), getSize());
	}

	public Map2D(Map2D map) {
		constructInit();
		range = new Range2D(map.getRange());
		name = map.getName();
		open = map.isOpen();
		scale = map.getScale();
		setDimensions(map.getSize());
	}

	public Map2D() {
		constructInit();
		range = new Range2D();
	}

	public void constructInit() {
		theGridMap = new Grid<MapComponent>();
		theGridMap.setWrap(false);
		subMapStorage = new HashMap<String, SubMapStore>();
		listOfCarParkLocations = new List<Vector2>();
		listOfMapTransitPoints = new List<Vector2>();
		listOfTrafficLights = new List<TrafficLight>();
	}

	public void init(Vector2 gridDims) {
		setDimensions(gridDims);
		theGridMap.init(gridDims, MapComponent.class);
		initializeEmptyMap();
	}

	public void initializeEmptyMap() {
		MapComponent[][] grid = theGridMap.getGrid();
		for (int i = 0; i < grid[0].length; i++) {
			for (int j = 0; j < grid.length; j++) {
				theGridMap.getGrid()[j][i] = new MapComponent(new Vector2(j, i), Type.UNSET);
			}
		}
	}

	public void initialize(Vector2 gridDims, String pathToMapFile, LayoutParameters lp) {
		setDimensions(gridDims);
		theGridMap.init(gridDims, MapComponent.class);
		importMap(pathToMapFile);
		log("Map2D file initialized with name " + name + " and dims " + dimensions.toString());
		System.out.println(printMap());
	}

	public void initialize(Vector2 gridDims, Map2D theMap, LayoutParameters lp) {
		setDimensions(gridDims);
		scale = theMap.getScale();
		open = theMap.isOpen();
		range = new Range2D(theMap.getRange());
		listOfMapTransitPoints = new List<Vector2>();
		for (Vector2 v : theMap.getListOfMapTransitPoints()) {
			listOfMapTransitPoints.add(new Vector2(v));
		}
		listOfTrafficLights = new List<TrafficLight>();
		for (TrafficLight tl : theMap.getListOfTrafficLights()) {
			listOfTrafficLights.add(new TrafficLight(tl.getLocation(), tl.getLightPatterns(), tl.getType()));
		}

		Grid<MapComponent> oldMap = theMap.theGridMap;
		theGridMap.init(oldMap.getDimensions(), MapComponent.class);
		MapComponent[][] oldGrid = oldMap.getGrid();
		for (int i = 0; i < oldGrid[0].length; i++) {
			for (int j = 0; j < oldGrid.length; j++) {
				theGridMap.getGrid()[j][i] = new MapComponent(oldGrid[j][i]);
			}
		}
	}

	public List<TrafficLight> getListOfTrafficLights() {
		return listOfTrafficLights;
	}

	public TrafficLight getTrafficLightAtPosition(Vector2 pos) {
		for (TrafficLight tl : listOfTrafficLights) {
			if (tl.getLocation().compare(pos)) {
				return tl;
			}
		}
		log("no traffic light was found at location " + pos);
		return null;
	}

	public List<TrafficLight> getTrafficLightsInRange(Range2D r2d) {
		List<TrafficLight> tlList = new List<TrafficLight>();
		for (TrafficLight tl : listOfTrafficLights) {
			if (r2d.containsPoint(tl.getLocation())) {
				tlList.add(tl);
			}
		}
		return tlList;
	}

	public Vector2 getDimensions() {
		return dimensions;
	}

	public void setRange(Range2D r) {
		this.range = new Range2D(r);
	}

	public void setRange(Vector2 a, Vector2 b, Vector2 c, Vector2 d) {
		range = new Range2D(a, b, c, d);
	}

	public Grid<MapComponent> getTheGridMap() {
		return theGridMap;
	}

	public void createSubMaps(int numberOfSections) {
		// How to divide a grid into X evenly sized chunks
		int xChunkSize = (int) (dimensions.getX() / numberOfSections);
		int yChunkSize = (int) (dimensions.getY() / numberOfSections);

		int prevX = 0;
		int prevY = 0;
		int nextX = xChunkSize;
		int nextY = yChunkSize;
		for (int i = 0; i < numberOfSections; i++) {
			Range2D grabRange = new Range2D(new Vector2(prevX, prevY), new Vector2(prevX, nextY),
					new Vector2(nextX, nextY), new Vector2(nextX, prevY));
			Map2D sub = extractMapSection(grabRange);
			String smName = name + "_submap_" + i;
			subMapStorage.put(smName, new SubMapStore(sub, smName, grabRange));
		}
	}

	public List<Map2D> getSubMapsAsList() {
		List<SubMapStore> subMapsst = new List<SubMapStore>(subMapStorage.values());
		List<Map2D> subMaps = new List<Map2D>();
		for (SubMapStore sms : subMapsst) {
			subMaps.add(sms.getMap());
		}
		return subMaps;
	}

	public List<Range2D> getSubMapRangesAsList() {
		List<SubMapStore> subMapsst = new List<SubMapStore>(subMapStorage.values());
		List<Range2D> subMapRanges = new List<Range2D>();
		for (SubMapStore sms : subMapsst) {
			subMapRanges.add(sms.getR2d());
		}
		return subMapRanges;
	}

	public void importMap(String pathToMapFile) {
		// Make sure this Map is initialized and clean
		Map2DParser map2dParser = new Map2DParser(this);
		map2dParser.parseMapFile(pathToMapFile);
		setRange(Range2D.createRange(new Vector2(0, 0), getSize()));
		finishValidation();
	}

	public void finishValidation() {
		List<Vector2> coord = range.getAllIndexCoordPairs();
		for (Vector2 v : coord) {
			MapComponent mc = getMapComponent(v);
			// TODO: OOB
			Vector2 Wp = new Vector2(v).add(new Vector2(-1, 0));
			Vector2 Ep = new Vector2(v).add(new Vector2(1, 0));
			Vector2 Np = new Vector2(v).add(new Vector2(0, -1));
			Vector2 Sp = new Vector2(v).add(new Vector2(0, 1));
			Type W = getMapComponentType(Wp);
			Type E = getMapComponentType(Ep);
			Type N = getMapComponentType(Np);
			Type S = getMapComponentType(Sp);

			if (mc.getType() == Type.T_SEC) {

				if (W.isHoriz() && E.isHoriz()) {
					if (N.isVert() && !S.isVert()) {
						if (N == Type.ONEWAY_S) {
							// Yay
							mc.addValidExit(Wp);
							mc.addValidExit(Ep);
						} else {
							// Yay
							mc.addValidExit(Wp);
							mc.addValidExit(Ep);
							mc.addValidExit(Np);
						}
					} else if (!N.isVert() && S.isVert()) {
						if (S == Type.ONEWAY_N) {
							// Yay
							mc.addValidExit(Wp);
							mc.addValidExit(Ep);
						} else {
							// Yay
							mc.addValidExit(Wp);
							mc.addValidExit(Ep);
							mc.addValidExit(Sp);
						}
					}
				} else if (N.isVert() && S.isVert()) {
					if (W.isHoriz() && !E.isHoriz()) {
						if (W == Type.ONEWAY_E) {
							// Yay
							mc.addValidExit(Np);
							mc.addValidExit(Sp);
						} else {
							// Yay
							mc.addValidExit(Np);
							mc.addValidExit(Sp);
							mc.addValidExit(Wp);
						}
					} else if (!W.isHoriz() && E.isHoriz()) {
						if (E == Type.ONEWAY_W) {
							// Yay
							mc.addValidExit(Np);
							mc.addValidExit(Sp);
						} else {
							// Yay
							mc.addValidExit(Np);
							mc.addValidExit(Sp);
							mc.addValidExit(Ep);
						}
					}
				}

			} else if (mc.getType() == Type.PARK) {
				if (N.isVert() && N != Type.ONEWAY_N) {
					mc.addValidExit(Np);
				}
				if (S.isVert() && S != Type.ONEWAY_S) {
					mc.addValidExit(Sp);
				}
				if (W.isHoriz() && W != Type.ONEWAY_W) {
					mc.addValidExit(Wp);
				}
				if (E.isHoriz() && E != Type.ONEWAY_E) {
					mc.addValidExit(Ep);
				}
			}
		}
	}

	public Type getMostCommonType(List<Type> types) {
		HashMap<Type, Integer> lazyCounter = new HashMap<Type, Integer>();
		Type maxType = null;
		int maxCount = 0;
		for (Type t : types) {
			if (lazyCounter.get(t) != null) {
				lazyCounter.put(t, lazyCounter.get(t) + 1);
				if (lazyCounter.get(t) > maxCount) {
					maxCount = lazyCounter.get(t);
					maxType = t;
				}
			}
		}
		return maxType;
	}

	public int numberOfMatchingTypes(List<Type> types) {
		HashMap<Type, Integer> lazyCounter = new HashMap<Type, Integer>();
		Type maxType = null;
		int maxCount = 0;
		for (Type t : types) {
			if (lazyCounter.get(t) != null) {
				lazyCounter.put(t, lazyCounter.get(t) + 1);
				if (lazyCounter.get(t) > maxCount) {
					maxCount = lazyCounter.get(t);
					maxType = t;
				}
			}
		}
		return maxCount;
	}

	public Vector2 getPositionOfEntity(Entity e) {
		// Find entity and return its position
		// Cycle through grid, and check with each containedEntities map
		List<MapComponent> mapComponents = new List<MapComponent>(theGridMap.getEntities());
		Vector2 pos = Vector2.NULL;
		for (MapComponent mc : mapComponents) {
			if (mc.checkForEntity(e.getEntityID())) {
				pos = new Vector2(mc.getPosition());
				break;
			}
		}
		// Otherwise return the NULL vector2
		return pos;
	}

	public Range2D getRange() {
		return range;
	}

	public Entity getEntity() {
		List<MapComponent> mapComponents = new List<MapComponent>(theGridMap.getEntities());
		if (mapComponents.size() > 1) {
			log("Map2D: More than 1 MapComponent here");
			return null;
		}
		HashMap<String, Entity> ents = mapComponents.get(0).getContainedEntities();
		if (ents.size() > 1) {
			log("Map2D: More than 1 entity in the component");
			log("They are :");
			for (Entity e : ents.values()) {
				log(e.getEntityID());
			}
			return null;
		}
		return (Entity) mapComponents.get(0).getContainedEntities().values().toArray()[0];
	}

	public Entity getEntityFromID(EntityID eid) {
		List<MapComponent> mapComponents = new List<MapComponent>(theGridMap.getEntities());
		for (MapComponent mc : mapComponents) {
			for (Entity e : mc.getContainedEntitiesAsList()) {
				if (e.getEntityID().equals(eid)) {
					return e;
				}
			}
		}
		return null;
	}

	public Outcome initialEntityMove(Entity e, Vector2 pos) {
		MapComponent mc = getMapComponent(pos);
		mc.addEntity(e);
		return Outcome.VALID;
	}

	// This should return states

	// Heading?
	public Outcome moveTo(Entity e, Vector2 oldPos, Vector2 intendedPos) {
		// Move an entity to a particular location
		if (!range.containsIndexPoint(intendedPos)) {
			return Outcome.OUT_OF_BOUNDS;
		}

		if (isNoGo(intendedPos)) {
			log("ENTITY " + e.getEntityID().toString() + " IS TRYING TO ENTER A NOGO");
			return Outcome.INVALID;
		}

		MapComponent mc = getMapComponent(intendedPos);
		MapComponent oldMC = getMapComponent(oldPos);
		if (oldMC.isValidExit(intendedPos)) {
			boolean removeSuccess = oldMC.removeEntity(e.getID());
			if (!removeSuccess) {
				log("ENTITY " + e.getEntityID().toString() + " WAS NOT IN THIS LOCATION OF " + oldPos);
			}

			mc.addEntity(e);
		} else {
			log("NOT VALID EXIT. CURRPOS: " + oldPos + " new pos: " + intendedPos);
			// Turn the car around
			return Outcome.DEADEND;
		}

		return Outcome.VALID;
	}

	public Outcome isIntendedDestinationValid(Entity e, Vector2 oldPos, Vector2 intendedPos) {
		if (!range.containsIndexPoint(intendedPos)) {
			return Outcome.OUT_OF_BOUNDS;
		}

		if (isNoGo(intendedPos)) {
			log("ENTITY " + e.getEntityID().toString() + " IS TRYING TO ENTER A NOGO");
			return Outcome.INVALID;
		}

		MapComponent oldMC = getMapComponent(oldPos);
		boolean ePresent = oldMC.checkForEntity(e.getEntityID());
		if (!ePresent) {
			log("ENTITY " + e.getEntityID().toString() + " WAS NOT IN THIS LOCATION OF " + oldPos);
			return Outcome.INVALID;
		}

		if (!oldMC.isValidExit(intendedPos)) {
			log("NOT VALID EXIT. CURRPOS: " + oldPos + " new pos: " + intendedPos);
			// Turn the car around
			return Outcome.DEADEND;
		}

		return Outcome.VALID;
	}

	public Outcome moveToFreely(Entity e, Vector2 oldPos, Vector2 intendedPos) {
		if (!range.containsIndexPoint(intendedPos)) {
			return Outcome.OUT_OF_BOUNDS;
		}
		boolean removeSuccess = moveEntityBetweenMapComponents(e, oldPos, intendedPos);
		if (!removeSuccess) {
			log("ENTITY " + e.getEntityID().toString() + " WAS NOT IN THIS LOCATION OF " + oldPos);
		}

		return Outcome.VALID;
	}

	public boolean moveEntityBetweenMapComponents(Entity e, Vector2 source, Vector2 dest) {
		MapComponent sourceMC = getMapComponent(source);
		MapComponent destMC = getMapComponent(dest);

		boolean b = sourceMC.removeEntity(e.getID());
		destMC.addEntity(e);
		return b;
	}

	public boolean hasValidExit(Vector2 currPos, Vector2 intendedPos) {
		MapComponent mc = getMapComponent(currPos);
		return mc.isValidExit(intendedPos);
	}

	public boolean validatePath(Vector2 source, Vector2 dest, Vector2 vh) {
		boolean acheiveable = false;
		boolean running = true;
		Vector2 currPos = new Vector2(source);
		// How do we terminate this loop?
		while (running) {
			MapComponent mc = getMapComponent(currPos);
			Type type = mc.getType();
			// Get the type of the next segment
			Vector2 nextPos = new Vector2(currPos).add(vh);
			if (dest.compare(dest)) {
				running = false;
				acheiveable = true;
				break;
			} else {
				currPos = new Vector2(nextPos);
			}
		}
		return acheiveable;
	}

	public Type getNextSegmentTypeFromHeading(Vector2 currPos, Vector2 vh) {
		return getMapComponent(getNextSegmentPosition(currPos, vh)).getType();
	}

	public Vector2 getNextSegmentPosition(Vector2 currPos, Vector2 vh) {
		MapComponent mc = getMapComponent(currPos);
		Type currType = mc.getType();
		Heading head = calculateHeadingFromVector(vh);
		return new Vector2(currPos).add(vh);
	}

	public Heading calculateHeadingFromVector(Vector2 vh) {
		Vector2 unitVector = vh.getUnitVector();
		double x = unitVector.getX();
		double y = unitVector.getY();
		if (x == -1) {
			if (y == 1) {
				return Heading.NW;
			} else if (y == 0) {
				return Heading.W;
			} else if (y == -1) {
				return Heading.SW;
			}
		} else if (x == 1) {
			if (y == 1) {
				return Heading.NE;
			} else if (y == 0) {
				return Heading.E;
			} else if (y == -1) {
				return Heading.SE;
			}
		} else if (x == 0) {
			if (y == 1) {
				return Heading.N;
			} else if (y == 0) {
				return Heading.O;
			} else if (y == -1) {
				return Heading.S;
			}
		}
		return Heading.O;
	}

	public Vector2 intendedDestination(Entity e, Vector2 pos, float speed, Vector2 heading) {
		Vector2 newPos = new Vector2(pos).add(new Vector2(heading).multiply(speed));
		return newPos;
	}

	// TODO
	// This is a standard range
	public int countEntitiesInRange(Vector2 pos, Vector2 range) {

		return 0;
	}

	// This is a total range (i.e. 360° vis)
	public int countEntitiesInRange(Vector2 pos, int range) {

		List<MapComponent> mcs = new List<MapComponent>(theGridMap.getNeighboursFromVector(pos, range));
		HashSet<Entity> ents = new HashSet<Entity>();
		for (MapComponent mc : mcs) {
			if (mc != null) {
				ents.addAll(mc.getContainedEntitiesAsList());
			}
		}
		return ents.size();
	}

	public int countEntitiesInRangeOfType(Vector2 pos, int range, String theType) {
		List<MapComponent> mcs = new List<MapComponent>(theGridMap.getNeighboursFromVector(pos, range));
		HashSet<Entity> ents = new HashSet<Entity>();
		for (MapComponent mc : mcs) {
			if (mc != null) {
				ArrayList<Entity> cEnt = mc.getContainedEntitiesAsList();
				for (Entity e : cEnt) {
					if (e.getType().compareToIgnoreCase(theType) == 0) {
						ents.add(e);
						log(e.getEntityID() + " has been found as a neighbour candidate");
					}
				}
			}
		}
		return ents.size();
	}

	public boolean isRoad(Vector2 pos) {
		MapComponent m = getMapComponent(pos);
		return (m.getType() == Type.ROAD_H || m.getType() == Type.ROAD_V || m.getType() == Type.ONEWAY_N
				|| m.getType() == Type.ONEWAY_S || m.getType() == Type.ONEWAY_E || m.getType() == Type.ONEWAY_W);
	}

	public boolean isPark(Vector2 pos) {
		MapComponent m = getMapComponent(pos);
		return (m.getType() == Type.PARK);

	}

	public boolean isNoGo(Vector2 pos) {
		MapComponent m = getMapComponent(pos);
		return (m.getType() == Type.NOGO);

	}

	// How do we add Maps in Maps and still be able to retrieve these submaps
	public void addSubMap(Vector2 topLeft, Map2D theSubMap) {
		Vector2 size = theSubMap.getSize();
		// What is the actual range of this submap now that it's contained
		Range2D nr2d = new Range2D(theSubMap.getRange());
		nr2d.shiftByVector(topLeft);
		// Contains submaps option?
		// HashMap with
		SubMapStore sms = new SubMapStore(theSubMap, theSubMap.getName(), nr2d);
		subMapStorage.put(theSubMap.getName(), sms);
	}

	public Map2D findSubMapFromVector(Vector2 v) {
		List<SubMapStore> smsList = new List<SubMapStore>(subMapStorage.values());
		for (SubMapStore sms : smsList) {
			if (sms.containsPoint(v)) {
				return sms.getMap();
			}
		}
		return null;
	}

	public Map2D getSubMapFromName(String n) {
		return subMapStorage.get(n).getMap();
	}

	public String getName() {
		return name;
	}

	public boolean addMapSection(Map2D map, Vector2 pos) {
		return getMapComponent(pos).setMap(map);
	}

	public Map2D getMapSection(Vector2 pos) {
		return findSubMapFromVector(pos);
	}

	@SuppressWarnings("unchecked")
	public List<Entity> getEntitiesAtPos(Vector2 pos) {
		MapComponent m = getMapComponent(pos);
		return new List<Entity>((Collection<? extends Entity>) Utilities.getMapAsList(m.getContainedEntities()));
	}

	public MapComponent getMapComponent(Vector2 pos) {
		return theGridMap.getEntityAtPos(pos);
	}

	public Type getMapComponentType(Vector2 pos) {
		if (theGridMap.isOutOfBounds(pos)) {
			return Type.UNSET;
		}
		return getMapComponent(pos).getType();
	}

	public boolean addEntity(Entity e, Vector2 pos) {
		MapComponent m = getMapComponent(pos);
		return m.addEntity(e);
	}

	public void addEntityOverRange(Entity e, Range2D r2d) {
		List<Vector2> allCoordPairs = r2d.getAllCoordPairs();
		for (Vector2 v : allCoordPairs) {
			addEntity(e, v);
		}
	}

	public Park getParkAtPosition(Vector2 pos) {
		if (isPark(pos)) {
			MapComponent m = getMapComponent(pos);
			return m.getPark();
		} else {
			return null;
		}
	}

	public boolean entityParking(Entity e, Vector2 pos) {
		// check if park
		Park p = getParkAtPosition(pos);
		if (p != null) {
			// check if spaces
			if (p.freeSpaces()) {
				// Then Park
				// TODO: Park the entity
				p.addOccupant(e);
				return true;
			}
		}
		return false;
	}

	// Map building functions
	public void addMapComponent(Vector2 pos, Type t) {
		MapComponent mc = new MapComponent(pos, t);
		theGridMap.addCell(mc, pos);
		if (t == Type.PARK) {
			log("adding car park to " + pos);
			addCarPark(new Vector2(pos));
		} else if (t == Type.ENTRY) {
			addTransitPoint(new Vector2(pos));
		}
	}

	public void addCarPark(Vector2 pos) {
		MapComponent mc = getMapComponent(pos);
		Park p = new Park();
		p.setMaxCapacity(100);
		mc.setPark(p);
		listOfCarParkLocations.add(pos);
	}

	public void addTransitPoint(Vector2 pos) {
		listOfMapTransitPoints.add(pos);
		getMapComponent(pos).setExitPoint(true);
	}

	public void addTrafficLight(Vector2 pos, ArrayList<Vector2> patterns) {
		log("adding light at " + pos);
		if (getMapComponent(pos).getType().isJunction()) {
			TrafficLight tl = new TrafficLight(pos, patterns, getMapComponent(pos).getType());
			tl.addExits(getMapComponent(pos).getValidExits());
			listOfTrafficLights.add(tl);
			getMapComponent(pos).setTrafficLightPresent(true);
			// log("adding light at "+pos+" and is set to
			// "+getMapComponent(pos).isTrafficLightPresent());
		} else {
			log("Traffic light being added not at a junction. Check position " + pos);
		}
	}

	public void log(Object str) {
		System.out.println("Map2D Warning: " + str);
	}

	public void setName(String n) {
		this.name = n;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public void setScale(int s) {
		this.scale = s;
	}

	public void setDimensions(Vector2 v) {
		this.size = new Vector2(v);
		this.dimensions = new Vector2(v).subtract(new Vector2(1, 1));
	}

	public boolean validateDimensions() {
		log(dimensions.toString());
		log(theGridMap.getDimensions().toString());
		return dimensions.compare(theGridMap.getDimensions());
	}

	public String toString() {
		return getInformation() + '\n' + printMap();
	}

	public String getInformation() {
		return "----Map2D Information----\nName: " + name + "\nisOpen: " + open + "\nsScale: " + scale
				+ "\nDimensions: " + size.toString();
	}

	public String printMap() {
		String str = "";
		MapComponent[][] mc = theGridMap.getGrid();
		for (int i = 0; i < mc[0].length; i++) {
			for (int j = 0; j < mc.length; j++) {
				Type currType = mc[j][i].getType();
				switch (currType) {
				case NOGO:
					str += Map2DParser.NOGO;
					break;
				case PARK:
					str += Map2DParser.PARK;
					break;
				case ROAD_H:
					str += Map2DParser.ROAD_H;
					break;
				case ROAD_V:
					str += Map2DParser.ROAD_V;
					break;
				case ONEWAY_N:
					str += Map2DParser.ONEWAY_N;
					break;
				case ONEWAY_S:
					str += Map2DParser.ONEWAY_S;
					break;
				case ONEWAY_E:
					str += Map2DParser.ONEWAY_E;
					break;
				case ONEWAY_W:
					str += Map2DParser.ONEWAY_W;
					break;
				case FOUR_WAY:
					str += Map2DParser.FOUR_WAY;
					break;
				case T_SEC:
					str += Map2DParser.T_SEC;
					break;
				case UNSET:
					str += Map2DParser.UNSET;
					break;
				case EVENT:
					str += Map2DParser.EVENT;
					break;
				default:
					log("aosjhdilajsd");
					break;
				}
			}
			str += '\n';
		}
		return str;
	}

	public Map2D extractMapSection(Range2D coords) {
		// Make a blank Map2D the same size as the existing one
		Map2D existingMap = new Map2D(getSize());
		existingMap.init(getSize());
		// 1: Get all co-ord pairs from the range
		List<Vector2> allCoords = coords.getAllCoordPairs();
		// 2: Store existing section of the map
		for (Vector2 v : allCoords) {
			MapComponent mc = getMapComponent(v);
			existingMap.setMapComponent(v, mc);
			if (mc.getType() == Type.PARK) {
				existingMap.addCarPark(v);
			}
			if (mc.isExitPoint()) {
				existingMap.addTransitPoint(v);
			}
			if (mc.isTrafficLightPresent()) {
				TrafficLight tl = getTrafficLightAtPosition(v);
				existingMap.addTrafficLight(tl.getLocation(), tl.getLightPatterns());
			}
		}
		return existingMap;
	}

	public void changeMapComponentType(Vector2 coords, Type type) {
		getMapComponent(coords).setType(type);
	}

	public void setMapComponent(Vector2 coords, MapComponent newMC) {
		theGridMap.setEntityAtPos(coords, new MapComponent(newMC));
	}

	public boolean changeSectionOfMapToType(Range2D coords, String name, Type type) {
		boolean changeOccured = false;
		List<Vector2> allCoords = coords.getAllCoordPairs();
		for (Vector2 v : allCoords) {
			changeMapComponentType(v, type);
		}
		return changeOccured;
	}

	public boolean replaceSectionOfMap(Range2D coords, Map2D newSection) {
		boolean changeOccured = false;
		List<Vector2> allCoords = coords.getAllCoordPairs();
		for (Vector2 v : allCoords) {
			changeMapComponentType(v, newSection.getMapComponent(v).getType());
		}
		log("MAGICS: " + allCoords.size());
		log(printMap());

		return changeOccured;
	}

	public int getChangeCounter() {
		return changeCounter;
	}

	public void setChangeCounter(int changeCounter) {
		this.changeCounter = changeCounter;
	}

	public HashMap<String, SubMapStore> getSubMapStorage() {
		return subMapStorage;
	}

	public void setSubMapStorage(HashMap<String, SubMapStore> subMapStorage) {
		this.subMapStorage = subMapStorage;
	}

	public boolean isOpen() {
		return open;
	}

	public int getScale() {
		return scale;
	}

	public void setTheGridMap(Grid<MapComponent> theGridMap) {
		this.theGridMap = theGridMap;
	}

	public Vector2 getSize() {
		return size;
	}

	public int countEntitiesOfTypeAtPos(String type, Vector2 t1Pos) {
		int count = 0;
		MapComponent mc = getMapComponent(t1Pos);
		List<Entity> ents = new List<Entity>(mc.getContainedEntitiesAsList());
		for (Entity e : ents) {
			if (e.getType().compareToIgnoreCase(type) == 0) {
				count++;
			}
		}
		return count;
	}

	public List<Entity> getEntitiesOfTypeAtPos(String type, Vector2 t1Pos) {
		MapComponent mc = getMapComponent(t1Pos);
		List<Entity> returningEnts = new List<Entity>();
		List<Entity> ents = new List<Entity>(mc.getContainedEntitiesAsList());
		for (Entity e : ents) {
			if (e.getType().compareToIgnoreCase(type) == 0) {
				returningEnts.add(e);
			}
		}
		return returningEnts;
	}

	public Vector2 findNearestExitPoint(Vector2 v) {
		log("num exits: " + listOfMapTransitPoints.size());
		Vector2 minVec = new Vector2();
		double minDist = Double.MAX_VALUE;
		for (Vector2 v2 : listOfMapTransitPoints) {
			double cand = v.calculateDistance(v2);
			if (cand < minDist) {
				minDist = cand;
				minVec = new Vector2(v2);
			}
		}
		return minVec;
	}

	public Vector2 findNearestSegmentOfType(Vector2 v, Type type) {
		Vector2 theLoca = Vector2.NULL;
		if (type == Type.PARK) {
			Vector2 minVec = new Vector2();
			double minDist = Double.MAX_VALUE;
			for (Vector2 v2 : listOfCarParkLocations) {
				double cand = v.calculateDistance(v2);
				if (cand < minDist) {
					minDist = cand;
					minVec = new Vector2(v2);
				}
			}
			theLoca = new Vector2(minVec);
		} else if (type == Type.ENTRY) {
			theLoca = findNearestExitPoint(v);
		}

		return theLoca;
	}

	public List<Vector2> getListOfMapTransitPoints() {
		return listOfMapTransitPoints;
	}

	public List<Vector2> getListOfCarParkLocations() {
		return listOfCarParkLocations;
	}
}

enum Heading {
	N, NE, E, SE, S, SW, W, NW, O
}

class SubMapStore {
	Map2D map;
	String name;
	Range2D r2d;
	Vector2 offset = new Vector2();

	public SubMapStore(Map2D map, String name, Range2D r2d) {
		this.map = map;
		this.name = name;
		this.r2d = r2d;

		// TODO calculate offset
		offset = r2d.getPoints()[0];

	}

	public Map2D getMap() {
		return map;
	}

	public void setMap(Map2D map) {
		this.map = map;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Range2D getR2d() {
		return r2d;
	}

	public void setR2d(Range2D r2d) {
		this.r2d = r2d;
	}

	public boolean containsPoint(Vector2 v) {
		return r2d.containsPoint(v);
	}

	public Vector2 getOffset() {
		return offset;
	}
}
