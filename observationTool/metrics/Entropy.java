package observationTool.metrics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import castleComponents.objects.Vector2;
import castleComponents.representations.Continuous;
import castleComponents.representations.Grid;
import experimentExecution.MetricInfo;
import experimentExecution.MetricVariableMapping;
import observationTool.Universals;
import observationTool.VEntity;
import castleComponents.Interaction;

public class Entropy extends MetricBase {

	private final double BOLTZMANN_CONSTANT = (1.38065 * Math.pow(10, -23));
	final String STATE_1 = "STATE_1";

	public Entropy(MetricInfo mi) {
		super("Entropy", mi);
	}

	public double BotzmannEntropy(ArrayList<VEntity> agents) {
		double w = 0.0;
		int numberOfAgents = agents.size();
		BigDecimal nf = new BigDecimal(1);
		// Get factorial of all agents
		for (int i = 1; i <= numberOfAgents; i++) {
			nf.multiply(new BigDecimal(i));
		}
		int countAlive = 0;
		int countDead = 0;
		for (VEntity n : agents) {
			if (n.getParameterValueFromStringAsString("Alive").compareToIgnoreCase("true") == 0) {
				countAlive++;
			} else {
				countDead++;
			}
		}
		BigDecimal n_dead = new BigDecimal(1);
		for (int i = 1; i <= countAlive; i++) {
			n_dead.multiply(new BigDecimal(i));
		}
		BigDecimal n_alive = new BigDecimal(1);
		for (int i = 1; i <= countDead; i++) {
			n_alive.multiply(new BigDecimal(i));
		}

		w = BOLTZMANN_CONSTANT * Math.log(nf.divide(n_dead.multiply(n_alive)).doubleValue());
		return w;
	}

	public double shannonEntropy_Neighbours(ArrayList<VEntity> agents, Vector2 gridSize, MetricParameters mp) {
		double d = 0.0;
		double neighbourDist = (Double) mp.getParameterValue("se-neighbour-dist");
		MetricVariableMapping mvm1 = metricVariableMappings.get(STATE_1);

		Continuous<VEntity> theCont = new Continuous<VEntity>();
		for (VEntity agt : agents) {
			theCont.addEntity(agt, agt.getPosition());
		}
		for (VEntity v : agents) {
			if (entityIsOfType(v, mvm1)) {
				if (v.getEntityID().getEntityType().compareToIgnoreCase(Universals.BIRD) == 0) {
					int currNum = Universals.numberOfNeighbours(v, agents, neighbourDist);
					if (currNum != 0) {
						double prob = (double) currNum / (double) agents.size();
						d += prob * Math.log(prob);
					}
				} else {
					Vector2 pos = v.getPosition();
					ArrayList<VEntity> neighbours = (ArrayList<VEntity>) theCont.getNeighborsFromVector(v.getPosition(),
							neighbourDist);
					double prob = 0;
					for (VEntity n : neighbours) {
						if (entityIsOfType(n, mvm1)) {
							if (isParameterEqualToDesiredValue(n, mvm1)) {
								prob++;
							}
						}
					}
					if (neighbours.size() == 0) {
						prob = 0;
					} else {
						prob = prob / (double) neighbours.size();
					}
					if (prob != 0) {
						d += prob * Math.log(prob);
					}
				}
			}
		}
		return -d;
	}

	public double shannonEntropy_Change(ArrayList<VEntity> agents, HashMap<String, VEntity> prevAgents,
			MetricParameters mp) {
		double d = 0.0;

		MetricVariableMapping mvm1 = metricVariableMappings.get(STATE_1);
		double neighbourDist = (Double) mp.getParameterValue("se-neighbour-dist");
		final double SEVENEIGHTHS = 7.0 / 8.0;
		final double ONEEIGHTH = 1.0 / 8.0;
		for (VEntity v : agents) {
			if (entityIsOfType(v, mvm1)) {
				if (v.getEntityID().getEntityType().compareToIgnoreCase(Universals.BIRD) == 0) {
					int currNum = Universals.numberOfNeighbours(v, agents, neighbourDist);
					VEntity pv = prevAgents.get(v.getName());
					if (pv == null) {
						continue;
					}
					int prevNum = Universals.numberOfNeighbours(pv, prevAgents.values(), neighbourDist);
					if (prevNum != currNum) {
						if (currNum > 1)
							d += ((currNum - 1) / currNum) * Math.log((currNum - 1) / currNum);
					} else {
						if (currNum > 1)
							d += (1 / currNum) * Math.log(1 / currNum);
					}

				} else {

					boolean lifeState = isParameterEqualToDesiredValue(v, mvm1);
					VEntity pv = prevAgents.get(v.getName());
					if (pv == null) {
						continue;
					}
					boolean prevState = isParameterEqualToDesiredValue(pv, mvm1);
					if (lifeState) {
						if (!prevState) {
							d += (SEVENEIGHTHS) * Math.log(SEVENEIGHTHS);
						}
					} else {
						if (prevState) {
							d += (ONEEIGHTH) * Math.log(ONEEIGHTH);
						}
					}
					if (d == Double.NaN) {
						System.out.println(d);
					}
				}
			}
		}

		return -d;

	}

