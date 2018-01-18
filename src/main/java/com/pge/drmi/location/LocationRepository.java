package com.pge.drmi.location;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface LocationRepository extends CrudRepository<Location, Integer> {
	
	Location findBylocationId(int locationId);
			
	List<Location> findByStatusIn(List<String> statusList, Pageable pageableFetch);

	List<Location> findByStatus(String status, Pageable pageableFetch);

	Location findBySan(String san);

	List<Location> findBySanIn(List<String> sanIds);

	List<Location> findByBatchId(String batchId);

	@Query("select distinct loc.batchId from Location loc where loc.status=:status")
	List<String> findDistinctBatchId(@Param("status") String status, Pageable pageable);

	@Query("select distinct loc.subLap from Location loc")
	List<String> findDistinctSublapId();

	List<Location> findBySubLapAndStatusIn(String subLap, List<String> statusList);


	List<Location> findBySubLapAndStatusInAndMeterDataUpdatedDateIsBefore(String subLap, List<String> statusList,Date date, Pageable pageableFetch);

	@Query(value = "select loc.id from {h-schema}drmi_registration__c reg inner join {h-schema}drmi_registration_enrollment__c re on reg.id=re.registrationid inner join {h-schema}drmi_enrollment__c e on re.enrollmentid=e.id inner join {h-schema}drmi_location__c loc on e.locationid=loc.id where loc.id IN (:locationIdList) and reg.status_ei__c IN (:registrationStatusList)", nativeQuery = true)
	List<Integer> findLocationIdbyLocationId(@Param("locationIdList") List<Integer> locationIdList,
			@Param("registrationStatusList") List<String> registrationStatusList);


}
