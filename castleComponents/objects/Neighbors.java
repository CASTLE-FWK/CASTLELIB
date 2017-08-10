package castleComponents.objects;

import java.util.ArrayList;

public class Neighbors<T>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4843180692751679850L;

	T Neighbor_U;
	T Neighbor_UL;
	T Neighbor_UR;
	
	T Neighbor_L;
	T Neighbor_R;
	
	T Neighbor_D;
	T Neighbor_DL;
	T Neighbor_DR;
	
	
	
	public Neighbors(){
		
	}
	
	public List<T> getAsList(){
		ArrayList<T> nei = new ArrayList<T>();
		nei.add(Neighbor_D);
		nei.add(Neighbor_DL);
		nei.add(Neighbor_DR);
		nei.add(Neighbor_L);
		nei.add(Neighbor_R);
		nei.add(Neighbor_U);
		nei.add(Neighbor_UL);
		nei.add(Neighbor_UR);
		return (List<T>) nei;
		
	}



	public T getU() {
		return Neighbor_U;
	}



	public void setU(T neighbor_U) {
		Neighbor_U = neighbor_U;
	}



	public T getUL() {
		return Neighbor_UL;
	}



	public void setUL(T neighbor_UL) {
		Neighbor_UL = neighbor_UL;
	}



	public T getUR() {
		return Neighbor_UR;
	}



	public void setUR(T neighbor_UR) {
		Neighbor_UR = neighbor_UR;
	}



	public T getL() {
		return Neighbor_L;
	}



	public void setL(T neighbor_L) {
		Neighbor_L = neighbor_L;
	}



	public T getR() {
		return Neighbor_R;
	}



	public void setR(T neighbor_R) {
		Neighbor_R = neighbor_R;
	}



	public T getD() {
		return Neighbor_D;
	}



	public void setD(T neighbor_D) {
		Neighbor_D = neighbor_D;
	}



	public T getDL() {
		return Neighbor_DL;
	}



	public void setDL(T neighbor_DL) {
		Neighbor_DL = neighbor_DL;
	}



	public T getDR() {
		return Neighbor_DR;
	}



	public void setDR(T neighbor_DR) {
		Neighbor_DR = neighbor_DR;
	}

}
