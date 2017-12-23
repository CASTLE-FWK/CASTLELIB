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
	public final static char PARK = 'P';
	public final static char NOGO = '*';
	public final static char ROAD_H = '-';
	public final static char ROAD_V = '|';
	public final static char ONEWAY_N = ';';
	public final static char ONEWAY_S = ':';
	public final static char ONEWAY_E = '>';
	public final static char ONEWAY_W = '<';
	public final static char FOUR_WAY = '+';
	public final static char ENTRY = '&';
	public final static char T_SEC = 'T';
	public final static char MAP = 'M';
	public final static char EVENT = 'E';
	public final static char UNSET = 'U';
	public final static char ENT_0 = '0';
	public final static char ENT_1 = '1';
	public final static char ENT_2 = '2';
	public final static char ENT_3 = '3';
	public final static char ENT_4 = '4';
	public final static char ENT_5 = '5';
	public final static char ENT_6 = '6';
	public final static char ENT_7 = '7';
	public final static char ENT_8 = '8';
	public final static char ENT_9 = '9';
	
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
								theMapToStore.setName(name);
							} else {
								//error
							}							
						break;
					case DIMENSIONS:
						if (line.startsWith("Dimensions:")){
							double x = Double.parseDouble(line.split("<")[1].split(",")[0]);
							double y = Double.parseDouble(line.split("<")[1].split(",")[1].split(">")[0]);
							dimensions = new Vector2(x,y);
							System.out.println("parse: "+dimensions.toString());
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
							theMapToStore.setOpen(open);
							currState = ParseState.SCALE;
						} else {
							//error
						}	
						break;
					case SCALE:
						if (line.startsWith("Scale:")){
							scale = Integer.parseInt(line.split(":")[1].trim().split("x")[0]);
							currState = ParseState.BEGIN_MAP;
							theMapToStore.setScale(scale);
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
				case ONEWAY_N:
					theMapToStore.addMapComponent(currentPosition, Type.ONEWAY_N);
				break;
				case ONEWAY_S:
					theMapToStore.addMapComponent(currentPosition, Type.ONEWAY_S);
				break;
				case ONEWAY_E:
					theMapToStore.addMapComponent(currentPosition, Type.ONEWAY_E);
				break;
				case ONEWAY_W:
					theMapToStore.addMapComponent(currentPosition, Type.ONEWAY_W);
				break;
				case FOUR_WAY:
					theMapToStore.addMapComponent(currentPosition, Type.FOUR_WAY);
				break;
				case T_SEC:
					theMapToStore.addMapComponent(currentPosition, Type.T_SEC);
				break;
				case MAP:
					theMapToStore.addMapComponent(currentPosition, Type.MAP);
				break;
				case UNSET:
					theMapToStore.addMapComponent(currentPosition, Type.UNSET);
				break;
				case EVENT:
					theMapToStore.addMapComponent(currentPosition, Type.EVENT);
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