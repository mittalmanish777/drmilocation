package com.pge.drmi.enrollment;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.pge.drmi.location.Location;
import com.pge.drmi.program.Program;


@Entity
@Table(name = "drmi_enrollment__c")
public class Enrollment {
	
	private Integer enrollmentId;
	private Program program;
	private Location location;
	private String lseId;	
	private String status;
	private String subLap;
	private String rejectReason;
	private String uuid;
	private String addressLine1;
	private String addressLine2;
	private String city;
	private String state;
	private String zipCode;
	private Date startDate;
	private String name;
	private String customerName;
	private String saId;

	
			
	public Enrollment() {
		
	}
	

	@Id
	@Column(name = "id")
	public Integer getEnrollmentId() {
		return enrollmentId;
	}

	public void setEnrollmentId(Integer enrollmentId) {
		this.enrollmentId = enrollmentId;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	 @JoinColumn(name = "programid")
	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	} 
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "locationid")
	public Location getLocation() {
		return location;
	}
	
	
	public void setLocation(Location location) {
		this.location = location;
	}

	
	@Column(name = "lseid_ei__c")
	public String getLseId() {
		return lseId;
	}

	public void setLseId(String lseId) {
		this.lseId = lseId;
	}

	@Column(name = "status_ei__c")
	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "sublap_ei__c")
	public String getSubLap() {
		return subLap;
	}


	public void setSubLap(String subLap) {
		this.subLap = subLap;
	}

	
	@Column(name = "ineligiblereason_ei__c")
	public String getRejectReason() {
		return rejectReason;
	}


	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	@Column(name = "uuid_ei__c")
	public String getUuid() {
		return uuid;
	}


	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Column(name = "street_address_1_ei__c")
	public String getAddressLine1() {
		return addressLine1;
	}


	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	@Column(name = "street_address_2_ei__c")
	public String getAddressLine2() {
		return addressLine2;
	}


	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}
	
	@Column(name = "city_ei__c")
	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "state_ei__c")
	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}
	
	@Column(name = "zipcode_ei__c")
	public String getZipCode() {
		return zipCode;
	}


	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	
	@Column(name = "start_date_ei__c")
	public Date getStartDate() {
		return startDate;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "customer_name_ei__c")
	public String getCustomerName() {
		return customerName;
	}


	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	@Column(name = "sa_id_ei__c")
	public String getSaId() {
		return saId;
	}


	public void setSaId(String saId) {
		this.saId = saId;
	}

	
	
}
