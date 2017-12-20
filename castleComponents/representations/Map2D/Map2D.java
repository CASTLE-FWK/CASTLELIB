package castleComponents.representations.Map2D;

import java.util.HashMap;
import java.util.HashSet;

import castleComponents.Entity;
import castleComponents.objects.Range2D;
import castleComponents.objects.Vector2;
import castleComponents.objects.List;
import castleComponents.representations.Grid;
import castleComponents.representations.LayoutParameters;
import stdSimLib.utilities.Utilities;

public class Map2D {
	
	//How can we do this
	//Can use a grid! 
	Grid<MapComponent> theGridMap;
	Range2D range;
	String name;
	boolean open = false;
	int scale = 1;
	Vector2 dimensions;
	
	
	//Map changing
	int changeCounter = 0;
	HashMap<Map2D, String> mapStorage;
	

	public Map2D(String name, boolean isOpen, int scale){
		this.name = name;
		this.open = isOpen;
		this.scale = scale;
		theGridMap = new Grid<MapComponent>();
		range = new Range2D();
		mapStorage = new HashMap<Map2D, String>();
	}
	
	public Map2D(Vector2 gridDims){
		theGridMap = new Grid<MapComponent>();
		theGridMap.init(gridDims, MapComponent.class);
		range = new Range2D();
		mapStorage = new HashMap<Map2D, String>();
	}
	
	public Map2D(Map2D map) {
		//TODO
	}
	
	public void init(Vector2 gridDims){
		theGridMap.init(gridDims, MapComponent.class);
	}
	
	public void setRange(Range2D r){
		this.range = r;
		//Apply this to Grid
	}
	
	public void setRange(Vector2 a, Vector2 b, Vector2 c, Vector2 d){
		range = new Range2D(a,b,c,d);
		//Apply this to Grid
	}
	
	public Map2D(){
		theGridMap = new Grid<MapComponent>();
		range = new Range2D();
	}
	
	public Grid<MapComponent> getTheGridMap(){
		return theGridMap;
	}
	
	public void initialize(Vector2 gridDims, String pathToMapFile, LayoutParameters lp) {
		theGridMap.init(gridDims, MapComponent.class);
		importMap(pathToMapFile);
		System.out.println("Map2D file initialized with name "+name+" and dims "+dimensions.toString());
		System.out.println(getMapComponent(new Vector2(0,0)));
	}
	
	public void initialize(Vector2 gridDims, Map2D theMap, LayoutParameters lp) {
		theGridMap.init(gridDims, MapComponent.class);
		//TODO: Clone existing Map
		scale = theMap.scale;
		open = theMap.open;
		range.copy(theMap.range);
		Grid<MapComponent> oldMap = theMap.theGridMap;
		theGridMap.init(oldMap.getDimensions(), MapComponent.class);
		MapComponent[][] oldGrid = oldMap.getGrid();
		for (int i = 0; i < oldGrid[0].length; i++) {
			for (int j = 0; j < oldGrid.length; j++) {
				theGridMap.getGrid()[j][i] = new MapComponent(oldGrid[j][i]);
			}
		}
		
		
		theGridMap.copy(MapComponent.class, theMap.theGridMap, lp);
	}
	
	
	//Chunking
	List<Range2D> chunkedRanges = null;
	List<Map2D> chunkedMap = null;
	public List<Map2D> chunkMapIntoSections(int numberOfSections){
		List<Map2D> chunks = new List<Map2D>();
		chunkedRanges = new List<Range2D>();
		//TODO
		if (numberOfSections == 1) {
			chunks.add(this);
			chunkedRanges.add(range);
		}
		
		//chunkedRanges = ;
		chunkedMap = chunks;
		return chunks;
	}
	
	public List<Range2D> getChunkedMapRanges(){
		return chunkedRanges;
	}
	
	public void importMap(String pathToMapFile){
		//Make sure this Map is initialized and clean
		
		Map2DParser map2dParser = new Map2DParser(this);
		map2dParser.parseMapFile(pathToMapFile);
		range = Range2D.createRange(new Vector2(0,0), dimensions);		
	}
	
	public Vector2 getPositionOfEntity(Entity e){
		//Find entity and return its position
		//Cycle through grid, and check with each containedEntities map
		List<MapComponent> mapComponents = new List<MapComponent>(theGridMap.getEntities());
		String eID = e.getID();
		for (MapComponent mc : mapComponents) {
			if (mc.checkForEntity(eID)){
				return mc.getPosition();
			}
		}

		//Otherwise return the NULL vector2
		return Vector2.NULL;
	}
	
	public Entity getEntity(){
		List<MapComponent> mapComponents = new List<MapComponent>(theGridMap.getEntities());
		if (mapComponents.size() > 1){
			System.out.println("Map2D: More than 1 entity here");
			return null;
		}
		HashMap<String, Entity> ents = mapComponents.get(0).getContainedEntities();
		if (ents.size() > 1){
			System.out.println("Map2D: More than 1 entity in the component");
			return null;
		}
		return (Entity)mapComponents.get(0).getContainedEntities().values().toArray()[0];
	}
	
	//This should return states
	public Outcome moveTo(Entity e, Vector2 pos){
		//Move an entity to a particular location
		if (!range.containsPoint(pos)){
			return Outcome.OUT_OF_BOUNDS;
		}
		
		if (isNoGo(pos)){
			return Outcome.INVALID;
		}

		//Surely there are some more bad cases here
		
		//This will be slow
		MapComponent oldMC = getMapComponent(getPositionOfEntity(e));
		oldMC.removeEntity(e.getID());
		
		//TODO: What the heck is this meant to do?
		MapComponent mc = getMapComponent(pos);
		mc.addEntity(e);
		
		return Outcome.VALID;
		
	}
	
