package com.company.manager.models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name="employees")
@Entity
public class Employee extends Addressable {
	@Column(name="working_since", nullable = false)
	private Date workingSince;
	@ManyToOne(fetch=FetchType.LAZY, optional=false, cascade= CascadeType.PERSIST)
	@JoinColumn(name="office_id")
	private Office office;
	
	public Employee(){
		super();
	}
	
	public Date getWorkingSince() {
		return workingSince;
	}
	public void setWorkingSince(Date workingSince) {
		this.workingSince = workingSince;
	}
	public Office getOffice() {
		return office;
	}
	public void setOffice(Office office) {
		this.office = office;
	}
}
