package castleComponents.representations;

import static castleComponents.Enums.getOpposite;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import castleComponents.Enums.GridPositions;
import castleComponents.objects.Neighbors;
import castleComponents.objects.Vector2;
import castleComponents.representations.Map2D.MapComponent;

//TODO: How to get this to work with multiple Entity types
public class Grid<E> implements Representation<E> {
	int X;
	int Y;
	E[][] grid;
	LayoutParameters layoutParameters;

	boolean allowPhantoms = false;
	boolean wraps = true;
	E[] phantoms_U;
	E[] phantoms_UR;
	E[] phantoms_R;
	E[] phantoms_DR;
	E[] phantoms_D;
	E[] phantoms_DL;
	E[] phantoms_L;
	E[] phantoms_UL;

	List<E> allContainedEntities;

	Class<E> theClass;
	
	public void setWrap(boolean b) {
		wraps = b;
	}
	public boolean isWrapped() {
		return wraps;
	}

	public Grid(Class<E> c, int X, int Y) {
		if (X == 0) {
			X = 1;
		}
		if (Y == 0) {
			Y = 1;
		}
		this.X = X;
		this.Y = Y;
		this.theClass = c;
		// grid = new E[this.X][this.Y];
		@SuppressWarnings("unchecked")
		final E[][] grid = (E[][]) Array.newInstance(c, X, Y);
		this.grid = grid;

		allContainedEntities = new ArrayList<E>();
	}
	public boolean isOutOfBounds(Vector2 v) {
		int x = (int)v.getX();
		int y = (int)v.getY();
		return (x >= X || y >= Y || x < 0 || y < 0);
	}
	// TODO: Set phantom size
	public Grid() {
		allContainedEntities = new ArrayList<E>();
	}

	public E[][] getGrid() {
		return grid;
	}

	public void copy(Class<E> c, Grid<E> g, LayoutParameters lp) {
		layoutParameters = lp;
		X = g.getX();
		Y = g.getY();
		this.theClass = c;
		@SuppressWarnings("unchecked")
		final E[][] grid = (E[][]) Array.newInstance(c, X, Y);
		this.grid = grid;

		E[][] gGrid = g.getGrid();
		for (int i = 0; i < gGrid[0].length; i++) {
			for (int j = 0; j < gGrid.length; j++) {
				grid[j][i] = gGrid[j][i];
			}
		}
	}

	public void setEntityAtPos(Vector2 pos, E e) {
		int x = (int) pos.getX();
		int y = (int) pos.getY();
		grid[x][y] = e;
	}

	public E getEntityAtPos(Vector2 pos) {
		int x = (int) pos.getX();
		int y = (int) pos.getY();
		return getEntityAtXY(x, y);
	}

	public E getEntityAtXY(int x, int y) {
		return grid[x][y];
	}

	public void setPhantomState(boolean p) {
		allowPhantoms = p;
	}

	public Vector2 getDimensions() {
		return new Vector2(X, Y);
	}

	@SuppressWarnings("unchecked")
	public void init(Vector2 layoutXY, LayoutParameters layoutParameters) {
		this.X = (int) layoutXY.getX();
		this.Y = (int) layoutXY.getY();
		this.layoutParameters = layoutParameters;
		setPhantomState(this.layoutParameters.allowPhantoms());

		// Check for 0 sized dimensions and fix
		if (X == 0) {
			X = 1;
		}
		if (Y == 0) {
			Y = 1;
		}
		theClass = (Class<E>) this.layoutParameters.getEntityType();
		// Allow the grid to store Entities of the type specified in the layout
		// parameters
		final E[][] grid = (E[][]) Array.newInstance(theClass, X, Y);
		this.grid = grid;

		// System.out.println("size: "+getDimensions().toString());
		// System.out.println("GRID INIT FUNCTION CALLA");
	}

