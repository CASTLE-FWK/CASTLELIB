package castleComponents;

public class Enums {
	
	public enum GridPositions {
		LEFT,RIGHT, DOWN, UP, UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT, TOP, BOTTOM, TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT;
	}
	
	public static GridPositions getOpposite(GridPositions gp){
		switch(gp){
		case LEFT:
			return GridPositions.RIGHT;
		case RIGHT:
			return GridPositions.LEFT;
		case DOWN:
			return GridPositions.UP;
		case UP:
			return GridPositions.DOWN;	
		case UPLEFT:
			return GridPositions.DOWNRIGHT;
		case UPRIGHT:
			return GridPositions.DOWNLEFT;
		case DOWNLEFT:
			return GridPositions.UPRIGHT;
		case DOWNRIGHT:
			return GridPositions.UPLEFT;
		
		case TOP:
			return GridPositions.BOTTOM;
		case BOTTOM:
			return GridPositions.TOP;
		case TOPLEFT:
			return GridPositions.BOTTOMRIGHT;
		case TOPRIGHT:
			return GridPositions.BOTTOMLEFT;
		case BOTTOMLEFT:
			return GridPositions.TOPRIGHT;
		case BOTTOMRIGHT:
			return GridPositions.TOPLEFT;
		default:
			return null;
		}
	}
	
	public enum ContinuousDirections {
		
	}

	public enum RepresentationTypes{
		GRID, TORUS, BOUND, GRAPH, NETWORK, GIS, MESH3D, CONTINUOUS
	}
}
