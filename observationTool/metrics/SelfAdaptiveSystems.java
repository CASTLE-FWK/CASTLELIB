package observationTool.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import castleComponents.objects.Vector2;
import castleComponents.representations.Continuous;
import experimentExecution.MetricInfo;
import experimentExecution.MetricVariableMapping;
import observationTool.Universals;
import observationTool.VEntity;
import castleComponents.Interaction;

public class SelfAdaptiveSystems extends MetricBase {

	final String STATE_1 = "STATE_1";

	public SelfAdaptiveSystems(MetricInfo mi) {
		// TODO Auto-generated constructor stub
		super("SelfAdaptiveSystems", mi);
	}

	public double KaddoumWAT(ArrayList<VEntity> agents, HashMap<String, VEntity> prevAgents,
			HashMap<String, ArrayList<Interaction>> interactions, double workingTime) {
		double WATNeighbourDist = 5;
		double adaptivityTime = 0.0;

		MetricVariableMapping mvm1 = metricVariableMappings.get(STATE_1);
		for (VEntity v : agents) {
			if (entityIsOfType(v, mvm1)) {
				if (v.getEntityID().getEntityType().compareToIgnoreCase(Universals.BIRD) == 0) {
					int currNum = Universals.numberOfNeighbours(v, agents, WATNeighbourDist);
					VEntity pv = prevAgents.get(v.getName());
					if (pv == null) {
						continue;
					}
					int prevNum = Universals.numberOfNeighbours(pv, prevAgents.values(), WATNeighbourDist);
					// If no change, continue
					if (prevNum == currNum)
						continue;

				} else {
					// Default behaviour
					boolean lifeState = isParameterEqualToDesiredValue(v, mvm1);
					VEntity pv = prevAgents.get(v.getName());
					if (pv == null) {

						continue;
					}
					boolean prevState = isParameterEqualToDesiredValue(pv, mvm1);
					if (lifeState == prevState) {
						continue;
					}
				}

				ArrayList<Interaction> theAgentsInteractions = interactions.get(v.getName());
				if (theAgentsInteractions == null) {
					// System.out.println("Issue here (1)");
				}
				adaptivityTime += theAgentsInteractions.size();
				System.out.println(":: " + adaptivityTime);
			}
		}
		return (adaptivityTime / workingTime);
	}

	public double PerfSit(ArrayList<VEntity> agents, HashMap<String, VEntity> prevAgents, Vector2 dimensions,
			MetricParameters mp) {
		double cMax = 0.0;
		double subsitSum = 0.0;

		MetricVariableMapping mvm1 = metricVariableMappings.get(STATE_1);

		double neighbourDist = (Double) mp.getParameterValue("neighbour-distance");

		Continuous<VEntity> theCont = new Continuous<VEntity>();
		Iterator<Entry<String, VEntity>> it = prevAgents.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, VEntity> pair = (Map.Entry<String, VEntity>) it.next();
			VEntity agt = pair.getValue();
			theCont.addEntity(agt, agt.getPosition());
		}

		for (VEntity v : agents) {
			if (entityIsOfType(v, mvm1)) {
				if (v.getEntityID().getEntityType().compareToIgnoreCase(Universals.BIRD) == 0) {
					int currNum = Universals.numberOfNeighbours(v, agents, neighbourDist);
					VEntity pv = prevAgents.get(v.getName());
					if (pv == null) {
						continue;
					}
					int prevNum = Universals.numberOfNeighbours(pv, prevAgents.values(), neighbourDist);

					// If no change, continue
					if (prevNum != currNum) {
						ArrayList<VEntity> neighbours = (ArrayList<VEntity>) theCont
								.getNeighborsFromVector(v.getPosition(), neighbourDist);
						for (VEntity vn : neighbours) {
							int tmpCurrNum = Universals.numberOfNeighbours(vn, agents, neighbourDist);
							VEntity pvn = prevAgents.get(vn.getName());
							if (pvn == null) {
								continue;
							}
							int tmpPrevNum = Universals.numberOfNeighbours(pvn, prevAgents.values(), neighbourDist);
							if (tmpCurrNum != tmpPrevNum) {
								subsitSum++;
							}
						}
						cMax += neighbours.size();
					}

				} else {
					boolean lifeState = isParameterEqualToDesiredValue(v, mvm1);
					VEntity pv = prevAgents.get(v.getName());
					if (pv == null) {
						// System.out.println("Agent didnt exist...");
						continue;
					}
					boolean prevState = isParameterEqualToDesiredValue(pv, mvm1);
					if (lifeState == prevState) {
						continue;
					}
					//Get agents interacted with in prevous step
					
					//
					
					
					
					if (lifeState) {
						if (!prevState) {
							ArrayList<VEntity> neighbours = (ArrayList<VEntity>) theCont
									.getNeighborsFromVector(v.getPosition(), neighbourDist);
							int lifeCount = 0;
							for (VEntity n : neighbours) {
								if (entityIsOfType(n, mvm1)) {
									if (isParameterEqualToDesiredValue(n, mvm1)) {
										lifeCount++;
									}
								}
							}
							subsitSum += lifeCount;
							cMax += neighbours.size();
						}
					} else {
						if (prevState) {
							subsitSum += 3.0;
							cMax += 3.0;
						}
					}
				}
			}

		}
		// System.out.println(subSitSum+" "+cMax);
		if (Double.isInfinite(cMax) || Double.isNaN(cMax) || cMax == 0) {
			return (1.0 - subsitSum);
		} else {
			return (1.0 - subsitSum / cMax);
		}
	}

}
