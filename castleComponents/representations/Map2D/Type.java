package castleComponents.representations.Map2D;

public enum Type {
	ROAD_H, ROAD_V, 
	ONEWAY_N, ONEWAY_S,
	ONEWAY_E, ONEWAY_W,
	FOUR_WAY, T_SEC,
	NOGO, PARK, 
	MAP, UNSET,
	EVENT, ENTRY;
	
	public boolean isHoriz() {
		return (this == ROAD_H || this == ONEWAY_E || this == ONEWAY_W);
	}
	
	public boolean isVert() {
		return (this == ROAD_V || this == ONEWAY_N || this == ONEWAY_S);
	}
	
	public boolean isJunction() {
		return (this == Type.FOUR_WAY || this == T_SEC);
	}
}