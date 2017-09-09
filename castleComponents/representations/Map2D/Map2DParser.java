package castleComponents.representations.Map2D;

import java.io.BufferedReader;
import java.io.IOException;

import castleComponents.objects.Vector2;
import stdSimLib.utilities.Utilities;

public class Map2DParser {

	Map2D theMapToStore;
	
	String name;
	Vector2 dimensions;
	boolean open;
	int scale;
	
	int xCounter;
	int yCounter;
	
	//Symbols
	final char PARK = 'P';
	final char NOGO = '*';
	final char ROAD_H = '-';
	final char ROAD_V = '|';
	final char TURN_R = '\\';
	final char TURN_L = '/';
	final char ENT_0 = '0';
	final char ENT_1 = '1';
	final char ENT_2 = '2';
	final char ENT_3 = '3';
	final char ENT_4 = '4';
	final char ENT_5 = '5';
	final char ENT_6 = '6';
	final char ENT_7 = '7';
	final char ENT_8 = '8';
	final char ENT_9 = '9';
	
	Vector2 currentPosition;
	public Map2DParser(Map2D theMapToStore){
		this.theMapToStore = theMapToStore;
		
	}
	
	
	
	public void parseMapFile(String filePath){
		BufferedReader br = Utilities.getFileAsBufferedReader(filePath);
		if (!filePath.endsWith(".map2d")){
			System.out.println("This is not a map2d file.");
			System.exit(0);
		}
		String line = null;
		currentPosition = new Vector2(0,0);
		ParseState currState = ParseState.NAME;		
		try {
			while ((line = br.readLine()) != null){
				switch(currState){
					case NAME:
							if (line.startsWith("Name:")){
								this.name = line.split("\"")[1];
								currState = ParseState.DIMENSIONS;
							} else {
								//error
							}							
						break;
					case DIMENSIONS:
						if (line.startsWith("Dimensions:")){
							double x = Double.parseDouble(line.split("<")[1].split(",")[0]);
							double y = Double.parseDouble(line.split("<")[1].split(",")[0].split(">")[0]);
							dimensions = new Vector2(x,y);
							theMapToStore.init(dimensions);
							currState = ParseState.OPEN;
						} else {
							//error
						}
						break;
					case OPEN:
						//This could be wayyy more robust
						if (line.startsWith("Open:")){
							if (line.contains("true")) {
								open = true;
							} else if (line.contains("false")){
								open = false;
							}
							currState = ParseState.SCALE;
						} else {
							//error
						}	
						break;
					case SCALE:
						if (line.startsWith("Scale:")){
							scale = Integer.parseInt(line.split(":")[1].trim().split("x")[0]);
							currState = ParseState.BEGIN_MAP;
						} else {
							//error
						}
						break;
					case BEGIN_MAP:
						//Do the bu
						if (line.startsWith("BEGIN MAP:")){
							currState = ParseState.READING_MAP;
						} else {
							//Do nothing
						}
						break;
					case READING_MAP:
						if (line.startsWith("END MAP")){
							currState = ParseState.END_MAP;
							break;
						} else {
							parseMapContent(line);
						}
						break;
					case END_MAP:{
						if (yCounter != (int)dimensions.getY()){
							//Uh-oh
						}
						//This will just be noise
					}
				}								
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void parseMapContent(String line){
		char[] chars = line.toCharArray();
		xCounter = chars.length;		
		if (xCounter != (int)dimensions.getX()){
			//Uh-oh
		}
		for (int i = 0; i < chars.length; i++){
			currentPosition.modify(i, currentPosition.getY());
			//Parse each symbol
			char currCar = chars[i];
			switch (currCar){
				case NOGO:
					theMapToStore.addMapComponent(currentPosition, Type.NOGO);
				break;
				case PARK:
					theMapToStore.addMapComponent(currentPosition, Type.PARK);
				break;
				case ROAD_H:
					theMapToStore.addMapComponent(currentPosition, Type.ROAD_H);
				break;
				case ROAD_V:
					theMapToStore.addMapComponent(currentPosition, Type.ROAD_V);
				break;
				case TURN_R:
					theMapToStore.addMapComponent(currentPosition, Type.TURN_R);
				break;
				case TURN_L:
					theMapToStore.addMapComponent(currentPosition, Type.TURN_L);
				break;
				case ENT_0:
				break;
			}
				
		}
		yCounter++;
		currentPosition.modify(currentPosition.getX(), yCounter);
	}
	
	public Map2D getMap(){
		return theMapToStore;
	}
	
}

enum ParseState{
	NAME, DIMENSIONS, OPEN, SCALE, BEGIN_MAP, READING_MAP, END_MAP
}