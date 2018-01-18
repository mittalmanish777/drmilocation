package com.pge.drmi.location.gateway;

import java.util.HashMap;
import java.util.Map;

public class MeterDataResponse {

	private ArrayofIds ArrayOfIds;

	public ArrayofIds getArrayOfIds() {
		return ArrayOfIds;
	}

	public void setArrayOfIds(ArrayofIds arrayOfIds) {
		ArrayOfIds = arrayOfIds;
	}

	public static class ArrayofIds {
		public Header[] getHeader() {
			return Header;
		}

		public void setHeader(Header[] header) {
			this.Header = header;
		}

		private Header[] Header;

	}

	public static class Header {
		private String Id;
		private String IdType;
		private String AverageReadingPercent;

		public String getId() {
			return Id;
		}

		public void setId(String id) {
			Id = id;
		}

		public String getIdType() {
			return IdType;
		}

		public void setIdType(String idType) {
			IdType = idType;
		}

		public String getAverageReadingPercent() {
			return AverageReadingPercent;
		}

		public void setAverageReadingPercent(String averageReadingPercent) {
			AverageReadingPercent = averageReadingPercent;
		}

		

	}
	
	private Map<String, String> map = new HashMap<>();

	public String getAverageReadingPercent(String id) {
		
		if(map.isEmpty()) {
			Header[] headers = ArrayOfIds.getHeader();
			for(Header header : headers) {
				map.put(header.Id, header.AverageReadingPercent);
			}
		}
		return map.get(id);
		
	}

}
