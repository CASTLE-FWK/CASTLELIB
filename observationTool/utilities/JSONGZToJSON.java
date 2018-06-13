package observationTool.utilities;
import java.io.IOException;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import stdSimLib.utilities.Utilities;
public class JSONGZToJSON {

	public static void main(String[] args) {
		//testing
	}
	
	public static void convertGZtoJSON(String inFP, String outFP) {
		try {
			JsonObject jo = Json.parse(Utilities.decompressStringFromFile(inFP)).asObject();
			Utilities.writeToFile(jo.toString(), outFP, false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
