package com.pge.drmi.location.gateway;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

@Service
public class MeterReadGateway {

	@Value("${drmi.location.meterdata.authuser}")
	private String authUser = "DRMI";

	@Value("${drmi.location.meterdata.authpassword}")
	private String authpassword = "6=N]dHrK$R";

	@Value("${drmi.location.meterdata.availUrl}")
	private String availUrl = "https://apiqa.pge.com/dev/demandresponse/v1/meterdata/availability";

	public RestTemplate getRestTemplate3() {
		CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier())
				.build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(authUser, authpassword));
		return restTemplate;
	}

	public MeterDataResponse getMeterReadData(MeterDataRequest mdrq) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		Gson gson = new Gson();
		String jsonVal = gson.toJson(mdrq);
		System.out.println(jsonVal);

		HttpEntity<String> entity = new HttpEntity<String>(jsonVal, headers);

		ResponseEntity<String> response = getRestTemplate3().exchange(availUrl, HttpMethod.POST, entity, String.class);
		System.out.println(response.getBody());

		MeterDataResponse mdrs = gson.fromJson(response.getBody(), MeterDataResponse.class);
		return mdrs;
	}

	public static void main(String[] args) throws Exception {

		//LocalDateTime now = LocalDateTime.now();
	  DateFormat format = new SimpleDateFormat("yyyy-MM-ddThh:mm:ss.SSSZ");
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(new java.util.Date());
	    cal.add(Calendar.DAY_OF_MONTH, -50);
		String startDate = format.format(cal.getTime());
		cal.add(Calendar.DAY_OF_MONTH, 45);
		String endDate = format.format(cal.getTime());

		
		  startDate = "2016-01-01T21:40:50.200Z"; endDate = "2016-10-05T21:40:50.200Z";
		 
		MeterDataRequest request = new MeterDataRequest(Arrays.asList(new String[] { "2587120518","2587120518" }),
				startDate, endDate);

		new MeterReadGateway().getMeterReadData(request);
	}

	public static void testResponse() {
		MeterDataResponse temp = new MeterDataResponse();
		MeterDataResponse.ArrayofIds arrayofIds = new MeterDataResponse.ArrayofIds();
		MeterDataResponse.Header header = new MeterDataResponse.Header();
		MeterDataResponse.Header[] hdrlist = new MeterDataResponse.Header[] { header };
		header.setId("123");
		header.setIdType("UUID");
		header.setAverageReadingPercent("12");
		temp.setArrayOfIds(arrayofIds);
		arrayofIds.setHeader(hdrlist);
		Gson gson = new Gson();
		System.out.println(gson.toJson(temp));
	}

}
