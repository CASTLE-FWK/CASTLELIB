package castleComponents.representations.MapGraph;

import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class StreamToGephi {

	// http://localhost:8080/workspace0?
	public String GEPHI_LOCATION = "";

	public StreamToGephi(String location) {
		GEPHI_LOCATION = location;
	}

	public void sendAction(String action) throws Exception {
//		OutputStreamWriter writer = null;
//		URL url = new URL("http://localhost:8080/workspace1?operation=updateGraph");
//		URLConnection conn = url.openConnection();
//		conn.setDoOutput(true);
//		writer = new OutputStreamWriter(conn.getOutputStream());
//		writer.write(action);
//		writer.flush();
//
//		conn.getInputStream();

	}

}
