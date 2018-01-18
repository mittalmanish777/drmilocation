package com.pge.drmi.location.jobs;

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
import com.pge.drmi.enrollment.Enrollment;
import com.pge.drmi.enrollment.EnrollmentRepository;
import com.pge.drmi.enrollment.EnrollmentState;
import com.pge.drmi.location.Location;
import com.pge.drmi.location.LocationState;
import com.pge.drmi.utils.DateUtility;

@Service
public class EligibilityJob {

	private static final Logger LOGGER = Logger.getLogger(RetrieveLocationJob.class);

	@Autowired
	private EnrollmentRepository enrollmentRepository;

	@Autowired
	private ConfigurationRepository configurationRepository;

	@Value("${drmi.location.recordfetchcountForEligibility}")
	private Integer numberOfRecordstoFetch;

	@Value("${drmi.location.yearOffSetforEndDate}")
	private int yearOffSetforEndDate;

	@Value("${drmi.location.dayOffsetforStartDate}")
	private int dayOffsetforStartDate;

	public void setNumberOfRecordstoFetch(Integer numberOfRecordstoFetch) {
		this.numberOfRecordstoFetch = numberOfRecordstoFetch;
	}

	@Scheduled(cron = "${drmi.location.schedulFetchTimeForEligibility}")
	public int validateEnrollmentAndCreateLocationRecords() {
		LOGGER.info("Entering method validateEnrollmentAndCreateLocationRecords");
		int count = 0;
		while (true) {
			try {
				Pageable pageableFetch = new PageRequest(0, numberOfRecordstoFetch);
				List<Enrollment> enrollmentList = enrollmentRepository.findByStatus(EnrollmentState.NEW.getValue(),
						pageableFetch);
				if (enrollmentList.size() == 0) {
					LOGGER.info("Exiting method validateEnrollmentAndCreateLocationRecords");
					return count;
				}
				LOGGER.info("Retrieved enrollment list of size:" + enrollmentList != null ? enrollmentList.size() : 0);
				List<Enrollment> enrollmentsToUpdate = new ArrayList<>();
				List<Location> locationsToCreate = new ArrayList<>();
				String udcId = configurationRepository.findByProcessName(ConfigEntries.UDC_ID).getValue();
				String drpId = configurationRepository.findByProcessName(ConfigEntries.DRP_ID).getValue();
				Date startDate = DateUtility.addDays(new Date(), dayOffsetforStartDate);

				for (Enrollment enrollment : enrollmentList) {

					// iterate through the list of location objects to see if this uuid is present,
					// save the existing so that the reference is picked subsequently
					for (Location location : locationsToCreate) {
						if (location.getUuid().equals(enrollment.getUuid())) {
							enrollmentRepository.save(enrollmentsToUpdate);
							count += enrollmentsToUpdate.size();
							enrollmentsToUpdate = new ArrayList<>();
							break;
						}
					}

					Location location = null;
					StringBuilder rejectReason = new StringBuilder();

					// Check if program id is null
					if (null == enrollment.getProgram()) {
						LOGGER.info("ProgramId is not set for enrollment");
						rejectReason.append("ProgramId is not set. ");
					}

					// Check if there is a valid retail to wholesale program mapping
					if (enrollment.getProgram() != null && enrollment.getProgram().getMarketProgramId() == null) {
						LOGGER.info("Market Program id is not set. Retail to wholesale mapping is not present");
						rejectReason.append("Retail to wholesale mapping is not present. ");
					}

					// Check if lseId is available
					if (null == enrollment.getLseId()) {
						LOGGER.info("LSEID is not set");
						rejectReason.append("LSE ID is not available. ");
					}

					// Check if udcid is available
					if (null == udcId) {
						LOGGER.info("UDCID is not set");
						rejectReason.append("UDC ID is not available. ");
					}

					// check if sublap is defined
					if (null == enrollment.getSubLap()) {
						LOGGER.info("Sublap not defined");
						rejectReason.append("Sublap is not available. ");
					}
					
					if(null == enrollment.getUuid()) {
						LOGGER.info("UUID not defined");
						rejectReason.append("UUID is not available. ");
					}
					
					if(null == enrollment.getAddressLine1()) {
						LOGGER.info("AddressLine1 not defined");
						rejectReason.append("AddressLine1 is not available. ");
					}
					
					if(null == enrollment.getCity()) {
						LOGGER.info("City not available");
						rejectReason.append("City is not available. ");
					}
					
					if(null == drpId) {
						LOGGER.info("DRPID is not set");
						rejectReason.append("DRPID is not available. ");
					}
					
					if(null == enrollment.getSaId()) {
						LOGGER.info("SAID is not set");
						rejectReason.append("SAID is not available. ");
					}					
					
					if(null == enrollment.getZipCode()) {
						LOGGER.info("ZipCode is not set");
						rejectReason.append("ZipCode is not available. ");
					}
					
					if(null == enrollment.getState()) {
						LOGGER.info("State is not set");
						rejectReason.append("State is not available. ");
					}
					
					if(enrollment.getCustomerName() == null && enrollment.getName()==null) {
						LOGGER.info("Name is not set");
						rejectReason.append("Name is not available. ");
					}					
					
					// UUID validation.

					// check if any other uuid's exist in enrollment table if uuid exist check the
					// program id if program id is same reject if program id is different check for
					// location id if it is associated with existing location id link that location
					// id to the current enrollment other wise create a new location record.

					List<Enrollment> enrollmentUUIDValidationList = enrollmentRepository
							.findByUuidAndEnrollmentIdNotIn(enrollment.getUuid(), enrollment.getEnrollmentId());

					LOGGER.info("Found " + enrollmentUUIDValidationList.size() + " records for UUID/EnrollmentId: "
							+ enrollment.getUuid() + "/" + enrollment.getEnrollmentId());

					for (Enrollment validateEnrollmentRecord : enrollmentUUIDValidationList) {

						if (null != validateEnrollmentRecord.getLocation()) {
							if (validateEnrollmentRecord.getProgram().getProgramId()
									.equals(enrollment.getProgram().getProgramId())) {
								// Check if programid is same then reject
								LOGGER.info("Program id is same. Rejecting the enrollment record.");
								rejectReason.append("UUID for an enrollment associated with same program");
								break;

							} else {
								// if it is associated with existing location id link that location id to the
								// current enrollment
								LOGGER.info(
										"Program id is different. Enrollment is associated with existing locationid. update enrollment with the locatinid");
								location = validateEnrollmentRecord.getLocation();
								break;
							}
						}
					}

					if (rejectReason.length() > 0) {
						enrollment.setRejectReason(rejectReason.toString());
						enrollment.setStatus(EnrollmentState.REJECTED.getValue());
					} else {
						enrollment.setRejectReason(null);
						enrollment.setStatus(EnrollmentState.PROPOSED.getValue());

						if (location == null) {
							location = new Location();
							location.setFullName(enrollment.getName());
							location.setSan(udcId.concat(enrollment.getUuid()));
							location.setSubLap(enrollment.getSubLap());
							location.setStartDate(startDate);
							location.setEndDate(DateUtility.addYear(startDate, yearOffSetforEndDate));
							location.setUuid(enrollment.getUuid());
							location.setStatus(LocationState.PROPOSED.getValue());
							location.setMeterDataUpdatedDate(DateUtility.subtractDays(new Date(), 1));
							// added as part of registration
							location.setLseId(enrollment.getLseId());
							location.setSaId(enrollment.getSaId());
							locationsToCreate.add(location);
						}

						// Update the newly created/existing location to this enrollment
						enrollment.setLocation(location);

					}

					enrollmentsToUpdate.add(enrollment);

				}
				// save enrollment records to DB, which in turn saves location records as well
				if (enrollmentsToUpdate.size() > 0) {
					LOGGER.info("Saving enrollment and creating new location records.");
					count += enrollmentsToUpdate.size();
					enrollmentRepository.save(enrollmentsToUpdate);
				}
			} catch (Exception e) {
				LOGGER.error("Exception occured during eligibility check", e);
				LOGGER.info("Processed " + count + " records before exception");
				return count;
			}
		}
	}
}
