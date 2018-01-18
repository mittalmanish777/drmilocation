package com.pge.drmi.program;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.pge.drmi.enrollment.Enrollment;

@Entity
@Table(name = "drmi_program__c")
public class Program {

	private Integer programId;
	private String marketProgramId;
	private Set<Enrollment> enrollment;

	@Id
	@Column(name = "id")
	public Integer getProgramId() {
		return programId;
	}

	public void setProgramId(Integer programId) {
		this.programId = programId;
	}

	@Column(name = "market_program_id__c")
	public String getMarketProgramId() {
		return marketProgramId;
	}

	public void setMarketProgramId(String marketProgramId) {
		this.marketProgramId = marketProgramId;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "program", cascade = CascadeType.ALL)
	public Set<Enrollment> getEnrollment() {
		return enrollment;
	}

	public void setEnrollment(Set<Enrollment> enrollment) {
		this.enrollment = enrollment;
	}
}
