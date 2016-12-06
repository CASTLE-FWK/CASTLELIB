package repastGroups;

import interLib.Vector2;

import java.util.ArrayList;

public class CapsuleGrid{
	int X;
	int Y;
	Capsule[][] capsuleGrid;
	public CapsuleGrid(int X, int Y){
		if (X == 0){
			X = 1;
		} 
		if (Y == 0){
			Y = 1;
		}
		this.X = X;
		this.Y = Y;
		capsuleGrid = new Capsule[this.X][this.Y];
	}

	public void addCell(Capsule c, int x, int y){
		capsuleGrid[x][y] = c;
	}
	public void addCell(Capsule c, Vector2 vec){
		capsuleGrid[(int)vec.getX()][(int)vec.getY()] = c;
	}
	
	public int getX(){
		return X;
	}
	
	public int getY(){
		return Y;
	}

	ArrayList<Capsule> getNeighbours(int x, int y){
		ArrayList<Capsule> neighbours = new ArrayList<Capsule>();

		//Edge cases??
		if (x == 0){
			if (y == 0){
				neighbours.add(capsuleGrid[x][y+1]);
				neighbours.add(capsuleGrid[x][Y-1]);

				neighbours.add(capsuleGrid[x+1][y]);
				neighbours.add(capsuleGrid[x+1][y+1]);
				neighbours.add(capsuleGrid[x+1][Y-1]);

				neighbours.add(capsuleGrid[X-1][y]);
				neighbours.add(capsuleGrid[X-1][Y-1]);
				neighbours.add(capsuleGrid[X-1][y+1]);
							
			} else if (y == Y-1){
				neighbours.add(capsuleGrid[x][0]);
				neighbours.add(capsuleGrid[x][Y-1]);

				neighbours.add(capsuleGrid[x+1][y]);
				neighbours.add(capsuleGrid[x+1][0]);
				neighbours.add(capsuleGrid[x+1][y-1]);	

				neighbours.add(capsuleGrid[X-1][y]);
				neighbours.add(capsuleGrid[X-1][y-1]);				
				neighbours.add(capsuleGrid[X-1][0]);
				
			} else {
				neighbours.add(capsuleGrid[x][y+1]);
				neighbours.add(capsuleGrid[x][y-1]);

				neighbours.add(capsuleGrid[x+1][y]);
				neighbours.add(capsuleGrid[x+1][y+1]);
				neighbours.add(capsuleGrid[x+1][y-1]);
				
				neighbours.add(capsuleGrid[X-1][y]);
				neighbours.add(capsuleGrid[X-1][y+1]);
				neighbours.add(capsuleGrid[X-1][y-1]);
			}
		} else if (x == X-1){
			if (y == 0){
				neighbours.add(capsuleGrid[x][y+1]);
				neighbours.add(capsuleGrid[x][Y-1]);

				neighbours.add(capsuleGrid[x-1][y]);
				neighbours.add(capsuleGrid[x-1][y+1]);
				neighbours.add(capsuleGrid[x-1][Y-1]);
				
				neighbours.add(capsuleGrid[0][y]);
				neighbours.add(capsuleGrid[0][y+1]);
				neighbours.add(capsuleGrid[0][Y-1]);
			} else if (y == Y-1){
				neighbours.add(capsuleGrid[x][0]);
				neighbours.add(capsuleGrid[x][y-1]);

				neighbours.add(capsuleGrid[x-1][y]);
				neighbours.add(capsuleGrid[x-1][0]);
				neighbours.add(capsuleGrid[x-1][y-1]);
				
				neighbours.add(capsuleGrid[0][y]);
				neighbours.add(capsuleGrid[0][0]);
				neighbours.add(capsuleGrid[0][y-1]);

			} else {
				neighbours.add(capsuleGrid[x][y+1]);
				neighbours.add(capsuleGrid[x][y-1]);

				neighbours.add(capsuleGrid[x-1][y]);
				neighbours.add(capsuleGrid[x-1][y+1]);
				neighbours.add(capsuleGrid[x-1][y-1]);
				
				neighbours.add(capsuleGrid[0][y]);
				neighbours.add(capsuleGrid[0][y+1]);
				neighbours.add(capsuleGrid[0][y-1]);
			}
		} else {
			if (y == 0){
				neighbours.add(capsuleGrid[x][y+1]);
				neighbours.add(capsuleGrid[x][Y-1]);

				neighbours.add(capsuleGrid[x-1][y]);
				neighbours.add(capsuleGrid[x-1][y+1]);
				neighbours.add(capsuleGrid[x-1][Y-1]);
				
				neighbours.add(capsuleGrid[x+1][y]);
				neighbours.add(capsuleGrid[x+1][y+1]);
				neighbours.add(capsuleGrid[x+1][Y-1]);

			} else if (y == Y-1){
				neighbours.add(capsuleGrid[x][0]);
				neighbours.add(capsuleGrid[x][y-1]);

				neighbours.add(capsuleGrid[x-1][y]);
				neighbours.add(capsuleGrid[x-1][0]);
				neighbours.add(capsuleGrid[x-1][y-1]);
				
				neighbours.add(capsuleGrid[x+1][y]);
				neighbours.add(capsuleGrid[x+1][0]);
				neighbours.add(capsuleGrid[x+1][y-1]);
			} else {
				neighbours.add(capsuleGrid[x][y-1]);
				neighbours.add(capsuleGrid[x][y+1]);
				
				neighbours.add(capsuleGrid[x-1][y-1]);
				neighbours.add(capsuleGrid[x-1][y+1]);
				neighbours.add(capsuleGrid[x-1][y]);
				
				neighbours.add(capsuleGrid[x+1][y-1]);
				neighbours.add(capsuleGrid[x+1][y+1]);
				neighbours.add(capsuleGrid[x+1][y]);
			}
		}
		return neighbours;
	}

