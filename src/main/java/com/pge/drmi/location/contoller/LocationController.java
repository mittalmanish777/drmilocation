package com.pge.drmi.location.contoller;

import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pge.drmi.location.jobs.DefendLocationJob;
import com.pge.drmi.location.jobs.EligibilityJob;
import com.pge.drmi.location.jobs.MeterDataJob;
import com.pge.drmi.location.jobs.PreSubmitJob;
import com.pge.drmi.location.jobs.RetrieveBatchJob;
import com.pge.drmi.location.jobs.RetrieveLocationJob;
import com.pge.drmi.location.jobs.SubmitJob;
import com.pge.drmi.location.service.LocationService;

@RestController
@RequestMapping("/api")
public class LocationController {

	private static final Logger LOGGER = Logger.getLogger(LocationController.class);

	@Autowired
	private EligibilityJob eligibilityJob;

	@Autowired
	private SubmitJob submitJob;

	@Autowired
	private DefendLocationJob defendLocationJob;

	@Autowired
	private PreSubmitJob preSubmitJob;

	@Autowired
	private RetrieveLocationJob retrieveLocationJob;

	@Autowired
	private RetrieveBatchJob retrieveBatchJob;

	@Autowired
	private MeterDataJob meterDataJob;

	@Autowired
	private LocationService locationService;

	// @Autowired
	// private GenericDao genericDao;

	@RequestMapping(method = RequestMethod.GET, value = "/eligibility")
	public int createLocation() {
		LOGGER.info("Calling Eligibility Job");
		return eligibilityJob.validateEnrollmentAndCreateLocationRecords();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/submitLocations")

	public int submitLocationToCAISO() {
		LOGGER.info("Calling Submit Job");
		return submitJob.submitLocationRecordsToCAISO();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/defendLocations")
	public int defendLocationToCAISO() {
		LOGGER.info("Calling defend job");
		return defendLocationJob.defendLocationJobToCAISO();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/preSubmit")
	public int preSubmitJob() {
		LOGGER.info("Calling presubmit job");
		return preSubmitJob.preSubmitJob();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/retrieveLocations")
	public void retrieveLocation1() {
		LOGGER.info("Calling retrieve location status from caiso");
		retrieveLocationJob.retrieveLocStatusFromCaiso();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/retrieveBatchStatus")
	public void retrieveBatchStatus() {
		LOGGER.info("Calling retrieve batch job");
		retrieveBatchJob.retrieveBatchStatus();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/retrieveMeterAvailDays")
	public void retrieveMeterAvailDays() {
		LOGGER.info("Calling meter data job");
		meterDataJob.retrieveMeterData();
	}

	// @RequestMapping(method = RequestMethod.POST, value = "/runScripts", consumes
	// = "text/plain")
	// public String runScripts(@RequestBody String sql) {
	// LOGGER.info("Calling run scripts..");
	// return genericDao.runScripts(sql);
	// }

	@RequestMapping(method = RequestMethod.GET, value = "/retrieveSublapLocations")
	public void retrieveSublapLocations(@RequestParam(value = "sublap") String sublap,
			@RequestParam(value = "status") String status) {
		LOGGER.info("Calling retrieve location status from caiso for a sublap..");
		retrieveLocationJob.retrieveSublapLocations(sublap, status);
	}

	@RequestMapping(value = "/getLocation", produces = "text/plain", method = RequestMethod.GET)
	public String getLocation(@RequestParam(value = "locationId") int locationId) {
		LOGGER.info("Retrieving location using location ID..");
		return locationService.getLocation(locationId);
	}
	
    @RequestMapping(value = "/getDistinctSubLaps", produces = "application/json", method = RequestMethod.GET)
    public List<String> getDistinctSubLaps() {
        return locationService.getDistinctSubLaps();
    }
    @RequestMapping(value = "/hello", produces = "text/plain", method = RequestMethod.GET)
    public String hello() {
        return "Hi there. Lets finish this POC";
    }
}
