package com.pge.drms.location.jobs.test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import com.pge.drmi.config.ConfigurationRepository;
import com.pge.drmi.location.Location;
import com.pge.drmi.location.LocationRepository;
import com.pge.drmi.location.LocationState;
import com.pge.drmi.location.jobs.SubmitJob;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class SubmitJobTest {

	@Mock
	private static LocationRepository locationRepository;
	
	@Mock
	private static ConfigurationRepository configurationRepository;

	@InjectMocks
	private static SubmitJob submitJobToCAISOService = new SubmitJob();
	
	private Integer newStatus = new Integer(2);
	private Integer size = 0;
	private String UPGE = "UPGE";
	private String LPGE = "LPGE";
	
	private void mockingObjects(List<Location> locationList) {
		submitJobToCAISOService.setNumberOfRecordstoFetchForSubmit(20);
		List<String> locationStates = new ArrayList<>();
		locationStates.add(LocationState.PENDING_BATCH.getValue());
		locationStates.add(LocationState.PENDING_MANUAL.getValue());
	
		Pageable pageableFetch = new PageRequest(0, 20, Direction.ASC, "locationId"); 
		Mockito.when(locationRepository.findByStatusIn(locationStates, pageableFetch)).thenReturn(locationList);
		//Mockito.when(configurationRepository.findByProcessName("UDCID")).thenReturn(UPGE);
		
		Mockito.when(locationRepository.save(locationList)).thenReturn(locationList);			
	}

	@Test
	public void testsubmitLocationRecordsToCAISO_NoEligible_location_records() {
		
		List<Location> locationList = new ArrayList<Location>();			
		mockingObjects(locationList);		
		size = submitJobToCAISOService.submitLocationRecordsToCAISO();
		assertTrue(size <= 0);

	}
	
	@Test
	public void testsubmitLocationRecordsToCAISO_SubmitFailed() {
		
	

	
	}
}
