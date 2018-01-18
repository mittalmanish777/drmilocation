package com.pge.drmi.location.jobs;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pge.drmi.config.ConfigEntries;
import com.pge.drmi.config.ConfigurationRepository;
import com.pge.drmi.config.LSEMapping;
import com.pge.drmi.enrollment.Enrollment;
import com.pge.drmi.location.Location;
import com.pge.drmi.location.LocationRepository;
import com.pge.drmi.location.LocationState;
import com.pge.drmi.registration.RegistrationState;
import com.pge.drmi.utils.DateUtility;
import com.pge.drms.caisohandler.SubmitLocations;
import com.pge.drms.caisohandler.SubmitLocationsImpl;
import com.pge.drms.caisohandler.SubmitLocationsRequest;
import com.pge.drms.caisohandler.SubmitLocationsResponse;

@Service
public class SubmitJob {

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private ConfigurationRepository configurationRepository;

	@Autowired
	private LSEMapping customProperties;

	@Value("${drmi.location.recordfetchcountForSubmitJob}")
	private Integer numberOfRecordstoFetchForSubmit;

	private static final Logger LOGGER = Logger.getLogger(SubmitJob.class);

	public void setNumberOfRecordstoFetchForSubmit(Integer numberOfRecordstoFetchForSubmit) {
		this.numberOfRecordstoFetchForSubmit = numberOfRecordstoFetchForSubmit;
	}

