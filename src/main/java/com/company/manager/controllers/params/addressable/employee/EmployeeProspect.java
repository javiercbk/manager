package com.company.manager.controllers.params.addressable.employee;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.company.manager.controllers.params.addressable.AddressableProspect;

public class EmployeeProspect extends AddressableProspect{
	@NotNull
	private Date workingSince;
	@NotNull
	private Long office;
	
	public Long getOffice() {
		return office;
	}
	public void setOffice(Long office) {
		this.office = office;
	}
	public Date getWorkingSince() {
		return workingSince;
	}
	public void setWorkingSince(Date workingSince) {
		this.workingSince = workingSince;
	}
}
