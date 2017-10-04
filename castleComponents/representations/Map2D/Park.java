package castleComponents.representations.Map2D;

import java.util.HashMap;

import castleComponents.E;

public class Park {
	int maxCapacity;
	int currentCapacity;
	HashMap<String, E> currentOccupants;
	
	public Park(){
		
	}
	
	public boolean addOccupant(E e){
		if (currentCapacity < maxCapacity) {
			currentOccupants.put(e.getID(), e);
			currentCapacity = currentOccupants.size();
			return true;
		} else {
			return false;
		}
	}
	
	public E removeOccupant(String id){
		E e = currentOccupants.get(id);
		if (e != null){
			currentCapacity = currentOccupants.size();
			return e;
		} else {
			return null;
		}		
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public int getCurrentCapacity() {
		return currentCapacity;
	}

	public void setCurrentCapacity(int currentCapacity) {
		this.currentCapacity = currentCapacity;
	}

	public HashMap<String, E> getCurrentOccupants() {
		return currentOccupants;
	}
	
	public boolean freeSpaces(){
		return (currentCapacity < maxCapacity);
	}
	
}
