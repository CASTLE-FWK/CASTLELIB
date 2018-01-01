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
}



//ROAD_H, ROAD_V, 4WAY, ONEWAY_L, ONEWAY_R, 
	//ONEWAY_U, ONEWAY_D, T-SEC, NOGO, PARK, MAP, UNSET, EVENT_LOCATION
//-, |, +, <, >, ;, :, T, *, P, M, U, E
