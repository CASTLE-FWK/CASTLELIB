package castleComponents.representations;

import java.util.List;

import castleComponents.Entity;

public interface Representation {
	
	public List<Entity> getEntities();
	public boolean addEntity(Entity e);
	public boolean removeEntity(Entity e);
	public boolean removeEntityByID(String id);
	public boolean initialize(Object...objects);
	public boolean initializeEntity(Object...objects);
	public boolean initializeEntities(Object...objects);
}
