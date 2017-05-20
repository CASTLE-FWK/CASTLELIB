package observationTool.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import castleComponents.objects.Vector2;
import castleComponents.representations.Grid;
import observationTool.VEntity;
import stdSimLib.Interaction;

public class SelfAdaptiveSystems {

	public SelfAdaptiveSystems() {
		// TODO Auto-generated constructor stub
	}
	
	public static double KaddoumWAT(ArrayList<VEntity> agents, HashMap<String, VEntity> prevAgents, HashMap<String, ArrayList<Interaction>> interactions, double workingTime){
		double adaptivityTime = 0.0;
		for (VEntity v : agents){
			boolean lifeState = v.getParameterValueFromStringAsString("Alive").compareToIgnoreCase("true") == 0;
			VEntity pv = prevAgents.get(v.getName());
			if (pv == null){
				System.out.println("Agent didnt exist...");
				continue;
			}
			boolean prevState = pv.getParameterValueFromStringAsString("Alive").compareToIgnoreCase("true") == 0;
			if (lifeState  == prevState){
				continue;
			}
			
			
			ArrayList<Interaction> theAgentsInteractions = interactions.get(v.getName());
			if (theAgentsInteractions == null){
				System.out.println("Issue here (1)");
			}
			adaptivityTime += theAgentsInteractions.size();

		}
		return (adaptivityTime/workingTime);
	}
	
	public static double PerfSit(ArrayList<VEntity> agents, HashMap<String, VEntity> prevAgents, Vector2 dimensions){
		double cMax = 0.0; 
		double subsitSum = 0.0;
		
		Grid<VEntity> theGrid = new Grid<VEntity>(VEntity.class, (int)dimensions.getX(), (int)dimensions.getY());
		Iterator<Entry<String, VEntity>> it = prevAgents.entrySet().iterator();
		while (it.hasNext()){
			Map.Entry<String, VEntity> pair = (Map.Entry<String, VEntity>)it.next();
			VEntity agt = pair.getValue();
			theGrid.addCell(agt, agt.getPosition());
		}
		
		for (VEntity v : agents){
			boolean lifeState = v.getParameterValueFromStringAsString("Alive").compareToIgnoreCase("true") == 0;
			VEntity pv = prevAgents.get(v.getName());
			if (pv == null){
				System.out.println("Agent didnt exist...");
				continue;
			}
			boolean prevState = pv.getParameterValueFromStringAsString("Alive").compareToIgnoreCase("true") == 0;
			if (lifeState  == prevState){
				continue;
			}
			if (lifeState) {
				if (!prevState){
					ArrayList<VEntity> neighbours = (ArrayList<VEntity>) theGrid.getNeighbours((int)v.getPosition().getX(), (int)v.getPosition().getY(), 1);						
					int lifeCount = 0;
					for (VEntity n : neighbours){
						if (n.getParameterValueFromStringAsString("Alive").compareToIgnoreCase("true") == 0){
							lifeCount++;
						}
					}
					subsitSum += lifeCount;
					cMax += 8.0;
				}
			} else {
				if (prevState){
					subsitSum += 3.0;
					cMax += 3.0;
				}
			}
			
		}
//		System.out.println(subSitSum+"  "+cMax);
		if (Double.isInfinite(cMax) || Double.isNaN(cMax) || cMax == 0){
			return (1.0 - subsitSum);
		} else { 
			return (1.0 - subsitSum / cMax);
		}
	}

}
