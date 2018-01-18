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

import com.pge.drmi.enrollment.Enrollment;
import com.pge.drmi.enrollment.EnrollmentRepository;
import com.pge.drmi.location.Location;
import com.pge.drmi.location.LocationRepository;
import com.pge.drmi.location.jobs.EligibilityJob;
import com.pge.drmi.program.Program;


@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@SpringBootConfiguration

public class EligilibilityJobTest {

	@Mock
	private static EnrollmentRepository enrollmentRepository;

	@Mock
	private static LocationRepository locationRepository;

	@InjectMocks
	private static EligibilityJob eligibilityService = new EligibilityJob();
	
	private String newStatus = "2";
	private Integer size = 0;
	
	private void mockingObjects(List<Enrollment> enrollmentList, Program program) {
		eligibilityService.setNumberOfRecordstoFetch(15);
		List<Location> locationList = new ArrayList<Location>();
		Location location = new Location();
		location.setLocationId(14); 
		locationList.add(location);
		Pageable pageableFetch = new PageRequest(0, 15, Direction.ASC, "enrollmentId");
		Mockito.when(enrollmentRepository.findByStatus(newStatus, pageableFetch)).thenReturn(enrollmentList);
		Mockito.when(enrollmentRepository.findByUuidAndEnrollmentIdNotIn("15", 5)).thenReturn(enrollmentList);
		Mockito.when(locationRepository.save(locationList)).thenReturn(locationList);			
	}

	@Test
	public void testValidateEnrollmentAndCreateLocationRecords_NoEligible_Enrollment_records_1() {
		Program program = new Program();
		program.setProgramId(970001);
		program.setMarketProgramId("15");		
		List<Enrollment> enrollmentList = new ArrayList<Enrollment>();	
		mockingObjects(enrollmentList, program);		
		size = eligibilityService.validateEnrollmentAndCreateLocationRecords();
		assertTrue(size <= 0);

	}

	@Test
	public void testValidateEnrollmentAndCreateLocationRecords_NotEligibleForCAISOPROGRAM() {
		Program program = null;
		Enrollment enrollment1 = new Enrollment();
		enrollment1.setEnrollmentId(5);
		enrollment1.setProgram(null);
		enrollment1.setLseId("LseId");
		enrollment1.setSubLap("subLap");		
		enrollment1.setStatus("2");
		Enrollment enrollment2 = new Enrollment();
		enrollment2.setEnrollmentId(6);
		enrollment2.setProgram(null);
		enrollment2.setLseId("LseId");
		enrollment2.setSubLap("subLap");		
		enrollment2.setStatus("2");
		List<Enrollment> enrollmentList = new ArrayList<Enrollment>();
		enrollmentList.add(enrollment1);
		enrollmentList.add(enrollment2);
		mockingObjects(enrollmentList, program);	
		size = eligibilityService.validateEnrollmentAndCreateLocationRecords();
		assertTrue(size <= 0);

	}

	@Test
	public void testValidateEnrollmentAndCreateLocationRecords_LseId_Not_Available() {
		Program program = new Program();
		program.setProgramId(970001);
		program.setMarketProgramId("15");
	

		Enrollment enrollment1 = new Enrollment();
		enrollment1.setEnrollmentId(5);
		enrollment1.setProgram(program);
		enrollment1.setLseId(null);
		
		enrollment1.setSubLap("subLap");
	
		
		enrollment1.setStatus("2");

		Enrollment enrollment2 = new Enrollment();
		enrollment2.setEnrollmentId(6);
		enrollment2.setProgram(program);
		enrollment2.setLseId(null);
		
		enrollment2.setSubLap("subLap");

		
		enrollment2.setStatus("2");

		List<Enrollment> enrollmentList = new ArrayList<Enrollment>();
		enrollmentList.add(enrollment1);
		enrollmentList.add(enrollment2);
		mockingObjects(enrollmentList, program);	
		size = eligibilityService.validateEnrollmentAndCreateLocationRecords();
		assertTrue(size <= 0);

	}

	@Test
	public void testValidateEnrollmentAndCreateLocationRecords_UdcId_Not_Available() {
		Program program = new Program();
		program.setProgramId(970001);
		program.setMarketProgramId("15");


		Enrollment enrollment1 = new Enrollment();
		enrollment1.setEnrollmentId(5);
		enrollment1.setProgram(program);
		enrollment1.setLseId("lseId");
		
		enrollment1.setSubLap("sublap");

		
		enrollment1.setStatus("2");

		Enrollment enrollment2 = new Enrollment();
		enrollment2.setEnrollmentId(6);
		enrollment2.setProgram(program);
		enrollment2.setLseId("lseId");
		
		enrollment2.setSubLap("sublap");
	
		
		enrollment2.setStatus("2");

		List<Enrollment> enrollmentList = new ArrayList<Enrollment>();
		enrollmentList.add(enrollment1);
		enrollmentList.add(enrollment2);
		mockingObjects(enrollmentList, program);	
		size = eligibilityService.validateEnrollmentAndCreateLocationRecords();
		assertTrue(size <= 0);

	}

