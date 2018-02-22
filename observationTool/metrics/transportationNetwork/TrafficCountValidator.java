package observationTool.metrics.transportationNetwork;

import java.util.ArrayList;

import experimentExecution.MetricInfo;
import experimentExecution.MetricVariableMapping;
import observationTool.VEntity;
import observationTool.metrics.MetricBase;

public class TrafficCountValidator extends MetricBase {

	private final String STATE_1 = "STATE_1";
	private final String TRAFFIC_COUNTER = "Traffic-counter";

	public TrafficCountValidator(MetricInfo mi) {
		super("TrafficCountValidator", mi);
		this.mi = mi;
		metricVariableMappings = mi.getMetricVariableMappings();
	}

	public int count(ArrayList<VEntity> ents) {
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

}
