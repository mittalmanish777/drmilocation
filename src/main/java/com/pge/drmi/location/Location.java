package com.pge.drmi.location;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.pge.drmi.enrollment.Enrollment;

@Entity
@Table(name = "DRMS_LOCATION")
public class Location {

	private int locationId;
	private String fullName;
	private Date startDate;
	private Date endDate;
	private List<Enrollment> enrollment;
	private String status;
	private String san;
	private int failureCount;
	private String defendReason;
	private String uuid;
	private String batchId;
	private String caisoError;
	private String caisoLocationId;
	private String subLap;
	private Double meterDataPercentage;
	private Date meterDataUpdatedDate;
	private Date caisoSubmissionDate;
	private String lseId;
	private String saId;

	@Id
	@SequenceGenerator(name = "seq-gen", sequenceName = "drmi_location__c_id_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
	@Column(name = "id")
	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	@Column(name = "CAISO_LOCATION_ID")
	public String getCaisoLocationId() {
		return caisoLocationId;
	}

	public void setCaisoLocationId(String caisoLocationId) {
		this.caisoLocationId = caisoLocationId;
	}

	@Column(name = "CAISO_ERROR")
	public String getCaisoError() {
		return caisoError;
	}

	public void setCaisoError(String caisoError) {
		this.caisoError = caisoError;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@Column(name = "CAISO_LOCATION_START_DT")
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Column(name = "CAISO_LOCATION_END_DT")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "location")
	public List<Enrollment> getEnrollment() {
		return enrollment;
	}

	public void setEnrollment(List<Enrollment> enrollment) {
		this.enrollment = enrollment;
	}

	@Column(name = "CAISO_LOCATION_STATUS")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "UDC_ID")
	public String getSan() {
		return san;
	}

	public void setSan(String san) {
		this.san = san;
	}

	public int getFailureCount() {
		return failureCount;
	}

	public void setFailureCount(int failureCount) {
		this.failureCount = failureCount;
	}


	public String getDefendReason() {
		return defendReason;
	}

	public void setDefendReason(String defendReason) {
		this.defendReason = defendReason;
	}

	@Column(name = "UNIQ_SA_UUID")
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Column(name = "CAISO_BATCH_ID")
	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	@Column(name = "SUBLAP")
	public String getSubLap() {
		return subLap;
	}

	public void setSubLap(String subLap) {
		this.subLap = subLap;
	}

	public Double getMeterDataPercentage() {
		return meterDataPercentage;
	}

	public void setMeterDataPercentage(Double meterDataPercentage) {
		this.meterDataPercentage = meterDataPercentage;
	}

	@Column(name = "UPDATED_DATE")
	public Date getMeterDataUpdatedDate() {
		return meterDataUpdatedDate;
	}

	public void setMeterDataUpdatedDate(Date meterDataUpdatedDate) {
		this.meterDataUpdatedDate = meterDataUpdatedDate;
	}

	@Column(name = "CREATED_DATE")
	public Date getCaisoSubmissionDate() {
		return caisoSubmissionDate;
	}

	public void setCaisoSubmissionDate(Date caisoSubmissionDate) {
		this.caisoSubmissionDate = caisoSubmissionDate;
	}

	@Column(name = "LSE_ID")
	public String getLseId() {
		return lseId;
	}

	public void setLseId(String lseId) {
		this.lseId = lseId;
	}
		
	@Column(name = "SA_ID")
	public String getSaId() {
		return saId;
	}

	public void setSaId(String saId) {
		this.saId = saId;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Location ID : " + locationId + "\n");
		sb.append("Location Name : " + fullName + "\n");
		sb.append("Location Start Date : " + startDate + "\n");
		sb.append("Location End Date : " + endDate + "\n");
		sb.append("Location Status : " + LocationState.getStateFromValue(status) + "\tValue : " + status + "\n");
		sb.append("Location San ID  : " + san + "\n");
		sb.append("Location Failure ount: " + failureCount + "\n");
		sb.append("Location Defend Reason : " + defendReason + "\n");
		sb.append("Location UUID : " + uuid + "\n");
		sb.append("Location Batch ID  : " + batchId + "\n");
		sb.append("Location Caiso Error  : " + caisoError + "\n");
		sb.append("Location Caiso Location ID : " + caisoLocationId + "\n");
		sb.append("Location Suplap : " + subLap + "\n");
		sb.append("Location Meter Data Percentage : " + meterDataPercentage + "\n");
		sb.append("Location Meter Data Updated Date : " + meterDataUpdatedDate + "\n");
		sb.append("Location Caiso Submission Date : " + caisoSubmissionDate + "\n");
		sb.append("Location LSE ID : " + lseId + "\n");

		return sb.toString();
	}

}
