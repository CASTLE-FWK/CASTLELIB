package castleComponents.representations;

import static castleComponents.Enums.getOpposite;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import castleComponents.Entity;
import castleComponents.EntityID;	
import castleComponents.Enums.GridPositions;
import castleComponents.objects.GridLocation;
import castleComponents.objects.Neighbors;
import castleComponents.objects.Vector2;


//TODO: How to get this to work with multiple Entity types
public class Grid implements Representation{
	int X;
	int Y;
	Entity[][] theGrid;
	LayoutParameters layoutParameters;
	
	boolean allowPhantoms = false;
	Entity[] phantoms_U;
	Entity[] phantoms_UR;
	Entity[] phantoms_R;
	Entity[] phantoms_DR;
	Entity[] phantoms_D;
	Entity[] phantoms_DL;
	Entity[] phantoms_L;
	Entity[] phantoms_UL;
	
	List<Entity> allContainedEntities;
	
//	Neighbors<Entity> phantoms_U;
//	Neighbors<Entity> phantoms_UR;
//	Neighbors<Entity> phantoms_R;
//	Neighbors<Entity> phantoms_DR;
//	Neighbors<Entity> phantoms_D;
//	Neighbors<Entity> phantoms_DL;
//	Neighbors<Entity> phantoms_L;
//	Neighbors<Entity> phantoms_UL;	
	
	Class<Entity> theClass;
	public Grid(Class<Entity> c, int X, int Y){
		if (X == 0){
			X = 1;
		} 
		if (Y == 0){
			Y = 1;
		}
		this.X = X;
		this.Y = Y;
		this.theClass = c;
//		grid = new E[this.X][this.Y];
		@SuppressWarnings("unchecked")
		final Entity[][] grid = (Entity[][]) Array.newInstance(c, X,Y);		
//		theGrid = new Entity[X][Y];
		this.theGrid = grid;
		
		allContainedEntities = new ArrayList<Entity>();
	}
	
	//TODO: Set phantom size
	public Grid(){
		
	}
	
	public Entity[][] getGrid(){
		return theGrid;
	}
	
