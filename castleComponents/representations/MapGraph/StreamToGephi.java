package castleComponents.representations.MapGraph;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class StreamToGephi {

	public final String DEFAULT_GEPHI_UPDATE_URL = "http://localhost:8080/workspace1?operation=updateGraph";
	public String GEPHI_LOCATION = "http://localhost:8080/";
	public int workspaceNum = 1;
	public String workspace = "workspace" + workspaceNum;
	public String operationUpdate = "operation=updateGraph";
	public final char FORWARD = '/';
	public final char QUEST = '?';
	public String gephiOp;

	public boolean active = true;

	public StreamToGephi(String location) {
		if (location.length() == 0) {
			active = false;
		}
		workspaceNum = 1;
		GEPHI_LOCATION = "http://localhost:8080/";
		workspace = "workspace" + workspaceNum;
		gephiOp = GEPHI_LOCATION + workspace + QUEST + operationUpdate;
	}

	public void sendAction(String action) throws Exception {
		if (active) {
			try {
				OutputStreamWriter writer = null;
				URL url = new URL(gephiOp);
				URLConnection conn = url.openConnection();
				conn.setDoOutput(true);
				writer = new OutputStreamWriter(conn.getOutputStream());
				writer.write(action);
				writer.flush();
	
				conn.getInputStream();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("continuing...");
				active = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}