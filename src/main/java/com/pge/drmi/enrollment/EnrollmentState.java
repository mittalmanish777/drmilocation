package com.pge.drmi.enrollment;

public enum EnrollmentState {
	
	NEW("1"),
	PROPOSED("2"), 
	REJECTED("3");

	private String value;

	EnrollmentState(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
