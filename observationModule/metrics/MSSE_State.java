package observationModule.metrics;

import java.util.Arrays;

import repastGroups.objects.Vector2;

public class MSSE_State {
	int tupleSize;
	double[] tupleValues;
	
	int time;
	String stateName;
	
	//New version
	double[][] tupleGrid;
	Vector2 numGridsXY;
	
	public MSSE_State(String stateName, int tupleSize){
		this.tupleSize = tupleSize;
		tupleValues = new double[tupleSize];
		Arrays.fill(tupleValues, 0);
		this.stateName = stateName;		
	}
	
	public MSSE_State(String stateName, Vector2 numGridsXY){
		this.stateName = stateName;
		this.numGridsXY = numGridsXY;
		tupleGrid = new double[(int) this.numGridsXY.getX()][(int) this.numGridsXY.getY()];
		clear();				
	}
	
	public void setTupleAtXY(int X, int Y, double value){
		tupleGrid[X][Y] = value;
	}
	
	public MSSE_State(MSSE_State old){
		this.tupleValues = Arrays.copyOf(old.getTupleValues(), old.getTupleValues().length);
		this.tupleSize = this.tupleValues.length;
		this.stateName = old.getStateName();
	}
	
	public String getStateName(){
		return stateName;
	}
	
	public void setTupleAtX(int x, double value){
		tupleValues[x] = value;
	}
	
	public double[] getTupleValues(){
		return tupleValues;
	}
	
	public double getTupleValueAtX(int x){
		return tupleValues[x];
	}
	
	public void reset(){
		tupleValues = new double[tupleSize];
		Arrays.fill(tupleValues, 0);
	}
	
	public boolean compareState(MSSE_State cmp){
		double[] cmpTuple = cmp.getTupleValues();
		for (int i = 0; i < cmpTuple.length; i++){
			if (tupleValues[i] != cmpTuple[i]){
				return false;
			}
		}
		return true;
	}
	
	public void clear(){
		for (int i = 0; i < tupleGrid.length; i++){
			Arrays.fill(tupleGrid[i],0);
		}
	}
	
	@Override
	public String toString(){
		String str = "<";
		for (int i = 0; i < tupleValues.length; i++){
			str += tupleValues[i];
			if (i != tupleValues.length -1){
				str += ",";
			}
		}
		str += ">";
		return str;
	}
}