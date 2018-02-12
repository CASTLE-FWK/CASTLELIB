package simulator;

import java.util.HashMap;

public class ExecutionParameters {
	HashMap<String, String> parameters;

	public ExecutionParameters() {
		parameters = new HashMap<String, String>();
	}

	public void parseArgs(String[] a) {
		for (int i = 0; i < a.length; i++) {
			String curr = a[i].trim();
			String[] spl = curr.split("=");
			String paramName = spl[0];
			String value = spl[1];
			parameters.put(paramName, value);
		}
	}

	public HashMap<String, String> getParameters() {
		return parameters;
	}
}
