package com.pge.drmi.location;

import java.util.Arrays;
import java.util.List;

public enum LocationState {
	
	PROPOSED("1"), 
	PENDING_BATCH("2"),
	PENDING_MANUAL("3"),
	SUBMITTED("4"),
	BATCH_FAILED("5"),
	PROCESSING("6"),
	PENDING("7"),
	NEW("8"),
	INACTIVE("9"),
	ACTIVE("10"),
	WITHDRAWN("11"),
	DUPLICATE("12"),
	PENDING_DEFEND("13"), 
	DISPUTED("14"),
	PENDING_END_DATED("15"), 
	END_DATED("16"),
	NON_CREATABLE("17"),
	REJECTED("18"),
	IN_DEFEND("19"),
	DEFEND_SUBMITTED("20"),	
	DEFEND_BATCH_FAILED("22"),
	DEFEND_REJECTED("23"),
	UNDEFINED("99");
	
	private String value;

	LocationState(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static List<LocationState> getStatusForRetrieve() {
		return Arrays.asList(new LocationState[] { DISPUTED, INACTIVE, PENDING, ACTIVE, DUPLICATE, END_DATED, WITHDRAWN });
	}
	
	public static List<LocationState> getStatusForMeterData() {
		return Arrays.asList(new LocationState[] { INACTIVE, PENDING, PROPOSED, PENDING_BATCH, PENDING_MANUAL, SUBMITTED, ACTIVE }); 
	}
	
	public static LocationState getStateFromValue(String value) {
		for(LocationState state : LocationState.values()) {
			if(state.getValue().equals(value)) {
				return state;
			}
		}
		return LocationState.UNDEFINED;
	}
	
	public static LocationState getNextState(LocationState source, LocationState target) {
		if(source == LocationState.SUBMITTED || source == LocationState.DEFEND_SUBMITTED) {
			return source;
		}
		if (source == LocationState.IN_DEFEND
				&& (target == LocationState.PENDING || target == LocationState.DUPLICATE)) {
			return LocationState.IN_DEFEND;
		}
		if(source == LocationState.NON_CREATABLE && target == LocationState.DISPUTED) { //Fix for Non creatable getting refreshed to Disputed during retrieve.
			return LocationState.NON_CREATABLE;
		}
		return target;
	}
	

}
