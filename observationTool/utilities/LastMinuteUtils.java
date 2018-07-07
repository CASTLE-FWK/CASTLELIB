package observationTool.utilities;

import castleComponents.objects.List;
import observationTool.DataCollector_FileSystem;
import stdSimLib.utilities.Utilities;

/**
 * The whole point of this class is to do dirty last minute things with data to
 * add more stuff to my thesis.
 * 
 * @author lachlan
 *
 */
public class LastMinuteUtils {

	static String TEST = "/home/lachlan/tmp/ac/paths.txt";

	public static void main(String[] args) {
		// String s = elapsedTimes(TEST);
		// System.out.println(s);

		String s = buildExperimentFile("/home/lachlan/repos/repastModels/runtime/output/ac/list.txt",
				"/home/lachlan/repos/repastModels/AntColony/AntColony/experiments/metrics.json", "AntColony");
		System.out.println(s);
	}

	// 1: Get all elapsed times from a list of paths
	// from experiments that dump their termination stats out
	// and have been converted from json.gz to json
	public static String elapsedTimes(String pathstxtFP) {
		List<String> paths = new List<String>(Utilities.parseFileLineXLine(pathstxtFP));
		String out = "Name\tTime\n";
		for (String p : paths) {
			DataCollector_FileSystem dfs = new DataCollector_FileSystem(p);
			out += p + "\t" + dfs.getElapsedTime() + "\n";
		}
		return out;
	}

	static String templateString = "{\n" + "			\"System-name\": \"CONFNAME COUNT\",\n"
			+ "			\"Configuration\": {\n" + "				\"Configuration-name\": \"CONFNAME\",\n"
			+ "				\"Dimensions\": \"\"\n" + "			},\n"
			+ "			\"System-storage-type\": \"file\",\n" + "			\"System-storage-location\": \"PATHREP\"\n"
			+ "		}";

	// flags
	static String CONFNAME = "CONFNAME";
	static String COUNTNAME = "COUNT";
	static String PATHREP = "PATHREP";

	public static String buildExperimentFile(String pathsTXT, String pathToMetricsJSON, String sysname) {
		List<String> paths = new List<String>(Utilities.parseFileLineXLine(pathsTXT));
		String out = "{\n" + "\"Experiment-id\": \"All\",\n" + "\"Description\": \"All\",\n" + "\"System-name\": \""
				+ sysname + "\",\n";
		out += "\"Test-Systems\": [\n";
		String currConfName = "NONE";
		int count = 0;

		for (int i = 0; i < paths.size(); i++) {
			String s = paths.get(i).trim();
			if (s.length() == 0) {
				continue;
			}
			if (s.startsWith("#")) {
				currConfName = s.replaceAll("#", "").trim();
				count = 0;
			} else {
				out += templateString.replace(COUNTNAME, "" + count).replace(CONFNAME, currConfName).replace(PATHREP,
						s);
				if (i == paths.size() - 1) {
					out += "\n";
				} else {
					out += ",\n";
				}
				count++;
			}
		}
		out += "],\n";
		List<String> metricsJson = new List<String>(Utilities.parseFileLineXLine(pathToMetricsJSON));
		for (String s : metricsJson) {
			out += s + "\n";
		}
		return out;
	}

}
