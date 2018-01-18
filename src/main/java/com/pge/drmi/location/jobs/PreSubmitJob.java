package com.pge.drmi.location.jobs;

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
import com.pge.drmi.location.Location;
import com.pge.drmi.location.LocationRepository;
import com.pge.drmi.location.LocationState;

@Service
public class PreSubmitJob {

	@Autowired
	private LocationRepository locationRepository;

	@Value("${drmi.location.recordfetchcountForPreSubmitJob}")
	private Integer numberOfRecordstoFetchForPreSubmit;

	@Autowired
	private ConfigurationRepository configurationRepository;

	private static final Logger LOGGER = Logger.getLogger(PreSubmitJob.class);

	public void setNumberOfRecordstoFetchForPreSubmit(Integer numberOfRecordstoFetchForPreSubmit) {
		this.numberOfRecordstoFetchForPreSubmit = numberOfRecordstoFetchForPreSubmit;
	}

	@Scheduled(cron = "${drmi.location.schedulFetchTimeForPreSubmit}")
	public int preSubmitJob() {
		LOGGER.info("Entering method preSubmitJob");
		int count = 0;
		String locationAutoSubmit = configurationRepository.findByProcessName(ConfigEntries.LOCATION_AUTO_SUBMIT)
				.getValue();
		if (!locationAutoSubmit.equals(ConfigEntries.AUTO_SUBMIT)) {
			LOGGER.info("Config is not set to Auto");
			return count;
		}

		// Auto is enabled, update all the records to Pending batch
		while (true) {
			try {
				// fetch the location records which are in proposed status.
				Pageable pageableFetch = new PageRequest(0, numberOfRecordstoFetchForPreSubmit);
				List<Location> locationList = locationRepository.findByStatus(LocationState.PROPOSED.getValue(),
						pageableFetch);
				if (locationList.size() == 0) {
					LOGGER.info("Exiting method preSubmitJob");
					return count;
				}

				for (Location location : locationList) {
					location.setStatus(LocationState.PENDING_BATCH.getValue());
				}

				locationRepository.save(locationList);
				count += locationList.size();
				LOGGER.info("Number of Proposed records updated to Pending Batch status:" + locationList.size());

			} catch (Exception e) {
				LOGGER.error("Exception occured during presubmit job", e);
				LOGGER.info("Processed " + count + " records before exception");
				return count;
			}
		}
	}
}
