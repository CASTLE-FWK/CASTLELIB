package castleComponents.representations.MapGraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xml.sax.SAXException;

import castleComponents.objects.Vector2;
import stdSimLib.utilities.Utilities;
import info.pavie.basicosmparser.controller.*;
import info.pavie.basicosmparser.model.Element;

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
			Map<String, Element> parsedMap = p.parse(osmFile);
			p.printStatistics(parsedMap); // Lets check things
			// Get Keys as Set
			Set<String> keys = parsedMap.keySet();
			// Cycle through keys
			for (String k : keys) {
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

					// Store this in our graph

					// If tags are present, figure them out
					if (tags.size() > 0) {
						if (tags.containsKey("highway")) {
							String highwayV = tags.get("highway");

						}
					}

				} else if (e instanceof info.pavie.basicosmparser.model.Way) {
					info.pavie.basicosmparser.model.Way w = (info.pavie.basicosmparser.model.Way) e;
					List<info.pavie.basicosmparser.model.Node> nodes = w.getNodes();
					if (tags.size() > 0) {
						if (tags.containsKey("highway")) {
							String highwayV = tags.get("highway");

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