	public String moveToWithVelocity(Entity e, Vector2 pos, Vector2 vel){
		Vector2 newPos = pos.add(vel);
		return moveTo(e,newPos).toString();		
	}

	
	
	//	TODO
	//This is a standard range
	public int countEntitiesInRange(Vector2 pos, Vector2 range){
		
		
		return 0;
	}
	
	
	//This is a total range (i.e. 360° vis)
	public int countEntitiesInRange(Vector2 pos, int range){
		List<MapComponent> mcs = new List<MapComponent>(theGridMap.getNeighboursFromVector(pos, range));
		HashSet<Entity> ents = new HashSet<Entity>();
		for (MapComponent mc : mcs){
			ents.addAll(mc.getContainedEntitiesAsList());
		}
		return ents.size();		
	}
	
	public boolean isRoad(Vector2 pos){
		MapComponent m = getMapComponent(pos);
		return (m.getType() == Type.ROAD_H || m.getType() == Type.ROAD_V ||
				m.getType() == Type.TURN_L || m.getType() == Type.TURN_R);				
	}
	
	public boolean isPark(Vector2 pos){
		MapComponent m = getMapComponent(pos);
		return (m.getType() == Type.PARK);
		
	}
	public boolean isNoGo(Vector2 pos){
		MapComponent m = getMapComponent(pos);
		return (m.getType() == Type.NOGO);
		
	}
	
	
//	public boolean isType(String typeName){
//		//
//	}
	
	public boolean addMapSection(Map2D map, Vector2 pos){
		return getMapComponent(pos).setMap(map);
	}
	
	public Map2D getMapSection(Vector2 pos){
		return getMapComponent(pos).getMap();
	}
	
	@SuppressWarnings("unchecked")
	public List<Entity> getEntitiesAtPos(Vector2 pos){
		MapComponent m = getMapComponent(pos);
		return (List<Entity>) Utilities.getMapAsList(m.getContainedEntities());
	}
	
	public MapComponent getMapComponent(Vector2 pos){
		return theGridMap.getEntityAtPos(pos);
	}
	
	public boolean addEntity(Entity e, Vector2 pos){
		MapComponent m = getMapComponent(pos);
		return m.addEntity(e);
	}
	
	public Park getParkAtPosition(Vector2 pos){		
		if (isPark(pos)){
			MapComponent m = getMapComponent(pos);
			return m.getPark();
		} else {
			return null;
		}
	}
	
	public boolean entityParking(Entity e, Vector2 pos){
		//check if park
		Park p = getParkAtPosition(pos);
		if (p != null) {
			//check if spaces
			if (p.freeSpaces()){
				//Then Park
				//TODO: Park the entity
				p.addOccupant(e);
				return true;
			}
		}
		return false;
	}
	
	//Map building functions
	public void addMapComponent(Vector2 pos, Type t){
		MapComponent mc = new MapComponent(pos, t);
		theGridMap.addCell(mc, pos);
	}
	
	public void setName(String n){
		this.name = n;		
	}
	
	public void setOpen(boolean open){
		this.open = open;
	}
	
	public void setScale(int s){
		this.scale = s;
	}
	
	public void setDimensions(Vector2 v){
		this.dimensions = new Vector2(v);
	}
	
	public boolean validateDimensions(){
		System.out.println(dimensions.toString());
		System.out.println(theGridMap.getDimensions().toString());
		return dimensions.compare(theGridMap.getDimensions());
	}
	
	public String toString(){
		return getInformation()+'\n'+printMap();
	}
	
	public String getInformation(){
		return "----Map2D Information----\nName: "+name
				+"\nisOpen: "+open
				+"\nsScale: "+scale
				+"\nDimensions: "+dimensions.toString();
	}
	
	public String printMap(){
		String str = "";
		MapComponent[][] mc = theGridMap.getGrid();
		for (int i = 0; i < mc.length; i++){
			for (int j = 0; j < mc[i].length; j++){
				Type currType = mc[i][j].getType();
				switch (currType){
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
					case TURN_R:
						str += Map2DParser.TURN_R;
					break;
					case TURN_L:
						str += Map2DParser.TURN_L;
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
	
	public boolean changeSectionOfMapToType(Range2D coords, String name, Type type) {
		boolean changeOccured = false;
		Vector2 dims = coords.getDimensions();
		//1: Get all co-ord pairs from the range
		List<Vector2> allCoords = coords.getAllCoordPairs();
		//2: Store existing section of the map
		Map2D existingMap = new Map2D();
		MapComponent[][] emc = existingMap.getTheGridMap().getGrid();
		
		MapComponent[][] mc = theGridMap.getGrid();
		
		//TODO: Time to resume
		existingMap.init(dims);
		for (Vector2 v : allCoords) {
			int x = (int) v.getX();
			int y = (int) v.getY();
			emc[x][y].setType(mc[x][y].getType());
		}
		
		//3: Change the corresponding points to 
		
		for (Vector2 v : allCoords) {
			int x = (int) v.getX();
			int y = (int) v.getY();
			mc[x][y].setType(type);
		}
		
		//4: Finish
		return changeOccured;
	}
	public boolean changeSectionOfMap(Range2D coords, String name, Map2D newSection) {
		boolean changeOccured = false;
		//TODO
		
		//1: Store existing section of the map
		
		//2: Replace with new section
		return changeOccured;
	}

}

enum Outcome {
	OUT_OF_BOUNDS, INVALID, VALID, MOVED;
}

