package castleComponents.representations.MapGraph;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.xml.sax.SAXException;

import castleComponents.objects.Vector2;
import info.pavie.basicosmparser.controller.*;
import info.pavie.basicosmparser.model.Element;
import info.pavie.basicosmparser.model.Relation;

public class MapGraphParser {
	public static final String MOTORWAY = "MOTORWAY";
	public static final String TRUNK = "TRUNK";
	public static final String PRIMARY = "PRIMARY";
	public static final String SECONDARY = "SECONDARY";
	public static final String TERTIARY = "TERTIARY";
	public static final String RESIDENTIAL = "RESIDENTIAL";
	public static final String TRAFFIC_SIGNALS = "TRAFFIC_SIGNALS";

	public static void main(String[] args) {
		parseMapGraph(args[0]);
	}

	public static void parseMapGraph(String pathToFile) {
		OSMParser p = new OSMParser();
		File osmFile = new File(pathToFile);
		try {
			int nodeCounter = 0;
			int wayCounter = 0;
			HashMap<Long, Node> storedNodes = new HashMap<Long, Node>();
			Map<String, Element> parsedMap = p.parse(osmFile);
			p.printStatistics(parsedMap); // Lets check things
			
//			System.exit(0);
			// Get Keys as Set
			Set<String> keys = parsedMap.keySet();
			ArrayList<String> sortedKeys = new ArrayList<String>(keys);
			Collections.sort(sortedKeys);
			// Cycle through keys
			for (String k : sortedKeys) {
				// Get element
				Element e = parsedMap.get(k);
				// Get ID
				String id = e.getId().substring(1);
				long idl = Long.parseLong(id);
				// Get Tags
				Map<String, String> tags = e.getTags();
				if (e instanceof info.pavie.basicosmparser.model.Node) {
					// Cast to node
					info.pavie.basicosmparser.model.Node n = (info.pavie.basicosmparser.model.Node) e;
					// Get lat & lon
					double lat = n.getLat();
					double lon = n.getLon();
					Vector2 coords = new Vector2(lat, lon);
					Node nd = new Node();
					nd.setID(idl);
					nd.setCoords(coords);
					// Store this in our graph
					storedNodes.put(nd.getID(), nd);
					// If tags are present, figure them out
					if (tags.size() > 0) {
						if (tags.containsKey("highway")) {
							String value = tags.get("highway");
							nd.setNodeType(value);
						}
					}

				} else if (e instanceof info.pavie.basicosmparser.model.Way) {
					info.pavie.basicosmparser.model.Way w = (info.pavie.basicosmparser.model.Way) e;
					List<info.pavie.basicosmparser.model.Node> nodes = w.getNodes();
					// Need to find nodes above first
					Edge ed = new Edge();
					ed.setID(idl);
					for (info.pavie.basicosmparser.model.Node n : nodes) {
						if (n == null) {
							//Has to be ignored
							continue;
						}
						Node nd = storedNodes.get(Long.parseLong(n.getId().substring(1)));
						if (nd == null) {
							System.err.println("Node "+n.getId()+" has not been seen");
						} else {
							ed.addWayPoint(nd);
						}
					}

					if (tags.size() > 0) {
						if (tags.containsKey("highway")) {
							String highwayV = tags.get("highway");
							ed.setRoadType(highwayV);
						}
						if (tags.containsKey("name")) {
							String value = tags.get("name");
							ed.setName(value);
						}
						if (tags.containsKey("lanes")) {
							String value = tags.get("lanes");
							ed.setLanes(Integer.parseInt(value));
						}
						if (tags.containsKey("oneway")) {
							String value = tags.get("oneway");
							ed.setOneWay(Boolean.parseBoolean(value));
						}
						if (tags.containsKey("maxspeed")) {
							String value = tags.get("maxspeed");
							ed.setMaxSpeed(Integer.parseInt(value));
						}
						if (tags.containsKey("bicycle")) {
							String value = tags.get("bicycle");
							ed.setBicycle(Boolean.parseBoolean(value));
						}
						if (tags.containsKey("cycleway")) {
							String value = tags.get("cycleway");
							ed.setCycleWay(value);
						}
						if (tags.containsKey("lit")) {
							String value = tags.get("lit");
							ed.setLit(Boolean.parseBoolean(value));
						}
					}
					ed.calculateLength();
					System.out.println(ed.toString());

				} else if (e instanceof Relation) {
					//Not sure what to do with this yet
					Relation r = (Relation)e;
					List<Element> members = r.getMembers();
					for (Element mem : members) {
						if (mem instanceof info.pavie.basicosmparser.model.Node) {
							//Check to see if it exists
							if (storedNodes.containsKey(Long.parseLong(mem.getId().substring(1)))) {
								//YAY
							} else {
								Node n = new Node();
								n.setID(Long.parseLong(mem.getId().substring(1)));
								n.setCoords(Vector2.NULL);
								n.setOutOfBounds(true);
								storedNodes.put(n.getID(), n);
							}
						} else if (mem instanceof info.pavie.basicosmparser.model.Way) {
							
						}
					}
					
				}
			}

			System.out.println("nc: " + nodeCounter);
			System.out.println("wc: " + wayCounter);

		} catch (IOException | SAXException e) {
			e.printStackTrace();
		}

	}

}

enum ParseState {
	NODE, WAY, NONE
}
