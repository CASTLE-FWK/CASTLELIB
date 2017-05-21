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
public class Grid<E> implements Representation{
	int X;
	int Y;
	E[][] grid;
	LayoutParameters layoutParameters;
	
	boolean allowPhantoms = false;
	E[] phantoms_U;
	E[] phantoms_UR;
	E[] phantoms_R;
	E[] phantoms_DR;
	E[] phantoms_D;
	E[] phantoms_DL;
	E[] phantoms_L;
	E[] phantoms_UL;
	
	List<E> allContainedEntities;
	
//	Neighbors<E> phantoms_U;
//	Neighbors<E> phantoms_UR;
//	Neighbors<E> phantoms_R;
//	Neighbors<E> phantoms_DR;
//	Neighbors<E> phantoms_D;
//	Neighbors<E> phantoms_DL;
//	Neighbors<E> phantoms_L;
//	Neighbors<E> phantoms_UL;	
	
	Class<E> theClass;
	public Grid(Class<E> c, int X, int Y){
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
		final E[][] grid = (E[][]) Array.newInstance(c, X,Y);		
		this.grid = grid;
		
		allContainedEntities = new ArrayList<E>();
	}
	
	//TODO: Set phantom size
	public Grid(){
		
	}
	
	public E[][] getGrid(){
		return grid;
	}
	
	public E getEntityAtXY(int x, int y){
		return grid[x][y];
	}
	public void setPhantomState(boolean p){
		allowPhantoms = p;
	}
	
	@SuppressWarnings("unchecked")
	public void init(Vector2 layoutXY, LayoutParameters layoutParameters){
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
		theClass = (Class<E>) this.layoutParameters.getEntityType();
		//Allow the grid to store Entities of the type specified in the layout parameters
		final E[][] grid = (E[][]) Array.newInstance(theClass, X,Y);		
		this.grid = grid;
		
		
//		System.out.println("GRID INIT FUNCTION CALL");
	}
	
	public void initCells(Object... objs){
		
	}
	
	public void place() {
//		System.out.println("GRID PLACE FUNCTION CALL");
		//Create the Cell with only Entity instantiation
		//Figure out the class
//		theClass = (Class<E>) this.layoutParameters.getEntityType();
//		int count = 0;
//		for (int i = 0; i < X; i++){
//			for (int j = 0; j < Y; j++){
//				//Create some EntityIDs
//				EntityID eid = new EntityID(theClass.getCanonicalName()+"_"+i+"_"+j, count);
//				E ent = (E) theClass.newInstance();
//				
//			}
//		}
		
	}
	
	public void send(GridPositions location, Neighbors<E> neighbors, String function){
		System.out.println("GRID SEND FUNCTION CALL");
	}
	
//	public Neighbors<E> getAllNeighborsFromPosition(GridPositions gp){
//		Neighbors<E> neigh = new Neighbors<E>();
////		neigh.setD(getNeighbour_D(x, y));
//		E[] tmpArray = getAllAsArray(gp);
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
	
