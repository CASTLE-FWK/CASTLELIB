package castleComponents.representations.MapGraph;

import java.util.HashMap;

import castleComponents.Entity;

public class Park {
	int maxCapacity = 50;
	int currentCapacity = 0;
	HashMap<String, Entity> currentOccupants;

	public Park() {
		currentOccupants = new HashMap<String, Entity>();
	}

	public boolean addOccupant(Entity e) {
		if (currentCapacity < maxCapacity) {
			currentOccupants.put(e.getID(), e);
			currentCapacity = currentOccupants.size();
			return true;
		} else {
			return false;
		}
	}
	
	public Entity getEntity(String id) {
		return currentOccupants.get(id);
	}

	public Entity removeOccupant(String id) {
		Entity e = currentOccupants.get(id);
		if (e != null) {
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

	public HashMap<String, Entity> getCurrentOccupants() {
		return currentOccupants;
	}

	public boolean freeSpaces() {
		return (currentCapacity < maxCapacity);
	}

}
