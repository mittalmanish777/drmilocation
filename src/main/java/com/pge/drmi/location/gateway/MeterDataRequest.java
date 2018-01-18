package com.pge.drmi.location.gateway;

import java.util.List;

public class MeterDataRequest {

	private Id[] arrayOfIds;

	private String startDate;

	private String endDate;

	private String idType = "UUID";

	private String responseLevel = "SUMMARY";

	public Id[] getArrayOfIds() {
		return arrayOfIds;
	}

	public void setArrayOfIds(Id[] arrayOfIds) {
		this.arrayOfIds = arrayOfIds;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getResponseLevel() {
		return responseLevel;
	}

	public void setResponseLevel(String responseLevel) {
		this.responseLevel = responseLevel;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	private String direction = "DELIVERED";

	public static class Id {
		private String id;

		public String getId() {
			return id;
		}

		public Id(String id) {
			this.id = id;
		}
	}

	public MeterDataRequest(List<String> ids, String startDate, String endDate) {
		arrayOfIds = new Id[ids.size()];
		for (int i = 0; i < ids.size(); i++) {
			arrayOfIds[i] = new Id(ids.get(i));
		}
		this.startDate = startDate;
		this.endDate = endDate;

	}
	

}