	public List<E> getAll(GridPositions gp){
		E[] arr = getAllAsArray(gp);
		ArrayList<E> list = new ArrayList<E>();
		for (E e : arr){
			list.add(e);
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public E[] getAllAsArray(GridPositions gp){
		switch(gp){
		case LEFT:
			//y is 0
			final E[] neighborsl = (E[]) Array.newInstance(theClass, X);
			for (int i = 0; i < X; i++){
				neighborsl[i] = grid[0][i];
			}
			return neighborsl;
		case RIGHT:
			//y is Y-1
			final E[] neighborsr = (E[]) Array.newInstance(theClass, X);
			for (int i = 0; i < X; i++){
				neighborsr[i] = grid[X-1][i];
			}
			return neighborsr;
		case BOTTOM:
			//x is X-1
			final E[] neighborsd = (E[]) Array.newInstance(theClass, Y);
			for (int i = 0; i < Y; i++){
//				neighborsd[i] = grid[X-1][i];
				neighborsd[i] = grid[i][X-1];
			}
			return neighborsd;
		case TOP:
			//x is 0
			final E[] neighborsu = (E[]) Array.newInstance(theClass, Y);
			for (int i = 0; i < Y; i++){
//				neighborsu[i] = grid[0][i];
				neighborsu[i] = grid[i][0];
			}
			return neighborsu;
		case TOPLEFT:
			//x is 0, y is 0
			final E[] neighborsul = (E[]) Array.newInstance(theClass, 1);
			neighborsul[0] = grid[0][0];
			return neighborsul;
		case TOPRIGHT:
			//x is 0, y is Y-1
			final E[] neighborsur = (E[]) Array.newInstance(theClass, 1);
//			neighborsur[0] = grid[0][Y-1];
			neighborsur[0] = grid[X-1][0];
			return neighborsur;
		case BOTTOMLEFT:
			//x is X-1, y is 0
			final E[] neighborsdl = (E[]) Array.newInstance(theClass, 1);
//			neighborsdl[0] = grid[X-1][0];
			neighborsdl[0] = grid[0][Y-1];
			return neighborsdl;
		case BOTTOMRIGHT:
			//x is X-1, y is Y-1
			final E[] neighborsdr = (E[]) Array.newInstance(theClass, 1);
			neighborsdr[0] = grid[X-1][Y-1];
			return neighborsdr;
		default:
			return null;
		}
	}

	public void addCell(E c, int x, int y){
		grid[x][y] = c;
		allContainedEntities.add(c);
	}
	public void addCell(E c, Vector2 vec){
		grid[(int)vec.getX()][(int)vec.getY()] = c;
	}
	
	public int getX(){
		return X;
	}
	
	public int getY(){
		return Y;
	}

	public List<E> getNeighboursFromVector(Vector2 v, int depth){
		return getNeighbours((int)v.getX(), (int)v.getY(), depth);
	}
	
	public List<E> getNeighbours(int x, int y, int depth){
		ArrayList<E> neighbours = new ArrayList<E>();
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
	
	public List<E> getNeighbours_Old(int x, int y, int depth){
		ArrayList<E> neighbours = new ArrayList<E>();

		//Edge cases??
		if (x == 0){
			if (y == 0){
				neighbours.add(grid[x][y+1]);
				neighbours.add(grid[x][Y-1]);

				neighbours.add(grid[x+1][y]);
				neighbours.add(grid[x+1][y+1]);
				neighbours.add(grid[x+1][Y-1]);

				neighbours.add(grid[X-1][y]);
				neighbours.add(grid[X-1][Y-1]);
				neighbours.add(grid[X-1][y+1]);
							
			} else if (y == Y-1){
				neighbours.add(grid[x][0]);
				neighbours.add(grid[x][Y-1]);

				neighbours.add(grid[x+1][y]);
				neighbours.add(grid[x+1][0]);
				neighbours.add(grid[x+1][y-1]);	

				neighbours.add(grid[X-1][y]);
				neighbours.add(grid[X-1][y-1]);				
				neighbours.add(grid[X-1][0]);
				
			} else {
				neighbours.add(grid[x][y+1]);
				neighbours.add(grid[x][y-1]);

				neighbours.add(grid[x+1][y]);
				neighbours.add(grid[x+1][y+1]);
				neighbours.add(grid[x+1][y-1]);
				
				neighbours.add(grid[X-1][y]);
				neighbours.add(grid[X-1][y+1]);
				neighbours.add(grid[X-1][y-1]);
			}
		} else if (x == X-1){
			if (y == 0){
				neighbours.add(grid[x][y+1]);
				neighbours.add(grid[x][Y-1]);

				neighbours.add(grid[x-1][y]);
				neighbours.add(grid[x-1][y+1]);
				neighbours.add(grid[x-1][Y-1]);
				
				neighbours.add(grid[0][y]);
				neighbours.add(grid[0][y+1]);
				neighbours.add(grid[0][Y-1]);
			} else if (y == Y-1){
				neighbours.add(grid[x][0]);
				neighbours.add(grid[x][y-1]);

				neighbours.add(grid[x-1][y]);
				neighbours.add(grid[x-1][0]);
				neighbours.add(grid[x-1][y-1]);
				
				neighbours.add(grid[0][y]);
				neighbours.add(grid[0][0]);
				neighbours.add(grid[0][y-1]);

			} else {
				neighbours.add(grid[x][y+1]);
				neighbours.add(grid[x][y-1]);

				neighbours.add(grid[x-1][y]);
				neighbours.add(grid[x-1][y+1]);
				neighbours.add(grid[x-1][y-1]);
				
				neighbours.add(grid[0][y]);
				neighbours.add(grid[0][y+1]);
				neighbours.add(grid[0][y-1]);
			}
		} else {
			if (y == 0){
				neighbours.add(grid[x][y+1]);
				neighbours.add(grid[x][Y-1]);

				neighbours.add(grid[x-1][y]);
				neighbours.add(grid[x-1][y+1]);
				neighbours.add(grid[x-1][Y-1]);
				
				neighbours.add(grid[x+1][y]);
				neighbours.add(grid[x+1][y+1]);
				neighbours.add(grid[x+1][Y-1]);

			} else if (y == Y-1){
				neighbours.add(grid[x][0]);
				neighbours.add(grid[x][y-1]);

				neighbours.add(grid[x-1][y]);
				neighbours.add(grid[x-1][0]);
				neighbours.add(grid[x-1][y-1]);
				
				neighbours.add(grid[x+1][y]);
				neighbours.add(grid[x+1][0]);
				neighbours.add(grid[x+1][y-1]);
			} else {
				neighbours.add(grid[x][y-1]);
				neighbours.add(grid[x][y+1]);
				
				neighbours.add(grid[x-1][y-1]);
				neighbours.add(grid[x-1][y+1]);
				neighbours.add(grid[x-1][y]);
				
				neighbours.add(grid[x+1][y-1]);
				neighbours.add(grid[x+1][y+1]);
				neighbours.add(grid[x+1][y]);
			}
		}
		return neighbours;
	}

	public E getNeighbour_U(int x, int y){
		if (allowPhantoms){
			
			if (y == 0){				
				return phantoms_U[x];
			} else {
				return grid[x][y-1];
			}
		} else {
			if (y == 0){
				return grid[x][Y-1];
			} else {
				return grid[x][y-1];
			}	
		}
	}

	public E getNeighbour_UR(int x, int y){
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
				return grid[gx][gy];
			}
		} else {
			return grid[gx][gy];
		}
	}

	public E getNeighbour_R(int x, int y){
		
		if (allowPhantoms){
			if (x == X-1){
				return phantoms_R[y];
			} else {
				return grid[x+1][y];
			}
		} else { 
			if (x == X-1){			
				return grid[0][y];
			} else {
				return grid[x+1][y];
			}
		}		
	}

	public E getNeighbour_DR(int x, int y){
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
				return grid[gx][gy];
			}
		} else{ 
			return grid[gx][gy];
		}
	}

	public E getNeighbour_D(int x, int y){
		if (allowPhantoms){
			if (y == Y - 1){
				return phantoms_D[x];
			} else {
				return grid[x][y+1];
			}
		} else {
			if (y == Y - 1){
				return grid[x][0];
			} else {
				return grid[x][y+1];
			}
		}
	}

	public E getNeighbour_DL(int x, int y){
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
				return grid[gx][gy];
			}
		} else {
			return grid[gx][gy];
		}
	}

	public E getNeighbour_L(int x, int y){
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
				return grid[x-1][y];
			}
		} else {		
			if (x == 0){
				return grid[X-1][y];
			} else {
				return grid[x-1][y];
			}
		}
	}

	public E getNeighbour_UL(int x, int y){
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
				return grid[gx][gy];
			}
		} else {
			return grid[gx][gy];
		}
	}

	String[][] toGridString(){
		String[][] cellAsString = new String[X][Y];
		for (int i = 0; i < X; i++){
			for (int j = 0; j < Y; j++){
				cellAsString[i][j] = grid[i][j].toString(); 
			}
		}
		return cellAsString;
	}
	
	
	//This is lazy and just for GoL
	public String toBitString(){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < X; i++){
			for (int j = 0; j < Y; j++){
				sb.append(grid[i][j].toString()); 
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
				theMap.put(new Vector2(i + xOffset, j + yOffset), grid[i][j].toString());
			}
		}
		return theMap;
	}
	
	//Phantom State stuff
	//TODO
	public void receiveStates(GridPositions placement, E[] stateVector){
		
	}				
	
	public void addPhantomCells(GridPositions placement, E[] stateVector){
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

	public void _addPhantomCells(GridPositions gp, Neighbors<E> neighbors) {
//		GridPositions oppo = getOpposite(gp);
//		switch(oppo){
//		case LEFT:
//			phantoms_L = neighbors;
//			break;
//		case RIGHT:
//			phantoms_R = neighbors;
//			break;
//		case DOWN:
//			phantoms_D = neighbors;
//			break;
//		case UP:
//			phantoms_U = neighbors;
//			break;
//		case UPLEFT:
//			phantoms_UL = neighbors;
//			break;
//		case UPRIGHT:
//			phantoms_UR = neighbors;
//			break;
//		case DOWNLEFT:
//			phantoms_DL = neighbors;
//			break;
//		case DOWNRIGHT:
//			phantoms_DR = neighbors;
//			break;
//		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Entity> getEntities() {
		return (List<Entity>) allContainedEntities;
	}
	
}