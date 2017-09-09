package castleComponents.representations.Map2D;

public class Map2DTester {
	
	
	public static void main(String[] args){
		String mapPath = args[0];
		Map2D map = new Map2D();
		Map2DParser parser = new Map2DParser(map);
		parser.parseMapFile(mapPath);
		if (!map.validateDimensions()){
			System.out.println("WRONG");
			System.exit(0);
		}
		System.out.println(map.toString());
	}
}
