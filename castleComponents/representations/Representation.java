package castleComponents.representations;

import java.util.List;

import castleComponents.E;
import castleComponents.objects.Vector2;

public interface Representation<E> {
	
	public List<E> getEntities();
	public boolean addEntity(E e, Vector2 p);
	public boolean addEntities(List<E> es);
	public boolean removeEntity(E e);
	public boolean removeEntityByID(String id);
	public boolean initialize(Object...objects);
	public boolean initializeEntity(Object...objects);
	public boolean initializeEntities(Object...objects);
}
