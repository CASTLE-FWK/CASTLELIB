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
	private String RESULT_STRING = "Counter(XXX)";

	public Counter(MetricInfo mi) {
		super("Counter", mi);
		this.mi = mi;
		metricVariableMappings = mi.getMetricVariableMappings();
	}

	private int numberOfSteps = -1;
	private DataCollector_FileSystem collector;
	private String countingType = "";

	enum DataType {
		INT, DOUBLE, STRING, BOOL
	};

	DataType dt;

	public void setup(int nOS, String ct) {
		this.numberOfSteps = nOS;
		this.countingType = ct;
		RESULT_STRING = "Counter(XXX)";
		switch (countingType) {
		case "int":
			dt = DataType.INT;
			break;
		case "double":
			dt = DataType.DOUBLE;
			break;
		case "string":
			dt = DataType.STRING;
			break;
		case "boolean":
			dt = DataType.BOOL;
			break;
		default:
			errLog("not valid datatype");
			break;
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
		String resiString = RESULT_STRING.replace("XXX", dt.toString());
		result.addResultType(resiString);

		for (int time = 1; time < numberOfSteps; time++) {
			ArrayList<VEntity> agents = collector.buildVAgentList(time);
			double count = doCount(agents);
			result.addResultAtStep(resiString, count, time);
		}
	}

	public double doCount(ArrayList<VEntity> ents) {
		double counter = 0;
		MetricVariableMapping mvm1 = metricVariableMappings.get(STATE_1);
		for (VEntity v : ents) {
			if (entityIsOfType(v, mvm1)) {
				String paramName = getAllParameterNames(v, mvm1).get(0);
				switch (dt) {
				case BOOL:
					boolean b = isParameterEqualToDesiredValue(v, mvm1);
					if (b) {
						counter++;
					}
					break;
				case DOUBLE:
					counter += parseDouble(getParameter(v, paramName));
					break;
				case INT:
					counter += parseInt(getParameter(v, paramName));
					break;
				case STRING:
					boolean c = isParameterEqualToDesiredValue(v, mvm1);
					if (c) {
						counter++;
					}
					break;
				default:
					break;
				}
			}
		}

		return counter;
	}

	@Override
	public MetricResult getResults() {
		return result;
	}

}