	public Entity getEntityAtXY(int x, int y){
		return theGrid[x][y];
	}
	public void setPhantomState(boolean p){
		allowPhantoms = p;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean initialize(Object...objects){
		if (objects.length > 2 || objects.length < 2){
			return false; 
		}
		//First parameter is Vector2 layoutXY, 2nd parameter is LayoutParameters layoutParameters
		Vector2 layoutXY = (Vector2)objects[0];
		LayoutParameters layoutParameters = (LayoutParameters)objects[1]; 
		
		//Vector2 layoutXY, LayoutParameters layoutParameters
		
		this.X = (int)layoutXY.getX();
		this.Y = (int)layoutXY.getY();
		this.layoutParameters = layoutParameters;
		setPhantomState(this.layoutParameters.allowPhantoms());
		
		//Check for 0 sized dimensions and fix
		if (X == 0){
			X = 1;
		} 
		if (Y == 0){
			Y = 1;
		}
		theClass = (Class<Entity>) this.layoutParameters.getEntityType();
		//Allow the grid to store Entities of the type specified in the layout parameters
		final Entity[][] grid = (Entity[][]) Array.newInstance(theClass, X,Y);
		this.theGrid = grid;
		
		return true;
		
//		System.out.println("GRID INIT FUNCTION CALL");
	}
	
	@Override
	public boolean addEntities(List<Entity> es){
		if (es == null){
			return false;
		}
		if (es.size() == 0){
			return false;
		}
		
		for (Entity e : es){
			Vector2 pos = e.getPosition();
			if (pos == null){
				return false;
			}
			addCell(e,(int)pos.getX(),(int)pos.getY());
		}
		
		return true;
	}
	
	@Override
	public boolean initializeEntities(Object... objects) {
		//First object should be a counter, in this case a Vector2
		if (objects.length > 4){
			return false;
		}
		if (!(objects[0] instanceof Vector2)){
			return false;
		}
//		Vector2 range = (Vector2)objects[0];
//		int xRange = (int)range.getX();
//		int yRange = (int)range.getY();
//		for (int i = 0; i < xRange; i++){
//			for (int j = 0; j < yRange; j++){ 
//			}
//		}
		
		//TODO: Pull some form on input validation from the entity for this
		
		//
		
		
		return false;
	}
	
	public void initCells(Object... objs){
		
	}
	
	public void place() {
//		System.out.println("GRID PLACEntity FUNCTION CALL");
		//Create the Cell with only Entity instantiation
		//Figure out the class
//		theClass = (Class<Entity>) this.layoutParameters.getEntityType();
//		int count = 0;
//		for (int i = 0; i < X; i++){
//			for (int j = 0; j < Y; j++){
//				//Create some EntityIDs
//				EntityID eid = new EntityID(theClass.getCanonicalName()+"_"+i+"_"+j, count);
//				Entity ent = (E) theClass.newInstance();
//				
//			}
//		}
		
	}
	
	public void send(GridPositions location, Neighbors<Entity> neighbors, String function){
		System.out.println("GRID SEND FUNCTION CALL");
	}
	
//	public Neighbors<Entity> getAllNeighborsFromPosition(GridPositions gp){
//		Neighbors<Entity> neigh = new Neighbors<Entity>();
////		neigh.setD(getNeighbour_D(x, y));
//		Entity[] tmpArray = getAllAsArray(gp);
//		switch(gp){
//		case LEFT:
//			neigh.setL(tmpArray);
//			return neigh;
//		case RIGHT:
//			neigh.setR(tmpArray);
//			return neigh;
//		case BOTTOM:
//			neigh.setD(tmpArray);
//			return neigh;
//		case TOP:
//			neigh.setU(tmpArray);
//			return neigh;
//		case TOPLEFT:
//			neigh.setUL(tmpArray);
//			return neigh;
//		case TOPRIGHT:
//			neigh.setUR(tmpArray);
//			return neigh;
//		case BOTTOMLEFT:
//			neigh.setDL(tmpArray);
//			return neigh;
//		case BOTTOMRIGHT:
//			neigh.setDR(tmpArray);
//			return neigh;		
//		}
//		return null;
//	}
	
	public List<Entity> getAll(GridPositions gp){
		Entity[] arr = getAllAsArray(gp);
		ArrayList<Entity> list = new ArrayList<Entity>();
		for (Entity e : arr){
			list.add(e);
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public Entity[] getAllAsArray(GridPositions gp){
		switch(gp){
		case LEFT:
			//y is 0
			final Entity[] neighborsl = (Entity[]) Array.newInstance(theClass, X);
			for (int i = 0; i < X; i++){
				neighborsl[i] = theGrid[0][i];
			}
			return neighborsl;
		case RIGHT:
			//y is Y-1
			final Entity[] neighborsr = (Entity[]) Array.newInstance(theClass, X);
			for (int i = 0; i < X; i++){
				neighborsr[i] = theGrid[X-1][i];
			}
			return neighborsr;
		case BOTTOM:
			//x is X-1
			final Entity[] neighborsd = (Entity[]) Array.newInstance(theClass, Y);
			for (int i = 0; i < Y; i++){
//				neighborsd[i] = grid[X-1][i];
				neighborsd[i] = theGrid[i][X-1];
			}
			return neighborsd;
		case TOP:
			//x is 0
			final Entity[] neighborsu = (Entity[]) Array.newInstance(theClass, Y);
			for (int i = 0; i < Y; i++){
//				neighborsu[i] = grid[0][i];
				neighborsu[i] = theGrid[i][0];
			}
			return neighborsu;
		case TOPLEFT:
			//x is 0, y is 0
			final Entity[] neighborsul = (Entity[]) Array.newInstance(theClass, 1);
			neighborsul[0] = theGrid[0][0];
			return neighborsul;
		case TOPRIGHT:
			//x is 0, y is Y-1
			final Entity[] neighborsur = (Entity[]) Array.newInstance(theClass, 1);
//			neighborsur[0] = grid[0][Y-1];
			neighborsur[0] = theGrid[X-1][0];
			return neighborsur;
		case BOTTOMLEFT:
			//x is X-1, y is 0
			final Entity[] neighborsdl = (Entity[]) Array.newInstance(theClass, 1);
//			neighborsdl[0] = grid[X-1][0];
			neighborsdl[0] = theGrid[0][Y-1];
			return neighborsdl;
		case BOTTOMRIGHT:
			//x is X-1, y is Y-1
			final Entity[] neighborsdr = (Entity[]) Array.newInstance(theClass, 1);
			neighborsdr[0] = theGrid[X-1][Y-1];
			return neighborsdr;
		default:
			return null;
		}
	}

	public void addCell(Entity c, int x, int y){
		theGrid[x][y] = c;
		allContainedEntities.add(c);
	}
	public void addCell(Entity c, Vector2 vec){
		theGrid[(int)vec.getX()][(int)vec.getY()] = c;
	}
	
	public int getX(){
		return X;
	}
	
	public int getY(){
		return Y;
	}

	public List<Entity> getNeighboursFromVector(Vector2 v, int depth){
		return getNeighbours((int)v.getX(), (int)v.getY(), depth);
	}
	
	public List<Entity> getNeighbours(int x, int y, int depth){
		ArrayList<Entity> neighbours = new ArrayList<Entity>();
		neighbours.add(getNeighbour_U(x, y));
		neighbours.add(getNeighbour_UR(x, y));
		neighbours.add(getNeighbour_R(x, y));
		neighbours.add(getNeighbour_DR(x, y));
		neighbours.add(getNeighbour_D(x, y));
		neighbours.add(getNeighbour_DL(x, y));
		neighbours.add(getNeighbour_L(x, y));
		neighbours.add(getNeighbour_UL(x, y));
		
		return neighbours;
	}
	
	public List<Entity> getNeighbours_Old(int x, int y, int depth){
		ArrayList<Entity> neighbours = new ArrayList<Entity>();

		//Edge cases??
		if (x == 0){
			if (y == 0){
				neighbours.add(theGrid[x][y+1]);
				neighbours.add(theGrid[x][Y-1]);

				neighbours.add(theGrid[x+1][y]);
				neighbours.add(theGrid[x+1][y+1]);
				neighbours.add(theGrid[x+1][Y-1]);

				neighbours.add(theGrid[X-1][y]);
				neighbours.add(theGrid[X-1][Y-1]);
				neighbours.add(theGrid[X-1][y+1]);
							
			} else if (y == Y-1){
				neighbours.add(theGrid[x][0]);
				neighbours.add(theGrid[x][Y-1]);

				neighbours.add(theGrid[x+1][y]);
				neighbours.add(theGrid[x+1][0]);
				neighbours.add(theGrid[x+1][y-1]);	

				neighbours.add(theGrid[X-1][y]);
				neighbours.add(theGrid[X-1][y-1]);				
				neighbours.add(theGrid[X-1][0]);
				
			} else {
				neighbours.add(theGrid[x][y+1]);
				neighbours.add(theGrid[x][y-1]);

				neighbours.add(theGrid[x+1][y]);
				neighbours.add(theGrid[x+1][y+1]);
				neighbours.add(theGrid[x+1][y-1]);
				
				neighbours.add(theGrid[X-1][y]);
				neighbours.add(theGrid[X-1][y+1]);
				neighbours.add(theGrid[X-1][y-1]);
			}
		} else if (x == X-1){
			if (y == 0){
				neighbours.add(theGrid[x][y+1]);
				neighbours.add(theGrid[x][Y-1]);

				neighbours.add(theGrid[x-1][y]);
				neighbours.add(theGrid[x-1][y+1]);
				neighbours.add(theGrid[x-1][Y-1]);
				
				neighbours.add(theGrid[0][y]);
				neighbours.add(theGrid[0][y+1]);
				neighbours.add(theGrid[0][Y-1]);
			} else if (y == Y-1){
				neighbours.add(theGrid[x][0]);
				neighbours.add(theGrid[x][y-1]);

				neighbours.add(theGrid[x-1][y]);
				neighbours.add(theGrid[x-1][0]);
				neighbours.add(theGrid[x-1][y-1]);
				
				neighbours.add(theGrid[0][y]);
				neighbours.add(theGrid[0][0]);
				neighbours.add(theGrid[0][y-1]);

			} else {
				neighbours.add(theGrid[x][y+1]);
				neighbours.add(theGrid[x][y-1]);

				neighbours.add(theGrid[x-1][y]);
				neighbours.add(theGrid[x-1][y+1]);
				neighbours.add(theGrid[x-1][y-1]);
				
				neighbours.add(theGrid[0][y]);
				neighbours.add(theGrid[0][y+1]);
				neighbours.add(theGrid[0][y-1]);
			}
		} else {
			if (y == 0){
				neighbours.add(theGrid[x][y+1]);
				neighbours.add(theGrid[x][Y-1]);

				neighbours.add(theGrid[x-1][y]);
				neighbours.add(theGrid[x-1][y+1]);
				neighbours.add(theGrid[x-1][Y-1]);
				
				neighbours.add(theGrid[x+1][y]);
				neighbours.add(theGrid[x+1][y+1]);
				neighbours.add(theGrid[x+1][Y-1]);

			} else if (y == Y-1){
				neighbours.add(theGrid[x][0]);
				neighbours.add(theGrid[x][y-1]);

				neighbours.add(theGrid[x-1][y]);
				neighbours.add(theGrid[x-1][0]);
				neighbours.add(theGrid[x-1][y-1]);
				
				neighbours.add(theGrid[x+1][y]);
				neighbours.add(theGrid[x+1][0]);
				neighbours.add(theGrid[x+1][y-1]);
			} else {
				neighbours.add(theGrid[x][y-1]);
				neighbours.add(theGrid[x][y+1]);
				
				neighbours.add(theGrid[x-1][y-1]);
				neighbours.add(theGrid[x-1][y+1]);
				neighbours.add(theGrid[x-1][y]);
				
				neighbours.add(theGrid[x+1][y-1]);
				neighbours.add(theGrid[x+1][y+1]);
				neighbours.add(theGrid[x+1][y]);
			}
		}
		return neighbours;
	}

	public Entity getNeighbour_U(int x, int y){
		if (allowPhantoms){
			
			if (y == 0){				
				return phantoms_U[x];
			} else {
				return theGrid[x][y-1];
			}
		} else {
			if (y == 0){
				return theGrid[x][Y-1];
			} else {
				return theGrid[x][y-1];
			}	
		}
	}

	public Entity getNeighbour_UR(int x, int y){
		int gx = x + 1;
		int gy = y - 1;
		if (gx == X){
			gx = 0;
		}
		if (gy == -1){
			gy = Y - 1;
		}
		if (allowPhantoms){
			if (x + 1 == X && y - 1 == -1){
//				System.out.println("UR: "+ phantoms_UR[0]+"  |  "+grid[gx][gy]);
				return phantoms_UR[0];
				
			} else if (y - 1 == -1 && !(x + 1 == X)){
				return phantoms_U[x+1];
			} else if (x + 1 == X && !(y - 1 == -1)){
				return phantoms_R[y-1];
			} else {
				return theGrid[gx][gy];
			}
		} else {
			return theGrid[gx][gy];
		}
	}

	public Entity getNeighbour_R(int x, int y){
		
		if (allowPhantoms){
			if (x == X-1){
				return phantoms_R[y];
			} else {
				return theGrid[x+1][y];
			}
		} else { 
			if (x == X-1){			
				return theGrid[0][y];
			} else {
				return theGrid[x+1][y];
			}
		}		
	}

	public Entity getNeighbour_DR(int x, int y){
		int gx = x + 1;
		int gy = y + 1;
		if (gx == X){
			gx = 0;
		}
		if (gy == Y){
			gy = 0;
		}
		if (allowPhantoms){
			if (x + 1 == X && y+1 == Y){
//				System.out.println("DR: "+ phantoms_DR[0]+"  |  "+grid[gx][gy]);
				return phantoms_DR[0];
				
			}  else if (y + 1 == Y && !(x + 1 == X)){
				return phantoms_D[x+1];
			} else if (x + 1 == X && !(y + 1 == Y)){
				return phantoms_R[y+1];
			}  else {
				return theGrid[gx][gy];
			}
		} else{ 
			return theGrid[gx][gy];
		}
	}

	public Entity getNeighbour_D(int x, int y){
		if (allowPhantoms){
			if (y == Y - 1){
				return phantoms_D[x];
			} else {
				return theGrid[x][y+1];
			}
		} else {
			if (y == Y - 1){
				return theGrid[x][0];
			} else {
				return theGrid[x][y+1];
			}
		}
	}

	public Entity getNeighbour_DL(int x, int y){
		int gx = x - 1;
		int gy = y + 1;
		if (gx == -1){
			gx = X-1;
		}
		if (gy == Y){
			gy = 0;
		}
		if (allowPhantoms){
			if (x - 1 == -1 && y + 1 == Y){
//				System.out.println("DL: "+ phantoms_DL[0]+"  |  "+grid[gx][gy]);
				return phantoms_DL[0];
			} else if (y + 1 == Y && !(x - 1 == -1)){
				return phantoms_D[x-1];
			} else if (x - 1 == -1 && !(y + 1 == Y)){
				return phantoms_L[y+1];
			}  else {
				return theGrid[gx][gy];
			}
		} else {
			return theGrid[gx][gy];
		}
	}

	public Entity getNeighbour_L(int x, int y){
		if (allowPhantoms){
			if (x == 0){
//				System.out.println("pos y: "+y);
//				for (int i = 0; i < X; i++){
//					System.out.println(phantoms_L[i] + "  |  "+grid[X-1][i]);
//				}
//				System.out.println("orig: "+grid[0][y]);
//				System.out.println("");
				return phantoms_L[y];
			} else {
				return theGrid[x-1][y];
			}
		} else {		
			if (x == 0){
				return theGrid[X-1][y];
			} else {
				return theGrid[x-1][y];
			}
		}
	}

	public Entity getNeighbour_UL(int x, int y){
		int gx = x - 1;
		int gy = y - 1;
		if (gx == -1){
			gx = X-1;
		}
		if (gy == -1){
			gy = Y-1;
		}
		if (allowPhantoms){
			if (x - 1 == -1 && y - 1 == -1){
//				System.out.println("UL: "+ phantoms_UL[0]+"  |  "+grid[gx][gy]);
				return phantoms_UL[0];
			} else if (y - 1 == -1 && !(x - 1 == -1)){
				return phantoms_U[x-1];
			} else if (x - 1 == -1 && !(y - 1 == -1)){
				return phantoms_L[y-1];
			} else {
				return theGrid[gx][gy];
			}
		} else {
			return theGrid[gx][gy];
		}
	}

	String[][] toGridString(){
		String[][] cellAsString = new String[X][Y];
		for (int i = 0; i < X; i++){
			for (int j = 0; j < Y; j++){
				cellAsString[i][j] = theGrid[i][j].toString(); 
			}
		}
		return cellAsString;
	}
	
	
	//This is lazy and just for GoL
	public String toBitString(){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < X; i++){
			for (int j = 0; j < Y; j++){
				sb.append(theGrid[i][j].toString()); 
			}
		}
		return sb.toString();
	}
	
	//Oh god, this is horrible
	public HashMap<Vector2, String> toBitMapWithCoords(Vector2 offset){
		int xOffset = (int)offset.getX();
		int yOffset = (int)offset.getY();
		HashMap<Vector2, String> theMap = new HashMap<Vector2, String>();
		for (int i = 0; i < X; i++){
			for (int j = 0; j < Y; j++){
				theMap.put(new Vector2(i + xOffset, j + yOffset), theGrid[i][j].toString());
			}
		}
		return theMap;
	}
	
	//Phantom State stuff
	//TODO
	public void receiveStates(GridPositions placement, Entity[] stateVector){
		
	}				
	
	public void addPhantomCells(GridPositions placement, Entity[] stateVector){
		GridPositions oppo = getOpposite(placement);
//		System.out.println(placement.toString()+" opposite: "+oppo.toString());
		switch(oppo){
			case LEFT:
				phantoms_L = stateVector;				
				break;
			case RIGHT:
				phantoms_R = stateVector;
				break;
			case DOWN:
				phantoms_D = stateVector;
				break;
			case UP:
				phantoms_U = stateVector;
				break;
			case UPLEFT:
				phantoms_UL = stateVector;
				break;
			case UPRIGHT:
				phantoms_UR = stateVector;
				break;
			case DOWNLEFT:
				phantoms_DL = stateVector;
				break;
			case DOWNRIGHT:
				phantoms_DR = stateVector;
				break;
			default:
				System.out.println("FELL THROUGH");
				break;
		}
	}
	
	public void addPhantomCells(GridPositions gp, List<Entity> list){
		@SuppressWarnings("unchecked")
		Entity[] arr = (Entity[]) Array.newInstance(theClass, list.size());
		list.toArray(arr);
	}
	
	public void phantomCheck(){
		if (phantoms_L == null){
			System.out.println("phantoms_L is null");
		}
		if (phantoms_DL == null){
			System.out.println("phantoms_DL is null");
		}
		if (phantoms_U == null){
			System.out.println("phantoms_U is null");
		}
		if (phantoms_UL == null){
			System.out.println("phantoms_UL is null");
		}
		if (phantoms_D == null){
			System.out.println("phantoms_D is null");
		}
		if (phantoms_R == null){
			System.out.println("phantoms_R is null");
		}
		if (phantoms_DR == null){
			System.out.println("phantoms_DR is null");
		}
		if (phantoms_UR == null){
			System.out.println("phantoms_UR is null");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<castleComponents.Entity> getEntities() {
		return (List<castleComponents.Entity>) allContainedEntities;
	}

	@Override
	public boolean removeEntityByID(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean initializeEntity(Object... objects) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addEntity(castleComponents.Entity e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeEntity(castleComponents.Entity e) {
		// TODO Auto-generated method stub
		return false;
	}

	
	
}