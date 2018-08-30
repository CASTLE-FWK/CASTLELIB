package observationTool.metrics;

import java.util.ArrayList;

import experimentExecution.MetricInfo;
import experimentExecution.MetricVariableMapping;
import observationTool.VEntity;

public class SNMetric extends MetricBase {
	final String STATE_1 = "STATE_1";

	public SNMetric(MetricInfo mi) {
		super("SNMetric", mi);
		// TODO Auto-generated constructor stub
	}
	
	public double calculateAverageSentiment(ArrayList<VEntity> agents, MetricParameters mp) {
		//We need NonAdvocates and to get their "agreementTest" parameter
		
		double res = 0;
		double cumSent = 0;
		double vCount = 0;
		
		MetricVariableMapping mvm1 = metricVariableMappings.get(STATE_1);
		for (VEntity v : agents) {
			if (entityIsOfType(v, mvm1)) {
				//Get agreementTest
				double sent = Double.parseDouble(v.getParameterValueFromStringAsString("agreementTest"));
				//Now what?
				//Calculate average sentiment? Some other calculation? 
				cumSent += sent;
				vCount++;
			}
		}
		res = cumSent/vCount;
		
		return res;
	}

}