	@Scheduled(cron = "${drmi.location.schedulFetchTimeForSubmit}")
	public int submitLocationRecordsToCAISO() {
		LOGGER.info("Entering submit method");
		int count = 0;
		while (true) {
			List<SubmitLocationsRequest> submitLocationsRequestList = new ArrayList<SubmitLocationsRequest>();
			List<String> locationStates = new ArrayList<>();
			locationStates.add(LocationState.PENDING_BATCH.getValue());
			locationStates.add(LocationState.PENDING_MANUAL.getValue());
			Pageable pageableFetch = new PageRequest(0, numberOfRecordstoFetchForSubmit);
			List<Location> locationList = locationRepository.findByStatusIn(locationStates, pageableFetch);
			LOGGER.info("Retrieved location list of size:" + locationList != null ? locationList.size() : 0);
			if (locationList.size() == 0) {
				LOGGER.info("Exiting submit method after updating " + count + " records");
				return count;
			}
			List<Location> locationListStatusSave = new ArrayList<Location>();
			List<Integer> locationIdList = new ArrayList<>();

			Enrollment enrollment = new Enrollment();
			String udcId = configurationRepository.findByProcessName(ConfigEntries.UDC_ID).getValue();
			String drpId = configurationRepository.findByProcessName(ConfigEntries.DRP_ID).getValue();
			for (Location location : locationList) {
				if (location.getCaisoLocationId() != null && location.getStartDate().equals(location.getEndDate())) {
					locationIdList.add(location.getLocationId());
				}
			}

			List<Integer> locationIdWithRegistration = null;
			if (locationIdList.size() > 0) {
				locationIdWithRegistration = locationRepository.findLocationIdbyLocationId(locationIdList,
						RegistrationState.getStatusForTermiateRegistrations());
			}
			Date currentDate = DateUtility.getCurrentDate();
			for (Location location : locationList) {
				SubmitLocationsRequest submitLocationRequest = new SubmitLocationsRequest();
				enrollment = location.getEnrollment().get(0);
				submitLocationRequest.setAddressLine1(enrollment.getAddressLine1());
				submitLocationRequest.setAddressLine2(enrollment.getAddressLine2());
				submitLocationRequest.setCity(enrollment.getCity());
				// since CAISO doesn't accept past dates as location start date,
				// setting current date if start date is on past
				boolean pastDate = location.getStartDate().before(currentDate);
				if (pastDate && location.getCaisoLocationId() == null) {
					submitLocationRequest.setLocationStartDate(currentDate);
					location.setStartDate(currentDate);
				} else {
					submitLocationRequest.setLocationStartDate(location.getStartDate());
				}
				submitLocationRequest.setLocationEndDate(location.getEndDate());
				submitLocationRequest.setLseId(customProperties.getPathMapper().get(enrollment.getLseId()));
				// format zip code
				String zipCode = enrollment.getZipCode();
				if (zipCode != null && zipCode.length() > 5) {
					submitLocationRequest.setPostalCode(!zipCode.isEmpty() ? zipCode.replaceAll("(.{5})", "$1-") : "");
				} else
					submitLocationRequest.setPostalCode(enrollment.getZipCode());
				submitLocationRequest.setState(enrollment.getState());
				submitLocationRequest.setSubLap(location.getSubLap());
				submitLocationRequest.setUdcId(udcId);
				submitLocationRequest.setDrpId(drpId);
				submitLocationRequest.setUuId(location.getUuid());
				if (location.getCaisoLocationId() != null) {
					submitLocationRequest.setAction("MODIFY");
					submitLocationRequest.setLocationId(new BigInteger(location.getCaisoLocationId()));
				} else {
					submitLocationRequest.setAction("SUBMIT");
				}

				if (locationIdWithRegistration != null
						&& locationIdWithRegistration.contains(location.getLocationId())) {
					submitLocationRequest.setTerminateRegistrationFlag(true);
				}

				submitLocationRequest.setLocationName(
						enrollment.getCustomerName() != null ? enrollment.getCustomerName().replaceAll(",", " ")
								: enrollment.getName() != null ? enrollment.getName().replaceAll(",", " ") : null);
				submitLocationsRequestList.add(submitLocationRequest);
				locationListStatusSave.add(location);
			}

			// if location records exist for submission to ciaso.
			if (submitLocationsRequestList.size() > 0) {
				LOGGER.info("Number of records eligible for caiso submission:" + submitLocationsRequestList != null
						? submitLocationsRequestList.size()
						: 0);
				SubmitLocationsResponse submitLocationResponse = new SubmitLocationsResponse();
				SubmitLocations submitLocationImpl = new SubmitLocationsImpl();
				try {
					submitLocationResponse = submitLocationImpl.submitLocations(submitLocationsRequestList);
					// submitLocationResponse.setResult("error");

				} catch (Exception e) {
					LOGGER.info("Exception occured during submission to caiso.");
					for (Location locationSubmissionFailed : locationListStatusSave) {
						locationSubmissionFailed.setFailureCount(locationSubmissionFailed.getFailureCount() + 1);
						locationSubmissionFailed.setCaisoError("Exception occured during submission to caiso");
						if (locationSubmissionFailed.getFailureCount() > 4) {
							locationSubmissionFailed.setStatus(LocationState.BATCH_FAILED.getValue());
							locationSubmissionFailed.setCaisoError("Exception occured during submission to caiso");
						}
					}
					LOGGER.info("Incrementing failure count and status to batch failed in location table");
					locationRepository.save(locationListStatusSave);
					count += locationListStatusSave.size();
				}
				// if the response from caiso is not success for the batch, set batch failed to
				// the location records in the batch.
				if (!submitLocationResponse.getResult().equals("Success")) {
					LOGGER.info(
							"Caiso submission is not successful. Incrementing the failure count for all the records.");
					for (Location locationSubmissionFailed : locationListStatusSave) {
						locationSubmissionFailed.setFailureCount(locationSubmissionFailed.getFailureCount() + 1);
						// saving error message
						locationSubmissionFailed.setCaisoError(submitLocationResponse.getResult());
						if (locationSubmissionFailed.getFailureCount() > 4) {
							locationSubmissionFailed.setStatus(LocationState.BATCH_FAILED.getValue());
						}
					}
					LOGGER.info(
							"Updating location record with incremented failure count. If failure count is greate than 4 update the status to Batch Failed. Total number of records eligible for updation:"
									+ locationListStatusSave != null ? locationListStatusSave.size() : 0);
					locationRepository.save(locationListStatusSave);
					count += locationListStatusSave.size();
				} else {
					// if the repsponse is success from caiso, change status to submitted for all
					// the records in the batch and increment the count.
					LOGGER.info("Caiso submission is successful.");
					List<Location> locationListSubmitted = new ArrayList<Location>();
					for (Location locationSubmitted : locationList) {
						locationSubmitted.setStatus(LocationState.SUBMITTED.getValue());
						locationSubmitted.setBatchId(submitLocationResponse.getBatchId());
						locationSubmitted.setCaisoSubmissionDate(submitLocationResponse.getCreationTime());
						// clearing error message and retryCount if success
						locationSubmitted.setCaisoError(null);
						locationSubmitted.setFailureCount(0);
						locationListSubmitted.add(locationSubmitted);
					}
					LOGGER.info(
							"Updating all the records with status as submitted, batch id and caiso submission time retrieved from caiso response to location table. Total number of records eligible for updation:"
									+ locationListSubmitted != null ? locationListSubmitted.size() : 0);
					locationRepository.save(locationListSubmitted);
					count += locationListSubmitted.size();
					
				}
			}
		}
	}
}
