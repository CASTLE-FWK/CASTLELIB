package observationTool.metrics;

import java.util.ArrayList;
import java.util.HashMap;

import observationTool.VEntity;
import stdSimLib.Parameter;

public class ChanInteractionMetric extends AbstractMetric {

	//Metric computation mapping variables
	//Needs to be declared manually and must match metric specification JSON
	String state;
	
	//Metric result variables
	//Needs to be declared manually and must match metric specification JSON
	int[] overallChanges;
	double[] maxAtT;
	long result_Zt;
	double result_It;
	double[][] result_Yit;
	
	//Helper variables
	public HashMap<String, Double> cumulativeIndiv;
	
	public ChanInteractionMetric(){
		super();
		cumulativeIndiv = new HashMap<String, Double>();
	}
	
	
	@Override
	public void setup(HashMap<String, Parameter<?>> params) {
		notSpecialised(); //Ideally this would be called from MetricInfo
		ArrayList<VEntity> step_zero = (ArrayList<VEntity>) currentCalculationParameters.get("step_zero").getValue();
		for (VEntity vagent : step_zero) {
			cumulativeIndiv.put(vagent.getID(), 0.0);
		}
		
		//These could be done programatically
		addMetricResult("overallChanges", overallChanges);
		addMetricResult("maxAtT", maxAtT);
		addMetricResult("result_Zt", result_Zt);
		addMetricResult("result_It", result_It);
		addMetricResult("result_Yit",result_Yit);
		
	}
	
	@Override
	public void calculateResults() {
		ArrayList<VEntity> step_tm1 = (ArrayList<VEntity>) getParameter("step_tm1");
		ArrayList<VEntity> step_t = (ArrayList<VEntity>) getParameter("step_tm2");
		int currentStep = (int)getParameter("currentStep");
		
		
		if (step_tm1.size() != step_t.size()) {
			// System.out.println("Agent lists are not the same size.
			// Terminating metric.");
			// return;
		}
		
		for (int i = 0; i < step_tm1.size(); i++) {
			VEntity vat = step_t.get(i);
			VEntity vatm1 = step_tm1.get(i);
			if (vat.getID().compareTo(vatm1.getID()) != 0) {
				// System.out.println("Issue with comparing non-identical
				// agents. Terminating metric.");
				continue;
			}

			// if
			// (MetricRunner_ED.getCurrentState(vatm1).compareTo(MetricRunner_ED.getCurrentState(vat))
			// != 0 ){
			// cumulativeIndiv.put(vat.getID(), cumulativeIndiv.get(vat.getID())
			// + 1.0);
			// overallChanges[currentStep]++;
			// if (cumulativeIndiv.get(vat.getID()) > maxAtT[currentStep]){
			// maxAtT[currentStep] =
			// cumulativeIndiv.get(vat.getID()).intValue();
			// }
			// }

			if (vatm1.getParameterValueFromStringAsString(state)
					.compareTo(vat.getParameterValueFromStringAsString(state)) != 0) {

				// Increment count of agent change
				cumulativeIndiv.put(vat.getID(), cumulativeIndiv.get(vat.getID()) + 1.0);
				
				((int[])getMetricResult("overallChanges"))[currentStep]++;

				if (cumulativeIndiv.get(vat.getID()) >((int[])getMetricResult("overallChanges"))[currentStep]) {
					((int[])getMetricResult("overallChanges"))[currentStep] = cumulativeIndiv.get(vat.getID()).intValue();
				}
			}

		}
		

	}

}
