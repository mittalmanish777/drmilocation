package com.pge.drmi.location.jobs;

import java.util.ArrayList;
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
import com.pge.drms.caisohandler.SubmitLocations;
import com.pge.drms.caisohandler.SubmitLocationsImpl;
import com.pge.drms.caisohandler.SubmitLocationsRequest;
import com.pge.drms.caisohandler.SubmitLocationsResponse;

@Service
public class DefendLocationJob {
	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private LSEMapping customProperties;

	@Value("${drmi.location.recordfetchcountForDefendSubmitJob}")
	private Integer numberOfRecordstoFetchForDefendSubmit;

	private static final Logger LOGGER = Logger.getLogger(DefendLocationJob.class);

	public void setNumberOfRecordstoFetchForDefendSubmit(Integer numberOfRecordstoFetchForDefendSubmit) {
		this.numberOfRecordstoFetchForDefendSubmit = numberOfRecordstoFetchForDefendSubmit;
	}

	@Autowired
	private ConfigurationRepository configurationRepository;

	@Scheduled(cron = "${drmi.location.schedulFetchTimeForDefenedJob}")
	public int defendLocationJobToCAISO() {
		LOGGER.info("Entering defend method");
		int count = 0;
		while (true) {
			Pageable pageableFetch = new PageRequest(0, numberOfRecordstoFetchForDefendSubmit);
			List<Location> locations = locationRepository.findByStatus(LocationState.PENDING_DEFEND.getValue(),
					pageableFetch);
			if (locations.size() == 0) {
				LOGGER.info("Exiting defend method after updating " + count + " records");
				return count;
			}
			Enrollment enrollment = new Enrollment();
			String udcId = configurationRepository.findByProcessName(ConfigEntries.UDC_ID).getValue();
			String drpId = configurationRepository.findByProcessName(ConfigEntries.DRP_ID).getValue();
			List<SubmitLocationsRequest> submitLocationsRequestList = new ArrayList<SubmitLocationsRequest>();

			for (Location location : locations) {
				SubmitLocationsRequest submitLocationRequest = new SubmitLocationsRequest();
				enrollment = location.getEnrollment().get(0);
				submitLocationRequest.setAddressLine1(enrollment.getAddressLine1());
				submitLocationRequest.setAddressLine2(enrollment.getAddressLine2());
				submitLocationRequest.setCity(enrollment.getCity());
				submitLocationRequest.setLocationStartDate(location.getStartDate());
				submitLocationRequest.setLocationEndDate(location.getEndDate());
				submitLocationRequest.setLseId(customProperties.getPathMapper().get(enrollment.getLseId()));
				submitLocationRequest.setPostalCode(enrollment.getZipCode());
				submitLocationRequest.setState(enrollment.getState());
				submitLocationRequest.setSubLap(enrollment.getSubLap());
				submitLocationRequest.setUdcId(udcId);
				submitLocationRequest.setDrpId(drpId);
				submitLocationRequest.setUuId(location.getUuid());
				submitLocationRequest.setAction("INITIATE_DEFEND");
				submitLocationRequest.setDefendComment(location.getDefendReason());
				submitLocationsRequestList.add(submitLocationRequest);
			}
			if (submitLocationsRequestList.size() > 0) {
				SubmitLocationsResponse submitLocationResponse = new SubmitLocationsResponse();
				SubmitLocations submitLocationImpl = new SubmitLocationsImpl();
				try {
					submitLocationResponse = submitLocationImpl.submitLocations(submitLocationsRequestList);
					// submitLocationResponse.setResult("success");
				} catch (Exception e) {
					LOGGER.info("Exception occured during submission to caiso.", e);
					for (Location locationSubmissionFailed : locations) {
						locationSubmissionFailed.setStatus(LocationState.DEFEND_BATCH_FAILED.getValue());
					}
					locationRepository.save(locations);
					count += locations.size();
				}
				// if the response from caiso is not success for the batch, set batch failed to
				// the location records in the batch.
				if (!submitLocationResponse.getResult().equals("success")) {
					for (Location locationSubmissionFailed : locations) {
						locationSubmissionFailed.setStatus(LocationState.DEFEND_BATCH_FAILED.getValue());
					}
					locationRepository.save(locations);
					count += locations.size();
				} else {
					// if the repsponse is success from caiso, change status to In-defend for all
					// the records in the batch and increment the count.
					for (Location location : locations) {
						location.setStatus(LocationState.DEFEND_SUBMITTED.getValue());
					}
					locationRepository.save(locations);
					count += locations.size();
				}
			}
		}
	}
}
