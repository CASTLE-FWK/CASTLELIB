package observationTool.utilities;

import java.io.BufferedReader;
import java.util.HashMap;

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
		// System.out.println(s);\
		

		String s = buildExperimentFile("/home/lachlan/repos/repastModels/runtime/output/fob/fob/list.txt",
				"/home/lachlan/repos/repastModels/FlockOfBirds/FlockOfBirds_Non_SG/experiments/metrics.json", "FlockOfBirds");
//		String s = pullRuntimesFromSlurmOuts("/home/lachlan/repos/repastModels/sgrun/snsg/slurm/list.txt", "SN");
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

	static String templateString = "{\n" + "			\"System-name\": \"CONFNAME-COUNT\",\n"
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
		out += "\"Test-systems\": [\n";
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
	
	
	public static String pullRuntimesFromSlurmOuts(String pathsTXT, String sysname) {
		String out = "SystemName,SystemConfig,Runtime(ms)\n";
		HashMap<String, Double> rts = new HashMap<String, Double>();
		HashMap<String, Integer> cnt = new HashMap<String, Integer>();
		final String COMMA = ",";
		final String NL = "\n";
		List<String> paths = new List<String>(Utilities.parseFileLineXLine(pathsTXT));
		for (String s : paths) {
			BufferedReader br = Utilities.getFileAsBufferedReader(s);
			String line = null;
			String infoLine = "";
			try {
				while ((line = br.readLine()) != null) {
					if (line.startsWith("name=")) {
						infoLine = line;
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (infoLine.length() > 0) {
				//Parse infoLine
				System.out.println(infoLine);
				String[] ss = infoLine.split(",");
				String n = ss[0].replace("name=", "");
				
				String rt = ss[1].replace("runtime=", "");
				
				String cn = ss[2].replace("config-name=","");
				
				if (rts.containsKey(cn)) {
					rts.put(cn, rts.get(cn)+Double.parseDouble(rt));
					cnt.put(cn, cnt.get(cn)+1);
				} else {
					rts.put(cn, Double.parseDouble(rt));
					cnt.put(cn, 1);
				}
				
				out += n+COMMA+cn+COMMA+rt+NL;
			}
		}
		out += "Averages:"+NL;
		for (String k : rts.keySet()) {
			out += k;
			double rrt = rts.get(k) / (double)cnt.get(k);
			out += ","+rrt+NL;
		}
		return out;
	}

}
