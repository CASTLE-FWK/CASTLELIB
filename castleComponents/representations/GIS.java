package castleComponents.representations;

import java.util.List;

import castleComponents.Entity;

/**
 * GIS representation.
 * I have no idea how to do this.
 * @author lachlan
 *
 */
public class GIS implements Representation{

	@Override
	public List<Entity> getEntities() {
		// TODO Auto-generated method stub
		return null;
	}
	//TODO: ALL
	
	public GIS(){
		System.out.println("THE GIS REPRESENTATION CURRENTLY DOES NOTHING");
	}

	@Override
	public boolean addEntity(Entity e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeEntity(Entity e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeEntityByID(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean initialize(Object... objects) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean initializeEntity(Object... objects) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean initializeEntities(Object... objects) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addEntities(List<Entity> es) {
		// TODO Auto-generated method stub
		return false;
	}
}
