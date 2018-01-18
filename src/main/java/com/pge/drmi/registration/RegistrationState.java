package com.pge.drmi.registration;

import java.util.ArrayList;
import java.util.List;

public enum RegistrationState {
	
	PROPOSED("1"),
	PENDING_AUTO("2"),
	PENDING_MANUAL("3"),
	VALIDATION_FAILED("4"),
	PENDING_SUBMISSION("5"),
	SUBMITTED("6"),
	REJECTED("7"),
	SUBMISSION_FAILED("8"),
	PENDING_TERMINATION("9"),
	TERMINATION_SUBMITTED("10"),
	TERMINATION_FAILED("11"),
	PENDING_REGISTRATION_NAME_REFRESH("12"), 
	REGISTRATION_NAME_SUBMITTED("13"),
	REGISTRATION_NAME_FAILED("14"),
	REGISTRATION_NAME_REJECTED("15"),
	NEW("16"),
	PENDING("17"),
	CONFIRMED("18"),
	TERMINATED("19"),
	GRDT_PENDING("20"),
	GRDT_COMPLETE("21"),
	SQMD_PENDING("22"),
	SQMD_FAILED("23"),
	READY_TO_BID("24"),
	REGISTRATION_NAME_BATCH_SUCCESS("25"),
	TERMINATED_BATCH_SUCCESS("26"),
	UNDEFINED("99");
	

	private String value;

	RegistrationState(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static RegistrationState getStateFromValue(String value) {
		for(RegistrationState state : RegistrationState.values()) {
			if(state.getValue().equals(value)) {
				return state;
			}
		}
		return RegistrationState.UNDEFINED;
	}
	
	public static List<String> getStatusForTermiateRegistrations() {
		List<String> statusList = new ArrayList<>();
		
		statusList.add(PENDING_TERMINATION.getValue());
		statusList.add(PENDING_REGISTRATION_NAME_REFRESH.getValue());
		statusList.add(REGISTRATION_NAME_SUBMITTED.getValue());
		statusList.add(REGISTRATION_NAME_FAILED.getValue());
		statusList.add(REGISTRATION_NAME_REJECTED.getValue());
		statusList.add(PENDING.getValue());
		statusList.add(CONFIRMED.getValue());
		statusList.add(GRDT_PENDING.getValue());
		statusList.add(GRDT_COMPLETE.getValue());
		statusList.add(SQMD_PENDING.getValue());
		statusList.add(SQMD_FAILED.getValue());
		statusList.add(READY_TO_BID.getValue());
		
		return statusList;
	}

}
