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
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import com.pge.drmi.config.ConfigEntries;
import com.pge.drmi.config.ConfigurationRepository;
import com.pge.drmi.enrollment.Enrollment;
import com.pge.drmi.location.Location;
import com.pge.drmi.location.LocationRepository;
import com.pge.drmi.location.jobs.EligibilityJob;
import com.pge.drmi.location.jobs.PreSubmitJob;
import com.pge.drmi.program.Program;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@SpringBootConfiguration
public class PreSubmitJobTest {
	@Mock
	private static LocationRepository locationRepository;
	
	@Mock
	private ConfigurationRepository configurationRepository;
	
	@InjectMocks
	private static PreSubmitJob preSubmitJob = new PreSubmitJob();
	private String newStatus = "2";
	
	private void mockingObjects(List<Location> locationList) {
		preSubmitJob.setNumberOfRecordstoFetchForPreSubmit(10);
			
		Pageable pageableFetch = new PageRequest(0, 15);
		Mockito.when(locationRepository.findByStatus(newStatus, pageableFetch)).thenReturn(locationList);
		Mockito.when(configurationRepository.findByProcessName(ConfigEntries.LOCATION_AUTO_SUBMIT).getValue()).thenReturn("Auto");
	}
	
	@Test
	public void testValidateEnrollmentAndCreateLocationRecords_NoEligible_Location_1() {
	

		/*List<Location> locationList = new ArrayList<>();	
		
		mockingObjects(locationList);		
		int size = preSubmitJob.preSubmitJob();
		assertTrue(size <= 0);*/

	}
}
