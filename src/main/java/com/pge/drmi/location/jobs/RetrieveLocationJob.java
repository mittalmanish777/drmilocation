package com.pge.drmi.location.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pge.drmi.config.ConfigEntries;
import com.pge.drmi.config.ConfigurationRepository;
import com.pge.drmi.location.Location;
import com.pge.drmi.location.LocationRepository;
import com.pge.drmi.location.LocationState;
import com.pge.drms.caisohandler.LocationDetails;
import com.pge.drms.caisohandler.RetrieveLocations;
import com.pge.drms.caisohandler.RetrieveLocationsImpl;
import com.pge.drms.caisohandler.RetrieveLocationsRequest;
import com.pge.drms.caisohandler.RetrieveLocationsRequest.Status;
import com.pge.drms.caisohandler.RetrieveLocationsResponse;

@Service
public class RetrieveLocationJob {

	@Autowired
	private TaskExecutorBean taskExecutor;

	@Value("${drmi.location.retrieve.commitcount}")
	private int commitCount;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private ConfigurationRepository configurationRepository;

	private static final Logger LOGGER = Logger.getLogger(RetrieveLocationJob.class);

	@Scheduled(cron = "${drmi.location.schedulFetchTimeForRetrieveJob}")
	public void retrieveLocStatusFromCaiso() {
		
		List<LocationState> statesToQuery = LocationState.getStatusForRetrieve();
		List<String> sublapLst = locationRepository.findDistinctSublapId();
		String drpId = configurationRepository.findByProcessName(ConfigEntries.DRP_ID).getValue();
		for (String sublap : sublapLst) {
		   for(LocationState status: statesToQuery) {
			
				RetrieveLocationsRequest request = new RetrieveLocationsRequest();
				request.setSubLapId(sublap);
				request.setLocationStatus(Status.valueOf(status.toString()));
				request.setDrpId(drpId);

				SublapStatusRunnable runnable = new SublapStatusRunnable(request);
				taskExecutor.getTaskExecutor().execute(runnable);
		   };

		}
	}

	public void retrieveSublapLocations(String sublap, String status) {
		RetrieveLocationsRequest request = new RetrieveLocationsRequest();
		request.setSubLapId(sublap);
		request.setLocationStatus(Status.valueOf(status.toString()));
		String drpId = configurationRepository.findByProcessName(ConfigEntries.DRP_ID).getValue();
		request.setDrpId(drpId);

		SublapStatusRunnable runnable = new SublapStatusRunnable(request);
		runnable.run();

	}

	class SublapStatusRunnable implements Runnable {

		private RetrieveLocationsRequest request = null;

		public SublapStatusRunnable(RetrieveLocationsRequest request) {
			this.request = request;
		}

		@Override
		public void run() {
			RetrieveLocations retrieveLocations = new RetrieveLocationsImpl();
			LOGGER.info("Calling CAISO for Sublap: " + request.getSubLapId() + " and status: "
					+ request.getLocationStatus());
			RetrieveLocationsResponse response = retrieveLocations.retrieveLocations(request);

			LOGGER.info("Response location list  " + response.getLocations().size() + "  for subLap "
					+ request.getSubLapId());
			int count = 0;
			int size = response.getLocations().size();
			Map<String, LocationDetails> caisoLocationMap = new HashMap<>();
			for (int i = 0; i < size; i++) {
				LocationDetails locationCaiso = response.getLocations().get(i);
				caisoLocationMap.put(locationCaiso.getSanId(), locationCaiso);// inserting sanid as key and locationCaiso as values.

				if (++count == commitCount || i == size - 1) {
					//					LOGGER.info("@@@@@@@@@ " + caisoLocationMap);
					LOGGER.info("Processing  " + (count) + "  out of " + size + " records for subLap "
							+ request.getSubLapId());

					List<String> keySet = new ArrayList<String>(caisoLocationMap.keySet());
					List<Location> locationDrmi = locationRepository.findBySanIn(keySet);
					LOGGER.info("Found " + locationDrmi.size() + "  matching records in the database ");

					List<Location> finalLocationDrmi = new ArrayList<Location>();
					for (Location location : locationDrmi) {
						boolean updateFlag = updateDrmiLocation(location, caisoLocationMap.get(location.getSan()));
						if (updateFlag) {
							finalLocationDrmi.add(location);
						}
					}

					if (!finalLocationDrmi.isEmpty()) {
						LOGGER.info("Updating " + finalLocationDrmi.size() + " records to the database");
						locationRepository.save(finalLocationDrmi);
					}
					caisoLocationMap.clear(); // clearing caisoLocationMap
					count = 0; // resetting count
				}

			}
		}

		private boolean updateDrmiLocation(Location drmiLocation, LocationDetails caisoLocation) {
			String caisoStatus = caisoLocation.getLocationStatus();
			LocationState drmiState = LocationState.getStateFromValue(drmiLocation.getStatus());
			LocationState stateToSet = LocationState.getNextState(drmiState, LocationState.valueOf(caisoStatus));

			if (stateToSet == drmiState && drmiLocation.getCaisoLocationId() != null) {
				return false;
			} else if (stateToSet == drmiState && drmiLocation.getCaisoLocationId() == null) {
				drmiLocation.setCaisoLocationId(caisoLocation.getLocationId());
				return true;
			} else {
				drmiLocation.setStatus(stateToSet.getValue());
				return true;
			}
		}

	}

}
