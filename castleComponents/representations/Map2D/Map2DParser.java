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
	
	public Map2DParser(Map2D theMapToStore){
		this.theMapToStore = theMapToStore;
	}
	
	
	
	public void parseMapFile(String filePath){
		BufferedReader br = Utilities.getFileAsBufferedReader(filePath);
		String line = null;
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
		
	}
	
}

enum ParseState{
	NAME, DIMENSIONS, OPEN, SCALE, BEGIN_MAP, READING_MAP, END_MAP
}