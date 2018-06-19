package observationTool.utilities;

import castleComponents.objects.List;
import observationTool.DataCollector_FileSystem;
import stdSimLib.utilities.Utilities;
/**
 * The whole point of this class is to do dirty last minute things with data
 * to add more stuff to my thesis.
 * @author lachlan
 *
 */
public class LastMinuteUtils {

	static String TEST = "/home/lachlan/tmp/ac/paths.txt";
	public static void main(String[] args) {
		String s = elapsedTimes(TEST);
		System.out.println(s);
	}
	
	//1: Get all elapsed times from a list of paths
	//from experiments that dump their termination stats out
	//and have been converted from json.gz to json
	public static String elapsedTimes(String pathstxtFP) {
		List<String> paths = new List<String>(Utilities.parseFileLineXLine(pathstxtFP));
		String out = "Name\tTime\n";
		for (String p : paths) {
			DataCollector_FileSystem dfs = new DataCollector_FileSystem(p);
			out += p+"\t"+dfs.getElapsedTime()+"\n";
		}
		return out;
	}

}