	public double conditionalEntropy(ArrayList<VEntity> agents, HashMap<String, VEntity> prevAgents, Vector2 gridSize,
			MetricParameters mp) {
		double d = 0.0;
		final double SEVENEIGHTHS = 7.0 / 8.0;
		final double ONEEIGHTH = 1.0 / 8.0;
		double neighbourDist = (Double) mp.getParameterValue("se-neighbour-dist");
		MetricVariableMapping mvm1 = metricVariableMappings.get(STATE_1);

		// p(x,y) = p(probabilty that neighbours are in their current states,
		// currentState of this) (actually, vice versa)
		Continuous<VEntity> theCont = new Continuous<VEntity>();
		for (VEntity agt : agents) {
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
					if (prevNum != currNum) {
						if (currNum > 1)
							d += ((currNum - 1) / currNum) * Math.log((currNum - 1) / currNum);
					} else {
						if (currNum > 1)
							d += (1 / currNum) * Math.log(1 / currNum);
					}

				} else {

					VEntity pv = prevAgents.get(v.getName());
					if (pv == null) {
						// System.out.println("Agent didnt exist...");
						continue;
					}
					boolean prevState = isParameterEqualToDesiredValue(pv, mvm1);
					// Count alive neighbours
					Vector2 pos = v.getPosition();
					ArrayList<VEntity> neighbours = (ArrayList<VEntity>) theCont.getNeighborsFromVector(v.getPosition(),
							neighbourDist);
					double prob = 0;
					for (VEntity n : neighbours) {
						if (entityIsOfType(v, mvm1)) {
							boolean neighbourState = isParameterEqualToDesiredValue(n, mvm1);
							if (neighbourState) {
								if (prevState) {
									d += (2.0 / 64.0) * Math.log(ONEEIGHTH / (2.0 / 64.0));
								} else {
									d += (16.0 / 64.0) * Math.log(SEVENEIGHTHS / (16.0 / 64.0));
								}
							} else {
								if (prevState) {
									d += (7.0 / 64.0) * Math.log(ONEEIGHTH / (7.0 / 64.0));
								} else {
									d += (56.0 / 64.0) * Math.log(SEVENEIGHTHS / (56.0 / 64.0));
								}
							}
						}
					}
				}
			}
		}
		return d;
	}

	public double shannonEntropy_NeighboursSN(HashMap<String, VEntity> agents, Vector2 gridSize, MetricParameters mp,
			HashMap<String, ArrayList<Interaction>> inters) {
		double d = 0.0;
		MetricVariableMapping mvm1 = metricVariableMappings.get(STATE_1);
		for (VEntity v : agents.values()) {
			if (entityIsOfType(v, mvm1)) {

				double prob = 0;
				int size = 0;
				ArrayList<Interaction> agtInt = inters.get(v.getID());
				for (Interaction in : agtInt) {
					VEntity n = agents.get(in.getVentityTo().getID());
					if (n == null) {
						continue;
					}
					if (entityIsOfType(n, mvm1)) {
						size++;
						if (isParameterEqualToDesiredValue(n, mvm1)) {
							prob++;
						}
					}
				}
				if (size == 0) {
					prob = 0;
				} else {
					prob = prob / (double) size;
				}
				if (prob != 0) {
					d += prob * Math.log(prob);
				}

			}
		}
		return -d;
	}

	public double shannonEntropy_ChangeSN(ArrayList<VEntity> agents, HashMap<String, VEntity> prevAgents,
			MetricParameters mp) {
		double d = 0.0;

		MetricVariableMapping mvm1 = metricVariableMappings.get(STATE_1);
		for (VEntity v : agents) {
			if (entityIsOfType(v, mvm1)) {
				VEntity pv = prevAgents.get(v.getName());
				if (pv == null) {
					continue;
				}
				boolean same = compareParameters(v, pv, mvm1);

				if (same) {
					d += ((double) (agents.size() - 1) / (double) agents.size())
							* Math.log((agents.size() - 1) / (double) agents.size());
				} else {
					d += (1.0 / (double) agents.size()) * Math.log(1 / (double) agents.size());
				}

				if (d == Double.NaN) {
					System.out.println(d);

				}
			}
		}
		return -d;

	}

}
