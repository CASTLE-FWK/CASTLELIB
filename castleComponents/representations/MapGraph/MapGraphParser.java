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
import stdSimLib.utilities.Utilities;

public class MapGraphParser {
	public final String LIT = "lit";

	public MapGraph mapGraph;

	public static void main(String[] args) {
		MapGraphParser mgp = new MapGraphParser(new MapGraph());
		mgp.parseMapGraph(args[0]);
	}

	public MapGraphParser(MapGraph mg) {
		mapGraph = mg;
		mapGraph.setId(0);
	}

	public void parseMapGraph(String pathToFile) {
		System.out.println("********PARSING MAPGRAPH from " + pathToFile + "*******");
		OSMParser p = new OSMParser();
		File osmFile = new File(pathToFile);
		try {
			HashMap<Long, Node> storedNodes = new HashMap<Long, Node>();
			Map<String, Element> parsedMap = p.parse(osmFile);
			p.printStatistics(parsedMap); // Lets check things

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
					nd.setGeoCoords(coords);
					// Store this in our graph
					storedNodes.put(nd.getID(), nd);
					mapGraph.addNode(nd);
					// If tags are present, figure them out
					if (tags.size() > 0) {
						if (tags.containsKey("highway")) {
							String value = tags.get("highway");
							nd.setNodeType(value);
						}
						if (tags.containsKey("amenity")) {
							String val = tags.get("amenity");
							nd.setNodeType(val);
							nd.setCarPark(true);
							nd.createCarPark();
							mapGraph.addCarParkLocation(nd);
						}
					}

				} else if (e instanceof info.pavie.basicosmparser.model.Way) {
					info.pavie.basicosmparser.model.Way w = (info.pavie.basicosmparser.model.Way) e;
					List<info.pavie.basicosmparser.model.Node> nodes = w.getNodes();
					// Need to find nodes above first
					Link ln = new Link();
					ln.setID(idl);
					for (info.pavie.basicosmparser.model.Node n : nodes) {
						if (n == null) {
							// Has to be ignored
							continue;
						}
						Node nd = storedNodes.get(Long.parseLong(n.getId().substring(1)));
						if (nd == null) {
							System.err.println("Node " + n.getId() + " has not been seen");
						} else {
							ln.addWayPoint(nd);
							nd.addLink(ln);
						}
					}
					boolean isService = false;
					boolean isParking = false;
					if (tags.size() > 0) {
						if (tags.containsKey("highway")) {
							String highwayV = tags.get("highway");
							ln.setRoadType(highwayV);
							if (highwayV.compareToIgnoreCase("footway") == 0) {
								ln.setHumanAccessible(true);
							}
							if (highwayV.compareToIgnoreCase("pedestrian") == 0) {
								ln.setHumanAccessible(true);
							}
							if (highwayV.compareToIgnoreCase("service") == 0) {
								// TODO Something should happen here?
								isService = true;
							}

						}

						if (tags.containsKey("name")) {
							String value = tags.get("name");
							ln.setName(value);
						}
						if (tags.containsKey("lanes")) {
							String value = tags.get("lanes");
							ln.setLanes(Integer.parseInt(value));
						}
						if (tags.containsKey("oneway")) {
							String value = tags.get("oneway");
							ln.setOneWay(Boolean.parseBoolean(value));
						}
						if (tags.containsKey("maxspeed")) {
							String value = tags.get("maxspeed");
							ln.setMaxSpeed(Integer.parseInt(value));
						}
						if (tags.containsKey("bicycle")) {
							String value = tags.get("bicycle");
							ln.setBicycle(Boolean.parseBoolean(value));
						}
						if (tags.containsKey("cycleway")) {
							String value = tags.get("cycleway");
							ln.setCycleWay(value);
							if (value.compareToIgnoreCase("*") != 0) {
								ln.setHumanAccessible(true);
							}
						}
						if (tags.containsKey(LIT)) {
							String value = tags.get(LIT);
							ln.setLit(Boolean.parseBoolean(value));
						}
						if (tags.containsKey("foot")) {
							String value = tags.get("foot");
							ln.setHumanAccessible(Boolean.parseBoolean(value));
						}
						if (tags.containsKey("amenity")) {
							String value = tags.get("amenity");
							if (value.compareToIgnoreCase("parking") == 0) {
								isParking = true;
							}
						}
					}
					ln.setup();
					ln.setCarParkArea(isService && isParking);
					mapGraph.addLink(ln);
					// System.out.println(ln.toString());

				} else if (e instanceof Relation) {
					// Not sure what to do with this yet
					Relation r = (Relation) e;
					List<Element> members = r.getMembers();
					for (Element mem : members) {
						if (mem instanceof info.pavie.basicosmparser.model.Node) {
							// Check to see if it exists
							if (storedNodes.containsKey(Long.parseLong(mem.getId().substring(1)))) {
								// YAY
							} else {
								Node n = new Node();
								n.setID(Long.parseLong(mem.getId().substring(1)));
								n.setGeoCoords(Vector2.NULL);
								n.setOutOfBounds(true);
								storedNodes.put(n.getID(), n);
							}
						} else if (mem instanceof info.pavie.basicosmparser.model.Way) {
							// TODO no idea
						}
					}
				}
			}
			p = null;
			// Order may be important here
			// mapGraph.extractEdges();
//			mapGraph.assignEdges();
//			mapGraph.connectedComponents();
//			System.out.println(mapGraph.ccStats());
//			mapGraph.prune();
//			mapGraph.clean();
//
//			// Map Infrastructure building
//			mapGraph.calculateBounds();
//			mapGraph.normalise();
//			mapGraph.buildLights();
//			mapGraph.buildCarParks();
//			mapGraph.generateTransitPoints(20);
//			mapGraph.prunePhase2();
//
//			mapGraph.nodeValidation();

//			System.out.println(mapGraph.getTransitNodesAsString());
			// System.out.println(mapGraph.getRandomNode());
			// System.out.println(mapGraph.getRandomNode());
			// System.out.println(mapGraph.getRandomNode());

//			System.out.println(mapGraph.range);
//			System.out.println(mapGraph.toString());
			System.out.println("********FINISHED PARSING: " + pathToFile + "*******");
			// System.exit(0);

			// System.out.println("STREAMING TO GEPHI");
//			StreamToGephi stg = new StreamToGephi("http://localhost:8080/workspace0?");
//			String[] testOut = mapGraph.exportGraphAsJSON().split("\n");
//			for (String s : testOut) {
//				stg.sendAction(s);
//			}

		} catch (IOException | SAXException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void errLog(Object o) {
		System.err.println("MapGraphParser Warning: " + o.toString());
	}
}
