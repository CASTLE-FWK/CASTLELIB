package castleComponents.representations;

import java.util.ArrayList;
import java.util.List;

import castleComponents.Agent;
import castleComponents.Entity;
import castleComponents.Enums;
import castleComponents.Environment;
import castleComponents.SemanticGroup;

public class LayoutParameters {
	Enums.RepresentationTypes representationType;
	Class<?> clazz;
	ArrayList<Entity> containedEntities;
	private boolean allowPhantoms = false;

	public LayoutParameters() {
		containedEntities = new ArrayList<Entity>();
	}

	public LayoutParameters(Enums.RepresentationTypes type) {
		this.representationType = type;
		containedEntities = new ArrayList<Entity>();
	}

	public void setRepresentationType(Enums.RepresentationTypes rt) {
		this.representationType = rt;
	}

	public void addEntityType(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Class<?> getEntityType() {
		return clazz;
	}

	public boolean allowPhantoms() {
		return allowPhantoms;
	}

	public void setAllowPhantoms(boolean b) {
		allowPhantoms = b;
	}

	public void addContainedEntities(Representation<Entity> r) {
//		List<?> entities = r.getEntities();
		containedEntities = (ArrayList<Entity>) r.getEntities();
//		containedEntities.addAll(entities);
	}

	public ArrayList<Entity> getContainedEntities() {
		return containedEntities;
	}

	public ArrayList<Environment> getContainedEnvironments() {
		ArrayList<Environment> envs = new ArrayList<Environment>();
		for (int i = 0; i < containedEntities.size(); i++) {
			if (containedEntities.get(i) instanceof Environment) {
				envs.add((Environment) containedEntities.get(i));
			}
		}
		return envs;
	}

	public ArrayList<SemanticGroup> getContainedGroups() {
		ArrayList<SemanticGroup> envs = new ArrayList<SemanticGroup>();
		for (int i = 0; i < containedEntities.size(); i++) {
			if (containedEntities.get(i) instanceof SemanticGroup) {
				envs.add((SemanticGroup) containedEntities.get(i));
			}
		}
		return envs;
	}

	public ArrayList<Agent> getContainedAgents() {
		ArrayList<Agent> envs = new ArrayList<Agent>();
		for (int i = 0; i < containedEntities.size(); i++) {
			if (containedEntities.get(i) instanceof Agent) {
				envs.add((Agent) containedEntities.get(i));
			}
		}
		return envs;
	}

}