	@SuppressWarnings("unchecked")
	public void init(Vector2 layoutXY, Class<E> theClass) {
		this.X = (int) layoutXY.getX();
		this.Y = (int) layoutXY.getY();
		// setPhantomState(this.layoutParameters.allowPhantoms());

		// Check for 0 sized dimensions and fix
		if (X == 0) {
			X = 1;
		}
		if (Y == 0) {
			Y = 1;
		}
		// Allow the grid to store Entities of the type specified in the layout
		// parameters
		final E[][] grid = (E[][]) Array.newInstance(theClass, X, Y);
		this.grid = grid;

		// System.out.println("size: "+getDimensions().toString());
		// System.out.println("GRID INIT FUNCTION CALLB");
	}

	public void initializeAllCells(E e) {
		E[][] gGrid = getGrid();
		for (int i = 0; i < gGrid[0].length; i++) {
			for (int j = 0; j < gGrid.length; j++) {
				grid[j][i] = e;
			}
		}
	}

	public void place() {
		
		//TODO FIX
		
		// System.out.println("GRID PLACE FUNCTION CALL");
		// Create the Cell with only Entity instantiation
		// Figure out the class
		// theClass = (Class<E>) this.layoutParameters.getEntityType();
		// int count = 0;
		// for (int i = 0; i < X; i++){
		// for (int j = 0; j < Y; j++){
		// //Create some EntityIDs
		// EntityID eid = new EntityID(theClass.getCanonicalName()+"_"+i+"_"+j, count);
		// E ent = (E) theClass.newInstance();
		//
		// }
		// }

	}

	public void send(GridPositions location, Neighbors<E> neighbors, String function) {
		System.out.println("GRID SEND FUNCTION CALL");
	}

	// public Neighbors<E> getAllNeighborsFromPosition(GridPositions gp){
	// Neighbors<E> neigh = new Neighbors<E>();
	//// neigh.setD(getNeighbour_D(x, y));
	// E[] tmpArray = getAllAsArray(gp);
	// switch(gp){
	// case LEFT:
	// neigh.setL(tmpArray);
	// return neigh;
	// case RIGHT:
	// neigh.setR(tmpArray);
	// return neigh;
	// case BOTTOM:
	// neigh.setD(tmpArray);
	// return neigh;
	// case TOP:
	// neigh.setU(tmpArray);
	// return neigh;
	// case TOPLEFT:
	// neigh.setUL(tmpArray);
	// return neigh;
	// case TOPRIGHT:
	// neigh.setUR(tmpArray);
	// return neigh;
	// case BOTTOMLEFT:
	// neigh.setDL(tmpArray);
	// return neigh;
	// case BOTTOMRIGHT:
	// neigh.setDR(tmpArray);
	// return neigh;
	// }
	// return null;
	// }

	public List<E> getAll() {
		ArrayList<E> list = new ArrayList<E>();
		E[][] gGrid = getGrid();
		for (int i = 0; i < gGrid[0].length; i++) {
			for (int j = 0; j < gGrid.length; j++) {
				list.add(grid[j][i]);
			}
		}
		return list;
		
	}
	
