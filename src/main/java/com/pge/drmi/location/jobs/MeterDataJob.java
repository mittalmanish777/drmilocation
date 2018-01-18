package com.pge.drmi.location.jobs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.pge.drmi.location.gateway.MeterDataRequest;
import com.pge.drmi.location.gateway.MeterDataResponse;
import com.pge.drmi.location.gateway.MeterReadGateway;

@Service
public class MeterDataJob {

	@Autowired
	private TaskExecutorBean taskExecutor;

	private static final Logger LOGGER = Logger.getLogger(RetrieveLocationJob.class);

	@Value("${drmi.location.recordfetchcountForMeterData}")
	private int numberOfRecordstoFetchForMeterDataReading;

	@Value("${drmi.location.meterdata.startDaysOffSet}")
	private long startDaysOffSet;

	@Value("${drmi.location.meterdata.endDaysOffSet}")
	private long endDaysOffSet;

	public void setNumberOfRecordstoFetchForMeterDataReading(int numberOfRecordstoFetchForMeterDataReading) {
		this.numberOfRecordstoFetchForMeterDataReading = numberOfRecordstoFetchForMeterDataReading;
	}

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private MeterReadGateway meterReadGateway;
	//public static final String START_DATE = "2016-01-01T21:40:50.200Z";

	//public static final String END_DATE = "2016-10-05T21:40:50.200Z";

	@Scheduled(cron="${drmi.location.schedulFetchTimeForMeterJob}")
	public void retrieveMeterData() {
		LOGGER.info("start retrieveMeterData: ");
		List<String> sublapLst = locationRepository.findDistinctSublapId();
		LOGGER.info("retrieved distinct sublap list :" + sublapLst.size());

		for (String sublap : sublapLst) {
			MeterDataRunnable runnable = new MeterDataRunnable(sublap);
			taskExecutor.getTaskExecutor().execute(runnable);
		}

	}

	class MeterDataRunnable implements Runnable {

		private String sublap;

		public MeterDataRunnable(String sublap) {
			this.sublap = sublap;
		}

		@Override
		public void run() {
			List<LocationState> locationStatuses = LocationState.getStatusForMeterData();
			List<String> locationStates = new ArrayList<>();
			for(LocationState status: locationStatuses) {
				locationStates.add(status.getValue());
			}

			Date currentDate = new Date();

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			Calendar cal = Calendar.getInstance();
			cal.setTime(new java.util.Date());
			cal.add(Calendar.DAY_OF_MONTH, -1*(int)startDaysOffSet);
			String startDate = df.format(cal.getTime());

	         cal.setTime(new java.util.Date());
	            cal.add(Calendar.DAY_OF_MONTH, -1*(int)endDaysOffSet);
			String endDate = df.format(cal.getTime());

			while (true) {

				Pageable pageableFetch = new PageRequest(0, numberOfRecordstoFetchForMeterDataReading);
				List<Location> locationList = locationRepository.findBySubLapAndStatusInAndMeterDataUpdatedDateIsBefore(
						sublap, locationStates, currentDate, pageableFetch);

				if (locationList == null || locationList.isEmpty()) {
					LOGGER.info("There are no further records in this sublap: " + sublap + " for meter data updation.");
					return;
				}

				List<String> uuidList = new ArrayList<>();
				for (Location location : locationList) {
					uuidList.add(location.getUuid());
				}
				LOGGER.info("uuid list:" + uuidList.size() + " for sublap:" + sublap + " and location list"
						+ locationList.size() + " start date:" + startDate + " end date:" + endDate);

				MeterDataResponse meterDataResponse = null;
				MeterDataRequest request = null;
				try {
					request = new MeterDataRequest(uuidList, startDate, endDate);
					meterDataResponse = meterReadGateway.getMeterReadData(request);
				} catch (Exception e) {
					LOGGER.error("Exception occured while fetching meter data with the input details as start date:"
							+ startDate + " end date:" + endDate + " and uuidList:" + request.getArrayOfIds().length);
					e.printStackTrace();
				}

				for (Location location : locationList) {
					String averageReadingPercent = meterDataResponse == null ? null
							: meterDataResponse.getAverageReadingPercent(location.getUuid());
					LOGGER.info("averageReadingPercent:" + averageReadingPercent + " for location id:"
							+ location.getLocationId());
					if (averageReadingPercent != null) {
						location.setMeterDataPercentage(Double.parseDouble(averageReadingPercent));
					}
					location.setMeterDataUpdatedDate(currentDate);
				}
				locationRepository.save(locationList);
			}

		}
	}
}
