package observationTool.utilities;
import java.io.IOException;
import java.util.ArrayList;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import castleComponents.objects.List;
import stdSimLib.utilities.Utilities;
public class JSONGZToJSON {

	public static void main(String[] args) {
		//testing
//		convertGZtoJSON("/home/lachlan/tmp/ac/AntColony-AntColony-18-06-09-20-42-14bz/steps/Step84.json.gz", "/home/lachlan/tmp/ac/AntColony-AntColony-18-06-09-20-42-14bz/steps/Step84.json");
		batchGZtoJSON("/home/lachlan/repos/repastModels/AntColony/AntColony/res/AntColony-AntColony-18-07-04-11-16-41sg/stepList.txt");
	}
	
	public static void convertGZtoJSON(String inFP, String outFP) {
		if (inFP.compareTo(outFP) == 0) {
			System.err.println("Input and output paths are the same. Will cause overwrite. Cancelling.");
			System.exit(0);
		}
		try {
			JsonObject jo = Json.parse(Utilities.decompressStringFromFile(inFP)).asObject();
			Utilities.writeToFile(jo.toString(), outFP, false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void batchGZtoJSON(String pathstxtFP) {
		List<String> paths = new List<String>(Utilities.parseFileLineXLine(pathstxtFP));
		for (String p : paths) {
			String dest = p.replace(".gz", "");
			convertGZtoJSON(p,dest);
		}
	}
}