	public List<E> getAll(GridPositions gp) {
		E[] arr = getAllAsArray(gp);
		ArrayList<E> list = new ArrayList<E>();
		for (E e : arr) {
			list.add(e);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public E[] getAllAsArray(GridPositions gp) {
		switch (gp) {
		case LEFT:
			// y is 0
			final E[] neighborsl = (E[]) Array.newInstance(theClass, X);
			for (int i = 0; i < X; i++) {
				neighborsl[i] = grid[0][i];
			}
			return neighborsl;
		case RIGHT:
			// y is Y-1
			final E[] neighborsr = (E[]) Array.newInstance(theClass, X);
			for (int i = 0; i < X; i++) {
				neighborsr[i] = grid[X - 1][i];
			}
			return neighborsr;
		case BOTTOM:
			// x is X-1
			final E[] neighborsd = (E[]) Array.newInstance(theClass, Y);
			for (int i = 0; i < Y; i++) {
				// neighborsd[i] = grid[X-1][i];
				neighborsd[i] = grid[i][X - 1];
			}
			return neighborsd;
		case TOP:
			// x is 0
			final E[] neighborsu = (E[]) Array.newInstance(theClass, Y);
			for (int i = 0; i < Y; i++) {
				// neighborsu[i] = grid[0][i];
				neighborsu[i] = grid[i][0];
			}
			return neighborsu;
		case TOPLEFT:
			// x is 0, y is 0
			final E[] neighborsul = (E[]) Array.newInstance(theClass, 1);
			neighborsul[0] = grid[0][0];
			return neighborsul;
		case TOPRIGHT:
			// x is 0, y is Y-1
			final E[] neighborsur = (E[]) Array.newInstance(theClass, 1);
			// neighborsur[0] = grid[0][Y-1];
			neighborsur[0] = grid[X - 1][0];
			return neighborsur;
		case BOTTOMLEFT:
			// x is X-1, y is 0
			final E[] neighborsdl = (E[]) Array.newInstance(theClass, 1);
			// neighborsdl[0] = grid[X-1][0];
			neighborsdl[0] = grid[0][Y - 1];
			return neighborsdl;
		case BOTTOMRIGHT:
			// x is X-1, y is Y-1
			final E[] neighborsdr = (E[]) Array.newInstance(theClass, 1);
			neighborsdr[0] = grid[X - 1][Y - 1];
			return neighborsdr;
		default:
			return null;
		}
	}

	public boolean addCell(E c, int x, int y) {
		grid[x][y] = c;
		return allContainedEntities.add(c);
	}

	public boolean addCell(E c, Vector2 vec) {
		if (grid == null) {
			System.out.println("GRID IS NULL");
		}
		grid[(int) vec.getX()][(int) vec.getY()] = c;
		// System.out.println("ll: " + (grid[(int)vec.getX()][(int)vec.getY()] ==
		// null));
		// System.out.println("lk: "+ (getEntityAtPos(vec) == null));
		return allContainedEntities.add(c);
	}

	public int getX() {
		return X;
	}

	public int getY() {
		return Y;
	}

	public Neighbors<E> getNeighborsFromVectorWithContext(Vector2 v, int depth) {
		Neighbors<E> nei = new Neighbors<E>();
		int x = (int) v.getX();
		int y = (int) v.getY();
		nei.setU(getNeighbour_U(x, y));
		nei.setD(getNeighbour_D(x, y));
		nei.setDL(getNeighbour_DL(x, y));
		nei.setDR(getNeighbour_DR(x, y));
		nei.setL(getNeighbour_L(x, y));
		nei.setR(getNeighbour_R(x, y));
		nei.setUL(getNeighbour_UL(x, y));
		nei.setUR(getNeighbour_UR(x, y));
		return nei;
	}

	// This is great and fast, but we lose all perspective of where the neighbors
	// are
	public List<E> getNeighboursFromVector(Vector2 v, int depth) {
		return getNeighbours((int) v.getX(), (int) v.getY(), depth);
	}

	public List<E> getNeighbours(int x, int y, int depth) {
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

	public E getNeighbour_U(int x, int y) {
		if (allowPhantoms) {
			if (y == 0) {
				return phantoms_U[x];
			} else {
				return grid[x][y - 1];
			}
		} else {
			if (y >= Y || y < 0 || x >= X || x < 0) {
				return null;
			}
			
			if (y == 0) {
				if (wraps)
					return grid[x][Y - 1];
				else
					return null;
			} else {
				return grid[x][y - 1];
			}
		}
	}

	public E getNeighbour_UR(int x, int y) {
		int gx = x + 1;
		int gy = y - 1;
		if (gx == X) {
			gx = 0;
			if (!wraps) {
				return null;
			}
		}
		if (gy == -1) {
			gy = Y - 1;
			if (!wraps) {
				return null;
			}
		}
		if (y >= Y || y < 0 || x >= X || x < 0) {
			return null;
		}

		if (allowPhantoms) {
			if (x + 1 == X && y - 1 == -1) {
				// System.out.println("UR: "+ phantoms_UR[0]+" | "+grid[gx][gy]);
				return phantoms_UR[0];

			} else if (y - 1 == -1 && !(x + 1 == X)) {
				return phantoms_U[x + 1];
			} else if (x + 1 == X && !(y - 1 == -1)) {
				return phantoms_R[y - 1];
			} else {
				return grid[gx][gy];
			}
		} else {
			return grid[gx][gy];
		}
	}

	public E getNeighbour_R(int x, int y) {

		if (allowPhantoms) {
			if (x == X - 1) {
				return phantoms_R[y];
			} else {
				return grid[x + 1][y];
			}
		} else {
			if (y >= Y || y < 0 || x >= X || x < 0) {
				return null;
			}
			if (x == X - 1) {
				if (wraps)
					return grid[0][y];
				else 
					return null;
			} else {
				return grid[x + 1][y];
			}
		}
	}

	public E getNeighbour_DR(int x, int y) {
		int gx = x + 1;
		int gy = y + 1;
		if (gx == X) {
			gx = 0;
			if (!wraps) {
				return null;
			}
		}
		if (gy == Y) {
			gy = 0;
			if (!wraps) {
				return null;
			}
		}
		if (y >= Y || y < 0 || x >= X || x < 0) {
			return null;
		}
		if (allowPhantoms) {
			if (x + 1 == X && y + 1 == Y) {
				// System.out.println("DR: "+ phantoms_DR[0]+" | "+grid[gx][gy]);
				return phantoms_DR[0];

			} else if (y + 1 == Y && !(x + 1 == X)) {
				return phantoms_D[x + 1];
			} else if (x + 1 == X && !(y + 1 == Y)) {
				return phantoms_R[y + 1];
			} else {
				return grid[gx][gy];
			}
		} else {
			return grid[gx][gy];
		}
	}

	public E getNeighbour_D(int x, int y) {
		if (allowPhantoms) {
			if (y == Y - 1) {
				return phantoms_D[x];
			} else {
				return grid[x][y + 1];
			}
		} else {
			if (y >= Y || y < 0 || x >= X || x < 0) {
				return null;
			}
			if (y == Y - 1) {
				if (!wraps) {
					return null;
				}
				return grid[x][0];
			} else {
				return grid[x][y + 1];
			}
		}
	}

	public E getNeighbour_DL(int x, int y) {
		int gx = x - 1;
		int gy = y + 1;
		if (gx == -1) {
			gx = X - 1;
			if (!wraps) {
				return null;
			}
		}
		if (gy == Y) {
			gy = 0;
			if (!wraps) {
				return null;
			}
		}
		if (y >= Y || y < 0 || x >= X || x < 0) {
			return null;
		}
		if (allowPhantoms) {
			if (x - 1 == -1 && y + 1 == Y) {
				// System.out.println("DL: "+ phantoms_DL[0]+" | "+grid[gx][gy]);
				return phantoms_DL[0];
			} else if (y + 1 == Y && !(x - 1 == -1)) {
				return phantoms_D[x - 1];
			} else if (x - 1 == -1 && !(y + 1 == Y)) {
				return phantoms_L[y + 1];
			} else {
				return grid[gx][gy];
			}
		} else {
			return grid[gx][gy];
		}
	}

	public E getNeighbour_L(int x, int y) {
		if (allowPhantoms) {
			if (x == 0) {
				return phantoms_L[y];
			} else {
				return grid[x - 1][y];
			}
		} else {
			if (y >= Y || y < 0 || x >= X || x < 0) {
				return null;
			}
			if (x == 0) {
				if (!wraps) {
					return null;
				}
				return grid[X - 1][y];
			} else {
				return grid[x - 1][y];
			}
		}
	}

	public E getNeighbour_UL(int x, int y) {
		int gx = x - 1;
		int gy = y - 1;
		if (gx == -1) {
			gx = X - 1;
			if (!wraps) {
				return null;
			}
		}
		if (gy == -1) {
			gy = Y - 1;
			if (!wraps) {
				return null;
			}
		}
		if (y >= Y || y < 0 || x >= X || x < 0) {
			return null;
		}
		if (allowPhantoms) {
			if (x - 1 == -1 && y - 1 == -1) {
				// System.out.println("UL: "+ phantoms_UL[0]+" | "+grid[gx][gy]);
				return phantoms_UL[0];
			} else if (y - 1 == -1 && !(x - 1 == -1)) {
				return phantoms_U[x - 1];
			} else if (x - 1 == -1 && !(y - 1 == -1)) {
				return phantoms_L[y - 1];
			} else {
				return grid[gx][gy];
			}
		} else {
			return grid[gx][gy];
		}
	}

	String[][] toGridString() {
		String[][] cellAsString = new String[X][Y];
		for (int i = 0; i < X; i++) {
			for (int j = 0; j < Y; j++) {
				cellAsString[i][j] = grid[i][j].toString();
			}
		}
		return cellAsString;
	}

	// This is lazy and just for GoL
	public String toBitString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < X; i++) {
			for (int j = 0; j < Y; j++) {
				sb.append(grid[i][j].toString());
			}
		}
		return sb.toString();
	}

	// Oh god, this is horrible
	public HashMap<Vector2, String> toBitMapWithCoords(Vector2 offset) {
		int xOffset = (int) offset.getX();
		int yOffset = (int) offset.getY();
		HashMap<Vector2, String> theMap = new HashMap<Vector2, String>();
		for (int i = 0; i < X; i++) {
			for (int j = 0; j < Y; j++) {
				theMap.put(new Vector2(i + xOffset, j + yOffset), grid[i][j].toString());
			}
		}
		return theMap;
	}

	// Phantom State stuff
	// TODO
	public void receiveStates(GridPositions placement, E[] stateVector) {

	}

	public void addPhantomCells(GridPositions placement, E[] stateVector) {
		GridPositions oppo = getOpposite(placement);
		// System.out.println(placement.toString()+" opposite: "+oppo.toString());
		switch (oppo) {
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

	public void addPhantomCells(GridPositions gp, List<E> list) {
		@SuppressWarnings("unchecked")
		E[] arr = (E[]) Array.newInstance(theClass, list.size());
		list.toArray(arr);
	}

	public void phantomCheck() {
		if (phantoms_L == null) {
			System.out.println("phantoms_L is null");
		}
		if (phantoms_DL == null) {
			System.out.println("phantoms_DL is null");
		}
		if (phantoms_U == null) {
			System.out.println("phantoms_U is null");
		}
		if (phantoms_UL == null) {
			System.out.println("phantoms_UL is null");
		}
		if (phantoms_D == null) {
			System.out.println("phantoms_D is null");
		}
		if (phantoms_R == null) {
			System.out.println("phantoms_R is null");
		}
		if (phantoms_DR == null) {
			System.out.println("phantoms_DR is null");
		}
		if (phantoms_UR == null) {
			System.out.println("phantoms_UR is null");
		}
	}

	@Override
	public List<E> getEntities() {
		List<E> entities = new ArrayList<E>();
		for (int i = 0; i < X; i++) {
			for (int j = 0; j < Y; j++) {
				entities.add(getEntityAtXY(i, j));
			}
		}

		return (List<E>) entities;
	}

	@Override
	public boolean addEntity(E e, Vector2 p) {
		return addCell(e, p);
	}

	@Override
	public boolean addEntities(List<E> es) {
		return allContainedEntities.addAll(es);
	}

	@Override
	public boolean removeEntity(E e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeEntityByID(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean initialize(Object... objects) {
		// Needs to be 2 objects
		if (objects.length != 2) {
			return false;
		}
		Vector2 layoutXY = null;
		LayoutParameters layoutParameters = null;
		if (objects[0] instanceof Vector2) {
			layoutXY = (Vector2) objects[0];
			if (objects[1] instanceof LayoutParameters) {
				layoutParameters = (LayoutParameters) objects[1];
			}
		} else if (objects[1] instanceof Vector2) {
			layoutXY = (Vector2) objects[1];
			if (objects[0] instanceof LayoutParameters) {
				layoutParameters = (LayoutParameters) objects[0];
			}
		}

		if (layoutXY == null || layoutParameters == null) {
			return false;
		}
		init(layoutXY, layoutParameters);
		return true;
	}

	@Override
	public boolean initializeEntity(Object... objects) {
		return false;
	}

	@Override
	public boolean initializeEntities(Object... objects) {
		// TODO Auto-generated method stub
		return false;
	}

	public List<E> getNeighbours_Old(int x, int y, int depth) {
		ArrayList<E> neighbours = new ArrayList<E>();

		// Edge cases??
		if (x == 0) {
			if (y == 0) {
				neighbours.add(grid[x][y + 1]);
				neighbours.add(grid[x][Y - 1]);

				neighbours.add(grid[x + 1][y]);
				neighbours.add(grid[x + 1][y + 1]);
				neighbours.add(grid[x + 1][Y - 1]);

				neighbours.add(grid[X - 1][y]);
				neighbours.add(grid[X - 1][Y - 1]);
				neighbours.add(grid[X - 1][y + 1]);

			} else if (y == Y - 1) {
				neighbours.add(grid[x][0]);
				neighbours.add(grid[x][Y - 1]);

				neighbours.add(grid[x + 1][y]);
				neighbours.add(grid[x + 1][0]);
				neighbours.add(grid[x + 1][y - 1]);

				neighbours.add(grid[X - 1][y]);
				neighbours.add(grid[X - 1][y - 1]);
				neighbours.add(grid[X - 1][0]);

			} else {
				neighbours.add(grid[x][y + 1]);
				neighbours.add(grid[x][y - 1]);

				neighbours.add(grid[x + 1][y]);
				neighbours.add(grid[x + 1][y + 1]);
				neighbours.add(grid[x + 1][y - 1]);

				neighbours.add(grid[X - 1][y]);
				neighbours.add(grid[X - 1][y + 1]);
				neighbours.add(grid[X - 1][y - 1]);
			}
		} else if (x == X - 1) {
			if (y == 0) {
				neighbours.add(grid[x][y + 1]);
				neighbours.add(grid[x][Y - 1]);

				neighbours.add(grid[x - 1][y]);
				neighbours.add(grid[x - 1][y + 1]);
				neighbours.add(grid[x - 1][Y - 1]);

				neighbours.add(grid[0][y]);
				neighbours.add(grid[0][y + 1]);
				neighbours.add(grid[0][Y - 1]);
			} else if (y == Y - 1) {
				neighbours.add(grid[x][0]);
				neighbours.add(grid[x][y - 1]);

				neighbours.add(grid[x - 1][y]);
				neighbours.add(grid[x - 1][0]);
				neighbours.add(grid[x - 1][y - 1]);

				neighbours.add(grid[0][y]);
				neighbours.add(grid[0][0]);
				neighbours.add(grid[0][y - 1]);

			} else {
				neighbours.add(grid[x][y + 1]);
				neighbours.add(grid[x][y - 1]);

				neighbours.add(grid[x - 1][y]);
				neighbours.add(grid[x - 1][y + 1]);
				neighbours.add(grid[x - 1][y - 1]);

				neighbours.add(grid[0][y]);
				neighbours.add(grid[0][y + 1]);
				neighbours.add(grid[0][y - 1]);
			}
		} else {
			if (y == 0) {
				neighbours.add(grid[x][y + 1]);
				neighbours.add(grid[x][Y - 1]);

				neighbours.add(grid[x - 1][y]);
				neighbours.add(grid[x - 1][y + 1]);
				neighbours.add(grid[x - 1][Y - 1]);

				neighbours.add(grid[x + 1][y]);
				neighbours.add(grid[x + 1][y + 1]);
				neighbours.add(grid[x + 1][Y - 1]);

			} else if (y == Y - 1) {
				neighbours.add(grid[x][0]);
				neighbours.add(grid[x][y - 1]);

				neighbours.add(grid[x - 1][y]);
				neighbours.add(grid[x - 1][0]);
				neighbours.add(grid[x - 1][y - 1]);

				neighbours.add(grid[x + 1][y]);
				neighbours.add(grid[x + 1][0]);
				neighbours.add(grid[x + 1][y - 1]);
			} else {
				neighbours.add(grid[x][y - 1]);
				neighbours.add(grid[x][y + 1]);

				neighbours.add(grid[x - 1][y - 1]);
				neighbours.add(grid[x - 1][y + 1]);
				neighbours.add(grid[x - 1][y]);

				neighbours.add(grid[x + 1][y - 1]);
				neighbours.add(grid[x + 1][y + 1]);
				neighbours.add(grid[x + 1][y]);
			}
		}
		return neighbours;
	}

}