	Capsule getNeighbour_U(int x, int y){
		if (y == 0){
			return capsuleGrid[x][Y-1];
		} else {
			return capsuleGrid[x][y-1];
		}
	}

	Capsule getNeighbour_UR(int x, int y){
		int gx = x + 1;
		int gy = y - 1;
		if (gx == X){
			gx = 0;
		}
		if (gy == -1){
			gy = Y - 1;
		}
		return capsuleGrid[gx][gy];
	}

	Capsule getNeighbour_R(int x, int y){
		if (x == X-1){
			return capsuleGrid[0][y];
		} else {
			return capsuleGrid[x+1][y];
		}
	}

	Capsule getNeighbour_DR(int x, int y){
		int gx = x + 1;
		int gy = y + 1;
		if (gx == X){
			gx = 0;
		}
		if (gy == Y){
			gy = 0;
		}
		return capsuleGrid[gx][gy];
	}

	Capsule getNeighbour_D(int x, int y){
		if (y == Y - 1){
			return capsuleGrid[x][0];
		} else {
			return capsuleGrid[x][y+1];
		}
	}

	Capsule getNeighbour_DL(int x, int y){
		int gx = x - 1;
		int gy = y + 1;
		if (gx == -1){
			gx = X-1;
		}
		if (gy == Y){
			gy = 0;
		}
		return capsuleGrid[gx][gy];
	}

	Capsule getNeighbour_L(int x, int y){
		if (x == 0){
			return capsuleGrid[X-1][y];
		} else {
			return capsuleGrid[x-1][y];
		}
	}

	Capsule getNeighbour_UL(int x, int y){
		int gx = x - 1;
		int gy = y - 1;
		if (gx == -1){
			gx = X-1;
		}
		if (gy == -1){
			gy = Y-1;
		}
		return capsuleGrid[gx][gy];
	}

	String[][] toGridString(){
		String[][] cellAsString = new String[X][Y];
		for (int i = 0; i < X; i++){
			for (int j = 0; j < Y; j++){
				cellAsString[i][j] = capsuleGrid[i][j].toString(); 
			}
		}
		return cellAsString;
	}
	
//	int area(){
//		return 
//	}
}