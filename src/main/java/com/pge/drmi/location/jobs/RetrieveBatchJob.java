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

import com.pge.drmi.location.Location;
import com.pge.drmi.location.LocationRepository;
import com.pge.drmi.location.LocationState;
import com.pge.drms.caisohandler.RetrieveBatchDetails;
import com.pge.drms.caisohandler.RetrieveBatchErrorLog;
import com.pge.drms.caisohandler.RetrieveLocationBatchValidationStatus;
import com.pge.drms.caisohandler.RetrieveLocationBatchValidationStatusImpl;
import com.pge.drms.caisohandler.RetrieveLocationBatchValidationStatusRequest;
import com.pge.drms.caisohandler.RetrieveLocationBatchValidationStatusResponse;

@Service
public class RetrieveBatchJob {

	@Autowired
	private LocationRepository locationRepository;

	@Value("${scheduling.enabled}")
	private boolean schedulingEnabled;

	private static final Logger LOGGER = Logger.getLogger(RetrieveLocationJob.class);

	@Value("${drmi.location.recordfetchcountforRetrieveBatchJob}")
	private Integer numberOfRecordstoFetchForRetriveBatchJob;

	public void setNumberOfRecordstoFetchForPreSubmit(int numberOfRecordstoFetchForRetriveRegistrationBatchJob) {
		this.numberOfRecordstoFetchForRetriveBatchJob = numberOfRecordstoFetchForRetriveRegistrationBatchJob;
	}

	@Scheduled(cron = "${drmi.location.schedulFetchTimeForRetrieveBatch}")
	public void retrieveBatchStatus() {
		processBatchByStatus(LocationState.SUBMITTED, LocationState.PENDING, LocationState.REJECTED,
				LocationState.PENDING_BATCH);
		processBatchByStatus(LocationState.DEFEND_SUBMITTED, LocationState.IN_DEFEND, LocationState.DEFEND_REJECTED,
				LocationState.PENDING_DEFEND);
	}

	private void processBatchByStatus(LocationState startState, LocationState successState, LocationState errorState,
			LocationState resetState) {

		while (true) {
			Pageable pageableFetch = new PageRequest(0, numberOfRecordstoFetchForRetriveBatchJob);
			List<String> batchIds = locationRepository.findDistinctBatchId(startState.getValue(), pageableFetch);

			if (batchIds.isEmpty()) {
				LOGGER.info("No further batch ids found for state: " + startState);
				return;
			}

			List<RetrieveLocationBatchValidationStatusRequest> requests = new ArrayList<RetrieveLocationBatchValidationStatusRequest>();
			for(String batchId: batchIds) {
			
				RetrieveLocationBatchValidationStatusRequest request = new RetrieveLocationBatchValidationStatusRequest();
				request.setBatchId(batchId);
				requests.add(request);
			}
			
			RetrieveLocationBatchValidationStatus retrieveLocationBatchValidationStatus = new RetrieveLocationBatchValidationStatusImpl();
			List<RetrieveLocationBatchValidationStatusResponse> responses = retrieveLocationBatchValidationStatus
					.retreiveBatchValidationStatus(requests);

			for (RetrieveLocationBatchValidationStatusResponse response : responses) {

				List<Location> locations = locationRepository.findByBatchId(response.getBatchId());

				if (response.getBatchStatus().equalsIgnoreCase("SUCCESS")) {
				  for(RetrieveBatchDetails batchDetail :response.getBatchDetails()) {
					for(Location location: locations) {
							// set status to pending
							location.setStatus(successState.getValue());
						}
					}
				} else if (response.getBatchStatus().equalsIgnoreCase("ERROR")) {

				  for(Location location: locations) {
						location.setStatus(resetState.getValue());
					}

				  for(RetrieveBatchDetails batchDetail :response.getBatchDetails()) {

						// find the location based on name
						Location location = findLocation(locations, batchDetail.getSanId());

						if (location != null) {

							// update the error of that location
							StringBuilder builder = new StringBuilder();
							for(RetrieveBatchErrorLog errorLst: batchDetail.getErrorLog()) {
								builder.append(errorLst.getErrorMessage()).append(" ");
							}

							// update the status to casio_submit_error
							location.setStatus(errorState.getValue());
							location.setCaisoError(builder.toString());

						}
					}
				}
				locationRepository.save(locations);
			}
		}
	}

	private Location findLocation(List<Location> locations, String sanId) {
		for (Location location : locations) {
			if (sanId != null && sanId.equals(String.valueOf(location.getSan()))) {
				return location;
			}
		}
		return null;
	}

}
