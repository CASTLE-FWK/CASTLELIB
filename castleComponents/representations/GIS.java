package castleComponents.representations;

import java.util.List;

import castleComponents.objects.Vector2;

/**
 * GIS representation.
 * I have no idea how to do this.
 * @author lachlan
 *
 */
public class GIS<E> implements Representation<E>{

	@Override
	public List<E> getEntities() {
		// TODO Auto-generated method stub
		return null;
	}
	//TODO: ALL
	
	public GIS(){
		System.out.println("THE GIS REPRESENTATION CURRENTLY DOES NOTHING");
	}

	public boolean addEntity(E e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeEntity(E e) {
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
	public boolean addEntities(List<E> es) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addEntity(E e, Vector2 p) {
		// TODO Auto-generated method stub
		return false;
	}
}
