package castleComponents.representations.Map2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.sun.org.apache.xml.internal.serialize.XHTMLSerializer;

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

	public Map2D(String name, boolean isOpen, int scale) {
		this.name = name;
		this.open = isOpen;
		this.scale = scale;
		theGridMap = new Grid<MapComponent>();
		range = new Range2D();
		subMapStorage = new HashMap<String, SubMapStore>();
	}

	public Map2D(Vector2 gridDims) {
		theGridMap = new Grid<MapComponent>();
		setDimensions(gridDims);
		theGridMap.init(gridDims, MapComponent.class);
		range = Range2D.createRange(new Vector2(0, 0), getSize());
		subMapStorage = new HashMap<String, SubMapStore>();
	}

	public Map2D(Map2D map) {
		range = new Range2D(map.getRange());
		name = map.getName();
		open = map.isOpen();
		scale = map.getScale();
		setDimensions(map.getSize());
		theGridMap = new Grid<MapComponent>();
		subMapStorage = new HashMap<String, SubMapStore>();
	}

	public Map2D() {
		theGridMap = new Grid<MapComponent>();
		range = new Range2D();
		subMapStorage = new HashMap<String, SubMapStore>();
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
				theGridMap.getGrid()[j][i] = new MapComponent(new Vector2(j,i), Type.UNSET);
			}
		}
	}

	public void initialize(Vector2 gridDims, String pathToMapFile, LayoutParameters lp) {
		setDimensions(gridDims);
		theGridMap.init(gridDims, MapComponent.class);
		importMap(pathToMapFile);
		System.out.println("Map2D file initialized with name " + name + " and dims " + dimensions.toString());
		System.out.println(printMap());
	}

	public void initialize(Vector2 gridDims, Map2D theMap, LayoutParameters lp) {
		setDimensions(gridDims);
		scale = theMap.getScale();
		open = theMap.isOpen();
		range = new Range2D(theMap.getRange());

		Grid<MapComponent> oldMap = theMap.theGridMap;
		theGridMap.init(oldMap.getDimensions(), MapComponent.class);
		MapComponent[][] oldGrid = oldMap.getGrid();
		for (int i = 0; i < oldGrid[0].length; i++) {
			for (int j = 0; j < oldGrid.length; j++) {
				theGridMap.getGrid()[j][i] = new MapComponent(oldGrid[j][i]);
			}
		}
//		theGridMap.copy(MapComponent.class, theMap.theGridMap, lp);
	}

	public Vector2 getDimensions() {
		return dimensions; // TODO Make sure this gets set
	}

	public void setRange(Range2D r) {
		this.range = new Range2D(r);
		// Apply this to Grid
	}

	public void setRange(Vector2 a, Vector2 b, Vector2 c, Vector2 d) {
		range = new Range2D(a, b, c, d);
		// Apply this to Grid
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
		// System.out.println("size of submapstorage; "+ subMapStorage.size());
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
		range = Range2D.createRange(new Vector2(0, 0), getSize());
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
			System.out.println("Map2D: More than 1 entity here");
			return null;
		}
		HashMap<String, Entity> ents = mapComponents.get(0).getContainedEntities();
		if (ents.size() > 1) {
			System.out.println("Map2D: More than 1 entity in the component");
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
	
	//Heading?
	public Outcome moveTo(Entity e, Vector2 oldPos, Vector2 intendedPos) {
		// Move an entity to a particular location
//		System.out.println("intendedPos: " + intendedPos.toString());
		if (!range.containsIndexPoint(intendedPos)) {
			return Outcome.OUT_OF_BOUNDS;
		}

		if (isNoGo(intendedPos)) {
			System.out.println("ENTITY " + e.getEntityID().toString() + " IS IN A NOGO");
			return Outcome.INVALID;
		}
		
		//Type of road (validity check) this is going to be bigsssssss
		MapComponent mc = getMapComponent(intendedPos);
		//Track road between curr pos and intended pos

		// Surely there are some more bad cases here

		// This will be slow
		MapComponent oldMC = getMapComponent(oldPos);
		boolean removeSuccess = oldMC.removeEntity(e.getID());
		if (!removeSuccess) {
			System.out.println("ENTITY " + e.getEntityID().toString() + " WAS NOT IN THIS LOCATION OF "+oldPos);
		}
		// TODO: What the heck is this meant to do?
		
		mc.addEntity(e);

		return Outcome.VALID;

	}
	
	public boolean validatePath(Vector2 source, Vector2 dest, Vector2 vh) {
		boolean acheiveable = false;
		boolean running = true;
		Vector2 currPos = new Vector2(source);
		//How do we terminate this loop?
		while (running) {
			MapComponent mc = getMapComponent(currPos);
			Type type = mc.getType();
			//Get the type of the next segment
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
	
	public Type getNextSegmentFromHeading(Vector2 currPos, Vector2 vh) {
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

	public String moveAlongWithSpeed(Entity e, Vector2 pos, float speed, Vector2 heading) {
		return moveToWithVelocity(e,pos,new Vector2(heading).multiply(speed));
	}

	public String moveToWithVelocity(Entity e, Vector2 pos, Vector2 vel) {
		Vector2 unitVector = vel.getUnitVector();
		Vector2 newPos = new Vector2(pos).add(vel);
		return moveTo(e, pos, newPos).toString();
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
						System.out.println("irt: "+e.getEntityID() + " at " + mc.getPosition());
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
		// Is the subMap + its setting position to big for the current map?
		// TODO
		// Place the submap in

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
		return (List<Entity>) Utilities.getMapAsList(m.getContainedEntities());
	}

	public MapComponent getMapComponent(Vector2 pos) {
		return theGridMap.getEntityAtPos(pos);
	}

	public boolean addEntity(Entity e, Vector2 pos) {
		MapComponent m = getMapComponent(pos);
		return m.addEntity(e);
	}

	public void addEntityOverRange(Entity e, Range2D r2d) {
		List<Vector2> allCoordPairs = r2d.getAllIndexCoordPairs();
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
		// this.dimensions = new Vector2(v);
	}

	public boolean validateDimensions() {
		System.out.println(dimensions.toString());
		System.out.println(theGridMap.getDimensions().toString());
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
					System.out.println("aosjhdilajsd");
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
			existingMap.setMapComponent(v, getMapComponent(v));
//			mcc.setPosition(new Vector2(v));
//			mcc.setType(getMapComponent(v).getType());
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
		System.out.println("MAGICS: " + allCoords.size());
		System.out.println(printMap());

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
}

enum Outcome {
	OUT_OF_BOUNDS, INVALID, VALID, MOVED;
}

enum Heading {
	N, NE, E, SE, S, SW, W, NW, O
}
class SubMapStore {
	Map2D map;
	String name;
	Range2D r2d;

	public SubMapStore(Map2D map, String name, Range2D r2d) {
		this.map = map;
		this.name = name;
		this.r2d = r2d;
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
}
