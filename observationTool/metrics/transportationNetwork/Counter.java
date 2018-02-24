package observationTool.metrics.transportationNetwork;

import java.util.ArrayList;

import experimentExecution.MetricInfo;
import experimentExecution.MetricVariableMapping;
import observationTool.DataCollector_FileSystem;
import observationTool.VEntity;
import observationTool.metrics.MetricBase;
import observationTool.metrics.MetricInterface;
import observationTool.results.MetricResult;

public class Counter extends MetricBase implements MetricInterface {

	private final String STATE_1 = "STATE_1";
	private MetricResult result;

	private final String DOUBLE_STRING = "Counter(Double)";
	private final String INTEGER_STRING = "Counter(Integer)";

	public Counter(MetricInfo mi) {
		super("Counter", mi);
		this.mi = mi;
		metricVariableMappings = mi.getMetricVariableMappings();
	}

	private int numberOfSteps = -1;
	private DataCollector_FileSystem collector;
	private String countingType = "";
	boolean isDouble = false;

	public void setup(int nOS, String ct) {
		this.numberOfSteps = nOS;
		this.countingType = ct;
		if (countingType == "double") {
			isDouble = true;
		}
	}

	public void setResultStore(MetricResult mr) {
		this.result = mr;
	}

	public void setCollector(DataCollector_FileSystem dfs) {
		this.collector = dfs;
	}

	public void run() {
		// Do a check to make sure everything is a-okay
		if (result == null || numberOfSteps < 0 || collector == null || countingType.isEmpty()) {
			errLog("Something ain't right. Need to investigate. Dying.");
			return;
		}

		if (isDouble) {
			result.addResultType(DOUBLE_STRING);
			for (int time = 1; time < numberOfSteps; time++) {
				ArrayList<VEntity> agents = collector.buildVAgentList(time);
				double count = countDouble(agents);
				result.addResultAtStep(DOUBLE_STRING, count, time);
			}

		} else {
			result.addResultType(INTEGER_STRING);
			for (int time = 1; time < numberOfSteps; time++) {
				ArrayList<VEntity> agents = collector.buildVAgentList(time);
				int count = countInt(agents);
				result.addResultAtStep(INTEGER_STRING, count, time);
			}

		}
	}

	public int countInt(ArrayList<VEntity> ents) {
		MetricVariableMapping mvm1 = metricVariableMappings.get(STATE_1);
		int counter = 0;
		for (VEntity v : ents) {
			if (entityIsOfType(v, mvm1)) {
				String paramName = getAllParameterNames(v, mvm1).get(0);
				counter += parseInt(getParameter(v, paramName));
			}
		}

		return counter;
	}

	public double countDouble(ArrayList<VEntity> ents) {
		MetricVariableMapping mvm1 = metricVariableMappings.get(STATE_1);
		double counter = 0;
		for (VEntity v : ents) {
			if (entityIsOfType(v, mvm1)) {
				String paramName = getAllParameterNames(v, mvm1).get(0);
				counter += parseDouble(getParameter(v, paramName));
			}
		}

		return counter;
	}

	@Override
	public MetricResult getResults() {
		return result;
	}

}
