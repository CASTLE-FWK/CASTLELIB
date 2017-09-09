package castleComponents.representations.Map2D;

import java.util.ArrayList;
import java.util.List;

import javax.activation.MailcapCommandMap;

import castleComponents.Entity;
import castleComponents.objects.Range2D;
import castleComponents.objects.Vector2;
import castleComponents.representations.Grid;
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
	
	public Map2D(String name, boolean isOpen, int scale){
		this.name = name;
		this.open = isOpen;
		this.scale = scale;
		theGridMap = new Grid<MapComponent>();
		
	}
	
	public Map2D(Vector2 gridDims){
		theGridMap = new Grid<MapComponent>();
		theGridMap.init(gridDims, MapComponent.class);
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
	}
	
	
	
	public static void importMap(String parsedMapFile){
		//Oh boy, this will be fun
		
		
		
	}
	
	public Vector2 getPositionOfEntity(Entity e){
		//Find entity and return its position
		//Cycle through grid, and check with each containedEntities map
		ArrayList<MapComponent> mapComponents = new ArrayList<MapComponent>(theGridMap.getEntities());
		String eID = e.getID();
		for (MapComponent mc : mapComponents) {
			if (mc.checkForEntity(eID)){
				return mc.getPosition();
			}
		}

		//Otherwise return the NULL vector2
		return Vector2.NULL;
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
		
		MapComponent mc = getMapComponent(pos);
		mc.addEntity(e);
		
		return Outcome.VALID;
		
	}
	
	public Outcome moveToWithVelocity(Entity e, Vector2 pos, Vector2 vel){
		Vector2 newPos = pos.add(vel);
		return moveTo(e,newPos);		
	}

	
	
	//	TODO
//	//This is a standard range
//	public int countEntitiesInRange(Vector2 pos, Vector2 range){
//		
//	}
//	
//	//This is a total range (i.e. 360° vis)
//	public int countEntitiesInRange(Vector2 pos, int range){
//		
//	}
	
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
	
	public Park getParkAtPos(Vector2 pos){		
		if (isPark(pos)){
			MapComponent m = getMapComponent(pos);
			return m.getPark();
		} else {
			return null;
		}
	}
	
	public boolean entityParking(Entity e, Vector2 pos){
		//check if park
		Park p = getParkAtPos(pos);
		if (p != null) {
			//check if spaces
			if (p.freeSpaces()){
				//Then Park
				//TODO
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

}

enum Outcome {
	OUT_OF_BOUNDS, INVALID, VALID;
}