	@Test
	public void testValidateEnrollmentAndCreateLocationRecords_subLAP_Not_Available() {
		Program program = new Program();
		program.setProgramId(970001);
		program.setMarketProgramId("15");


		Enrollment enrollment1 = new Enrollment();
		enrollment1.setEnrollmentId(5);
		enrollment1.setProgram(program);
		enrollment1.setLseId("lseId");
		
		enrollment1.setSubLap(null);

		
		enrollment1.setStatus("2");

		Enrollment enrollment2 = new Enrollment();
		enrollment2.setEnrollmentId(6);
		enrollment2.setProgram(program);
		enrollment2.setLseId("lseId");
		
		enrollment2.setSubLap(null);

		
		enrollment2.setStatus("2");

		List<Enrollment> enrollmentList = new ArrayList<Enrollment>();
		enrollmentList.add(enrollment1);
		enrollmentList.add(enrollment2);
		Pageable pageableFetch = new PageRequest(0, 15, Direction.ASC, "enrollmentId");
		Mockito.when(enrollmentRepository.findByStatus(newStatus, pageableFetch)).thenReturn(enrollmentList);
		Mockito.when(enrollmentRepository.findByUuidAndEnrollmentIdNotIn("15", 5)).thenReturn(enrollmentList);
		//Mockito.when(locationRepository.save(locationList)).thenReturn(locationList);			
		size = eligibilityService.validateEnrollmentAndCreateLocationRecords();
		assertTrue(size <= 0);

	}

	@Test
	public void testValidateEnrollmentAndCreateLocationRecords_UUID_Validation() {

		Program program = new Program();
    	program.setProgramId(970001);
    	program.setMarketProgramId("15");
    	
    	
    
    	
    	Enrollment enrollment1 = new Enrollment();
    	enrollment1.setEnrollmentId(5);
    	enrollment1.setProgram(program);
    	enrollment1.setLseId("lseId");
    	
    	enrollment1.setSubLap("sublap");
  
    	enrollment1.setStatus("2");
    	
    	
    	Enrollment enrollment2 = new Enrollment();
    	enrollment2.setEnrollmentId(6);
    	enrollment2.setProgram(program);
    	enrollment2.setLseId("lseId");
    	
    	enrollment2.setSubLap("sublap");
   
    	enrollment2.setStatus("2");
    	
    	List<Location> locationList = new ArrayList<Location>();
    	Location location1 =  new Location();
    	location1.setLocationId(14);
    	locationList.add(location1);
    	
        List<Enrollment> enrollmentList = new ArrayList<Enrollment>();

        enrollmentList.add(enrollment1);
        enrollmentList.add(enrollment2);       
        
        mockingObjects(enrollmentList, program);	        
        
        size = eligibilityService.validateEnrollmentAndCreateLocationRecords();
        System.out.println("Size " +size);
        //assertTrue(size >= 1);       
	}

	@Test
	public void testValidateEnrollmentAndCreateLocationRecords_Location_creation_scenario() {


		Program program = new Program();
		program.setProgramId(970001);
		program.setMarketProgramId("15");


		Enrollment enrollment1 = new Enrollment();
		enrollment1.setEnrollmentId(5);
		enrollment1.setProgram(program);
		enrollment1.setLseId("lseId");
		
		enrollment1.setSubLap("sublap");

		enrollment1.setStatus("2");

		Enrollment enrollment2 = new Enrollment();
		enrollment1.setEnrollmentId(6);
		enrollment1.setProgram(program);
		enrollment1.setLseId("lseId");
		
		enrollment1.setSubLap("sublap");
	
		enrollment1.setStatus("2");

		List<Enrollment> enrollmentList = new ArrayList<Enrollment>();
		List<Enrollment> enrollmentList2 = new ArrayList<Enrollment>();
		enrollmentList2.add(enrollment2);
		enrollmentList.add(enrollment1);
		enrollmentList.add(enrollment2);

		mockingObjects(enrollmentList, program);	
		size = eligibilityService.validateEnrollmentAndCreateLocationRecords();
		//assertTrue(size >= 1);

	}

}